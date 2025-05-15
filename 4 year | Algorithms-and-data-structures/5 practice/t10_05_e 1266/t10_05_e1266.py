def solve(N, tracks):
    best_sum = 0
    best_combination = []

    def backtrack(i, current_sum, current_tracks):
        nonlocal best_sum, best_combination

        if current_sum > N:
            return
        if current_sum > best_sum:
            best_sum = current_sum
            best_combination = current_tracks[:]

        if i >= len(tracks):
            return

        current_tracks.append(tracks[i])
        backtrack(i + 1, current_sum + tracks[i], current_tracks)
        current_tracks.pop()

        backtrack(i + 1, current_sum, current_tracks)

    backtrack(0, 0, [])

    return best_sum


if __name__ == "__main__":
    f = open("input.txt")
    for line in f:
        data = list(map(int, line.strip().split()))
        N = data[0]
        tracks = data[2:]
        print(f"sum:{solve(N, tracks)}")

#       url = r"https://eolymp.com/uk/problems/1266"
