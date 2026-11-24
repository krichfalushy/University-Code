#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Порівняння швидкодії на 1..N потоках і графік прискорення.
#
#  Використання:
#      ./run_bench.sh                        # data/vix-daily.csv
#      ./run_bench.sh data/brent-daily.csv
#      ./run_bench.sh data/spx-daily.csv 8   # примусово до 8 потоків
#      REPEAT=40 TRIALS=5 ./run_bench.sh
#
#  Результати: results/scaling.csv, results/speedup.png, results/table.md
# ---------------------------------------------------------------------------
set -euo pipefail
cd "$(dirname "$0")"

DATA="${1:-data/vix-daily.csv}"

# Кількість ядер: nproc є в Linux, на macOS — sysctl
if command -v nproc >/dev/null 2>&1; then
    CORES="$(nproc)"
else
    CORES="$(sysctl -n hw.physicalcpu 2>/dev/null || echo 2)"
fi

MAXT="${2:-$CORES}"
WMAX="${WMAX:-252}"
REPEAT="${REPEAT:-20}"
TRIALS="${TRIALS:-3}"

if [[ ! -f "$DATA" ]]; then
    echo "[!] Немає файлу котирувань '$DATA'."
    echo "    У комплекті є data/vix-daily.csv і data/brent-daily.csv,"
    echo "    інші можна завантажити:  ./get_data.sh ^spx"
    exit 1
fi

[[ -x ./forecast_omp ]] || make

mkdir -p results
CSV="results/scaling.csv"
rm -f "$CSV"

TLIST=()
for t in 1 2 3 4 5 6 7 8 10 12 14 16 20 24 32; do
    (( t <= MAXT )) && TLIST+=("$t")
done
[[ " ${TLIST[*]} " == *" $MAXT "* ]] || TLIST+=("$MAXT")

echo "====================================================================="
echo " Котирування    : $DATA"
echo " Ядер           : $CORES"
echo " Потоків        : ${TLIST[*]}"
echo " Вікна          : 2..$WMAX,  повторів у заміри: $REPEAT,  запусків: $TRIALS"
echo "====================================================================="

for mode in days windows; do
  for t in "${TLIST[@]}"; do
    TAG="phys"
    (( t > CORES )) && TAG="oversub"
    printf "  parallel=%-8s потоків=%-3s " "$mode" "$t"
    for ((k = 0; k < TRIALS; ++k)); do
        ./forecast_omp "$DATA" --threads "$t" --parallel "$mode" \
            --wmax "$WMAX" --repeat "$REPEAT" \
            --csv "$CSV" --tag "$TAG" >/dev/null
        printf "."
    done
    printf " ok\n"
  done
done

echo
echo "[i] Сирі результати: $CSV"
python3 tools/plot_results.py --scaling "$CSV" --cores "$CORES" \
        --speedup results/speedup.png --table results/table.md --trials "$TRIALS"
