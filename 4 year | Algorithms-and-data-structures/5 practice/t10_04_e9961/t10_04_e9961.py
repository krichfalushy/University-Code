def generate_permutations(n, k):
    used = [False] * (n + 1)
    result = [0] * k

    def backtrack(pos):
        if pos == k:
            print(*result)
            return
        for i in range(1, n + 1):
            if not used[i]:
                used[i] = True
                result[pos] = i

                backtrack(pos + 1)

                used[i] = False

    backtrack(0)


if __name__ == "__main__":
    n, k = map(int, input().split())
    generate_permutations(n, k)

#    url = r"https://eolymp.com/uk/problems/9961"
    
