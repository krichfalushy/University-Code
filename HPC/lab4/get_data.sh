#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Завантаження біржових котирувань зі Stooq (https://stooq.com).
#
#  Використання:
#      ./get_data.sh                  # індекс S&P 500 (^spx), з 1789 року
#      ./get_data.sh aapl.us          # акції Apple
#      ./get_data.sh ^dji dji.csv     # Dow Jones під власним ім'ям
#
#  Формат на виході: Date,Open,High,Low,Close,Volume — програма розпізнає
#  колонки за назвою, тому нічого правити не треба.
#
#  У комплекті вже є дві готові серії на випадок, якщо Stooq недоступний:
#      data/vix-daily.csv    індекс волатильності CBOE VIX, з 1990 (36 років)
#      data/brent-daily.csv  нафта Brent, з 1987 (39 років)
# ---------------------------------------------------------------------------
set -euo pipefail
cd "$(dirname "$0")"

TICKER="${1:-^spx}"
OUT="${2:-data/$(echo "$TICKER" | tr -d '^' | tr '.' '-')-daily.csv}"

mkdir -p data

# ^ у URL треба закодувати
ENC="${TICKER//^/%5E}"
URL="https://stooq.com/q/d/l/?s=${ENC}&i=d"

echo "[i] Завантажую $TICKER зі Stooq ..."
curl -fsSL --max-time 120 -o "$OUT.tmp" "$URL"

# Stooq на помилку віддає коротку сторінку замість CSV
if [[ "$(wc -l < "$OUT.tmp")" -lt 100 ]]; then
    echo "[!] Схоже, Stooq не віддав дані для '$TICKER'. Отримано:"
    head -3 "$OUT.tmp"
    rm -f "$OUT.tmp"
    echo "    Перевірте тікер на https://stooq.com або скористайтеся"
    echo "    готовими серіями з теки data/."
    exit 1
fi

mv "$OUT.tmp" "$OUT"
echo "[OK] $OUT"
head -2 "$OUT"
tail -1 "$OUT"
echo "рядків: $(wc -l < "$OUT")"
