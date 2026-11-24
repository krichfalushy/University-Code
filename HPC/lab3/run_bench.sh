#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Дослідження масштабованості: моделювання на 1, 2, 3, ... процесах
#  для двох порядків нумерації граней і побудова графіка прискорення.
#
#  Використання:
#      ./run_bench.sh                        # data/sphere.stl, P до nproc
#      ./run_bench.sh data/sphere.stl 16     # примусово до 16 процесів
#      STEPS=8000 TRIALS=5 ./run_bench.sh
#
#  Результати: results/scaling.csv, results/speedup.png, results/table.md
# ---------------------------------------------------------------------------
set -euo pipefail
cd "$(dirname "$0")"

MESH="${1:-data/sphere.stl}"
# Кількість фізичних ядер: nproc є в Linux, на macOS — sysctl
if command -v nproc >/dev/null 2>&1; then
    CORES="$(nproc)"
else
    CORES="$(sysctl -n hw.physicalcpu 2>/dev/null || echo 2)"
fi

# Прив'язка процесів до ядер робить заміри стабільнішими, але macOS її
# не підтримує — там запускаємо без неї
case "$(uname -s)" in
    Darwin) BIND_DEFAULT="" ;;
    *)      BIND_DEFAULT="--bind-to core --map-by core" ;;
esac
MAXP="${2:-$CORES}"
STEPS="${STEPS:-4000}"
TRIALS="${TRIALS:-3}"

if [[ ! -f "$MESH" ]]; then
    echo "[!] Немає файлу меша '$MESH'."
    echo "    Створіть його:  python3 tools/make_mesh.py --subdiv 6 --out $MESH"
    exit 1
fi

mkdir -p results
CSV="results/scaling.csv"
rm -f "$CSV"

FLAGS=""
[[ "$(id -u)" -eq 0 ]] && FLAGS="--allow-run-as-root"

PLIST=()
for p in 1 2 3 4 5 6 7 8 10 12 14 16 20 24 28 32; do
    (( p <= MAXP )) && PLIST+=("$p")
done
[[ " ${PLIST[*]} " == *" $MAXP "* ]] || PLIST+=("$MAXP")

echo "====================================================================="
echo " Меш            : $MESH"
echo " Фізичних ядер  : $CORES"
echo " Значення P     : ${PLIST[*]}"
echo " Кроків/запуск  : $STEPS,  запусків на точку: $TRIALS"
echo "====================================================================="

for order in bfs file; do
  for p in "${PLIST[@]}"; do
    OVER=""; TAG="phys"
    if (( p > CORES )); then OVER="--oversubscribe"; TAG="oversub"; fi
    BIND="$BIND_DEFAULT"
    (( p > CORES )) && BIND=""

    printf "  order=%-5s P=%-3s " "$order" "$p"
    for ((t = 0; t < TRIALS; ++t)); do
        mpirun $FLAGS $OVER $BIND -np "$p" python3 src/heat_mpi.py "$MESH" \
            --steps "$STEPS" --order "$order" --diag-every 0 \
            --csv "$CSV" --tag "$TAG" >/dev/null 2>&1
        printf "."
    done
    printf " ok\n"
  done
done

echo
echo "[i] Сирі результати: $CSV"
python3 tools/plot_speedup.py --csv "$CSV" --cores "$CORES" \
        --out results/speedup.png --table results/table.md --trials "$TRIALS"
