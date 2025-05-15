def multiply_brute_force(A: str, B: str) -> str:

    a = list(map(int, A[::-1]))
    b = list(map(int, B[::-1]))

    res = [0] * (len(a) + len(b))

    for i in range(len(a)):
        for j in range(len(b)):
            res[i + j] += a[i] * b[j]

            res[i + j + 1] += res[i + j] // 10
            res[i + j] %= 10

    while len(res) > 1 and res[-1] == 0:
        res.pop()

    return "".join(map(str, res[::-1]))


if __name__ == "__main__":
    with open("input.txt") as f:
        A, B = f.readline().split()

    result = multiply_brute_force(A, B)
    print(result)

#   url = r"https://eolymp.com/uk/problems/317"
