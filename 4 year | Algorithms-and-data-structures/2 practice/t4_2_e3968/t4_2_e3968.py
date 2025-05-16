def sqrt(x):
    if x == 0:
        return 0
    left, right = (0, x) if x >= 1 else (0, 1)
    for _ in range(100):
        mid = (left + right) / 2
        if mid * mid < x:
            left = mid
        else:
            right = mid
    return (left + right) / 2


def solve(c):
    left = 0.0
    right = max(1.0, c)
    eps = 1e-7
    for _ in range(100):
        mid = (left + right) / 2
        f = mid * mid + sqrt(mid)
        if f < c:
            left = mid
        else:
            right = mid
    return (left + right) / 2


if __name__ == "__main__":
    c = float(input())
    x = solve(c)
    print(f"{x:.7f}")

#    url = r"https://basecamp.eolymp.com/uk/problems/3968"
