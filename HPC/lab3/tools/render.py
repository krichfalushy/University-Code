#!/usr/bin/env python3
"""
Візуалізація результатів моделювання.

    python3 tools/render.py --npz results/field.npz --outdir results

Створює:
    heat_frames.png     кадри поширення тепла по поверхні сфери
    decomposition.png   розбиття моделі між MPI-процесами
    history.png         T_max(t) і перевірка збереження енергії

Сфера опукла, тому для коректного зображення досить відкинути грані,
повернені від спостерігача (back-face culling) — сортувати за глибиною
не потрібно. Проєкція ортографічна; малюється плоским PolyCollection,
що значно швидше за mplot3d на десятках тисяч полігонів.
"""
import argparse
import os

import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.collections import PolyCollection
from matplotlib.colors import Normalize, ListedColormap

# палітра з довідника візуалізації (категорійні слоти 1..8)
CAT = ["#2a78d6", "#eb6834", "#1baf7a", "#eda100",
       "#e87ba4", "#008300", "#6250d6", "#e34948"]
C_TEXT, C_TEXT2, C_SURFACE, C_GRID = "#0b0b0b", "#52514e", "#fcfcfb", "#dedcd6"


# ---------------------------------------------------------------------------
def view_basis(target: np.ndarray, tilt: float = 0.45):
    """Ортонормований базис камери, спрямованої на точку target."""
    f = target / np.linalg.norm(target)
    helper = np.array([0.0, 0.0, 1.0])
    if abs(f @ helper) > 0.9:
        helper = np.array([0.0, 1.0, 0.0])
    side = np.cross(helper, f)
    side /= np.linalg.norm(side)
    f = f + tilt * side                      # легкий нахил, щоб було об'ємніше
    f /= np.linalg.norm(f)
    right = np.cross(np.array([0.0, 0.0, 1.0]), f)
    if np.linalg.norm(right) < 1e-8:
        right = np.array([1.0, 0.0, 0.0])
    right /= np.linalg.norm(right)
    up = np.cross(f, right)
    return right, up, f


def project(ax, V, F, centroid, values, cmap, norm, forward, right, up,
            edge=False):
    """Малює видиму півсферу, забарвлену за values (одне число на грань)."""
    vis = centroid @ forward > 0.0
    tri = V[F[vis]]
    xy = np.stack([tri @ right, tri @ up], axis=-1)      # (n, 3, 2)
    colors = cmap(norm(values[vis]))
    pc = PolyCollection(xy, facecolors=colors, edgecolors=colors,
                        linewidths=0.3, antialiased=False)
    ax.add_collection(pc)
    r = 1.02
    ax.set_xlim(-r, r)
    ax.set_ylim(-r, r)
    ax.set_aspect("equal")
    ax.axis("off")
    return vis


# ---------------------------------------------------------------------------
def fig_frames(d, outdir):
    V, F = d["vertices"], d["faces"]
    centroid, snaps, times = d["centroid"], d["snapshots"], d["snap_times"]
    hot = int(d["hot"])
    right, up, fwd = view_basis(centroid[hot])

    n = len(snaps)
    cols = min(n, 3)
    rows = int(np.ceil(n / cols))
    fig, axes = plt.subplots(rows, cols, figsize=(4.1 * cols, 4.25 * rows),
                             dpi=150)
    fig.patch.set_facecolor(C_SURFACE)
    axes = np.atleast_1d(axes).ravel()
    # «inferno» без найтемнішого кінця: холодна сфера має читатися як тіло,
    # а не як чорний диск
    cmap = ListedColormap(plt.get_cmap("inferno")(np.linspace(0.10, 1.0, 256)))

    T0 = float(snaps[0].min())
    for k, ax in enumerate(axes):
        ax.set_facecolor(C_SURFACE)
        if k >= n:
            ax.axis("off")
            continue
        T = snaps[k]
        vmax = max(float(T.max()), T0 + 1e-9)
        norm = Normalize(vmin=T0, vmax=vmax)
        project(ax, V, F, centroid, T, cmap, norm, fwd, right, up)
        if k == 0:                       # показати, де саме гарячий полігон
            hx, hy = centroid[hot] @ right, centroid[hot] @ up
            ax.add_patch(plt.Circle((hx, hy), 0.085, fill=False,
                                    color="#ffffff", linewidth=1.3))
            ax.annotate("один гарячий полігон", (hx + 0.07, hy - 0.06),
                        xytext=(hx + 0.30, hy - 0.40), fontsize=9,
                        color="#ffffff", ha="left", va="center",
                        arrowprops=dict(arrowstyle="-", color="#ffffff",
                                        linewidth=1.0))
        ax.set_title(f"t = {times[k]:.3g} с\nmax T = {vmax:.2f} °C",
                     fontsize=10.5, color=C_TEXT, pad=6)

    fig.suptitle("Поширення тепла від одного гарячого полігона по поверхні",
                 fontsize=13.5, color=C_TEXT, y=0.985)
    fig.text(0.5, 0.012,
             "Колір — температура полігона; у кожному кадрі своя шкала "
             f"від {T0:.0f} °C (темний) до max T (світлий), бо контраст швидко спадає.",
             ha="center", fontsize=9, color=C_TEXT2)
    fig.subplots_adjust(top=0.90, bottom=0.07, wspace=0.02, hspace=0.12)
    path = os.path.join(outdir, "heat_frames.png")
    fig.savefig(path, facecolor=C_SURFACE)
    plt.close(fig)
    print(f"[OK] {path}")


def fig_decomposition(mesh_path, outdir, nproc=8):
    import sys
    sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                    "..", "src"))
    import mesh_tools as mt

    # читаємо меш у ВИХІДНОМУ порядку граней, як він лежить у файлі STL
    V, F = mt.load_stl(mesh_path)
    nbr, _, _ = mt.build_adjacency(F, len(V))
    centroid = V[F].mean(axis=1)
    nf = len(F)

    order = mt.bfs_order(nbr)            # order[k] — грань на k-му місці обходу
    pos_bfs = np.empty(nf, dtype=np.int64)
    pos_bfs[order] = np.arange(nf)

    bounds = mt.block_bounds(nf, nproc)
    owner_file = np.searchsorted(bounds, np.arange(nf), side="right") - 1
    owner_bfs = np.searchsorted(bounds, pos_bfs, side="right") - 1

    halo_file = mt.halo_sizes(nbr, bounds).max()
    F2, nbr2 = mt.permute_faces(F, nbr, order)
    halo_bfs = mt.halo_sizes(nbr2, bounds).max()

    hot = int(np.argmax(centroid[:, 2]))
    right, up, fwd = view_basis(centroid[hot], tilt=0.6)

    cmap = ListedColormap(CAT[:nproc])
    norm = Normalize(vmin=-0.5, vmax=nproc - 0.5)

    fig, axes = plt.subplots(1, 2, figsize=(9.4, 5.4), dpi=150)
    fig.patch.set_facecolor(C_SURFACE)
    panels = ((axes[0], owner_file, "порядок граней як у файлі STL", halo_file),
              (axes[1], owner_bfs, "після перевпорядкування (BFS)", halo_bfs))
    for ax, own, ttl, halo in panels:
        ax.set_facecolor(C_SURFACE)
        project(ax, V, F, centroid, own.astype(float), cmap, norm,
                fwd, right, up)
        ax.set_title(f"{ttl}\nчужих полігонів на процес: {halo}",
                     fontsize=10.5, color=C_TEXT, pad=8)

    fig.suptitle(f"Розбиття меш-моделі між {nproc} MPI-процесами",
                 fontsize=13.5, color=C_TEXT, y=0.975)
    fig.text(0.5, 0.035,
             "Колір — номер процесу-власника полігона. Ліворуч сусідні за "
             "номером грані розкидані по всій поверхні, тож майже кожен сусід "
             "чужий;\nправоруч кожен процес дістає зв'язну ділянку, і "
             "обмінюватися треба лише вузькою смугою на її межі.",
             ha="center", fontsize=9, color=C_TEXT2)
    fig.subplots_adjust(top=0.86, bottom=0.14, wspace=0.03)
    path = os.path.join(outdir, "decomposition.png")
    fig.savefig(path, facecolor=C_SURFACE)
    plt.close(fig)
    print(f"[OK] {path}")


def fig_history(csv_path, d, outdir):
    import csv as _csv
    rows = list(_csv.DictReader(open(csv_path)))
    t = np.array([float(r["time"]) for r in rows])
    tmax = np.array([float(r["T_max"]) for r in rows])
    E = np.array([float(r["energy"]) for r in rows])
    T_eq = float(d["T_eq"])
    T0 = float(np.min([float(r["T_min"]) for r in rows]))

    fig, (ax, ax2) = plt.subplots(1, 2, figsize=(12.0, 4.5), dpi=160)
    fig.patch.set_facecolor(C_SURFACE)
    for a in (ax, ax2):
        a.set_facecolor(C_SURFACE)
        a.grid(True, color=C_GRID, linewidth=0.8)
        a.set_axisbelow(True)
        for sp in ("top", "right"):
            a.spines[sp].set_visible(False)
        for sp in ("left", "bottom"):
            a.spines[sp].set_color(C_GRID)
        a.tick_params(colors=C_TEXT2, labelsize=9.5)

    ax.plot(t[1:], tmax[1:] - T0, color=CAT[0], linewidth=2.0)
    ax.axhline(T_eq - T0, color="#9a9892", linestyle="--", linewidth=1.5)
    ax.text(0.98, 0.07, "рівноважний перегрів", transform=ax.transAxes,
            fontsize=9, color=C_TEXT2, ha="right", va="bottom")
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("модельний час t, с", fontsize=10.5, color=C_TEXT2)
    ax.set_ylabel("max T − T₀, °C", fontsize=10.5, color=C_TEXT2)
    ax.set_title("Остигання найгарячішого полігона", fontsize=12.5,
                 color=C_TEXT, loc="left", pad=10)

    # --- права панель: радіальні профілі температури ------------------
    centroid, snaps, times = d["centroid"], d["snapshots"], d["snap_times"]
    area = d["area"]
    hot = int(d["hot"])
    u = centroid[hot] / np.linalg.norm(centroid[hot])
    n = centroid / np.linalg.norm(centroid, axis=1, keepdims=True)
    theta = np.degrees(np.arccos(np.clip(n @ u, -1.0, 1.0)))   # кут від джерела

    nb = 64
    edges = np.linspace(0, 180, nb + 1)
    idx = np.clip(np.digitize(theta, edges) - 1, 0, nb - 1)
    wsum = np.bincount(idx, weights=area, minlength=nb)
    centers = 0.5 * (edges[1:] + edges[:-1])

    for j, (T, tt) in enumerate(zip(snaps, times)):
        if j == 0:
            continue                       # t = 0 — дельта-функція, не профіль
        num = np.bincount(idx, weights=area * (T - T0), minlength=nb)
        prof = np.divide(num, wsum, out=np.zeros(nb), where=wsum > 0)
        m = prof > 1e-9
        ax2.plot(centers[m], prof[m], color=CAT[(j - 1) % len(CAT)],
                 linewidth=2.0, label=f"t = {tt:.3g} с")

    ax2.set_yscale("log")
    ax2.set_xlim(0, 180)
    ax2.set_xticks([0, 30, 60, 90, 120, 150, 180])
    ax2.set_xlabel("кут від гарячого полігона θ, °", fontsize=10.5, color=C_TEXT2)
    ax2.set_ylabel("середній перегрів ⟨T⟩ − T₀, °C", fontsize=10.5, color=C_TEXT2)
    ax2.set_title("Як далеко дійшло тепло", fontsize=12.5,
                  color=C_TEXT, loc="left", pad=10)
    leg = ax2.legend(frameon=False, fontsize=8.5, loc="upper right")
    for txt in leg.get_texts():
        txt.set_color(C_TEXT)

    fig.subplots_adjust(top=0.88, bottom=0.14, left=0.07, right=0.97,
                        wspace=0.25)
    path = os.path.join(outdir, "history.png")
    fig.savefig(path, facecolor=C_SURFACE)
    plt.close(fig)
    print(f"[OK] {path}")


# ---------------------------------------------------------------------------
def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--npz", default="results/field.npz")
    ap.add_argument("--mesh", default="data/sphere.stl")
    ap.add_argument("--history", default="results/history.csv")
    ap.add_argument("--outdir", default="results")
    ap.add_argument("--procs", type=int, default=8,
                    help="скільки процесів показати на схемі розбиття")
    args = ap.parse_args()

    d = np.load(args.npz)
    os.makedirs(args.outdir, exist_ok=True)
    fig_frames(d, args.outdir)
    fig_decomposition(args.mesh, args.outdir, args.procs)
    if os.path.exists(args.history):
        fig_history(args.history, d, args.outdir)


if __name__ == "__main__":
    main()
