#!/usr/bin/env python3
"""
Побудова графіка прискорення S(P) = T(1)/T(P) та ефективності E(P) = S(P)/P
за результатами results/scaling.csv.

Використання:
    python3 tools/plot_speedup.py --csv results/scaling.csv --cores 8 \
            --out results/speedup.png --table results/table.md
"""
import argparse
import csv
import os
from collections import defaultdict

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

# ---- палітра (categorical слоти 1 і 2; еталонна лінія — нейтральна) -------
C_SERIES = {"sparse": "#2a78d6", "allgather": "#eb6834"}
C_IDEAL = "#9a9892"
C_TEXT = "#0b0b0b"
C_TEXT2 = "#52514e"
C_GRID = "#dedcd6"
C_SURFACE = "#fcfcfb"
C_OVER = "#f0efea"

LABEL = {
    "sparse": "розріджений обмін (MPI_Alltoallv)",
    "allgather": "наївний обмін (MPI_Allgatherv)",
}


def load(path):
    """-> {comm: {P: {поле: найкраще значення}}}, плюс метадані матриці."""
    best = defaultdict(dict)
    meta = {}
    with open(path, newline="") as f:
        for row in csv.DictReader(f):
            comm = row["comm"]
            p = int(row["procs"])
            t = float(row["t_iter"])
            cur = best[comm].get(p)
            if cur is None or t < cur["t_iter"]:
                best[comm][p] = {
                    "t_iter": t,
                    "t_kernel": float(row["t_kernel"]),
                    "t_comm": float(row["t_comm"]),
                    "gflops": float(row["gflops"]),
                    "imbalance": float(row["imbalance"]),
                    "ext_max": int(row["ext_max"]),
                    "rel_err": float(row["rel_err"]),
                    "tag": row["tag"],
                }
            meta = {
                "matrix": os.path.basename(row["matrix"]),
                "nrows": int(row["nrows"]),
                "ncols": int(row["ncols"]),
                "nnz": int(row["nnz"]),
                "reps": int(row["reps"]),
            }
    return best, meta


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--csv", default="results/scaling.csv")
    ap.add_argument("--out", default="results/speedup.png")
    ap.add_argument("--table", default="")
    ap.add_argument("--cores", type=int, default=0,
                    help="кількість фізичних ядер (для позначки надпідписки)")
    ap.add_argument("--trials", type=int, default=0,
                    help="скільки запусків на точку (лише для підпису)")
    args = ap.parse_args()

    best, meta = load(args.csv)
    if not best:
        raise SystemExit("[!] у CSV немає даних")

    modes = [m for m in ("sparse", "allgather") if m in best]
    all_p = sorted({p for m in modes for p in best[m]})
    pmax = max(all_p)

    # Спільна база T(1): при P = 1 обміну немає, тому обидві схеми — це той
    # самий послідовний код. Беремо найкращий із усіх запусків з P = 1, щоб
    # різниця між схемами не спотворювалася розкидом базового заміру.
    t1 = min(best[m][1]["t_iter"] for m in modes if 1 in best[m])

    fig, (ax, ax2) = plt.subplots(
        1, 2, figsize=(12.6, 5.0), dpi=160,
        gridspec_kw={"width_ratios": [1.25, 1.0], "wspace": 0.24})
    fig.patch.set_facecolor(C_SURFACE)

    for a in (ax, ax2):
        a.set_facecolor(C_SURFACE)
        a.grid(True, color=C_GRID, linewidth=0.8, linestyle="-", zorder=0)
        a.set_axisbelow(True)
        for s in ("top", "right"):
            a.spines[s].set_visible(False)
        for s in ("left", "bottom"):
            a.spines[s].set_color(C_GRID)
        a.tick_params(colors=C_TEXT2, labelsize=9.5, length=3)

    # зона надпідписки (процесів більше, ніж фізичних ядер)
    if args.cores and pmax > args.cores:
        for a in (ax, ax2):
            a.axvspan(args.cores, pmax * 1.06, color=C_OVER, zorder=0)
        ax.text(args.cores + (pmax - args.cores) * 0.5, 0.5,
                f"P > {args.cores}: процесів більше,\nніж фізичних ядер",
                ha="center", va="bottom", fontsize=8.5, color=C_TEXT2)

    # ---- панель 1: прискорення ------------------------------------------
    ax.plot([1, pmax], [1, pmax], linestyle="--", linewidth=1.5,
            color=C_IDEAL, zorder=1)
    ax.text(pmax, pmax, " ідеальне\n S = P", fontsize=8.5, color=C_TEXT2,
            va="center", ha="left")

    for m in modes:
        ps = sorted(best[m])
        s = [t1 / best[m][p]["t_iter"] for p in ps]
        ax.plot(ps, s, "-o", linewidth=2.0, markersize=7,
                color=C_SERIES[m], markeredgecolor=C_SURFACE,
                markeredgewidth=1.5, label=LABEL[m], zorder=3)
        ax.annotate(f"{s[-1]:.2f}", (ps[-1], s[-1]),
                    textcoords="offset points", xytext=(6, 7),
                    fontsize=9.5, color=C_TEXT, fontweight="bold")

    ax.set_xlabel("кількість MPI-процесів, P", fontsize=10.5, color=C_TEXT2)
    ax.set_ylabel("прискорення  S(P) = T(1) / T(P)", fontsize=10.5, color=C_TEXT2)
    ax.set_title("Прискорення паралельного множення", fontsize=12.5,
                 color=C_TEXT, pad=10, loc="left")
    ax.set_xlim(0.6, pmax * 1.1)
    ax.set_ylim(0, max(pmax, 2) * 1.12)
    ax.set_xticks(all_p)
    ax.set_xticklabels([str(p) for p in all_p])
    leg = ax.legend(frameon=False, fontsize=9.5, loc="upper left")
    for t in leg.get_texts():
        t.set_color(C_TEXT)

    # ---- панель 2: ефективність ------------------------------------------
    ax2.axhline(100, linestyle="--", linewidth=1.5, color=C_IDEAL, zorder=1)
    for m in modes:
        ps = sorted(best[m])
        e = [100.0 * (t1 / best[m][p]["t_iter"]) / p for p in ps]
        ax2.plot(ps, e, "-o", linewidth=2.0, markersize=7,
                 color=C_SERIES[m], markeredgecolor=C_SURFACE,
                 markeredgewidth=1.5, label=LABEL[m], zorder=3)
        ax2.annotate(f"{e[-1]:.0f}%", (ps[-1], e[-1]),
                     textcoords="offset points", xytext=(6, 6),
                     fontsize=9.5, color=C_TEXT, fontweight="bold")

    ax2.set_xlabel("кількість MPI-процесів, P", fontsize=10.5, color=C_TEXT2)
    ax2.set_ylabel("ефективність  E(P) = S(P) / P,  %", fontsize=10.5, color=C_TEXT2)
    ax2.set_title("Паралельна ефективність", fontsize=12.5,
                  color=C_TEXT, pad=10, loc="left")
    ax2.set_xlim(0.6, pmax * 1.1)
    ax2.set_ylim(0, 118)
    ax2.set_xticks(all_p)
    ax2.set_xticklabels([str(p) for p in all_p])

    def sp(n):                      # 1234567 -> "1 234 567"
        return f"{n:,}".replace(",", " ")

    sub = (f"матриця {meta['matrix']}:  {sp(meta['nrows'])} × {sp(meta['ncols'])}, "
           f"nnz = {sp(meta['nnz'])};   T — час однієї ітерації "
           f"(обмін + множення); для кожного P узято найкращий із "
           f"{args.trials if args.trials else 'кількох'} запусків, "
           f"база T(1) спільна для обох схем")
    fig.text(0.012, 0.015, sub, fontsize=9, color=C_TEXT2)
    fig.subplots_adjust(top=0.90, bottom=0.17, left=0.065, right=0.975)
    fig.savefig(args.out, facecolor=C_SURFACE)
    print(f"[OK] графік: {args.out}")

    # ---- таблиця ---------------------------------------------------------
    if args.table:
        lines = []
        for m in modes:
            ps = sorted(best[m])
            lines.append(f"\n### Схема обміну: {LABEL[m]}\n")
            lines.append("| P | T(P), мс | обчисл., мс | обмін, мс | S(P) | "
                         "E(P), % | GFLOP/s | дисбаланс | чужих комп. x | похибка |")
            lines.append("|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|")
            for p in ps:
                d = best[m][p]
                s = t1 / d["t_iter"]
                lines.append(
                    f"| {p}{'*' if d['tag'] == 'oversub' else ''} "
                    f"| {d['t_iter']*1e3:.3f} | {d['t_kernel']*1e3:.3f} "
                    f"| {d['t_comm']*1e3:.3f} | {s:.3f} | {100*s/p:.1f} "
                    f"| {d['gflops']:.2f} | {d['imbalance']:.3f} "
                    f"| {d['ext_max']} | {d['rel_err']:.1e} |")
        if any(best[m][p]["tag"] == "oversub" for m in modes for p in best[m]):
            lines.append("\n\\* процесів більше, ніж фізичних ядер "
                         "(надпідписка) — наведено для демонстрації насичення.")
        txt = "\n".join(lines) + "\n"
        with open(args.table, "w") as f:
            f.write(f"Матриця {meta['matrix']}: {meta['nrows']} × {meta['ncols']}, "
                    f"nnz = {meta['nnz']}, повторень на замір: {meta['reps']}\n")
            f.write(txt)
        print(f"[OK] таблиця: {args.table}")
        print(txt)


if __name__ == "__main__":
    main()
