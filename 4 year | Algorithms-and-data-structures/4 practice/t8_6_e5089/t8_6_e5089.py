def selection_sort(array):
    n = len(array)
    for i in range(n - 1, 0, -1):
        # реалізуємо пошук найбільшого елементу
        maxpos = 0
        for j in range(1, i + 1):
            if array[maxpos] < array[j]:
                maxpos = j
        # Міняємо місцями поточний і найбільший елемент
        array[i], array[maxpos] = array[maxpos], array[i]
    return array


if __name__ == "__main__":
    n = int(input())
    arrays = [input().strip() for _ in range(n)]

    sorted_arrays = selection_sort(arrays)
    for array in sorted_arrays:
        print(array)

#   url = r"https://eolymp.com/uk/problems/5089"
