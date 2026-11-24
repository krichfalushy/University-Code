#!/usr/bin/env python3
"""
Графіки для лабораторної №4.

    # точність залежно від довжини вікна
    python3 tools/plot_results.py --sweep results/sweep.csv \
            --accuracy results/accuracy.png

    # як прогноз іде за ціною
    python3 tools/plot_results.py --series results/series.csv \
            --forecast results/forecast.png --window 20

    # прискорення від кількості потоків
    python3 tools/plot_results.py --scaling results/scaling.csv --cores 8 \
            --speedup results/speedup.png --table results/table.md
"""
import argparse
import csv
import os
from collections import defaultdict

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

CAT = ["#2a78d6", "#eb6834", "#1baf7a", "#eda100",
       "#e87ba4", "#008300", "#6250d6", "#e34948"]
C_IDEAL, C_TEXT, C_TEXT2 = "#9a9892", "#0b0b0b", "#52514e"
C_GRID, C_SURFACE, C_OVER = "#dedcd6", "#fcfcfb", "#f0efea"


def style(ax):
    ax.set_facecolor(C_SURFACE)
    ax.grid(True, color=C_GRID, linewidth=0.8)
    ax.set_axisbelow(True)
    for sp in ("top", "right"):
        ax.spines[sp].set_visible(False)
    for sp in ("left", "bottom"):
        ax.spines[sp].set_color(C_GRID)
    ax.tick_params(colors=C_TEXT2, labelsize=9.5, length=3)


def legend(ax, loc="best"):
    leg = ax.legend(frameon=False, fontsize=9.5, loc=loc)
    for t in leg.get_texts():
        t.set_color(C_TEXT)


# ---------------------------------------------------------------------------
def fig_accuracy(sweep_path, out):
    rows = list(csv.DictReader(open(sweep_path)))
    naive = next(r for r in rows if r["method"] == "naive")
    data = defaultdict(lambda: {"w": [], "rmse": [], "da": []})
    for r in rows:
        if r["method"] == "naive":
            continue
        d = data[r["method"]]
        d["w"].append(int(r["window"]))
        d["rmse"].append(float(r["rmse"]))
        d["da"].append(float(r["dir_acc"]))

    fig, (ax, ax2) = plt.subplots(1, 2, figsize=(12.6, 4.9), dpi=160)
    fig.patch.set_facecolor(C_SURFACE)
    style(ax)
    style(ax2)

    names = {"sma": "просте середнє (SMA)", "wma": "зважене середнє (WMA)"}
    for i, m in enumerate(("sma", "wma")):
        if m not in data:
            continue
        ax.plot(data[m]["w"], data[m]["rmse"], color=CAT[i], linewidth=2.0,
                label=names[m])
        ax2.plot(data[m]["w"], data[m]["da"], color=CAT[i], linewidth=2.0,
                 label=names[m])

    nr = float(naive["rmse"])
    ax.axhline(nr, color=C_IDEAL, linestyle="--", linewidth=1.5)
    ax.text(0.015, nr, f"наївний прогноз (завтра = сьогодні): {nr:.3f}",
            transform=ax.get_yaxis_transform(), fontsize=9, color=C_TEXT2,
            va="bottom", ha="left")

    ax.set_xlabel("довжина вікна N, торгових днів", fontsize=10.5, color=C_TEXT2)
    ax.set_ylabel("RMSE прогнозу", fontsize=10.5, color=C_TEXT2)
    ax.set_title("Похибка прогнозу залежно від вікна", fontsize=12.5,
                 color=C_TEXT, loc="left", pad=10)
    ax.set_xscale("log")
    ax.set_xticks([2, 5, 10, 20, 50, 100, 252])
    ax.set_xticklabels(["2", "5", "10", "20", "50", "100", "252"])
    legend(ax, "upper left")

    ax2.axhline(50.0, color=C_IDEAL, linestyle="--", linewidth=1.5)
    ax2.text(0.015, 50.0, "випадкове вгадування: 50 %",
             transform=ax2.get_yaxis_transform(), fontsize=9, color=C_TEXT2,
             va="bottom", ha="left")
    peaks = []
    for i, m in enumerate(("sma", "wma")):
        if m not in data:
            continue
        w, da = data[m]["w"], data[m]["da"]
        j = max(range(len(da)), key=lambda k: da[k])
        ax2.plot([w[j]], [da[j]], "o", color=CAT[i], markersize=8,
                 markeredgecolor=C_SURFACE, markeredgewidth=1.5, zorder=4)
        peaks.append(f"{'SMA' if m == 'sma' else 'WMA'} — N = {w[j]} ({da[j]:.1f} %)")
    lo = min(min(data[m]["da"]) for m in data)
    hi = max(max(data[m]["da"]) for m in data)
    ax2.set_ylim(min(lo, 50.0) - 0.25, hi + 0.85)   # місце під підпис зверху
    if peaks:
        ax2.text(0.02, 0.97, "максимум влучань:  " + ",   ".join(peaks),
                 transform=ax2.transAxes, fontsize=9.5, color=C_TEXT,
                 va="top", ha="left", fontweight="bold")
    ax2.set_xlabel("довжина вікна N, торгових днів", fontsize=10.5, color=C_TEXT2)
    ax2.set_ylabel("вгадано напрямок руху, %", fontsize=10.5, color=C_TEXT2)
    ax2.set_title("Чи вгадує метод напрямок руху", fontsize=12.5,
                  color=C_TEXT, loc="left", pad=10)
    ax2.set_xscale("log")
    ax2.set_xticks([2, 5, 10, 20, 50, 100, 252])
    ax2.set_xticklabels(["2", "5", "10", "20", "50", "100", "252"])
    legend(ax2, "lower right")

    fig.subplots_adjust(top=0.89, bottom=0.14, left=0.065, right=0.975,
                        wspace=0.22)
    fig.savefig(out, facecolor=C_SURFACE)
    plt.close(fig)
    print(f"[OK] {out}")


# ---------------------------------------------------------------------------
def fig_forecast(series_path, out, window, last=260):
    rows = list(csv.DictReader(open(series_path)))[-last:]
    d = [r["date"] for r in rows]
    a = [float(r["actual"]) for r in rows]
    s = [float(r["sma"]) for r in rows]
    w = [float(r["wma"]) for r in rows]
    x = list(range(len(rows)))

    fig, ax = plt.subplots(figsize=(12.6, 4.6), dpi=160)
    fig.patch.set_facecolor(C_SURFACE)
    style(ax)

    ax.plot(x, a, color="#3f3e3a", linewidth=2.0, label="фактична ціна")
    ax.plot(x, s, color=CAT[0], linewidth=1.8, label=f"прогноз SMA, N = {window}")
    ax.plot(x, w, color=CAT[1], linewidth=1.8, label=f"прогноз WMA, N = {window}")

    step = max(len(x) // 8, 1)
    ax.set_xticks(x[::step])
    ax.set_xticklabels([d[i] for i in range(0, len(d), step)], fontsize=9)
    ax.set_xlabel("дата", fontsize=10.5, color=C_TEXT2)
    ax.set_ylabel("ціна", fontsize=10.5, color=C_TEXT2)
    ax.set_title("Прогноз на наступний день проти фактичної ціни",
                 fontsize=12.5, color=C_TEXT, loc="left", pad=10)
    legend(ax, "best")
    fig.text(0.012, 0.015,
             "Останні торгові дні ряду. Видно головну властивість рухомого "
             "середнього: воно запізнюється за ціною, і тим сильніше, чим "
             "довше вікно.",
             fontsize=9, color=C_TEXT2)
    fig.subplots_adjust(top=0.90, bottom=0.21, left=0.06, right=0.98)
    fig.savefig(out, facecolor=C_SURFACE)
    plt.close(fig)
    print(f"[OK] {out}")


# ---------------------------------------------------------------------------
def fig_speedup(scaling_path, out, cores, table, trials):
    best, meta = defaultdict(dict), {}
    for r in csv.DictReader(open(scaling_path)):
        mode, t, tm = r["parallel"], int(r["threads"]), float(r["time"])
        cur = best[mode].get(t)
        if cur is None or tm < cur["time"]:
            best[mode][t] = {"time": tm, "tag": r["tag"]}
        meta = {"data": os.path.basename(r["data"]), "points": int(r["points"]),
                "wmax": int(r["wmax"]), "repeat": int(r["repeat"])}

    modes = [m for m in ("days", "windows") if m in best]
    all_t = sorted({t for m in modes for t in best[m]})
    tmax = max(all_t)
    t1 = min(best[m][1]["time"] for m in modes if 1 in best[m])

    label = {"days": "розпаралелено за днями (дрібне зерно)",
             "windows": "розпаралелено за вікнами (грубе зерно)"}

    fig, (ax, ax2) = plt.subplots(
        1, 2, figsize=(12.6, 5.0), dpi=160,
        gridspec_kw={"width_ratios": [1.25, 1.0], "wspace": 0.24})
    fig.patch.set_facecolor(C_SURFACE)
    style(ax)
    style(ax2)

    if cores and tmax > cores:
        for a in (ax, ax2):
            a.axvspan(cores, tmax * 1.06, color=C_OVER, zorder=0)
        ax.text(cores + (tmax - cores) * 0.5, 0.45,
                f"потоків більше,\nніж ядер ({cores})", ha="center",
                va="bottom", fontsize=8.5, color=C_TEXT2)

    ax.plot([1, tmax], [1, tmax], "--", linewidth=1.5, color=C_IDEAL, zorder=1)
    ax.text(tmax, tmax, " ідеальне\n S = T", fontsize=8.5, color=C_TEXT2,
            va="center", ha="left")

    for i, m in enumerate(modes):
        ts = sorted(best[m])
        s = [t1 / best[m][t]["time"] for t in ts]
        ax.plot(ts, s, "-o", linewidth=2.0, markersize=7, color=CAT[i],
                markeredgecolor=C_SURFACE, markeredgewidth=1.5,
                label=label[m], zorder=3)
        ax.annotate(f"{s[-1]:.2f}", (ts[-1], s[-1]), textcoords="offset points",
                    xytext=(6, 7), fontsize=9.5, color=C_TEXT, fontweight="bold")
        e = [100.0 * (t1 / best[m][t]["time"]) / t for t in ts]
        ax2.plot(ts, e, "-o", linewidth=2.0, markersize=7, color=CAT[i],
                 markeredgecolor=C_SURFACE, markeredgewidth=1.5, zorder=3)
        ax2.annotate(f"{e[-1]:.0f}%", (ts[-1], e[-1]),
                     textcoords="offset points", xytext=(6, 6), fontsize=9.5,
                     color=C_TEXT, fontweight="bold")

    ax2.axhline(100, linestyle="--", linewidth=1.5, color=C_IDEAL, zorder=1)

    ax.set_xlabel("кількість потоків, T", fontsize=10.5, color=C_TEXT2)
    ax.set_ylabel("прискорення  S(T) = t(1) / t(T)", fontsize=10.5, color=C_TEXT2)
    ax.set_title("Прискорення обробки", fontsize=12.5, color=C_TEXT,
                 loc="left", pad=10)
    ax.set_xlim(0.6, tmax * 1.1)
    ax.set_ylim(0, max(tmax, 2) * 1.12)
    ax.set_xticks(all_t)
    legend(ax, "upper left")

    ax2.set_xlabel("кількість потоків, T", fontsize=10.5, color=C_TEXT2)
    ax2.set_ylabel("ефективність  E(T) = S(T) / T,  %", fontsize=10.5,
                   color=C_TEXT2)
    ax2.set_title("Паралельна ефективність", fontsize=12.5, color=C_TEXT,
                  loc="left", pad=10)
    ax2.set_xlim(0.6, tmax * 1.1)
    ax2.set_ylim(0, 118)
    ax2.set_xticks(all_t)

    def sp(n):
        return f"{n:,}".replace(",", " ")

    fig.text(0.012, 0.015,
             f"{meta['data']}: {sp(meta['points'])} торгових днів, вікна 2..{meta['wmax']}; "
             f"t — час повного перебору вікон, найкращий із "
             f"{trials if trials else 'кількох'} запусків",
             fontsize=9, color=C_TEXT2)
    fig.subplots_adjust(top=0.90, bottom=0.17, left=0.065, right=0.975)
    fig.savefig(out, facecolor=C_SURFACE)
    plt.close(fig)
    print(f"[OK] {out}")

    if table:
        lines = []
        for m in modes:
            ts = sorted(best[m])
            lines.append(f"\n### {label[m]}\n")
            lines.append("| потоків | t, с | S(T) | E(T), % |")
            lines.append("|---:|---:|---:|---:|")
            for t in ts:
                s = t1 / best[m][t]["time"]
                star = "*" if best[m][t]["tag"] == "oversub" else ""
                lines.append(f"| {t}{star} | {best[m][t]['time']:.4f} "
                             f"| {s:.3f} | {100 * s / t:.1f} |")
        if any(best[m][t]["tag"] == "oversub" for m in modes for t in best[m]):
            lines.append("\n\\* потоків більше, ніж фізичних ядер.")
        txt = "\n".join(lines) + "\n"
        with open(table, "w") as f:
            f.write(f"{meta['data']}: {meta['points']} торгових днів, "
                    f"вікна 2..{meta['wmax']}, повторів у заміри: "
                    f"{meta['repeat']}\n")
            f.write(txt)
        print(f"[OK] {table}")
        print(txt)


# ---------------------------------------------------------------------------
def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--sweep", default="")
    ap.add_argument("--accuracy", default="results/accuracy.png")
    ap.add_argument("--series", default="")
    ap.add_argument("--forecast", default="results/forecast.png")
    ap.add_argument("--window", type=int, default=20)
    ap.add_argument("--last", type=int, default=260)
    ap.add_argument("--scaling", default="")
    ap.add_argument("--speedup", default="results/speedup.png")
    ap.add_argument("--table", default="")
    ap.add_argument("--cores", type=int, default=0)
    ap.add_argument("--trials", type=int, default=0)
    args = ap.parse_args()

    if args.sweep:
        fig_accuracy(args.sweep, args.accuracy)
    if args.series:
        fig_forecast(args.series, args.forecast, args.window, args.last)
    if args.scaling:
        fig_speedup(args.scaling, args.speedup, args.cores, args.table,
                    args.trials)


if __name__ == "__main__":
    main()
