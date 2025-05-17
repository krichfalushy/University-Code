def insertion_sort(array):
    """
    Реалізує алгоритм сортування вставкою з виводом після кожної вставки
    :param array: Масив (список однотипових елементів)
    :return: None
    """
    n = len(array)
    for index in range(1, n):
        currentValue = array[index]  # запам’ятовуємо елемент
        position = index  # та його позицію
        original_position = index  # для перевірки зміни

        # пошук позиції для вставки поточного елемента
        while position > 0 and array[position - 1] > currentValue:
            # зсув елементу масиву вправо
            array[position] = array[position - 1]
            position -= 1

        # Вставка поточного елемента у знайдену позицію
        array[position] = currentValue

        # Виводимо масив лише якщо була зміна
        if position != original_position:
            print(*array)


if __name__ == "__main__":
    n = int(input())
    arr = list(map(int, input().split()))

    insertion_sort(arr)

#   url = r"https://eolymp.com/uk/problems/2664"
