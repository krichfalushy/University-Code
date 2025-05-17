def bubble_sort(array):
    n = len(array)
    swap_count = 0
    for i in range(n - 1, 0, -1):
        for j in range(i):
            if array[j] > array[j + 1]:
                array[j], array[j + 1] = array[j + 1], array[j]
                swap_count += 1
    return swap_count


if __name__ == "__main__":
    n = int(input())
    array = list(map(int, input().split()))

    print(bubble_sort(array))

#   url = r"https://eolymp.com/uk/problems/2663"
