def quick_sort(array):
    if len(array) <= 1:
        return array
    pivot = array[len(array) // 2]  # опорний елемент
    left = [x for x in array if x < pivot]
    middle = [x for x in array if x == pivot]
    right = [x for x in array if x > pivot]
    return quick_sort(left) + middle + quick_sort(right)


if __name__ == "__main__":
    n = int(input())
    array = list(map(int, input().split()))

    sorted_array = quick_sort(array)
    print(*sorted_array)

#   url = r"https://eolymp.com/uk/problems/2321"
