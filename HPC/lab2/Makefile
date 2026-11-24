# Лабораторна робота №2 — MPI SpMV
#
#   make            — зібрати
#   make run        — швидка перевірка на тестовій матриці
#   make clean

CC      := mpicc
CFLAGS  := -O3 -std=c99 -Wall -Wextra -funroll-loops
LDFLAGS := -lm

# -march=native вмикаємо, лише якщо компілятор його підтримує
HAS_NATIVE := $(shell $(CC) -march=native -E -x c /dev/null >/dev/null 2>&1 && echo yes)
ifeq ($(HAS_NATIVE),yes)
CFLAGS += -march=native
endif

BIN := spmv_mpi

all: $(BIN)

$(BIN): src/spmv_mpi.c
	$(CC) $(CFLAGS) -o $@ $< $(LDFLAGS)

run: $(BIN)
	mpirun -np 2 ./$(BIN) data/matrix.mtx --reps 20 --check

clean:
	rm -f $(BIN)

.PHONY: all run clean
