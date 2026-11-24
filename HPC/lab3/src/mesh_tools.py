"""
Лабораторна робота №3 — Високопродуктивні обчислення
MPI. Моделювання процесів на полігональних поверхнях

Модуль роботи з полігональною сіткою (мешем):
  * генерація сфери (ікосфера) та збереження у STL;
  * читання STL через numpy-stl і «зварювання» вершин;
  * побудова відношення сусідства між полігонами (спільне ребро);
  * геометрія, потрібна для схеми теплопровідності (площі, центроїди,
    довжини спільних ребер, відстані між центрами);
  * перевпорядкування полігонів обходом у ширину — щоб неперервні блоки
    полігонів були компактними ділянками поверхні, а не розкиданими
    трикутниками (це прямо впливає на обсяг обміну між MPI-процесами).
"""
from __future__ import annotations

import numpy as np


# ---------------------------------------------------------------------------
#  1. Генерація сфери: ікосаедр + рекурсивне поділення граней
# ---------------------------------------------------------------------------
def icosphere(subdivisions: int = 5):
    """Повертає (V, F): вершини (nv,3) на одиничній сфері та трикутники (nf,3).

    Кількість граней: 20 * 4**subdivisions.
    Трикутники ікосфери майже однакові за площею — зручно для моделювання.
    """
    phi = (1.0 + np.sqrt(5.0)) / 2.0
    V = np.array([
        [-1,  phi, 0], [1,  phi, 0], [-1, -phi, 0], [1, -phi, 0],
        [0, -1,  phi], [0, 1,  phi], [0, -1, -phi], [0, 1, -phi],
        [phi, 0, -1], [phi, 0, 1], [-phi, 0, -1], [-phi, 0, 1],
    ], dtype=np.float64)
    V /= np.linalg.norm(V, axis=1, keepdims=True)

    F = np.array([
        [0, 11, 5], [0, 5, 1], [0, 1, 7], [0, 7, 10], [0, 10, 11],
        [1, 5, 9], [5, 11, 4], [11, 10, 2], [10, 7, 6], [7, 1, 8],
        [3, 9, 4], [3, 4, 2], [3, 2, 6], [3, 6, 8], [3, 8, 9],
        [4, 9, 5], [2, 4, 11], [6, 2, 10], [8, 6, 7], [9, 8, 1],
    ], dtype=np.int64)

    for _ in range(subdivisions):
        # усі ребра кожного трикутника, впорядковані (менший, більший)
        e = np.concatenate([F[:, [0, 1]], F[:, [1, 2]], F[:, [2, 0]]], axis=0)
        e = np.sort(e, axis=1)
        uniq, inv = np.unique(e, axis=0, return_inverse=True)
        inv = np.asarray(inv).reshape(-1)      # сумісність із numpy 1.x/2.x

        mid = V[uniq[:, 0]] + V[uniq[:, 1]]
        mid /= np.linalg.norm(mid, axis=1, keepdims=True)   # назад на сферу

        nf = F.shape[0]
        m01 = len(V) + inv[0:nf]
        m12 = len(V) + inv[nf:2 * nf]
        m20 = len(V) + inv[2 * nf:3 * nf]

        V = np.concatenate([V, mid], axis=0)
        F = np.concatenate([
            np.stack([F[:, 0], m01, m20], axis=1),
            np.stack([F[:, 1], m12, m01], axis=1),
            np.stack([F[:, 2], m20, m12], axis=1),
            np.stack([m01, m12, m20], axis=1),
        ], axis=0)

    return V, F


# ---------------------------------------------------------------------------
#  2. STL: запис і читання через numpy-stl
# ---------------------------------------------------------------------------
def save_stl(path: str, V: np.ndarray, F: np.ndarray) -> None:
    from stl import mesh as stlmesh
    m = stlmesh.Mesh(np.zeros(F.shape[0], dtype=stlmesh.Mesh.dtype))
    m.vectors = V[F]
    m.save(path)


def load_stl(path: str, decimals: int = 6):
    """Читає STL і «зварює» вершини.

    У форматі STL кожен трикутник записаний трьома власними вершинами, і
    спільні вершини сусідніх трикутників дублюються. Щоб відновити топологію
    (а без неї немає сусідства), однакові координати зводяться до одного
    індексу: координати округлюються і беруться унікальні рядки.
    """
    from stl import mesh as stlmesh
    m = stlmesh.Mesh.from_file(path)
    tri = np.asarray(m.vectors, dtype=np.float64)           # (nf, 3, 3)
    pts = tri.reshape(-1, 3)

    key = np.round(pts, decimals)
    V, inv = np.unique(key, axis=0, return_inverse=True)
    inv = np.asarray(inv).reshape(-1)          # сумісність із numpy 1.x/2.x
    F = inv.reshape(-1, 3).astype(np.int64)

    # середні координати для кожної зварених вершини (точніше за округлені)
    Vexact = np.zeros_like(V)
    cnt = np.zeros(len(V))
    np.add.at(Vexact, inv, pts)
    np.add.at(cnt, inv, 1.0)
    Vexact /= cnt[:, None]
    return Vexact, F


# ---------------------------------------------------------------------------
#  3. Сусідство полігонів за спільним ребром
# ---------------------------------------------------------------------------
def build_adjacency(F: np.ndarray, nv: int):
    """Повертає nbr (nf,3) int64: номер сусіда через кожне ребро, або -1.

    Ребро k трикутника i з'єднує вершини F[i,k] та F[i,(k+1)%3].
    Два трикутники — сусіди, якщо мають спільне ребро (дві спільні вершини).
    """
    nf = F.shape[0]
    a = np.concatenate([F[:, 0], F[:, 1], F[:, 2]])
    b = np.concatenate([F[:, 1], F[:, 2], F[:, 0]])
    lo = np.minimum(a, b)
    hi = np.maximum(a, b)
    key = lo.astype(np.int64) * np.int64(nv) + hi        # унікальний код ребра

    face = np.tile(np.arange(nf, dtype=np.int64), 3)     # кому належить ребро
    slot = np.repeat(np.arange(3, dtype=np.int64), nf)   # номер ребра в трикутнику

    order = np.argsort(key, kind="stable")
    ks = key[order]

    start = np.flatnonzero(np.concatenate(([True], ks[1:] != ks[:-1])))
    counts = np.diff(np.append(start, ks.size))

    nbr = np.full((nf, 3), -1, dtype=np.int64)
    pair = start[counts == 2]
    i1, i2 = order[pair], order[pair + 1]
    nbr[face[i1], slot[i1]] = face[i2]
    nbr[face[i2], slot[i2]] = face[i1]

    n_boundary = int((counts == 1).sum())
    n_nonmanifold = int((counts > 2).sum())
    return nbr, n_boundary, n_nonmanifold


# ---------------------------------------------------------------------------
#  4. Геометрія для схеми теплопровідності
# ---------------------------------------------------------------------------
def geometry(V: np.ndarray, F: np.ndarray, nbr: np.ndarray):
    """Площі, центроїди та ваги провідності w = L / d для кожного ребра.

    L — довжина спільного ребра, d — відстань між центрами сусідніх
    трикутників. Для ребра без сусіда вага 0 (теплоізольована межа).
    """
    tri = V[F]                                     # (nf, 3, 3)
    centroid = tri.mean(axis=1)
    cross = np.cross(tri[:, 1] - tri[:, 0], tri[:, 2] - tri[:, 0])
    area = 0.5 * np.linalg.norm(cross, axis=1)

    # довжина кожного з трьох ребер трикутника
    L = np.stack([
        np.linalg.norm(tri[:, 1] - tri[:, 0], axis=1),
        np.linalg.norm(tri[:, 2] - tri[:, 1], axis=1),
        np.linalg.norm(tri[:, 0] - tri[:, 2], axis=1),
    ], axis=1)                                     # (nf, 3)

    has = nbr >= 0
    safe = np.where(has, nbr, 0)
    d = np.linalg.norm(centroid[:, None, :] - centroid[safe], axis=2)
    w = np.where(has & (d > 0), L / np.where(d > 0, d, 1.0), 0.0)
    return area, centroid, w


# ---------------------------------------------------------------------------
#  5. Перевпорядкування полігонів обходом у ширину (BFS)
# ---------------------------------------------------------------------------
def bfs_order(nbr: np.ndarray, seed: int = 0) -> np.ndarray:
    """Порядок обходу дуального графа в ширину, починаючи з грані seed.

    Неперервні блоки такого порядку — зв'язні компактні ділянки поверхні,
    тому в кожного MPI-процесу менше «чужих» сусідів на межі.
    """
    nf = nbr.shape[0]
    order = np.empty(nf, dtype=np.int64)
    seen = np.zeros(nf, dtype=bool)
    queue = np.empty(nf, dtype=np.int64)

    head = tail = 0
    pos = 0
    for start in range(nf):                 # цикл на випадок незв'язної сітки
        if seen[start]:
            continue
        seen[start] = True
        queue[tail] = start
        tail += 1
        while head < tail:
            f = queue[head]
            head += 1
            order[pos] = f
            pos += 1
            for k in range(3):
                g = nbr[f, k]
                if g >= 0 and not seen[g]:
                    seen[g] = True
                    queue[tail] = g
                    tail += 1
    return order


def permute_faces(F: np.ndarray, nbr: np.ndarray, order: np.ndarray):
    """Застосовує перестановку граней до F і перенумеровує сусідів."""
    inv = np.empty_like(order)
    inv[order] = np.arange(order.size, dtype=np.int64)
    F2 = F[order]
    nbr2 = nbr[order]
    nbr2 = np.where(nbr2 >= 0, inv[np.where(nbr2 >= 0, nbr2, 0)], -1)
    return F2, nbr2


# ---------------------------------------------------------------------------
#  6. Розбиття граней між процесами + оцінка обсягу обміну
# ---------------------------------------------------------------------------
def block_bounds(nf: int, nproc: int) -> np.ndarray:
    """Межі неперервних блоків граней: процес p отримує [b[p], b[p+1])."""
    return (np.arange(nproc + 1, dtype=np.int64) * nf) // nproc


def halo_sizes(nbr: np.ndarray, bounds: np.ndarray) -> np.ndarray:
    """Скільки чужих граней потрібно кожному процесу (розмір «гало»)."""
    out = []
    for p in range(bounds.size - 1):
        s, e = bounds[p], bounds[p + 1]
        nb = nbr[s:e]
        foreign = nb[(nb >= 0) & ((nb < s) | (nb >= e))]
        out.append(np.unique(foreign).size)
    return np.array(out, dtype=np.int64)
