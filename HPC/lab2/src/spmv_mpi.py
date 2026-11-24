#!/usr/bin/env python3
"""
Лабораторна робота № 2. MPI: паралельне множення розрідженої матриці
на вектор (SpMV) за алгоритмом Блеллоха.

    mpirun -np 4 python3 spmv_mpi.py data/wiki-Vote.txt --reps 20 --check

Детальний розбір кожної функції — у супровідному документі.
"""
import argparse
import os

import numpy as np
from mpi4py import MPI

MASK64 = (1 << 64) - 1                 # 64 одиниці: емуляція переповнення


#  1. Розріджена матриця у форматі CSR
class CSR:
    # rp[i]..rp[i+1]-1 — елементи рядка i; ci — стовпці; val — значення

    def __init__(self, rp, ci, val, nrows, ncols):
        self.rp = rp
        self.ci = ci
        self.val = val
        self.nrows = nrows
        self.ncols = ncols

    @property
    def nnz(self):
        return int(self.rp[-1])

    def row_stats(self):
        lens = np.diff(self.rp)
        return (int(lens.min()), int(lens.max()),
                float(lens.mean()), int((lens == 0).sum()))


def coo_to_csr(I, J, V, nrows, ncols):
    # Координатний список -> CSR, зі стовпцями, впорядкованими в рядку
    order = np.lexsort((J, I))                  # головний ключ I, вторинний J
    I, J, V = I[order], J[order], V[order]

    counts = np.bincount(I, minlength=nrows)    # довжина кожного рядка
    rp = np.zeros(nrows + 1, dtype=np.int64)
    np.cumsum(counts, out=rp[1:])               # rp[0] лишається нулем

    return CSR(rp, J.astype(np.int32), V.astype(np.float64), nrows, ncols)


#  2. Читання матриці
def read_matrix_market(path, verbose=False):
    # Формат бази SuiteSparse: real/integer/pattern, general/symmetric
    with open(path, "r", encoding="utf-8", errors="replace") as fh:
        header = fh.readline()
        if not header.startswith("%%MatrixMarket"):
            raise ValueError(f"'{path}' не схожий на Matrix Market")
        parts = header.lower().split()
        field = parts[3] if len(parts) > 3 else "real"
        symmetry = parts[4] if len(parts) > 4 else "general"

        line = fh.readline()
        while line.startswith("%") or line.strip() == "":
            line = fh.readline()
        nrows, ncols, nnz = (int(t) for t in line.split()[:3])

        data = np.loadtxt(fh, max_rows=nnz, ndmin=2)

    I = data[:, 0].astype(np.int64) - 1         # нумерація з 1 -> з 0
    J = data[:, 1].astype(np.int64) - 1
    if field == "pattern" or data.shape[1] < 3:
        V = np.ones(I.size, dtype=np.float64)
    else:
        V = data[:, 2].astype(np.float64)

    if symmetry in ("symmetric", "skew-symmetric"):
        off = I != J                            # діагональ не дублюємо
        sign = -1.0 if symmetry == "skew-symmetric" else 1.0
        I, J, V = (np.concatenate((I, J[off])),
                   np.concatenate((J, I[off])),
                   np.concatenate((V, sign * V[off])))

    if verbose:
        print(f"  файл : {path}\n"
              f"  тип : Matrix Market, {field}, {symmetry}\n"
              f"  розмір : {nrows} x {ncols}, nnz = {I.size}")

    return coo_to_csr(I, J, V, nrows, ncols)


def read_edge_list(path, verbose=False, weighted=False, seed=12345):
    # Список ребер SNAP: рядок 'a b' = ребро a->b, тобто елемент A[a][b]
    raw = np.loadtxt(path, dtype=np.int64, comments="#", ndmin=2)
    if raw.size == 0:
        raise ValueError(f"у файлі '{path}' не знайдено жодного ребра")
    src, dst = raw[:, 0], raw[:, 1]
    m = src.size

    flat = np.concatenate((src, dst))           # перенумерація вузлів
    ids, inv = np.unique(flat, return_inverse=True)
    inv = np.asarray(inv).reshape(-1)           # сумісність numpy 1.x / 2.x
    n = ids.size
    I, J = inv[:m], inv[m:]

    V = random_vector(m, seed) if weighted else np.ones(m, dtype=np.float64)

    if verbose:
        print(f"  файл : {path}\n"
              f"  тип : список ребер SNAP, орієнтований граф\n"
              f"  вузлів : {n} (перенумеровано у 0..{n - 1})\n"
              f"  ребер (nnz) : {m}, ваги: "
              f"{'випадкові' if weighted else 'одиничні'}")

    A = coo_to_csr(I, J, V, n, n)
    if verbose:
        lo, hi, avg, empty = A.row_stats()
        print(f"  довжина рядка   : min {lo}, max {hi}, середня {avg:.1f} "
              f"(порожніх рядків: {empty})")
    return A


def read_matrix(path, verbose=False, weighted=False, seed=12345):
    # Вибирає читач за першим рядком файлу
    with open(path, "r", encoding="utf-8", errors="replace") as fh:
        head = fh.readline()
    if head.lower().startswith("%%matrixmarket"):
        return read_matrix_market(path, verbose)
    return read_edge_list(path, verbose, weighted, seed)


#  3. Розбиття матриці між процесами
def partition_rows_nnz(rp, nproc):
    # межі блоків рядків так, щоб порівну НЕНУЛЬОВИХ елементів
    nrows = rp.size - 1
    nnz = int(rp[-1])
    bounds = np.zeros(nproc + 1, dtype=np.int32)
    bounds[nproc] = nrows
    for p in range(1, nproc):
        target = (nnz * p) // nproc
        bounds[p] = np.searchsorted(rp, target, side="left")
    np.maximum.accumulate(bounds, out=bounds)   # межі не спадають
    np.minimum(bounds, nrows, out=bounds)
    return bounds


def partition_rows_equal(nrows, nproc):
    # наївні межі: порівну рядків, залишок — першим процесам
    base, rest = divmod(nrows, nproc)
    sizes = np.full(nproc, base, dtype=np.int32)
    sizes[:rest] += 1
    bounds = np.zeros(nproc + 1, dtype=np.int32)
    np.cumsum(sizes, out=bounds[1:])
    return bounds


#  4. Ядро множення: y[i] = sum_k val[k] * x[ci[k]]
def spmv_loop(rp, ci, val, x, y):

    for i in range(rp.size - 1):
        s = 0.0
        for k in range(rp[i], rp[i + 1]):
            s += val[k] * x[ci[k]]
        y[i] = s
    return y


def spmv_vec(rp, ci, val, x, y):
    # векторизоване ядро: добутки масивом, суми по відрізках рядків
    prod = val * x[ci]
    y.fill(0.0)
    lens = rp[1:] - rp[:-1]
    ne = lens > 0                               # порожні рядки пропускаємо
    if np.any(ne):
        y[ne] = np.add.reduceat(prod, rp[:-1][ne])
    return y


#  5. План розрідженого обміну компонентами вектора x
class Halo:
    __slots__ = ("nown", "next", "recv_cnt", "recv_disp",
                 "send_cnt", "send_disp", "send_idx", "sendbuf")


def build_halo(ci, vec_start, comm):
    # план обміну + перенумерація у локальні індекси
    rank, nproc = comm.Get_rank(), comm.Get_size()
    vs, ve = int(vec_start[rank]), int(vec_start[rank + 1])

    h = Halo()
    h.nown = ve - vs

    # 1. які чужі компоненти потрібні і хто ними володіє
    foreign_mask = (ci < vs) | (ci >= ve)
    ext = np.unique(ci[foreign_mask]).astype(np.int32)
    h.next = int(ext.size)

    owners = np.searchsorted(vec_start, ext, side="right") - 1
    h.recv_cnt = np.bincount(owners, minlength=nproc).astype(np.int32)
    h.recv_disp = np.zeros(nproc, dtype=np.int32)
    np.cumsum(h.recv_cnt[:-1], out=h.recv_disp[1:])

    # 2. дзеркальний план: кому що відправляти
    h.send_cnt = np.empty(nproc, dtype=np.int32)
    comm.Alltoall([h.recv_cnt, MPI.INT], [h.send_cnt, MPI.INT])
    h.send_disp = np.zeros(nproc, dtype=np.int32)
    np.cumsum(h.send_cnt[:-1], out=h.send_disp[1:])
    send_total = int(h.send_cnt.sum())

    requested = np.empty(send_total, dtype=np.int32)
    comm.Alltoallv([ext, h.recv_cnt, h.recv_disp, MPI.INT],
                   [requested, h.send_cnt, h.send_disp, MPI.INT])
    h.send_idx = (requested - vs).astype(np.int64)
    h.sendbuf = np.empty(send_total, dtype=np.float64)

    # 3. свої стовпці -> 0..nown-1, чужі -> nown..nown+next-1
    ci_local = np.empty_like(ci)
    own = ~foreign_mask
    ci_local[own] = ci[own] - vs
    ci_local[foreign_mask] = h.nown + np.searchsorted(ext, ci[foreign_mask])

    return h, ci_local


def halo_exchange(h, xloc, comm):
    # пакування + один Alltoallv просто у хвіст xloc
    np.take(xloc, h.send_idx, out=h.sendbuf)
    comm.Alltoallv([h.sendbuf, h.send_cnt, h.send_disp, MPI.DOUBLE],
                   [xloc[h.nown:], h.recv_cnt, h.recv_disp, MPI.DOUBLE])


#  6. довільний вектор (xorshift64*, значення у [-1, 1])
def random_vector(n, seed):
    s = int(seed) if seed else 88172645463325252
    out = np.empty(n, dtype=np.float64)
    for i in range(n):
        s ^= s >> 12
        s = (s ^ (s << 25)) & MASK64
        s ^= s >> 27
        r = (s * 2685821657736338717) & MASK64
        out[i] = 2.0 * ((r >> 11) / 9007199254740992.0) - 1.0
    return out


#  7. Головна функція
def main():
    comm = MPI.COMM_WORLD
    rank, nproc = comm.Get_rank(), comm.Get_size()
    root = 0

    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("matrix", help="файл матриці (.mtx або список ребер)")
    ap.add_argument("--reps", type=int, default=10, help="повторень множення")
    ap.add_argument("--check", action="store_true", help="звірити з послідовним")
    ap.add_argument("--comm", choices=("sparse", "allgather"), default="sparse")
    ap.add_argument("--partition", choices=("nnz", "rows"), default="nnz")
    ap.add_argument("--weights", choices=("unit", "rand"), default="unit")
    ap.add_argument("--kernel", choices=("vec", "loop"), default="vec")
    ap.add_argument("--seed", type=int, default=42)
    ap.add_argument("--csv", default="", help="дописати рядок із вимірами")
    ap.add_argument("--tag", default="", help="позначка запуску в CSV")
    args = ap.parse_args()

    kernel = spmv_vec if args.kernel == "vec" else spmv_loop

    if rank == root:
        print("=" * 69)
        print(f" MPI SpMV (y = A*x) | процесів: {nproc} | обмін: {args.comm} "
              f"| розбиття: за {args.partition}")
        print("-" * 69)

    # --- читання матриці: тільки корінь
    A = None
    n = nnz_total = 0
    if rank == root:
        t0 = MPI.Wtime()
        A = read_matrix(args.matrix, verbose=True,
                        weighted=(args.weights == "rand"), seed=args.seed + 1)
        print(f"  час читання     : {MPI.Wtime() - t0:.3f} с")
        n, nnz_total = A.nrows, A.nnz
    n, nnz_total = comm.bcast((n, nnz_total), root=root)

    # --- межі блоків рядків 
    if rank == root:
        row_start = (partition_rows_nnz(A.rp, nproc) if args.partition == "nnz"
                     else partition_rows_equal(A.nrows, nproc))
    else:
        row_start = np.empty(nproc + 1, dtype=np.int32)
    comm.Bcast([row_start, MPI.INT], root=root)

    r0, r1 = int(row_start[rank]), int(row_start[rank + 1])
    nrows_loc = r1 - r0

    # rp невеликий (nrows+1 чисел) — розсилаємо цілком
    rp_full = A.rp if rank == root else np.empty(n + 1, dtype=np.int64)
    comm.Bcast([rp_full, MPI.INT64_T], root=root)

    nnz_cnt = np.diff(rp_full[row_start]).astype(np.int32)
    nnz_disp = np.zeros(nproc, dtype=np.int32)
    np.cumsum(nnz_cnt[:-1], out=nnz_disp[1:])
    nnz_loc = int(nnz_cnt[rank])

    # --- роздача ci та val
    ci_loc = np.empty(nnz_loc, dtype=np.int32)
    val_loc = np.empty(nnz_loc, dtype=np.float64)
    comm.Scatterv([A.ci if rank == root else None, nnz_cnt, nnz_disp, MPI.INT],
                  ci_loc, root=root)
    comm.Scatterv([A.val if rank == root else None, nnz_cnt, nnz_disp, MPI.DOUBLE],
                  val_loc, root=root)

    rp_loc = (rp_full[r0:r1 + 1] - rp_full[r0]).astype(np.int64)

    # --- вектор x
    x_glob = random_vector(n, args.seed) if rank == root else None
    vec_start = row_start                       # x ділимо так само, як рядки
    nown = nrows_loc
    vec_cnt = np.diff(vec_start).astype(np.int32)
    vec_disp = vec_start[:-1].astype(np.int32)

    if args.comm == "sparse":
        halo, ci_loc = build_halo(ci_loc, vec_start, comm)
        xloc = np.empty(halo.nown + halo.next, dtype=np.float64)
        comm.Scatterv([x_glob, vec_cnt, vec_disp, MPI.DOUBLE],
                      xloc[:nown], root=root)
        n_ext = halo.next
    else:
        halo = None
        xloc = np.empty(n, dtype=np.float64)    # весь вектор у кожного
        comm.Scatterv([x_glob, vec_cnt, vec_disp, MPI.DOUBLE],
                      xloc[vec_disp[rank]:vec_disp[rank] + nown], root=root)
        n_ext = n - nown

    y_loc = np.empty(nrows_loc, dtype=np.float64)

    def exchange():
        if args.comm == "sparse":
            halo_exchange(halo, xloc, comm)
        else:
            comm.Allgatherv(MPI.IN_PLACE,
                            [xloc, vec_cnt, vec_disp, MPI.DOUBLE])

    exchange()                                  # прогрів перед вимірюванням
    kernel(rp_loc, ci_loc, val_loc, xloc, y_loc)

    # --- вимірювання
    comm.Barrier()
    t_begin = MPI.Wtime()
    t_comm_loc = t_kernel_loc = 0.0
    for _ in range(args.reps):
        t0 = MPI.Wtime()
        exchange()
        t1 = MPI.Wtime()
        kernel(rp_loc, ci_loc, val_loc, xloc, y_loc)
        t2 = MPI.Wtime()
        t_comm_loc += t1 - t0
        t_kernel_loc += t2 - t1
    t_iter_loc = (MPI.Wtime() - t_begin) / args.reps
    t_comm_loc /= args.reps
    t_kernel_loc /= args.reps

    t_iter = comm.reduce(t_iter_loc, op=MPI.MAX, root=root)
    t_kernel = comm.reduce(t_kernel_loc, op=MPI.MAX, root=root)
    t_comm = comm.reduce(t_comm_loc, op=MPI.MAX, root=root)
    t_kernel_sum = comm.reduce(t_kernel_loc, op=MPI.SUM, root=root)
    ext_max = comm.reduce(n_ext, op=MPI.MAX, root=root)
    ext_sum = comm.reduce(n_ext, op=MPI.SUM, root=root)

    # --- збір результату
    row_cnt = np.diff(row_start).astype(np.int32)
    row_disp = row_start[:-1].astype(np.int32)
    y = np.empty(n, dtype=np.float64) if rank == root else None
    comm.Gatherv(y_loc, [y, row_cnt, row_disp, MPI.DOUBLE] if rank == root
                 else None, root=root)

    # --- звіт ---------------------------------------------------------------
    if rank == root:
        imbalance = t_kernel / (t_kernel_sum / nproc) if t_kernel_sum > 0 else 1.0
        print("-" * 69)
        print(f"  розмір          : {n} x {n},  nnz = {nnz_total}")
        print(f"  процесів (P)    : {nproc},  повторень: {args.reps}")
        print(f"  обмін x         : max {ext_max}, сумарно {ext_sum} "
              f"компонент за ітерацію ({100.0 * ext_sum / (nproc * n):.2f}% від N)")
        print(f"  t_ітерації      : {t_iter:.6f} с   (обмін + обчислення)")
        print(f"    t_обчислення  : {t_kernel:.6f} с")
        print(f"    t_обмін       : {t_comm:.6f} с   "
              f"({100.0 * t_comm / t_iter:.1f}% ітерації)")
        print(f"  продуктивність  : {2.0 * nnz_total / t_iter / 1e9:.3f} GFLOP/s")
        print(f"  дисбаланс max/avg : {imbalance:.3f}")
        print(f"  ||y||_2         : {np.linalg.norm(y):.12e}")

        if args.check:
            y_ref = np.empty(n, dtype=np.float64)
            kernel(A.rp, A.ci, A.val, x_glob, y_ref)
            err = float(np.max(np.abs(y - y_ref)))
            rel = err / max(float(np.max(np.abs(y_ref))), 1e-300)
            print(f"  ПЕРЕВІРКА       : max|y-y_посл| = {err:.3e}, "
                  f"відн. похибка = {rel:.3e} -> "
                  f"{'OK' if rel < 1e-12 else 'РОЗБІЖНІСТЬ'}")

        if args.csv:
            new = not os.path.exists(args.csv)
            with open(args.csv, "a") as fh:
                if new:
                    fh.write("procs,comm,partition,kernel,nnz,t_iter,t_kernel,"
                             "t_comm,imbalance,ext_sum,tag\n")
                fh.write(f"{nproc},{args.comm},{args.partition},{args.kernel},"
                         f"{nnz_total},{t_iter:.8f},{t_kernel:.8f},"
                         f"{t_comm:.8f},{imbalance:.4f},{ext_sum},{args.tag}\n")
            print(f"  → {args.csv}")
        print("=" * 69)


if __name__ == "__main__":
    main()