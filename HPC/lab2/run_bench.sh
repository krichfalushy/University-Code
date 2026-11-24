#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Дослідження масштабованості: запуск програми на 1, 2, 3, ... процесах
#  для обох схем обміну і побудова графіка прискорення.
#
#  Використання:
#      ./run_bench.sh                      # data/matrix.mtx, P до nproc
#      ./run_bench.sh data/pwtk.mtx        # інша матриця
#      ./run_bench.sh data/pwtk.mtx 16     # примусово до 16 процесів
#      REPS=500 TRIALS=5 ./run_bench.sh    # точніші заміри
#
#  Результати: results/scaling.csv, results/speedup.png
# ---------------------------------------------------------------------------
set -euo pipefail
cd "$(dirname "$0")"

MATRIX="${1:-data/wiki-Vote.txt}"
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
REPS="${REPS:-200}"
TRIALS="${TRIALS:-3}"      # кожне P запускаємо кілька разів, беремо найкращий

if [[ ! -f "$MATRIX" ]]; then
    echo "[!] Немає файлу матриці '$MATRIX'."
    echo "    У комплекті є data/wiki-Vote.txt; іншу матрицю з бази SuiteSparse"
    echo "    можна взяти командою:  ./get_matrix.sh Boeing pwtk"
    exit 1
fi

[[ -x ./spmv_mpi ]] || make

mkdir -p results
CSV="results/scaling.csv"
rm -f "$CSV"

# Open MPI відмовляється працювати від root без явного дозволу
FLAGS=""
[[ "$(id -u)" -eq 0 ]] && FLAGS="--allow-run-as-root"

PLIST=()
for p in 1 2 3 4 5 6 7 8 10 12 14 16 20 24 28 32 40 48 64 96 128; do
    (( p <= MAXP )) && PLIST+=("$p")
done
[[ " ${PLIST[*]} " == *" $MAXP "* ]] || PLIST+=("$MAXP")

echo "====================================================================="
echo " Матриця        : $MATRIX"
echo " Фізичних ядер  : $CORES"
echo " Значення P     : ${PLIST[*]}"
echo " Повторень/запуск: $REPS,  запусків на точку: $TRIALS"
echo "====================================================================="

for comm in sparse allgather; do
  for p in "${PLIST[@]}"; do
    OVER=""; TAG="phys"
    if (( p > CORES )); then OVER="--oversubscribe"; TAG="oversub"; fi

    BIND="$BIND_DEFAULT"
    (( p > CORES )) && BIND=""        # закріплення неможливе при надпідписці

    printf "  comm=%-9s P=%-3s " "$comm" "$p"
    for ((t = 0; t < TRIALS; ++t)); do
        mpirun $FLAGS $OVER $BIND -np "$p" ./spmv_mpi "$MATRIX" \
            --reps "$REPS" --check --comm "$comm" \
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
