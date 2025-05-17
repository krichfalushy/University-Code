class HashTable:
    def __init__(self, size):
        self.size = size
        self.table = [0] * size
        self.EMPTY = -1
        for i in range(size):
            self.table[i] = self.EMPTY

    def _hash(self, key):
        return key % self.size

    def insert(self, key):
        idx = self._hash(key)
        start_idx = idx
        while self.table[idx] != self.EMPTY:
            if self.table[idx] == key:
                return False
            idx = (idx + 1) % self.size
            if idx == start_idx:
                raise Exception("Hash table is full")
        self.table[idx] = key
        return True


def count_unique_numbers(numbers):
    table_size = 131071  # Просте число
    ht = HashTable(table_size)
    count = 0
    for number in numbers:
        if ht.insert(number):
            count += 1
    return count


def main():
    N = int(input())
    numbers = list(map(int, input().split()))
    print(count_unique_numbers(numbers))


if __name__ == "__main__":
    main()

#   url = r"https://eolymp.com/uk/problems/2797"
