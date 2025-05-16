def binary_search(array, x):
    left = 0
    right = len(array) - 1
    while left <= right:
        mid = (left + right) // 2
        if array[mid] == x:
            return True
        elif array[mid] < x:
            left = mid + 1
        else:
            right = mid - 1
    return False


n = int(input())
collection = list(map(int, input().split()))

m = int(input())
queries = list(map(int, input().split()))


for q in queries:
    if binary_search(collection, q):
        print("YES")
    else:
        print("NO")


# url = r"https://basecamp.eolymp.com/uk/problems/3966"
