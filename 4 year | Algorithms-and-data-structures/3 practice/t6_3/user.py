# user.py

# Розмір хеш-таблиці (бажано просте число, трохи більше за очікувану кількість книг)
TABLE_SIZE = 2003

# Хеш-таблиця
table = []


def init():
    """ Викликається 1 раз на початку виконання програми. """
    global table
    table = [None] * TABLE_SIZE  # ініціалізація порожньої таблиці


def _hash(author, title):
    """Обчислює хеш для пари (author, title)."""
    key = author + '#' + title
    h = 0
    for c in key:
        h = (h * 31 + ord(c)) % TABLE_SIZE
    return h


def _probe(idx, i):
    """Лінійне пробування: зміщення i."""
    return (idx + i) % TABLE_SIZE


def addBook(author, title):
    """ Додає книгу до бібліотеки. """
    global table
    idx = _hash(author, title)
    for i in range(TABLE_SIZE):
        pos = _probe(idx, i)
        if table[pos] is None:
            table[pos] = (author, title)
            return
        if table[pos] == (author, title):
            return  # уже існує


def find(author, title):
    """ Перевіряє, чи міститься задана книга у бібліотеці. """
    global table
    idx = _hash(author, title)
    for i in range(TABLE_SIZE):
        pos = _probe(idx, i)
        if table[pos] is None:
            return False
        if table[pos] == (author, title):
            return True
    return False


def delete(author, title):
    """ Видаляє книгу з бібліотеки. """
    global table
    idx = _hash(author, title)
    for i in range(TABLE_SIZE):
        pos = _probe(idx, i)
        if table[pos] is None:
            return
        if table[pos] == (author, title):
            table[pos] = None
            # Перехешування елементів після видалення
            j = _probe(pos, 1)
            while table[j] is not None:
                rehash_entry = table[j]
                table[j] = None
                addBook(*rehash_entry)
                j = _probe(j, 1)
            return


def findByAuthor(author):
    """ Повертає список книг заданого автора у алфавітному порядку. """
    global table
    result = []
    for entry in table:
        if entry is not None and entry[0] == author:
            result.append(entry[1])
    result.sort()
    return result
