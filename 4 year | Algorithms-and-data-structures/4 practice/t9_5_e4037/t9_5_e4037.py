def merge_sort(array):
    """ Реалізує алгоритм сортування злиттям
    :param array: Масив (список кортежів: (основний_номер, допоміжний_номер, індекс_введення))
    :return: None (сортує array in-place)
    """
    if len(array) > 1:
        mid = len(array) // 2
        lefthalf = array[:mid]
        righthalf = array[mid:]

        # Рекурсивний виклик
        merge_sort(lefthalf)
        merge_sort(righthalf)

        # Злиття
        i = 0
        j = 0
        k = 0
        while i < len(lefthalf) and j < len(righthalf):
            # Порівнюємо за основним номером, а якщо рівні — зберігаємо порядок введення (по індексу)
            if lefthalf[i][0] < righthalf[j][0]:
                array[k] = lefthalf[i]
                i += 1
            elif lefthalf[i][0] == righthalf[j][0] and lefthalf[i][2] < righthalf[j][2]:
                array[k] = lefthalf[i]
                i += 1
            else:
                array[k] = righthalf[j]
                j += 1
            k += 1

        while i < len(lefthalf):
            array[k] = lefthalf[i]
            i += 1
            k += 1

        while j < len(righthalf):
            array[k] = righthalf[j]
            j += 1
            k += 1


if __name__ == "__main__":
    n = int(input())
    robots = []

    for idx in range(n):
        main, aux = map(int, input().split())
        robots.append((main, aux, idx))

    # Сортування
    merge_sort(robots)

    for robot in robots:
        print(robot[0], robot[1])

#   url = r"https://eolymp.com/uk/problems/4037"
