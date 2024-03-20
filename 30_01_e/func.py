from math import factorial


def func(x, eps):
    assert abs(x) < 1
    assert eps > 0

    result = x
    a = x
    k = 1
    while abs(a) > eps:
        a *= (-x ** 2) / factorial(2*k + 1)
        result += a
        k += 1
    return result
