#!/usr/bin/env python3
"""
Генератор тестової розрідженої матриці у форматі Matrix Market.

Використання:  python3 tools/gen_matrix.py --n 60 --out data/matrix.mtx
"""
import argparse
import numpy as np


def build(n: int, seed: int = 1):
    """27-точковий шаблон на сітці n x n x n, лише нижній трикутник."""
    rng = np.random.default_rng(seed)
    N = n * n * n

    idx = np.arange(N, dtype=np.int64)
    iz, iy, ix = idx // (n * n), (idx // n) % n, idx % n

    rows, cols = [], []
    for dz in (-1, 0, 1):
        for dy in (-1, 0, 1):
            for dx in (-1, 0, 1):
                jz, jy, jx = iz + dz, iy + dy, ix + dx
                ok = (jz >= 0) & (jz < n) & (jy >= 0) & (jy < n) & (jx >= 0) & (jx < n)
                j = jz * n * n + jy * n + jx
                # Matrix Market symmetric: зберігаємо лише i >= j
                keep = ok & (idx >= j)
                rows.append(idx[keep])
                cols.append(j[keep])

    r = np.concatenate(rows)
    c = np.concatenate(cols)
    order = np.lexsort((r, c))            # впорядкування по стовпцях, як у SuiteSparse
    r, c = r[order], c[order]

    v = rng.uniform(-1.0, 1.0, size=r.size)
    v[r == c] = 30.0                      # домінуюча діагональ
    return N, r, c, v


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--n", type=int, default=60, help="розмір сітки (N = n^3 рядків)")
    ap.add_argument("--out", default="data/matrix.mtx")
    ap.add_argument("--seed", type=int, default=1)
    args = ap.parse_args()

    N, r, c, v = build(args.n, args.seed)
    print(f"[i] рядків: {N}, записів у файлі (нижній трикутник): {r.size}, "
          f"повний nnz після розгортання: {2 * r.size - N}")

    with open(args.out, "w") as f:
        f.write("%%MatrixMarket matrix coordinate real symmetric\n")
        f.write("% Синтетична матриця типу МСЕ (27-точковий шаблон), "
                "згенерована tools/gen_matrix.py\n")
        f.write(f"{N} {N} {r.size}\n")
        CH = 500_000
        for s in range(0, r.size, CH):
            e = min(s + CH, r.size)
            block = np.char.add(
                np.char.add((r[s:e] + 1).astype(str), " "),
                np.char.add(np.char.add((c[s:e] + 1).astype(str), " "),
                            np.char.mod("%.12g", v[s:e])))
            f.write("\n".join(block.tolist()))
            f.write("\n")
    print(f"[OK] записано {args.out}")


if __name__ == "__main__":
    main()
