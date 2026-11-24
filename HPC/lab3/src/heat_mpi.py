#!/usr/bin/env python3
"""
Лабораторна робота №3 — Високопродуктивні обчислення
MPI. Моделювання процесів на полігональних поверхнях. Частина 1

Паралельне моделювання перенесення теплової енергії по поверхні меш-моделі.

Задача:
Поверхня подана трикутниками. Кожному трикутнику приписано температуру
(атрибут меша). Початково вся поверхня має T0 = 20 °C, а один трикутник —
T_hot = 100 °C. Тепло перетікає між трикутниками, що мають спільне ребро
(атрибут «сусідства»), доки температура не вирівняється.

Модель:
Метод скінченних об'ємів на дуальному графі меша. Для трикутника i:

    A_i * dT_i/dt = alpha * sum_j  w_ij * (T_j - T_i),    w_ij = L_ij / d_ij

де A_i — площа трикутника, L_ij — довжина спільного ребра, d_ij — відстань
між центрами трикутників, alpha — коефіцієнт температуропровідності.
Явний крок Ейлера:

    T_i^(n+1) = T_i^n + dt * alpha / A_i * sum_j w_ij * (T_j^n - T_i^n)

Крок за часом обирається з умови стійкості dt <= min_i A_i/(alpha*sum_j w_ij).

Оскільки w_ij = w_ji, схема точно зберігає теплову енергію sum_i A_i*T_i —
це використано як перевірка коректності.

Паралелезація:
Кожен крок: обмін температурами гало через Isend/Irecv ЛИШЕ із сусідніми
за поверхнею процесами, далі локальне оновлення.

ЗАПУСК
------
    mpirun -np 4 python3 src/heat_mpi.py data/sphere.stl --steps 2000 --check
"""
from __future__ import annotations

import argparse
import os
import sys
import time

import numpy as np
from mpi4py import MPI

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import mesh_tools as mt                                       # noqa: E402


# ---------------------------------------------------------------------------
def parse_args():
    p = argparse.ArgumentParser(
        description="MPI-моделювання теплопровідності на поверхні меша")
    p.add_argument("mesh", help="файл STL із меш-моделлю")
    p.add_argument("--steps", type=int, default=2000, help="кроків за часом")
    p.add_argument("--alpha", type=float, default=1.0e-3,
                   help="коефіцієнт температуропровідності, м^2/с")
    p.add_argument("--cfl", type=float, default=0.4,
                   help="запас стійкості (частка граничного кроку)")
    p.add_argument("--t0", type=float, default=20.0, help="початкова T, °C")
    p.add_argument("--thot", type=float, default=100.0, help="T гарячого, °C")
    p.add_argument("--order", choices=("bfs", "file"), default="bfs",
                   help="порядок граней: bfs — компактні блоки, "
                        "file — як у файлі")
    p.add_argument("--snapshots", type=int, default=6,
                   help="скільки кадрів температури зберегти")
    p.add_argument("--out", default="", help="файл .npz для кадрів і сітки")
    p.add_argument("--history", default="", help="файл .csv з історією T(t)")
    p.add_argument("--csv", default="", help="файл .csv для часу виконання")
    p.add_argument("--tag", default="", help="мітка запуску в CSV")
    p.add_argument("--diag-every", type=int, default=-1,
                   help="через скільки кроків писати T(t); 0 — вимкнути "
                        "(для чистих замірів часу)")
    p.add_argument("--ref", default="",
                   help="файл .npz еталонного запуску: звірити поле T у кінці")
    p.add_argument("--check", action="store_true",
                   help="друкувати перевірки збереження енергії")
    return p.parse_args()


# ---------------------------------------------------------------------------
def main():
    comm = MPI.COMM_WORLD
    rank, nproc = comm.Get_rank(), comm.Get_size()
    root = 0
    args = parse_args()

    # =======================================================================
    #  1. Ранг 0: читає меш, будує сусідство та геометрію
    # =======================================================================
    if rank == root:
        t0 = time.perf_counter()
        V, F = mt.load_stl(args.mesh)
        nbr, n_bnd, n_nm = mt.build_adjacency(F, len(V))

        if args.order == "bfs":
            F, nbr = mt.permute_faces(F, nbr, mt.bfs_order(nbr))

        area, centroid, w = mt.geometry(V, F, nbr)
        nf = F.shape[0]
        t_load = time.perf_counter() - t0

        hot = int(np.argmax(centroid[:, 2]))      # трикутник біля «полюса»

        print("=" * 69)
        print(f" MPI: теплоперенос на поверхні меша | процесів: {nproc}")
        print("-" * 69)
        print(f"  файл            : {args.mesh}")
        print(f"  вершин / граней : {len(V)} / {nf}")
        print(f"  межових ребер   : {n_bnd}, немноговидних: {n_nm}"
              f"  -> {'замкнена поверхня' if n_bnd == 0 else 'є межа'}")
        print(f"  площа сумарна   : {area.sum():.6f}")
        print(f"  порядок граней  : {args.order}")
        print(f"  гарячий трикутник: #{hot}, T = {args.thot} °C, "
              f"решта {args.t0} °C")
        print(f"  час читання     : {t_load:.3f} с")
    else:
        V = F = nbr = area = centroid = w = None
        nf, hot = 0, 0

    nf = comm.bcast(nf, root=root)
    hot = comm.bcast(hot, root=root)

    # =======================================================================
    #  2. Розбиття моделі на частини й роздача фрагментів
    # =======================================================================
    bounds = mt.block_bounds(nf, nproc)
    s, e = int(bounds[rank]), int(bounds[rank + 1])
    n_own = e - s

    counts3 = np.diff(bounds).astype(np.int64) * 3
    disp3 = (bounds[:-1] * 3).astype(np.int64)
    counts1 = np.diff(bounds).astype(np.int64)
    disp1 = bounds[:-1].astype(np.int64)

    nbr_loc = np.empty((n_own, 3), dtype=np.int64)
    w_loc = np.empty((n_own, 3), dtype=np.float64)
    area_loc = np.empty(n_own, dtype=np.float64)

    send_nbr = [np.ascontiguousarray(nbr), counts3, disp3, MPI.INT64_T] \
        if rank == root else None
    send_w = [np.ascontiguousarray(w), counts3, disp3, MPI.DOUBLE] \
        if rank == root else None
    send_a = [np.ascontiguousarray(area), counts1, disp1, MPI.DOUBLE] \
        if rank == root else None
    comm.Scatterv(send_nbr, nbr_loc, root=root)
    comm.Scatterv(send_w, w_loc, root=root)
    comm.Scatterv(send_a, area_loc, root=root)


    #  3. План обміну гало: які чужі трикутники потрібні цьому процесу
    valid = nbr_loc >= 0
    g = nbr_loc[valid]
    foreign = np.unique(g[(g < s) | (g >= e)])          # відсортовані глобальні
    n_ghost = foreign.size

    owners = np.searchsorted(bounds, foreign, side="right") - 1
    recv_cnt = np.bincount(owners, minlength=nproc).astype(np.int64)
    recv_disp = np.concatenate(([0], np.cumsum(recv_cnt)[:-1])).astype(np.int64)

    send_cnt = np.empty(nproc, dtype=np.int64)
    comm.Alltoall([recv_cnt, MPI.INT64_T], [send_cnt, MPI.INT64_T])
    send_disp = np.concatenate(([0], np.cumsum(send_cnt)[:-1])).astype(np.int64)
    send_total = int(send_cnt.sum())

    send_glob = np.empty(send_total, dtype=np.int64)
    comm.Alltoallv([foreign, recv_cnt, recv_disp, MPI.INT64_T],
                   [send_glob, send_cnt, send_disp, MPI.INT64_T])
    send_loc = (send_glob - s).astype(np.int64)         # локальні індекси

    # перенумерація сусідів у локальні індекси масиву T
    nbr_local = np.full_like(nbr_loc, -1)
    own_mask = valid & (nbr_loc >= s) & (nbr_loc < e)
    nbr_local[own_mask] = nbr_loc[own_mask] - s
    ghost_mask = valid & ~own_mask
    nbr_local[ghost_mask] = n_own + np.searchsorted(foreign, nbr_loc[ghost_mask])

    # індекси для векторизованого оновлення (-1 замінюємо на 0, вага там 0)
    nbr_safe = np.where(nbr_local >= 0, nbr_local, 0)

    partners_recv = [p for p in range(nproc) if recv_cnt[p] > 0 and p != rank]
    partners_send = [p for p in range(nproc) if send_cnt[p] > 0 and p != rank]

    
    #  4. Початкові умови та крок за часом
    T = np.full(n_own + n_ghost, args.t0, dtype=np.float64)
    if s <= hot < e:
        T[hot - s] = args.thot

    wsum = w_loc.sum(axis=1)
    loc_lim = np.min(np.where(wsum > 0, area_loc / (args.alpha * np.maximum(wsum, 1e-300)),
                              np.inf))
    dt = args.cfl * comm.allreduce(float(loc_lim), op=MPI.MIN)

    inv_area = args.alpha * dt / area_loc
    sendbuf = np.empty(send_total, dtype=np.float64)

    E0 = comm.allreduce(float(area_loc @ T[:n_own]), op=MPI.SUM)
    A_tot = comm.allreduce(float(area_loc.sum()), op=MPI.SUM)
    T_eq = E0 / A_tot                       # рівноважна температура

    if rank == root:
        print(f"  крок за часом   : dt = {dt:.6e} с (CFL = {args.cfl})")
        print(f"  модельний час   : {args.steps * dt:.6e} с за {args.steps} кроків")
        print(f"  рівноважна T    : {T_eq:.9f} °C")

    # кадри для візуалізації — лише коли їх справді зберігають
    if args.out:
        # кадри в логарифмічному масштабі часу: на початку процес швидкий,
        # далі сповільнюється, тому рівномірні кадри нецікаві
        k = max(args.snapshots, 2)
        fr = np.concatenate(([0.0], np.geomspace(0.025, 1.0, k - 1)))
        snap_steps = sorted(set(np.round(fr * args.steps).astype(int).tolist()))
    else:
        snap_steps = []
    # моменти запису T(t): автоматично — логарифмічна сітка, бо процес
    # найшвидший на початку; 0 — вимкнути заміри (для чистих вимірів часу)
    if args.diag_every > 0:
        diag_steps = set(range(0, args.steps + 1, args.diag_every))
    elif args.diag_every == 0:
        diag_steps = set()
    else:
        ls = np.unique(np.round(np.logspace(
            0, np.log10(max(args.steps, 2)), 140)).astype(int))
        diag_steps = set([0] + ls.tolist())
    if diag_steps:
        diag_steps.add(args.steps)
    snaps, snap_times = [], []

    hist = []                               # (крок, час, Tmin, Tmax, E)

    def gather_T():
        """Збирає повне поле температур на ранзі 0."""
        buf = np.empty(nf, dtype=np.float64) if rank == root else None
        comm.Gatherv(np.ascontiguousarray(T[:n_own]),
                     [buf, counts1, disp1, MPI.DOUBLE] if rank == root else None,
                     root=root)
        return buf

    def record(step):
        tmin = comm.allreduce(float(T[:n_own].min()), op=MPI.MIN)
        tmax = comm.allreduce(float(T[:n_own].max()), op=MPI.MAX)
        E = comm.allreduce(float(area_loc @ T[:n_own]), op=MPI.SUM)
        if rank == root:
            hist.append((step, step * dt, tmin, tmax, E))


    #  5. Цикл за часом
    t_comm = t_calc = 0.0
    comm.Barrier()
    t_begin = MPI.Wtime()

    for step in range(args.steps + 1):
        # --- обмін температурами гало ---------------------------------
        tc = MPI.Wtime()
        if nproc > 1:
            np.take(T, send_loc, out=sendbuf)
            reqs = []
            for p in partners_recv:
                lo = n_own + int(recv_disp[p])
                reqs.append(comm.Irecv(T[lo:lo + int(recv_cnt[p])], source=p, tag=11))
            for p in partners_send:
                lo = int(send_disp[p])
                reqs.append(comm.Isend(sendbuf[lo:lo + int(send_cnt[p])], dest=p, tag=11))
            MPI.Request.Waitall(reqs)
        t_comm += MPI.Wtime() - tc

        if step in diag_steps:
            record(step)
        if step in snap_steps:
            full = gather_T()
            if rank == root:
                snaps.append(full.copy())
                snap_times.append(step * dt)
        if step == args.steps:
            break

        # --- локальне оновлення температур ----------------------------
        tk = MPI.Wtime()
        flux = (w_loc * (T[nbr_safe] - T[:n_own, None])).sum(axis=1)
        T[:n_own] += inv_area * flux
        t_calc += MPI.Wtime() - tk

    t_loop = MPI.Wtime() - t_begin



if __name__ == "__main__":
    main()
