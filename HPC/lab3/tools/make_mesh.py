#!/usr/bin/env python3
"""
Генерація меш-моделі (сфери) та збереження у STL.

    python3 tools/make_mesh.py --subdiv 5 --out data/sphere.stl

Кількість трикутників = 20 * 4**subdiv:
    3 ->   1 280      5 ->  20 480      7 -> 327 680
    4 ->   5 120      6 ->  81 920

Додатково друкує, наскільки компактними виходять частини моделі при
розбитті між MPI-процесами — у порядку граней «як у файлі» та після
перевпорядкування обходом у ширину.
"""
import argparse
import os
import sys

import numpy as np

sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src"))
import mesh_tools as mt                                       # noqa: E402


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--subdiv", type=int, default=5)
    ap.add_argument("--out", default="data/sphere.stl")
    ap.add_argument("--halo-report", action="store_true",
                    help="порівняти розмір гало для різних порядків граней")
    args = ap.parse_args()

    os.makedirs(os.path.dirname(os.path.abspath(args.out)), exist_ok=True)

    V, F = mt.icosphere(args.subdiv)
    print(f"[i] ікосфера: {len(V)} вершин, {len(F)} трикутників")
    mt.save_stl(args.out, V, F)
    size = os.path.getsize(args.out) / 1e6
    print(f"[OK] збережено {args.out} ({size:.1f} МБ)")

    # перечитуємо з STL — так само, як це робитиме програма моделювання
    V2, F2 = mt.load_stl(args.out)
    nbr, n_bnd, n_nm = mt.build_adjacency(F2, len(V2))
    area, _, _ = mt.geometry(V2, F2, nbr)
    print(f"[i] після читання STL і зварювання вершин: "
          f"{len(V2)} вершин, {len(F2)} трикутників")
    print(f"    межових ребер {n_bnd}, немноговидних {n_nm} -> "
          f"{'замкнена поверхня' if n_bnd == 0 and n_nm == 0 else 'УВАГА: сітка не замкнена'}")
    print(f"    площа {area.sum():.6f} (сфера 4*pi = {4 * np.pi:.6f}); "
          f"площі трикутників {area.min():.3e}..{area.max():.3e}")

    if args.halo_report:
        F3, nbr3 = mt.permute_faces(F2, nbr, mt.bfs_order(nbr))
        print("\n[i] розмір «гало» (чужих трикутників на процес), "
              "max по процесах:")
        print("    P   як у файлі   після BFS")
        for p in (2, 4, 8, 16):
            b = mt.block_bounds(len(F2), p)
            h_file = mt.halo_sizes(nbr, b).max()
            h_bfs = mt.halo_sizes(nbr3, b).max()
            print(f"   {p:3d}   {h_file:9d}   {h_bfs:9d}")


if __name__ == "__main__":
    main()
