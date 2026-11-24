/* ===========================================================================
 *  Лабораторна робота №4 — "Високопродуктивні обчислення"
 *
 *  OpenMP. Прогнозування за рухомим середнім
 *
 *  ЗАДАЧА
 *  ------
 *  За історією біржових котирувань обчислити просте (SMA) і зважене (WMA)
 *  рухоме середнє за вікном довжиною N і на їхній основі зробити прогноз
 *  ціни на наступний крок (наступний торговий день):
 *
 *      SMA:  f[t+1] = (1/N) * sum_{k=0..N-1} p[t-k]
 *      WMA:  f[t+1] = sum_{k=0..N-1} (N-k)*p[t-k] / sum_{k=0..N-1} (N-k)
 *
 *  У простому середньому всі дні вікна рівноправні; у зваженому новіші ціни
 *  впливають сильніше (ваги лінійно спадають углиб історії).
 *
 *  Програма перебирає всі довжини вікна з діапазону [--wmin, --wmax] і для
 *  кожної рахує похибки прогнозу, щоб відповісти, яке вікно точніше.
 *
 *  ПАРАЛЕЛІЗАЦІЯ (OpenMP)
 *  ----------------------
 *    --parallel days    (типово) потоки одночасно рахують прогнози для
 *                       РІЗНИХ ДНІВ у межах одного вікна: дрібне зерно,
 *                       251 вхід у паралельну область;
 *    --parallel windows потоки одночасно рахують РІЗНІ ДОВЖИНИ ВІКНА:
 *                       грубе зерно, один вхід у паралельну область, але
 *                       нерівномірне навантаження (робота росте як N).
 *
 *  Обидва режими дають побітно однаковий результат за будь-якої кількості
 *  потоків: прогнози складаються у масив, а підсумкові суми похибок
 *  обчислюються послідовно у фіксованому порядку.
 *
 *  Збірка:  make
 *  Запуск:  ./forecast_omp data/vix-daily.csv --threads 4 --sweep out.csv
 * ===========================================================================
 */

#if !defined(__APPLE__)        /* на macOS цей макрос навпаки ховає getline */
#define _POSIX_C_SOURCE 200809L
#endif

#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <math.h>
#include <ctype.h>
#include <strings.h>   /* strcasecmp */

/* ------------------------------------------------------------------------ */
static void die(const char *fmt, ...)
{
    va_list ap;
    fprintf(stderr, "ПОМИЛКА: ");
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    fprintf(stderr, "\n");
    exit(EXIT_FAILURE);
}

static void *xmalloc(size_t n)
{
    void *p = malloc(n ? n : 1);
    if (!p) die("не вдалося виділити %zu байт", n);
    return p;
}

/* ===========================================================================
 *  1. Читання котирувань із CSV
 *
 *     Підтримуються будь-які CSV із заголовком: колонки шукаються за НАЗВОЮ.
 *     Приклади:
 *        DATE,OPEN,HIGH,LOW,CLOSE          (CBOE VIX)
 *        Date,Price                        (Brent)
 *        Date,Open,High,Low,Close,Volume   (Stooq)
 *        Date,Open,High,Low,Close,Adj Close,Volume   (Yahoo Finance)
 * ========================================================================= */
typedef struct {
    long    n;
    char  (*date)[11];        /* YYYY-MM-DD */
    double *price;
} Series;

/* Номер колонки з такою назвою (без урахування регістру), або -1. */
static int find_col(char *header, const char *name)
{
    char buf[4096];
    strncpy(buf, header, sizeof buf - 1);
    buf[sizeof buf - 1] = '\0';

    int idx = 0;
    for (char *tok = strtok(buf, ",\r\n"); tok; tok = strtok(NULL, ",\r\n"), ++idx) {
        while (*tok == ' ' || *tok == '"') ++tok;
        char *end = tok + strlen(tok);
        while (end > tok && (end[-1] == ' ' || end[-1] == '"')) *--end = '\0';
        if (strcasecmp(tok, name) == 0) return idx;
    }
    return -1;
}

/* k-те поле рядка CSV (прості CSV без ком усередині лапок). */
static char *field(char *line, int k)
{
    static char buf[4096];
    strncpy(buf, line, sizeof buf - 1);
    buf[sizeof buf - 1] = '\0';
    char *p = buf;
    for (int i = 0; i < k; ++i) {
        p = strchr(p, ',');
        if (!p) return NULL;
        ++p;
    }
    char *e = strpbrk(p, ",\r\n");
    if (e) *e = '\0';
    while (*p == ' ' || *p == '"') ++p;
    char *end = p + strlen(p);
    while (end > p && (end[-1] == ' ' || end[-1] == '"')) *--end = '\0';
    return p;
}

static void read_csv(const char *path, const char *date_name,
                     const char *price_name, const char *from, const char *to,
                     Series *s, int verbose)
{
    FILE *f = fopen(path, "r");
    if (!f) die("не можу відкрити файл котирувань '%s'", path);

    char *line = NULL;
    size_t cap = 0;
    if (getline(&line, &cap, f) <= 0) die("порожній файл '%s'", path);

    int ci_date = -1, ci_price = -1;
    if (date_name) ci_date = find_col(line, date_name);
    else {
        const char *cand[] = {"Date", "DATE", "date", "Data", NULL};
        for (int i = 0; cand[i] && ci_date < 0; ++i)
            ci_date = find_col(line, cand[i]);
    }
    if (price_name) ci_price = find_col(line, price_name);
    else {
        /* "Adj Close" точніша за "Close": врахована дивідендами й дробленнями */
        const char *cand[] = {"Adj Close", "AdjClose", "Close", "CLOSE",
                              "Price", "Zamkniecie", NULL};
        for (int i = 0; cand[i] && ci_price < 0; ++i)
            ci_price = find_col(line, cand[i]);
    }
    if (ci_date < 0)
        die("у заголовку немає колонки з датою (спробуйте --date-col НАЗВА)");
    if (ci_price < 0)
        die("у заголовку немає колонки з ціною (спробуйте --price-col НАЗВА)");

    long cap_n = 1024, n = 0;
    char (*date)[11] = xmalloc((size_t)cap_n * 11);
    double *price = xmalloc((size_t)cap_n * sizeof(double));

    long skipped = 0;
    while (getline(&line, &cap, f) > 0) {
        if (line[0] == '\n' || line[0] == '\r' || line[0] == '\0') continue;

        char tmp[4096];
        strncpy(tmp, line, sizeof tmp - 1);
        tmp[sizeof tmp - 1] = '\0';

        char *d = field(tmp, ci_date);
        if (!d || strlen(d) < 10) { ++skipped; continue; }
        char dbuf[11];                      /* копія: field() має спільний буфер */
        memcpy(dbuf, d, 10);
        dbuf[10] = '\0';
        if (from && strncmp(dbuf, from, 10) < 0) continue;
        if (to && strncmp(dbuf, to, 10) > 0) continue;

        strncpy(tmp, line, sizeof tmp - 1);
        char *v = field(tmp, ci_price);
        if (!v || *v == '\0') { ++skipped; continue; }   /* пропуск у даних */
        char *endp = NULL;
        double x = strtod(v, &endp);
        if (endp == v || !(x > 0.0)) { ++skipped; continue; }

        if (n == cap_n) {
            cap_n *= 2;
            date = realloc(date, (size_t)cap_n * 11);
            price = realloc(price, (size_t)cap_n * sizeof(double));
            if (!date || !price) die("не вистачає пам'яті на ряд котирувань");
        }
        memcpy(date[n], dbuf, 11);
        price[n] = x;
        ++n;
    }
    free(line);
    fclose(f);

    if (n < 10) die("у файлі замало придатних рядків (%ld)", n);

    s->n = n; s->date = date; s->price = price;

    if (verbose) {
        printf("  файл            : %s\n", path);
        printf("  період          : %s .. %s\n", date[0], date[n - 1]);
        int y0 = atoi(date[0]), y1 = atoi(date[n - 1]);
        printf("  спостережень    : %ld (приблизно %d років)\n", n, y1 - y0);
        double mn = price[0], mx = price[0];
        for (long i = 1; i < n; ++i) {
            if (price[i] < mn) mn = price[i];
            if (price[i] > mx) mx = price[i];
        }
        printf("  ціна            : min %.4f, max %.4f\n", mn, mx);
        if (skipped) printf("  пропущено рядків (порожні/некоректні): %ld\n", skipped);
    }
}

/* ===========================================================================
 *  2. Прогноз за вікном N для всіх днів відрізка [t0, t1]
 *
 *     f_sma[t], f_wma[t] — прогноз ціни на день t+1, зроблений за даними
 *     днів t-N+1 .. t. Це і є "прогноз на наступний крок".
 * ========================================================================= */
static void forecast_window(const double *p, long t0, long t1, int N,
                            double *f_sma, double *f_wma, int parallel_days)
{
    const double wsum = 0.5 * (double)N * (double)(N + 1);   /* N + (N-1) + ... + 1 */

#pragma omp parallel for schedule(static) if (parallel_days)
    for (long t = t0; t <= t1; ++t) {
        double s = 0.0, ws = 0.0;
        for (int k = 0; k < N; ++k) {
            const double x = p[t - k];
            s += x;
            ws += (double)(N - k) * x;
        }
        f_sma[t] = s / (double)N;
        f_wma[t] = ws / wsum;
    }
}

/* ===========================================================================
 *  3. Похибки прогнозу
 *
 *     Підсумовування ПОСЛІДОВНЕ у фіксованому порядку, тому результат не
 *     залежить від кількості потоків побітно.
 * ========================================================================= */
typedef struct {
    double mae, rmse, mape, dir_acc;
    long   n_eval;
} Metrics;

static Metrics metrics_of(const double *p, const double *f, long t0, long t1)
{
    Metrics m = {0, 0, 0, 0, 0};
    double sae = 0.0, sse = 0.0, sape = 0.0;
    long hit = 0, moves = 0;

    for (long t = t0; t <= t1; ++t) {
        const double a = p[t + 1];                 /* фактична ціна      */
        const double e = f[t] - a;                 /* похибка прогнозу   */
        sae += fabs(e);
        sse += e * e;
        sape += fabs(e) / fabs(a);

        const double d_pred = f[t] - p[t];         /* прогнозований рух  */
        const double d_real = a - p[t];            /* фактичний рух      */
        if (d_real != 0.0) {
            ++moves;
            if ((d_pred > 0.0 && d_real > 0.0) || (d_pred < 0.0 && d_real < 0.0))
                ++hit;
        }
    }
    const double cnt = (double)(t1 - t0 + 1);
    m.n_eval = t1 - t0 + 1;
    m.mae = sae / cnt;
    m.rmse = sqrt(sse / cnt);
    m.mape = 100.0 * sape / cnt;
    m.dir_acc = moves ? 100.0 * (double)hit / (double)moves : 0.0;
    return m;
}

/* ========================================================================= */
int main(int argc, char **argv)
{
    const char *path = NULL, *sweep_path = NULL, *series_path = NULL;
    const char *csv_path = NULL, *tag = "", *from = NULL, *to = NULL;
    const char *date_col = NULL, *price_col = NULL;
    int wmin = 2, wmax = 252, threads = 0, repeat = 1, series_w = 20;
    int parallel_days = 1;

    for (int i = 1; i < argc; ++i) {
        if      (!strcmp(argv[i], "--wmin")      && i+1 < argc) wmin = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--wmax")      && i+1 < argc) wmax = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--threads")   && i+1 < argc) threads = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--repeat")    && i+1 < argc) repeat = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--sweep")     && i+1 < argc) sweep_path = argv[++i];
        else if (!strcmp(argv[i], "--series")    && i+1 < argc) series_path = argv[++i];
        else if (!strcmp(argv[i], "--series-window") && i+1 < argc) series_w = atoi(argv[++i]);
        else if (!strcmp(argv[i], "--csv")       && i+1 < argc) csv_path = argv[++i];
        else if (!strcmp(argv[i], "--tag")       && i+1 < argc) tag = argv[++i];
        else if (!strcmp(argv[i], "--from")      && i+1 < argc) from = argv[++i];
        else if (!strcmp(argv[i], "--to")        && i+1 < argc) to = argv[++i];
        else if (!strcmp(argv[i], "--date-col")  && i+1 < argc) date_col = argv[++i];
        else if (!strcmp(argv[i], "--price-col") && i+1 < argc) price_col = argv[++i];
        else if (!strcmp(argv[i], "--parallel")  && i+1 < argc) {
            ++i;
            if      (!strcmp(argv[i], "days"))    parallel_days = 1;
            else if (!strcmp(argv[i], "windows")) parallel_days = 0;
            else die("--parallel приймає 'days' або 'windows'");
        }
        else if (argv[i][0] != '-' && !path) path = argv[i];
        else die("невідомий аргумент '%s'", argv[i]);
    }

    if (!path) {
        fprintf(stderr,
          "Використання: ./forecast_omp <котирування.csv> [параметри]\n"
          "  --wmin N --wmax N      діапазон довжин вікна (типово 2..252)\n"
          "  --threads T            кількість потоків OpenMP (0 = за замовч.)\n"
          "  --parallel days|windows  що розпаралелювати (типово days)\n"
          "  --repeat R             повторити розрахунок R разів (для замірів)\n"
          "  --sweep файл.csv       похибки для кожної довжини вікна\n"
          "  --series файл.csv      ряд прогнозів для одного вікна\n"
          "  --series-window N      яке вікно зберегти в --series (типово 20)\n"
          "  --from / --to YYYY-MM-DD   обмежити період\n"
          "  --date-col / --price-col   назви колонок, якщо не вгадалися\n"
          "  --csv файл.csv --tag назва   дописати час виконання\n");
        return 1;
    }
    if (wmin < 1) wmin = 1;
    if (wmax < wmin) wmax = wmin;
    if (repeat < 1) repeat = 1;
    if (threads > 0) omp_set_num_threads(threads);

    Series S;
    printf("=====================================================================\n");
    printf(" OpenMP: прогноз за рухомим середнім\n");
    printf("---------------------------------------------------------------------\n");
    read_csv(path, date_col, price_col, from, to, &S, 1);

    const long n = S.n;
    const double *p = S.price;

    /* Спільний відрізок оцінювання для ВСІХ довжин вікна: інакше різні N
       порівнювалися б на різних наборах днів і порівняння було б нечесним. */
    const long t0 = wmax - 1;
    const long t1 = n - 2;
    if (t1 < t0) die("замало даних для вікна %d", wmax);

    const int nthreads = omp_get_max_threads();
    printf("  вікна           : %d .. %d\n", wmin, wmax);
    printf("  днів оцінювання : %ld (%s .. %s)\n",
           t1 - t0 + 1, S.date[t0 + 1], S.date[t1 + 1]);
    printf("  потоків OpenMP  : %d, розпаралелено за: %s\n",
           nthreads, parallel_days ? "днями" : "вікнами");

    const int nw = wmax - wmin + 1;
    double *f_sma = xmalloc((size_t)n * sizeof(double));
    double *f_wma = xmalloc((size_t)n * sizeof(double));
    Metrics *m_sma = xmalloc((size_t)nw * sizeof(Metrics));
    Metrics *m_wma = xmalloc((size_t)nw * sizeof(Metrics));

    double *ser_sma = NULL, *ser_wma = NULL;
    if (series_path) {
        ser_sma = xmalloc((size_t)n * sizeof(double));
        ser_wma = xmalloc((size_t)n * sizeof(double));
    }

    /* --------------------------------------------------------------------
     *  Основний розрахунок. Він повторюється --repeat разів, щоб заміри
     *  часу були стабільними; результат від повторів не залежить.
     * ------------------------------------------------------------------ */
    const double t_begin = omp_get_wtime();

    for (int r = 0; r < repeat; ++r) {
        if (parallel_days) {
            for (int N = wmin; N <= wmax; ++N) {
                forecast_window(p, t0, t1, N, f_sma, f_wma, 1);
                m_sma[N - wmin] = metrics_of(p, f_sma, t0, t1);
                m_wma[N - wmin] = metrics_of(p, f_wma, t0, t1);
                if (series_path && N == series_w) {
                    memcpy(ser_sma, f_sma, (size_t)n * sizeof(double));
                    memcpy(ser_wma, f_wma, (size_t)n * sizeof(double));
                }
            }
        } else {
            /* грубе зерно: кожен потік веде власні буфери прогнозів */
#pragma omp parallel
            {
                double *ls = xmalloc((size_t)n * sizeof(double));
                double *lw = xmalloc((size_t)n * sizeof(double));
#pragma omp for schedule(dynamic, 1)
                for (int N = wmin; N <= wmax; ++N) {
                    forecast_window(p, t0, t1, N, ls, lw, 0);
                    m_sma[N - wmin] = metrics_of(p, ls, t0, t1);
                    m_wma[N - wmin] = metrics_of(p, lw, t0, t1);
                    if (series_path && N == series_w) {
                        memcpy(ser_sma, ls, (size_t)n * sizeof(double));
                        memcpy(ser_wma, lw, (size_t)n * sizeof(double));
                    }
                }
                free(ls); free(lw);
            }
        }
    }

    const double t_elapsed = (omp_get_wtime() - t_begin) / repeat;

    /* --------------------------------------------------------------------
     *  Еталон «наївного» прогнозу: завтра буде те саме, що сьогодні.
     *  Для біржових рядів це дуже сильний орієнтир, із ним і треба
     *  порівнювати будь-який метод.
     * ------------------------------------------------------------------ */
    for (long t = t0; t <= t1; ++t) f_sma[t] = p[t];
    const Metrics m_naive = metrics_of(p, f_sma, t0, t1);

    /* --------------------------------------------------------------------
     *  Найкраще вікно за кожним критерієм
     * ------------------------------------------------------------------ */
    int best_sma = 0, best_wma = 0, best_da_sma = 0, best_da_wma = 0;
    for (int i = 1; i < nw; ++i) {
        if (m_sma[i].rmse < m_sma[best_sma].rmse) best_sma = i;
        if (m_wma[i].rmse < m_wma[best_wma].rmse) best_wma = i;
        if (m_sma[i].dir_acc > m_sma[best_da_sma].dir_acc) best_da_sma = i;
        if (m_wma[i].dir_acc > m_wma[best_da_wma].dir_acc) best_da_wma = i;
    }

    printf("---------------------------------------------------------------------\n");
    printf("  час розрахунку  : %.4f с (%d потоків, повторів: %d)\n",
           t_elapsed, nthreads, repeat);
    printf("---------------------------------------------------------------------\n");
    printf("  НАЇВНИЙ прогноз (f[t+1] = p[t]):  RMSE %.5f  MAE %.5f  MAPE %.3f %%\n",
           m_naive.rmse, m_naive.mae, m_naive.mape);
    printf("  SMA, найкраще вікно за RMSE : N = %-4d  RMSE %.5f  MAE %.5f  MAPE %.3f %%\n",
           wmin + best_sma, m_sma[best_sma].rmse, m_sma[best_sma].mae,
           m_sma[best_sma].mape);
    printf("  WMA, найкраще вікно за RMSE : N = %-4d  RMSE %.5f  MAE %.5f  MAPE %.3f %%\n",
           wmin + best_wma, m_wma[best_wma].rmse, m_wma[best_wma].mae,
           m_wma[best_wma].mape);
    printf("  SMA, найкраще вікно за напрямком руху : N = %-4d  влучань %.2f %%\n",
           wmin + best_da_sma, m_sma[best_da_sma].dir_acc);
    printf("  WMA, найкраще вікно за напрямком руху : N = %-4d  влучань %.2f %%\n",
           wmin + best_da_wma, m_wma[best_da_wma].dir_acc);
    printf("=====================================================================\n");

    if (sweep_path) {
        FILE *fo = fopen(sweep_path, "w");
        if (!fo) die("не можу відкрити '%s'", sweep_path);
        fprintf(fo, "window,method,mae,rmse,mape,dir_acc,n_eval\n");
        fprintf(fo, "0,naive,%.10f,%.10f,%.10f,%.4f,%ld\n",
                m_naive.mae, m_naive.rmse, m_naive.mape, m_naive.dir_acc,
                m_naive.n_eval);
        for (int i = 0; i < nw; ++i) {
            fprintf(fo, "%d,sma,%.10f,%.10f,%.10f,%.4f,%ld\n", wmin + i,
                    m_sma[i].mae, m_sma[i].rmse, m_sma[i].mape,
                    m_sma[i].dir_acc, m_sma[i].n_eval);
            fprintf(fo, "%d,wma,%.10f,%.10f,%.10f,%.4f,%ld\n", wmin + i,
                    m_wma[i].mae, m_wma[i].rmse, m_wma[i].mape,
                    m_wma[i].dir_acc, m_wma[i].n_eval);
        }
        fclose(fo);
        printf("  похибки за вікнами -> %s\n", sweep_path);
    }

    if (series_path) {
        FILE *fo = fopen(series_path, "w");
        if (!fo) die("не можу відкрити '%s'", series_path);
        fprintf(fo, "date,actual,sma,wma,naive\n");
        for (long t = t0; t <= t1; ++t)
            fprintf(fo, "%s,%.6f,%.6f,%.6f,%.6f\n",
                    S.date[t + 1], p[t + 1], ser_sma[t], ser_wma[t], p[t]);
        fclose(fo);
        printf("  ряд прогнозів (вікно %d) -> %s\n", series_w, series_path);
    }

    if (csv_path) {
        FILE *fo = fopen(csv_path, "a");
        if (!fo) die("не можу відкрити '%s'", csv_path);
        fseek(fo, 0, SEEK_END);
        if (ftell(fo) == 0)
            fprintf(fo, "tag,data,parallel,threads,points,wmin,wmax,repeat,"
                        "time,rmse_best_sma,window_best_sma\n");
        fprintf(fo, "%s,%s,%s,%d,%ld,%d,%d,%d,%.9f,%.10f,%d\n",
                tag, path, parallel_days ? "days" : "windows", nthreads, n,
                wmin, wmax, repeat, t_elapsed, m_sma[best_sma].rmse,
                wmin + best_sma);
        fclose(fo);
    }

    free(f_sma); free(f_wma); free(m_sma); free(m_wma);
    free(ser_sma); free(ser_wma);
    free(S.date); free(S.price);
    return 0;
}
