# Лабораторна робота №4 — OpenMP
#
#   make          — зібрати
#   make run      — швидка перевірка
#   make clean
#
# macOS: Apple clang не має OpenMP «з коробки», потрібен libomp:
#            brew install libomp
#        Makefile сам знайде його і підставить потрібні ключі.

BIN     := forecast_omp
SRC     := src/forecast_omp.c
CFLAGS  := -O3 -std=c99 -Wall -Wextra -funroll-loops
LDLIBS  := -lm

UNAME := $(shell uname -s)

ifeq ($(UNAME),Darwin)
  LIBOMP := $(shell brew --prefix libomp 2>/dev/null)
  CC     := clang
  ifneq ($(LIBOMP),)
    OMPC := -Xpreprocessor -fopenmp -I$(LIBOMP)/include
    OMPL := -L$(LIBOMP)/lib -lomp
  endif
else
  CC   := gcc
  OMPC := -fopenmp
  OMPL := -fopenmp
endif

HAS_NATIVE := $(shell $(CC) -march=native -E -x c /dev/null >/dev/null 2>&1 && echo yes)
ifeq ($(HAS_NATIVE),yes)
CFLAGS += -march=native
endif

all: check-omp $(BIN)

check-omp:
ifeq ($(UNAME),Darwin)
ifeq ($(LIBOMP),)
	@echo "[!] Не знайдено libomp — без нього OpenMP на macOS не працює."
	@echo "    Виконайте:  brew install libomp"
	@exit 1
endif
endif

$(BIN): $(SRC)
	$(CC) $(CFLAGS) $(OMPC) -o $@ $< $(OMPL) $(LDLIBS)

run: $(BIN)
	./$(BIN) data/vix-daily.csv --wmax 60 --threads 2

clean:
	rm -f $(BIN)

.PHONY: all clean run check-omp
