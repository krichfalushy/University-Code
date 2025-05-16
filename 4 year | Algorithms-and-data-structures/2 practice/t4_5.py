def argument(l, r, m, f, eps):
    return r - l > eps


def value(l, r, m, f, eps):
    return abs(f(r) - f(l)) > eps


def neighbours(l, r, m, f, eps):
    return m != l and m != r


condition = neighbours


def solve(f, c, a, b):
    count = 0
    eps = 1e-10
    l = a
    r = b
    m = (l + r) / 2.0
    while condition(l, r, m, f, eps):
        count += 1
        if f(m) < c:
            l = m
        else:
            r = m
        m = (l + r) / 2.0
    print(f"Кількість ітерацій: {count}")
    return m


def f(x):
    return x**3 + 4*x**2 + x - 6


root = solve(f, 0, 0, 2)

print(f"Розв’язок рівняння x^3 + 4x^2 + x - 6 = 0: x ≈ {root}")
