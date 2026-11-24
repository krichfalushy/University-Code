#!/usr/bin/env python3
"""
Графік прискорення S(P) = T(1)/T(P) та паралельної ефективності E(P) = S(P)/P
за результатами results/scaling.csv.

Для кожного P береться найкращий (мінімальний) час із кількох запусків.
База T(1) спільна для обох порядків нумерації граней: при одному процесі
обміну немає, тому порядок граней на час не впливає.
"""
import argparse
import csv
import os
from collections import defaultdict

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

C_SERIES = {"bfs": "#2a78d6", "file": "#eb6834"}
C_IDEAL, C_TEXT, C_TEXT2 = "#9a9892", "#0b0b0b", "#52514e"
C_GRID, C_SURFACE, C_OVER = "#dedcd6", "#fcfcfb", "#f0efea"

LABEL = {
    "bfs": "компактні частини (перевпорядкування BFS)",
    "file": "частини «як у файлі» (без перевпорядкування)",
}


def load(path):
    best, meta = defaultdict(dict), {}
    with open(path, newline="") as f:
        for row in csv.DictReader(f):
            o, p, t = row["order"], int(row["procs"]), float(row["t_loop"])
            cur = best[o].get(p)
            if cur is None or t < cur["t_loop"]:
                best[o][p] = {
                    "t_loop": t,
                    "t_calc": float(row["t_calc"]),
                    "t_comm": float(row["t_comm"]),
                    "halo_max": int(row["halo_max"]),
                    "dE": float(row["dE_rel"]),
                    "tag": row["tag"],
                }
            meta = {"mesh": row["mesh"], "faces": int(row["faces"]),
                    "steps": int(row["steps"])}
    return best, meta


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--csv", default="results/scaling.csv")
    ap.add_argument("--out", default="results/speedup.png")
    ap.add_argument("--table", default="")
    ap.add_argument("--cores", type=int, default=0)
    ap.add_argument("--trials", type=int, default=0)
    args = ap.parse_args()

    best, meta = load(args.csv)
    if not best:
        raise SystemExit("[!] у CSV немає даних")

    orders = [o for o in ("bfs", "file") if o in best]
    all_p = sorted({p for o in orders for p in best[o]})
    pmax = max(all_p)
    t1 = min(best[o][1]["t_loop"] for o in orders if 1 in best[o])

    fig, (ax, ax2) = plt.subplots(
        1, 2, figsize=(12.6, 5.0), dpi=160,
        gridspec_kw={"width_ratios": [1.25, 1.0], "wspace": 0.24})
    fig.patch.set_facecolor(C_SURFACE)
    for a in (ax, ax2):
        a.set_facecolor(C_SURFACE)
        a.grid(True, color=C_GRID, linewidth=0.8)
        a.set_axisbelow(True)
        for sp in ("top", "right"):
            a.spines[sp].set_visible(False)
        for sp in ("left", "bottom"):
            a.spines[sp].set_color(C_GRID)
        a.tick_params(colors=C_TEXT2, labelsize=9.5, length=3)

    if args.cores and pmax > args.cores:
        for a in (ax, ax2):
            a.axvspan(args.cores, pmax * 1.06, color=C_OVER, zorder=0)
        ax.text(args.cores + (pmax - args.cores) * 0.5, 0.45,
                f"P > {args.cores}: процесів більше,\nніж фізичних ядер",
                ha="center", va="bottom", fontsize=8.5, color=C_TEXT2)

    ax.plot([1, pmax], [1, pmax], "--", linewidth=1.5, color=C_IDEAL, zorder=1)
    ax.text(pmax, pmax, " ідеальне\n S = P", fontsize=8.5, color=C_TEXT2,
            va="center", ha="left")

    for o in orders:
        ps = sorted(best[o])
        s = [t1 / best[o][p]["t_loop"] for p in ps]
        ax.plot(ps, s, "-o", linewidth=2.0, markersize=7, color=C_SERIES[o],
                markeredgecolor=C_SURFACE, markeredgewidth=1.5,
                label=LABEL[o], zorder=3)
        ax.annotate(f"{s[-1]:.2f}", (ps[-1], s[-1]), textcoords="offset points",
                    xytext=(6, 7), fontsize=9.5, color=C_TEXT, fontweight="bold")

        e = [100.0 * (t1 / best[o][p]["t_loop"]) / p for p in ps]
        ax2.plot(ps, e, "-o", linewidth=2.0, markersize=7, color=C_SERIES[o],
                 markeredgecolor=C_SURFACE, markeredgewidth=1.5, zorder=3)
        ax2.annotate(f"{e[-1]:.0f}%", (ps[-1], e[-1]),
                     textcoords="offset points", xytext=(6, 6),
                     fontsize=9.5, color=C_TEXT, fontweight="bold")

    ax2.axhline(100, linestyle="--", linewidth=1.5, color=C_IDEAL, zorder=1)

    ax.set_xlabel("кількість MPI-процесів, P", fontsize=10.5, color=C_TEXT2)
    ax.set_ylabel("прискорення  S(P) = T(1) / T(P)", fontsize=10.5, color=C_TEXT2)
    ax.set_title("Прискорення моделювання", fontsize=12.5, color=C_TEXT,
                 loc="left", pad=10)
    ax.set_xlim(0.6, pmax * 1.1)
    ax.set_ylim(0, max(pmax, 2) * 1.12)
    ax.set_xticks(all_p)
    leg = ax.legend(frameon=False, fontsize=9.5, loc="upper left")
    for t in leg.get_texts():
        t.set_color(C_TEXT)

    ax2.set_xlabel("кількість MPI-процесів, P", fontsize=10.5, color=C_TEXT2)
    ax2.set_ylabel("ефективність  E(P) = S(P) / P,  %", fontsize=10.5,
                   color=C_TEXT2)
    ax2.set_title("Паралельна ефективність", fontsize=12.5, color=C_TEXT,
                  loc="left", pad=10)
    ax2.set_xlim(0.6, pmax * 1.1)
    ax2.set_ylim(0, 118)
    ax2.set_xticks(all_p)

    def sp(n):
        return f"{n:,}".replace(",", " ")

    fig.text(0.012, 0.015,
             f"меш {meta['mesh']}: {sp(meta['faces'])} трикутників, "
             f"{sp(meta['steps'])} кроків за часом;  T — час циклу "
             f"моделювання, найкращий із "
             f"{args.trials if args.trials else 'кількох'} запусків",
             fontsize=9, color=C_TEXT2)
    fig.subplots_adjust(top=0.90, bottom=0.17, left=0.065, right=0.975)
    fig.savefig(args.out, facecolor=C_SURFACE)
    print(f"[OK] графік: {args.out}")

    if args.table:
        lines = []
        for o in orders:
            ps = sorted(best[o])
            lines.append(f"\n### Порядок граней: {LABEL[o]}\n")
            lines.append("| P | T(P), с | обчисл., с | обмін, с | S(P) | "
                         "E(P), % | чужих граней | dE/E |")
            lines.append("|---:|---:|---:|---:|---:|---:|---:|---:|")
            for p in ps:
                d = best[o][p]
                s = t1 / d["t_loop"]
                lines.append(
                    f"| {p}{'*' if d['tag'] == 'oversub' else ''} "
                    f"| {d['t_loop']:.3f} | {d['t_calc']:.3f} | {d['t_comm']:.3f} "
                    f"| {s:.3f} | {100 * s / p:.1f} | {d['halo_max']} "
                    f"| {d['dE']:.1e} |")
        if any(best[o][p]["tag"] == "oversub" for o in orders for p in best[o]):
            lines.append("\n\\* процесів більше, ніж фізичних ядер "
                         "(надпідписка) — для демонстрації насичення.")
        txt = "\n".join(lines) + "\n"
        with open(args.table, "w") as f:
            f.write(f"Меш {meta['mesh']}: {meta['faces']} трикутників, "
                    f"{meta['steps']} кроків за часом\n")
            f.write(txt)
        print(f"[OK] таблиця: {args.table}")
        print(txt)


if __name__ == "__main__":
    main()
