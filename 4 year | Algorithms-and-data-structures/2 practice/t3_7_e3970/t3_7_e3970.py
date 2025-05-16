def lower_bound(array, x):
    left = 0
    right = len(array)
    while left < right:
        mid = (left + right) // 2
        if array[mid] < x:
            left = mid + 1
        else:
            right = mid
    return left


def upper_bound(array, x):
    left = 0
    right = len(array)
    while left < right:
        mid = (left + right) // 2
        if array[mid] <= x:
            left = mid + 1
        else:
            right = mid
    return left


if __name__ == "__main__":
    n = int(input())
    if n > 0:
        colors = list(map(int, input().split()))
    else:
        colors = []

    m = int(input())
    queries = list(map(int, input().split()))

    for q in queries:
        count = upper_bound(colors, q) - lower_bound(colors, q)
        print(count)

#    url = r"https://eolymp.com/uk/problems/3970"
