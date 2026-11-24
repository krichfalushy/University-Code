/* ===========================================================================
 *  Лабораторна робота №2 — "Високопродуктивні обчислення"
 *
 *  MPI. Паралельне множення розрідженої матриці на вектор:  y = A * x
 *
 *  Алгоритм за статтею: Guy E. Blelloch, "Programming Parallel Algorithms",
 *  Communications of the ACM 39(3), 1996, розд. 3.2 "Sparse Matrix
 *  Multiplication"  (https://www.cs.cmu.edu/~scandal/cacm/node9.html).
 *
 *  У статті розріджена матриця — це послідовність рядків, а кожен рядок —
 *  послідовність пар (номер_стовпця, значення) для ненульових елементів.
 *  Множення на щільний вектор записується одним виразом мови NESL:
 *
 *      {sum({v * x[i] : (i,v) in row}) : row in A}
 *
 *  тобто два рівні вкладеного паралелізму:
 *    (1) зовнішній — по рядках матриці;
 *    (2) внутрішній — скалярний добуток рядка з вектором (редукція sum).
 *  Робота W = O(nnz), глибина D = O(log(max довжини рядка)).
 *
 *  ВІДОБРАЖЕННЯ НА MPI
 *  -------------------
 *  * Послідовність рядків зберігається у форматі CSR (row_ptr/col_idx/val) —
 *    це прямий C-еквівалент вкладеної послідовності пар (i,v).
 *  * Зовнішній паралелізм розподіляється між процесами: кожен процес
 *    отримує неперервний блок рядків, збалансований ЗА КІЛЬКІСТЮ НЕНУЛЬОВИХ
 *    ЕЛЕМЕНТІВ (а не за кількістю рядків) — у реальних розріджених матрицях
 *    довжини рядків відрізняються в десятки разів.
 *  * Внутрішня редукція sum виконується локально всередині процесу.
 *  * Вектор x розподілений блоками: процес володіє блоком x, що відповідає
 *    його блоку рядків. Перед кожним множенням потрібні "чужі" компоненти
 *    x треба отримати. Реалізовано дві схеми обміну:
 *
 *      --comm sparse     (за замовчуванням) кожен процес отримує ЛИШЕ ті
 *                        компоненти x, які реально зустрічаються в його
 *                        стовпцях: один MPI_Alltoallv за заздалегідь
 *                        побудованим планом. Обсяг обміну ~ O(поверхні
 *                        підобласті), тому схема масштабується.
 *
 *      --comm allgather  наївна схема: MPI_Allgatherv збирає весь вектор x
 *                        на кожному процесі. Обсяг обміну O(N) на процес
 *                        незалежно від P — саме це й обмежує прискорення.
 *
 *  Формат вхідних даних: Matrix Market (.mtx) — рідний формат бази
 *  SuiteSparse Matrix Collection (http://sparse.tamu.edu/).
 *
 *  Збірка:  make            (mpicc -O3 -march=native)
 *  Запуск:  mpirun -np 4 ./spmv_mpi data/pwtk.mtx --reps 200 --check
 * ===========================================================================
 */

#if !defined(__APPLE__)                 /* на macOS цей макрос ховає getline */
#define _POSIX_C_SOURCE 200809L
#endif

#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <math.h>
#include <limits.h>
#include <ctype.h>

static int g_rank = 0, g_size = 1;

static void die(const char *fmt, ...)
{
    va_list ap;
    fprintf(stderr, "[ранг %d] ПОМИЛКА: ", g_rank);
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    fprintf(stderr, "\n");
    fflush(stderr);
    MPI_Abort(MPI_COMM_WORLD, 1);
    exit(EXIT_FAILURE);
}

static void *xmalloc(size_t n)
{
    void *p = malloc(n ? n : 1);
    if (!p) die("не вдалося виділити %zu байт пам'яті", n);
    return p;
}

/* ===========================================================================
 *  1. Розріджена матриця у форматі CSR
 *
 *     rp[i]..rp[i+1]-1 — діапазон ненульових елементів рядка i;
 *     ci[k]            — номер стовпця;  val[k] — значення.
 *     Пара (ci[k], val[k]) — це пара (i,v) зі статті Blelloch.
 * ========================================================================= */
typedef struct {
    int     nrows, ncols;
    long    nnz;
    long   *rp;
    int    *ci;
    double *val;
} CSR;

static void csr_free(CSR *A)
{
    free(A->rp); free(A->ci); free(A->val);
    A->rp = NULL; A->ci = NULL; A->val = NULL;
}

/* ===========================================================================
 *  2. Читання Matrix Market (.mtx)
 *
 *     object=matrix, format=coordinate,
 *     field = real | integer | pattern,
 *     symmetry = general | symmetric | skew-symmetric.
 *     Для (косо)симетричних у файлі є лише нижній трикутник — позадіагональні
 *     елементи дзеркально добудовуються.
 * ========================================================================= */
static void lowercase(char *s) { for (; *s; ++s) *s = (char)tolower((unsigned char)*s); }

static void read_matrix_market(const char *path, CSR *A, int verbose)
{
    FILE *f = fopen(path, "r");
    if (!f) die("не можу відкрити файл матриці '%s'", path);

    char *line = NULL; size_t cap = 0; ssize_t len;

    if ((len = getline(&line, &cap, f)) <= 0) die("порожній файл '%s'", path);

    char banner[64]="", object[64]="", format[64]="", field[64]="", symm[64]="";
    if (sscanf(line, "%63s %63s %63s %63s %63s",
               banner, object, format, field, symm) != 5)
        die("некоректний заголовок Matrix Market у '%s'", path);
    lowercase(banner); lowercase(object); lowercase(format);
    lowercase(field);  lowercase(symm);

    if (strcmp(banner, "%%matrixmarket")) die("це не файл Matrix Market");
    if (strcmp(object, "matrix"))  die("підтримується лише object=matrix ('%s')", object);
    if (strcmp(format, "coordinate"))
        die("підтримується лише format=coordinate, отримано '%s' "
            "(щільний формат 'array' для цієї задачі не має сенсу)", format);

    const int is_pattern = !strcmp(field, "pattern");
    if (!is_pattern && strcmp(field, "real") && strcmp(field, "integer"))
        die("підтримуються field=real|integer|pattern, отримано '%s'", field);

    const int sym_gen  = !strcmp(symm, "general");
    const int sym_sym  = !strcmp(symm, "symmetric");
    const int sym_skew = !strcmp(symm, "skew-symmetric");
    if (!sym_gen && !sym_sym && !sym_skew)
        die("підтримуються symmetry=general|symmetric|skew-symmetric ('%s')", symm);

    do {
        if ((len = getline(&line, &cap, f)) <= 0)
            die("несподіваний кінець файлу в коментарях");
    } while (line[0] == '%');

    long M = 0, N = 0, NZ = 0;
    if (sscanf(line, "%ld %ld %ld", &M, &N, &NZ) != 3)
        die("не можу прочитати рядок розмірів 'M N NNZ'");
    if (M <= 0 || N <= 0 || NZ < 0)      die("некоректні розміри матриці");
    if (M > INT_MAX || N > INT_MAX)      die("матриця завелика (M або N > INT_MAX)");

    if (verbose)
        printf("  файл            : %s\n"
               "  тип             : coordinate, %s, %s\n"
               "  розмір          : %ld x %ld, записів у файлі: %ld\n",
               path, field, symm, M, N, NZ);

    const long capacity = sym_gen ? NZ : 2 * NZ;
    int    *I = (int *)   xmalloc((size_t)capacity * sizeof(int));
    int    *J = (int *)   xmalloc((size_t)capacity * sizeof(int));
    double *V = (double *)xmalloc((size_t)capacity * sizeof(double));

    long nnz = 0;
    for (long e = 0; e < NZ; ++e) {
        if ((len = getline(&line, &cap, f)) <= 0)
            die("несподіваний кінець файлу: %ld із %ld записів", e, NZ);
        char *p = line;
        long  i = strtol(p, &p, 10);
        long  j = strtol(p, &p, 10);
        double v = is_pattern ? 1.0 : strtod(p, &p);
        if (i < 1 || i > M || j < 1 || j > N)
            die("індекс поза межами у записі %ld: (%ld,%ld)", e + 1, i, j);
        --i; --j;
        I[nnz] = (int)i; J[nnz] = (int)j; V[nnz] = v; ++nnz;
        if (!sym_gen && i != j) {                 /* дзеркальний елемент */
            I[nnz] = (int)j; J[nnz] = (int)i;
            V[nnz] = sym_skew ? -v : v;
            ++nnz;
        }
    }
    free(line);
    fclose(f);

    if (nnz > INT_MAX)
        die("nnz = %ld перевищує INT_MAX — потрібні 64-бітні лічильники MPI", nnz);

    /* COO -> CSR двома стійкими сортуваннями підрахунком: спершу за
       стовпцем, потім за рядком. Результат: у кожному рядку стовпці
       впорядковані за зростанням (краща локальність доступу до x). */
    int    *I2 = (int *)   xmalloc((size_t)nnz * sizeof(int));
    int    *J2 = (int *)   xmalloc((size_t)nnz * sizeof(int));
    double *V2 = (double *)xmalloc((size_t)nnz * sizeof(double));
    long   *cnt = (long *)xmalloc(((size_t)(N > M ? N : M) + 1) * sizeof(long));

    memset(cnt, 0, ((size_t)N + 1) * sizeof(long));
    for (long k = 0; k < nnz; ++k) cnt[J[k]]++;
    for (long c = 0, s = 0; c < N; ++c) { long t = cnt[c]; cnt[c] = s; s += t; }
    for (long k = 0; k < nnz; ++k) {
        long d = cnt[J[k]]++;
        I2[d] = I[k]; J2[d] = J[k]; V2[d] = V[k];
    }

    long *rp = (long *)xmalloc(((size_t)M + 1) * sizeof(long));
    memset(cnt, 0, ((size_t)M + 1) * sizeof(long));
    for (long k = 0; k < nnz; ++k) cnt[I2[k]]++;
    rp[0] = 0;
    for (long r = 0; r < M; ++r) rp[r + 1] = rp[r] + cnt[r];
    for (long r = 0; r < M; ++r) cnt[r] = rp[r];
    for (long k = 0; k < nnz; ++k) {
        long d = cnt[I2[k]]++;
        J[d] = J2[k]; V[d] = V2[k];
    }

    free(I); free(I2); free(J2); free(V2); free(cnt);

    A->nrows = (int)M; A->ncols = (int)N; A->nnz = nnz;
    A->rp = rp; A->ci = J; A->val = V;

    if (verbose) {
        long maxlen = 0, minlen = LONG_MAX;
        for (long r = 0; r < M; ++r) {
            long l = rp[r + 1] - rp[r];
            if (l > maxlen) maxlen = l;
            if (l < minlen) minlen = l;
        }
        printf("  nnz (повний)    : %ld\n"
               "  довжина рядка   : min %ld, max %ld, середня %.1f\n",
               nnz, minlen, maxlen, (double)nnz / (double)M);
    }
}

/* ===========================================================================
 *  2b. Читання списку ребер у форматі SNAP (http://snap.stanford.edu/data/)
 *
 *      Рядки виду "FromNodeId <TAB> ToNodeId", коментарі починаються з '#'.
 *      Номери вузлів у файлі розріджені й непослідовні, тому вони
 *      перенумеровуються у 0..n-1 (n — кількість різних вузлів).
 *      Матриця суміжності графа: a[i][j] = 1, якщо є ребро i -> j.
 * ========================================================================= */
static int cmp_int_asc(const void *a, const void *b)
{
    const int x = *(const int *)a, y = *(const int *)b;
    return (x > y) - (x < y);
}

/* Номер вузла v у впорядкованому масиві ids[0..n) -> його новий індекс. */
static int remap_id(const int *ids, long n, int v)
{
    long lo = 0, hi = n - 1;
    while (lo <= hi) {
        const long mid = (lo + hi) / 2;
        if      (ids[mid] < v) lo = mid + 1;
        else if (ids[mid] > v) hi = mid - 1;
        else return (int)mid;
    }
    die("внутрішня помилка: вузол %d не знайдено в таблиці перенумерації", v);
    return -1;
}

static void read_edge_list(const char *path, CSR *A, int verbose,
                           int weighted, unsigned long long seed)
{
    FILE *f = fopen(path, "r");
    if (!f) die("не можу відкрити файл '%s'", path);

    char *line = NULL; size_t cap = 0; ssize_t len;
    long  n_alloc = 1024, m = 0;
    int  *src = (int *)xmalloc((size_t)n_alloc * sizeof(int));
    int  *dst = (int *)xmalloc((size_t)n_alloc * sizeof(int));

    while ((len = getline(&line, &cap, f)) > 0) {
        char *p = line;
        while (*p == ' ' || *p == '\t') ++p;
        if (*p == '#' || *p == '%' || *p == '\n' || *p == '\r' || *p == '\0')
            continue;                                  /* коментар / порожній */
        char *q = p;
        const long a = strtol(p, &q, 10);
        if (q == p) continue;
        p = q;
        const long b = strtol(p, &q, 10);
        if (q == p) die("рядок без другого номера вузла: %s", line);
        if (a < 0 || b < 0) die("від'ємний номер вузла у файлі");
        if (m == n_alloc) {
            n_alloc *= 2;
            src = (int *)realloc(src, (size_t)n_alloc * sizeof(int));
            dst = (int *)realloc(dst, (size_t)n_alloc * sizeof(int));
            if (!src || !dst) die("не вистачає пам'яті на список ребер");
        }
        src[m] = (int)a; dst[m] = (int)b; ++m;
    }
    free(line);
    fclose(f);
    if (m == 0) die("у файлі '%s' не знайдено жодного ребра", path);

    /* --- перенумерація вузлів у 0..n-1 ------------------------------- */
    int *ids = (int *)xmalloc((size_t)(2 * m) * sizeof(int));
    memcpy(ids,     src, (size_t)m * sizeof(int));
    memcpy(ids + m, dst, (size_t)m * sizeof(int));
    qsort(ids, (size_t)(2 * m), sizeof(int), cmp_int_asc);
    long n = 0;
    for (long k = 0; k < 2 * m; ++k)
        if (k == 0 || ids[k] != ids[k - 1]) ids[n++] = ids[k];

    int    *I = (int *)   xmalloc((size_t)m * sizeof(int));
    int    *J = (int *)   xmalloc((size_t)m * sizeof(int));
    double *V = (double *)xmalloc((size_t)m * sizeof(double));

    unsigned long long s = seed ? seed : 12345ULL;
    for (long k = 0; k < m; ++k) {
        I[k] = remap_id(ids, n, src[k]);
        J[k] = remap_id(ids, n, dst[k]);
        if (weighted) {                       /* псевдовипадкові ваги ребер */
            s ^= s >> 12; s ^= s << 25; s ^= s >> 27;
            V[k] = 2.0 * ((double)((s * 2685821657736338717ULL) >> 11)
                          / 9007199254740992.0) - 1.0;
        } else {
            V[k] = 1.0;                       /* матриця суміжності */
        }
    }
    free(src); free(dst); free(ids);

    if (verbose)
        printf("  файл            : %s\n"
               "  тип             : список ребер SNAP, орієнтований граф\n"
               "  вузлів          : %ld (перенумеровано у 0..%ld)\n"
               "  ребер (nnz)     : %ld, ваги: %s\n",
               path, n, n - 1, m, weighted ? "випадкові" : "одиничні");

    /* --- COO -> CSR (двічі сортування підрахунком) -------------------- */
    int    *I2 = (int *)   xmalloc((size_t)m * sizeof(int));
    int    *J2 = (int *)   xmalloc((size_t)m * sizeof(int));
    double *V2 = (double *)xmalloc((size_t)m * sizeof(double));
    long   *cnt = (long *)xmalloc(((size_t)n + 1) * sizeof(long));

    memset(cnt, 0, ((size_t)n + 1) * sizeof(long));
    for (long k = 0; k < m; ++k) cnt[J[k]]++;
    for (long c = 0, t = 0; c < n; ++c) { long u = cnt[c]; cnt[c] = t; t += u; }
    for (long k = 0; k < m; ++k) {
        long d = cnt[J[k]]++;
        I2[d] = I[k]; J2[d] = J[k]; V2[d] = V[k];
    }

    long *rp = (long *)xmalloc(((size_t)n + 1) * sizeof(long));
    memset(cnt, 0, ((size_t)n + 1) * sizeof(long));
    for (long k = 0; k < m; ++k) cnt[I2[k]]++;
    rp[0] = 0;
    for (long r = 0; r < n; ++r) rp[r + 1] = rp[r] + cnt[r];
    for (long r = 0; r < n; ++r) cnt[r] = rp[r];
    for (long k = 0; k < m; ++k) {
        long d = cnt[I2[k]]++;
        J[d] = J2[k]; V[d] = V2[k];
    }
    free(I); free(I2); free(J2); free(V2); free(cnt);

    A->nrows = (int)n; A->ncols = (int)n; A->nnz = m;
    A->rp = rp; A->ci = J; A->val = V;

    if (verbose) {
        long maxlen = 0, minlen = LONG_MAX, empty = 0;
        for (long r = 0; r < n; ++r) {
            const long l = rp[r + 1] - rp[r];
            if (l > maxlen) maxlen = l;
            if (l < minlen) minlen = l;
            if (l == 0) ++empty;
        }
        printf("  довжина рядка   : min %ld, max %ld, середня %.1f "
               "(порожніх рядків: %ld)\n",
               minlen, maxlen, (double)m / (double)n, empty);
    }
}

/* Диспетчер: розпізнає формат за першим рядком файлу. */
static void read_matrix(const char *path, CSR *A, int verbose,
                        int weighted, unsigned long long seed)
{
    FILE *f = fopen(path, "r");
    if (!f) die("не можу відкрити файл матриці '%s'", path);
    char head[32] = "";
    if (!fgets(head, sizeof head, f)) die("порожній файл '%s'", path);
    fclose(f);

    if (strncmp(head, "%%MatrixMarket", 14) == 0 ||
        strncmp(head, "%%matrixmarket", 14) == 0)
        read_matrix_market(path, A, verbose);
    else
        read_edge_list(path, A, verbose, weighted, seed);
}

/* ===========================================================================
 *  3. Розбиття рядків, збалансоване за кількістю ненульових елементів.
 *     Процес p отримує рядки [row_start[p], row_start[p+1]).
 *     Межі — бінарним пошуком у масиві префіксних сум rp[].
 * ========================================================================= */
static void partition_rows(const long *rp, int nrows, long nnz, int nproc,
                           int *row_start)
{
    row_start[0] = 0;
    for (int p = 1; p < nproc; ++p) {
        const long target = (long)((double)nnz * (double)p / (double)nproc + 0.5);
        int lo = row_start[p - 1], hi = nrows;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (rp[mid] < target) lo = mid + 1; else hi = mid;
        }
        row_start[p] = lo;
    }
    row_start[nproc] = nrows;
    for (int p = 1; p <= nproc; ++p)
        if (row_start[p] < row_start[p - 1]) row_start[p] = row_start[p - 1];
}

/* Наївне розбиття порівну ЗА КІЛЬКІСТЮ РЯДКІВ — для порівняння.
   На графах із степеневим розподілом степенів (як wiki-Vote) дає сильний
   дисбаланс: рядки різної довжини розподіляються нерівномірно за nnz. */
static void partition_rows_equal(int nrows, int nproc, int *row_start)
{
    for (int p = 0; p <= nproc; ++p)
        row_start[p] = (int)((long)nrows * p / nproc);
}

/* ===========================================================================
 *  4. ЯДРО: локальне множення блоку рядків на (локальний) вектор.
 *
 *     Безпосередня реалізація виразу Blelloch
 *         {sum({v * x[i] : (i,v) in row}) : row in A}
 *     для рядків, що належать цьому процесу.
 * ========================================================================= */
static void spmv_local(int nrows_loc, const long *rp, const int *ci,
                       const double *val, const double *x, double *y)
{
    for (int i = 0; i < nrows_loc; ++i) {
        double s = 0.0;                        /* редукція sum(...) */
        const long k0 = rp[i], k1 = rp[i + 1];
        for (long k = k0; k < k1; ++k)
            s += val[k] * x[ci[k]];            /* v * x[i]          */
        y[i] = s;
    }
}

/* ===========================================================================
 *  5. План розрідженого обміну компонентами вектора x
 * ========================================================================= */
typedef struct {
    int  nown;            /* компонент x, якими володіє процес            */
    int  next;            /* компонент, які треба отримувати щоітерації   */
    int *recv_cnt, *recv_disp;   /* від кого скільки отримуємо            */
    int *send_cnt, *send_disp;   /* кому скільки відправляємо             */
    int *send_idx;               /* локальні індекси для пакування        */
    int  send_total;
    double *sendbuf;
} Halo;

static int cmp_int(const void *a, const void *b)
{
    const int x = *(const int *)a, y = *(const int *)b;
    return (x > y) - (x < y);
}

/* Будує план обміну і ПЕРЕНУМЕРОВУЄ ci[] у локальні індекси масиву xloc:
 *   xloc[0 .. nown)              — власний блок x;
 *   xloc[nown .. nown+next)      — отримані чужі компоненти (за зростанням
 *                                  глобального номера, тобто групами по
 *                                  процесах-власниках).                     */
static void build_halo(int *ci, long nnz_loc, const int *vec_start,
                       int nproc, int rank, Halo *h)
{
    const int vs = vec_start[rank], ve = vec_start[rank + 1];
    h->nown = ve - vs;

    /* --- 5.1 зібрати унікальні "чужі" стовпці -------------------------- */
    int *ext = (int *)xmalloc((size_t)(nnz_loc ? nnz_loc : 1) * sizeof(int));
    long m = 0;
    for (long k = 0; k < nnz_loc; ++k) {
        const int c = ci[k];
        if (c < vs || c >= ve) ext[m++] = c;
    }
    qsort(ext, (size_t)m, sizeof(int), cmp_int);
    long u = 0;
    for (long k = 0; k < m; ++k)
        if (k == 0 || ext[k] != ext[k - 1]) ext[u++] = ext[k];
    h->next = (int)u;

    /* --- 5.2 скільки компонент отримуємо від кожного процесу ------------ */
    h->recv_cnt  = (int *)xmalloc((size_t)nproc * sizeof(int));
    h->recv_disp = (int *)xmalloc((size_t)nproc * sizeof(int));
    h->send_cnt  = (int *)xmalloc((size_t)nproc * sizeof(int));
    h->send_disp = (int *)xmalloc((size_t)nproc * sizeof(int));
    memset(h->recv_cnt, 0, (size_t)nproc * sizeof(int));

    /* ext упорядкований за зростанням, а власники володіють неперервними
       діапазонами => достатньо одного проходу двома вказівниками */
    {
        long k = 0;
        for (int p = 0; p < nproc; ++p) {
            const int hi = vec_start[p + 1];
            long k0 = k;
            while (k < u && ext[k] < hi) ++k;
            h->recv_cnt[p] = (int)(k - k0);
        }
    }
    h->recv_disp[0] = 0;
    for (int p = 1; p < nproc; ++p)
        h->recv_disp[p] = h->recv_disp[p - 1] + h->recv_cnt[p - 1];

    /* --- 5.3 дзеркальний план: кому що відправляти ---------------------- */
    MPI_Alltoall(h->recv_cnt, 1, MPI_INT, h->send_cnt, 1, MPI_INT, MPI_COMM_WORLD);
    h->send_disp[0] = 0;
    for (int p = 1; p < nproc; ++p)
        h->send_disp[p] = h->send_disp[p - 1] + h->send_cnt[p - 1];
    h->send_total = h->send_disp[nproc - 1] + h->send_cnt[nproc - 1];

    int *req = (int *)xmalloc((size_t)(h->send_total ? h->send_total : 1) * sizeof(int));
    MPI_Alltoallv(ext, h->recv_cnt, h->recv_disp, MPI_INT,
                  req, h->send_cnt, h->send_disp, MPI_INT, MPI_COMM_WORLD);

    h->send_idx = (int *)xmalloc((size_t)(h->send_total ? h->send_total : 1) * sizeof(int));
    for (int k = 0; k < h->send_total; ++k) h->send_idx[k] = req[k] - vs;
    free(req);
    h->sendbuf = (double *)xmalloc((size_t)(h->send_total ? h->send_total : 1) * sizeof(double));

    /* --- 5.4 перенумерація стовпців у локальні індекси ------------------ */
    for (long k = 0; k < nnz_loc; ++k) {
        const int c = ci[k];
        if (c >= vs && c < ve) {
            ci[k] = c - vs;
        } else {                                  /* бінарний пошук в ext */
            long lo = 0, hi = u - 1, pos = -1;
            while (lo <= hi) {
                long mid = (lo + hi) / 2;
                if (ext[mid] < c) lo = mid + 1;
                else if (ext[mid] > c) hi = mid - 1;
                else { pos = mid; break; }
            }
            if (pos < 0) die("внутрішня помилка: стовпець %d не знайдено в ext", c);
            ci[k] = h->nown + (int)pos;
        }
    }
    free(ext);
}

static void halo_free(Halo *h)
{
    free(h->recv_cnt); free(h->recv_disp);
    free(h->send_cnt); free(h->send_disp);
    free(h->send_idx); free(h->sendbuf);
}

/* Один обмін: пакування + MPI_Alltoallv просто у хвіст xloc */
static void halo_exchange(const Halo *h, double *xloc)
{
    for (int k = 0; k < h->send_total; ++k)
        h->sendbuf[k] = xloc[h->send_idx[k]];
    MPI_Alltoallv(h->sendbuf, h->send_cnt, h->send_disp, MPI_DOUBLE,
                  xloc + h->nown, h->recv_cnt, h->recv_disp, MPI_DOUBLE,
                  MPI_COMM_WORLD);
}

/* ===========================================================================
 *  6. Детермінований "довільний" вектор x (xorshift64*, значення у [-1,1])
 * ========================================================================= */
static void make_vector(double *x, int n, unsigned long long seed)
{
    unsigned long long s = seed ? seed : 88172645463325252ULL;
    for (int i = 0; i < n; ++i) {
        s ^= s >> 12; s ^= s << 25; s ^= s >> 27;
        const unsigned long long r = s * 2685821657736338717ULL;
        x[i] = 2.0 * ((double)(r >> 11) / 9007199254740992.0) - 1.0;
    }
}

/* ========================================================================= */

int main(int argc, char **argv)
{
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &g_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &g_size);
    const int root = 0;

    /* ---------------- аргументи ---------------------------------------- */
    const char *path = NULL, *csv_path = NULL, *tag = "", *y_path = NULL;
    int reps = 100, do_check = 0, sparse_comm = 1, weighted = 0, part_nnz = 1;
    unsigned long long seed = 42ULL;

    for (int i = 1; i < argc; ++i) {
        if      (!strcmp(argv[i], "--reps")   && i+1 < argc) reps = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--csv")    && i+1 < argc) csv_path = argv[++i];
        else if (!strcmp(argv[i], "--tag")    && i+1 < argc) tag = argv[++i];
        else if (!strcmp(argv[i], "--seed")   && i+1 < argc) seed = strtoull(argv[++i], NULL, 10);
        else if (!strcmp(argv[i], "--save-y") && i+1 < argc) y_path = argv[++i];
        else if (!strcmp(argv[i], "--comm")   && i+1 < argc) {
            ++i;
            if      (!strcmp(argv[i], "sparse"))    sparse_comm = 1;
            else if (!strcmp(argv[i], "allgather")) sparse_comm = 0;
            else die("--comm приймає 'sparse' або 'allgather'");
        }
        else if (!strcmp(argv[i], "--weights") && i+1 < argc) {
            ++i;
            if      (!strcmp(argv[i], "unit")) weighted = 0;
            else if (!strcmp(argv[i], "rand")) weighted = 1;
            else die("--weights приймає 'unit' або 'rand'");
        }
        else if (!strcmp(argv[i], "--partition") && i+1 < argc) {
            ++i;
            if      (!strcmp(argv[i], "nnz"))  part_nnz = 1;
            else if (!strcmp(argv[i], "rows")) part_nnz = 0;
            else die("--partition приймає 'nnz' або 'rows'");
        }
        else if (!strcmp(argv[i], "--check")) do_check = 1;
        else if (argv[i][0] != '-' && !path)  path = argv[i];
        else die("невідомий аргумент '%s'", argv[i]);
    }
    if (!path) {
        if (g_rank == root)
            fprintf(stderr,
              "Використання: mpirun -np P ./spmv_mpi <файл> [--reps N] [--check]\n"
              "   <файл>  : .mtx (Matrix Market) або список ребер SNAP\n"
              "   --comm sparse|allgather     схема обміну компонентами x\n"
              "   --partition nnz|rows        розбиття рядків між процесами\n"
              "   --weights unit|rand         ваги ребер (лише для списку ребер)\n"
              "   --csv файл  --tag назва  --seed S  --save-y файл\n");
        MPI_Finalize(); return 1;
    }
    if (reps < 1) reps = 1;

    CSR A = {0,0,0,NULL,NULL,NULL};
    double t_read = 0.0;

    /* ---------------- 1. читання матриці на корені ---------------------- */
    if (g_rank == root) {
        printf("=====================================================================\n");
        printf(" MPI SpMV (y = A*x) | процесів: %d | обмін: %s | розбиття: %s\n",
               g_size, sparse_comm ? "sparse (Alltoallv)" : "allgather (Allgatherv)",
               part_nnz ? "за nnz" : "за рядками");
        printf("---------------------------------------------------------------------\n");
        const double t0 = MPI_Wtime();
        read_matrix(path, &A, 1, weighted, seed + 1);
        t_read = MPI_Wtime() - t0;
        printf("  час читання     : %.3f с\n", t_read);
    }

    /* ---------------- 2. метадані та розбиття --------------------------- */
    int meta[2]; long nnz_total = 0;
    if (g_rank == root) { meta[0]=A.nrows; meta[1]=A.ncols; nnz_total=A.nnz; }
    MPI_Bcast(meta, 2, MPI_INT, root, MPI_COMM_WORLD);
    MPI_Bcast(&nnz_total, 1, MPI_LONG, root, MPI_COMM_WORLD);
    const int nrows = meta[0], ncols = meta[1];

    int *row_start = (int *)xmalloc((size_t)(g_size+1) * sizeof(int));
    if (g_rank == root) {
        if (part_nnz) partition_rows(A.rp, A.nrows, A.nnz, g_size, row_start);
        else          partition_rows_equal(A.nrows, g_size, row_start);
    }
    MPI_Bcast(row_start, g_size+1, MPI_INT, root, MPI_COMM_WORLD);
    const int nrows_loc = row_start[g_rank+1] - row_start[g_rank];

    /* розбиття вектора x: для квадратної матриці — таке саме, як рядків
       (тоді переважна частина потрібних компонент уже локальна) */
    int *vec_start = (int *)xmalloc((size_t)(g_size+1) * sizeof(int));
    if (nrows == ncols) {
        memcpy(vec_start, row_start, (size_t)(g_size+1) * sizeof(int));
    } else {
        for (int p = 0; p <= g_size; ++p)
            vec_start[p] = (int)((long)ncols * p / g_size);
    }

    int *nnz_cnt  = (int *)xmalloc((size_t)g_size * sizeof(int));
    int *nnz_disp = (int *)xmalloc((size_t)g_size * sizeof(int));
    int *row_cnt  = (int *)xmalloc((size_t)g_size * sizeof(int));
    int *row_disp = (int *)xmalloc((size_t)g_size * sizeof(int));
    int *vec_cnt  = (int *)xmalloc((size_t)g_size * sizeof(int));
    int *vec_disp = (int *)xmalloc((size_t)g_size * sizeof(int));
    for (int p = 0; p < g_size; ++p) {
        row_disp[p] = row_start[p];
        row_cnt[p]  = row_start[p+1] - row_start[p];
        vec_disp[p] = vec_start[p];
        vec_cnt[p]  = vec_start[p+1] - vec_start[p];
    }
    if (g_rank == root)
        for (int p = 0; p < g_size; ++p) {
            nnz_disp[p] = (int)A.rp[row_start[p]];
            nnz_cnt[p]  = (int)(A.rp[row_start[p+1]] - A.rp[row_start[p]]);
        }
    MPI_Bcast(nnz_cnt, g_size, MPI_INT, root, MPI_COMM_WORLD);
    const long nnz_loc = nnz_cnt[g_rank];

    /* ---------------- 3. роздача фрагментів CSR ------------------------- */
    long   *rp_loc  = (long *)  xmalloc((size_t)(nrows_loc+1) * sizeof(long));
    int    *ci_loc  = (int *)   xmalloc((size_t)(nnz_loc?nnz_loc:1) * sizeof(int));
    double *val_loc = (double *)xmalloc((size_t)(nnz_loc?nnz_loc:1) * sizeof(double));

    MPI_Scatterv(g_rank==root ? A.ci : NULL, nnz_cnt, nnz_disp, MPI_INT,
                 ci_loc, (int)nnz_loc, MPI_INT, root, MPI_COMM_WORLD);
    MPI_Scatterv(g_rank==root ? A.val : NULL, nnz_cnt, nnz_disp, MPI_DOUBLE,
                 val_loc, (int)nnz_loc, MPI_DOUBLE, root, MPI_COMM_WORLD);

    if (g_rank == root) {
        for (int p = 0; p < g_size; ++p) {
            const int rs = row_start[p], n = row_start[p+1] - row_start[p];
            const long base = A.rp[rs];
            if (p == root) {
                for (int i = 0; i <= n; ++i) rp_loc[i] = A.rp[rs+i] - base;
            } else {
                long *buf = (long *)xmalloc((size_t)(n+1) * sizeof(long));
                for (int i = 0; i <= n; ++i) buf[i] = A.rp[rs+i] - base;
                MPI_Send(buf, n+1, MPI_LONG, p, 77, MPI_COMM_WORLD);
                free(buf);
            }
        }
    } else {
        MPI_Recv(rp_loc, nrows_loc+1, MPI_LONG, root, 77,
                 MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    }

    /* ---------------- 4. вектор x --------------------------------------- */
    double *x_glob = NULL;                       /* потрібен лише кореню   */
    if (g_rank == root) {
        x_glob = (double *)xmalloc((size_t)ncols * sizeof(double));
        make_vector(x_glob, ncols, seed);
    }
    const int nown = vec_cnt[g_rank];

    Halo h; memset(&h, 0, sizeof(h));
    double *xloc = NULL;                         /* те, що бачить ядро     */

    if (sparse_comm) {
        build_halo(ci_loc, nnz_loc, vec_start, g_size, g_rank, &h);
        xloc = (double *)xmalloc((size_t)(h.nown + h.next + 1) * sizeof(double));
        MPI_Scatterv(x_glob, vec_cnt, vec_disp, MPI_DOUBLE,
                     xloc, nown, MPI_DOUBLE, root, MPI_COMM_WORLD);
    } else {
        xloc = (double *)xmalloc((size_t)ncols * sizeof(double));   /* увесь x */
        MPI_Scatterv(x_glob, vec_cnt, vec_disp, MPI_DOUBLE,
                     xloc + vec_disp[g_rank], nown, MPI_DOUBLE, root, MPI_COMM_WORLD);
    }

    double *y_loc = (double *)xmalloc((size_t)(nrows_loc?nrows_loc:1) * sizeof(double));

    /* ---------------- 5. прогрів ---------------------------------------- */
    for (int r = 0; r < 3; ++r) {
        if (sparse_comm) halo_exchange(&h, xloc);
        else MPI_Allgatherv(MPI_IN_PLACE, nown, MPI_DOUBLE,
                            xloc, vec_cnt, vec_disp, MPI_DOUBLE, MPI_COMM_WORLD);
        spmv_local(nrows_loc, rp_loc, ci_loc, val_loc, xloc, y_loc);
    }

    /* ---------------- 6. вимірювання ------------------------------------
     *  Одна ітерація = обмін компонентами x + локальний SpMV
     *  (саме так виглядає крок ітераційного методу, напр. CG чи степеневого).
     *  t_kernel — лише обчислення;  t_comm — лише обмін;  t_iter — разом.
     * ------------------------------------------------------------------- */
    double acc_kernel = 0.0, acc_comm = 0.0;

    MPI_Barrier(MPI_COMM_WORLD);
    const double t_begin = MPI_Wtime();
    for (int r = 0; r < reps; ++r) {
        double t0 = MPI_Wtime();
        if (sparse_comm) halo_exchange(&h, xloc);
        else MPI_Allgatherv(MPI_IN_PLACE, nown, MPI_DOUBLE,
                            xloc, vec_cnt, vec_disp, MPI_DOUBLE, MPI_COMM_WORLD);
        double t1 = MPI_Wtime();
        spmv_local(nrows_loc, rp_loc, ci_loc, val_loc, xloc, y_loc);
        double t2 = MPI_Wtime();
        acc_comm   += t1 - t0;
        acc_kernel += t2 - t1;
    }
    const double t_iter_loc   = (MPI_Wtime() - t_begin) / reps;
    const double t_kernel_loc = acc_kernel / reps;
    const double t_comm_loc   = acc_comm   / reps;

    double t_iter=0, t_kernel=0, t_comm=0, t_kernel_sum=0;
    MPI_Reduce(&t_iter_loc,   &t_iter,   1, MPI_DOUBLE, MPI_MAX, root, MPI_COMM_WORLD);
    MPI_Reduce(&t_kernel_loc, &t_kernel, 1, MPI_DOUBLE, MPI_MAX, root, MPI_COMM_WORLD);
    MPI_Reduce(&t_comm_loc,   &t_comm,   1, MPI_DOUBLE, MPI_MAX, root, MPI_COMM_WORLD);
    MPI_Reduce(&t_kernel_loc, &t_kernel_sum, 1, MPI_DOUBLE, MPI_SUM, root, MPI_COMM_WORLD);

    /* обсяг обміну: скільки чужих компонент отримує процес */
    int ext_max = 0, ext_sum = 0;
    int my_ext = sparse_comm ? h.next : (ncols - nown);
    MPI_Reduce(&my_ext, &ext_max, 1, MPI_INT, MPI_MAX, root, MPI_COMM_WORLD);
    MPI_Reduce(&my_ext, &ext_sum, 1, MPI_INT, MPI_SUM, root, MPI_COMM_WORLD);

    /* ---------------- 7. збирання y та перевірка ------------------------ */
    double *y = (g_rank==root) ? (double *)xmalloc((size_t)nrows*sizeof(double)) : NULL;
    MPI_Gatherv(y_loc, nrows_loc, MPI_DOUBLE, y, row_cnt, row_disp, MPI_DOUBLE,
                root, MPI_COMM_WORLD);

    double max_abs_err = 0.0, rel_err = 0.0, t_seq = 0.0;
    if (do_check && g_rank == root) {
        double *y_ref = (double *)xmalloc((size_t)nrows * sizeof(double));
        spmv_local(A.nrows, A.rp, A.ci, A.val, x_glob, y_ref);   /* прогрів */
        const int rseq = reps > 200 ? 200 : reps;   /* еталон не потребує
                                                       стількох повторів */
        const double ts = MPI_Wtime();
        for (int r = 0; r < rseq; ++r)
            spmv_local(A.nrows, A.rp, A.ci, A.val, x_glob, y_ref);
        t_seq = (MPI_Wtime() - ts) / rseq;

        double num = 0.0, den = 0.0;
        for (int i = 0; i < nrows; ++i) {
            const double d = fabs(y[i] - y_ref[i]);
            if (d > max_abs_err) max_abs_err = d;
            num += d*d; den += y_ref[i]*y_ref[i];
        }
        rel_err = (den > 0.0) ? sqrt(num)/sqrt(den) : sqrt(num);
        free(y_ref);
    }

    /* ---------------- 8. звіт ------------------------------------------- */
    if (g_rank == root) {
        const double gflops = 2.0*(double)nnz_total / t_kernel * 1e-9;
        const double imbalance = (t_kernel_sum > 0.0)
                                 ? t_kernel * g_size / t_kernel_sum : 1.0;
        double ynorm = 0.0;
        for (int i = 0; i < nrows; ++i) ynorm += y[i]*y[i];
        ynorm = sqrt(ynorm);

        printf("---------------------------------------------------------------------\n");
        printf("  розмір          : %d x %d,  nnz = %ld\n", nrows, ncols, nnz_total);
        printf("  процесів (P)    : %d,  повторень: %d\n", g_size, reps);
        printf("  nnz на процес   : ");
        for (int p = 0; p < g_size && p < 12; ++p) printf("%d ", nnz_cnt[p]);
        if (g_size > 12) printf("...");
        printf("\n");
        printf("  обмін x         : max %d, сумарно %d компонент за ітерацію "
               "(%.2f%% від N)\n", ext_max, ext_sum,
               100.0*(double)ext_sum/((double)ncols*g_size));
        printf("  t_ітерації      : %.6f с   (обмін + обчислення)\n", t_iter);
        printf("    t_обчислення  : %.6f с\n", t_kernel);
        printf("    t_обмін       : %.6f с   (%.1f%% ітерації)\n",
               t_comm, 100.0*t_comm/t_iter);
        printf("  продуктивність  : %.3f GFLOP/s (по ядру)\n", gflops);
        printf("  дисбаланс max/avg : %.3f\n", imbalance);
        printf("  ||y||_2         : %.12e\n", ynorm);
        if (do_check)
            printf("  ПЕРЕВІРКА       : max|y-y_посл| = %.3e, відн. похибка = %.3e -> %s\n"
                   "  послідовний час : %.6f с (1 процес, теплий кеш)\n",
                   max_abs_err, rel_err, (rel_err < 1e-12 ? "OK" : "РОЗБІЖНІСТЬ!"),
                   t_seq);
        printf("=====================================================================\n");

        if (csv_path) {
            FILE *fc = fopen(csv_path, "a");
            if (!fc) die("не можу відкрити CSV '%s'", csv_path);
            fseek(fc, 0, SEEK_END);
            if (ftell(fc) == 0)
                fprintf(fc, "tag,comm,part,matrix,nrows,ncols,nnz,procs,reps,"
                            "t_iter,t_kernel,t_comm,gflops,imbalance,ext_max,rel_err\n");
            fprintf(fc, "%s,%s,%s,%s,%d,%d,%ld,%d,%d,"
                        "%.9f,%.9f,%.9f,%.4f,%.4f,%d,%.3e\n",
                    tag, sparse_comm ? "sparse" : "allgather",
                    part_nnz ? "nnz" : "rows", path,
                    nrows, ncols, nnz_total, g_size, reps,
                    t_iter, t_kernel, t_comm, gflops, imbalance, ext_max, rel_err);
            fclose(fc);
        }
        if (y_path) {
            FILE *fy = fopen(y_path, "w");
            if (!fy) die("не можу відкрити '%s'", y_path);
            for (int i = 0; i < nrows; ++i) fprintf(fy, "%.17g\n", y[i]);
            fclose(fy);
            printf("  вектор y записано у %s\n", y_path);
        }
    }

    if (sparse_comm) halo_free(&h);
    free(xloc); free(y_loc); free(y); free(x_glob);
    free(rp_loc); free(ci_loc); free(val_loc);
    free(row_start); free(vec_start);
    free(nnz_cnt); free(nnz_disp); free(row_cnt); free(row_disp);
    free(vec_cnt); free(vec_disp);
    if (g_rank == root) csr_free(&A);

    MPI_Finalize();
    return 0;
}
