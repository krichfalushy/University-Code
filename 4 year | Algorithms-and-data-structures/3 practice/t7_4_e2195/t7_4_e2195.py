def read_input():
    N, M = map(int, input().split())
    vocab = []
    for _ in range(N):
        vocab.append(input().strip().lower())
    return N, M, vocab


def split_words(line):
    words = []
    word = ''
    for ch in line:
        if ch.isalpha():
            word += ch.lower()
        elif word:
            words.append(word)
            word = ''
    if word:
        words.append(word)
    return words


class HashTable:
    def __init__(self, size):
        self.size = size
        self.keys = [None] * self.size
        self.used = [0] * self.size

    def _hash(self, s):
        h = 0
        for ch in s:
            h = (h * 31 + ord(ch)) % self.size
        return h

    def insert(self, word):
        h = self._hash(word)
        while self.keys[h] is not None:
            if self.keys[h] == word:
                return
            h = (h + 1) % self.size
        self.keys[h] = word

    def find_index(self, word):
        h = self._hash(word)
        start = h
        while self.keys[h] is not None:
            if self.keys[h] == word:
                return h
            h = (h + 1) % self.size
            if h == start:
                break
        return -1

    def mark_used(self, idx):
        self.used[idx] = 1

    def all_used(self):
        for i in range(self.size):
            if self.keys[i] is not None and self.used[i] == 0:
                return False
        return True


def main():
    N, M, vocab = read_input()
    table_size = 2053  # просте число > 2 * N
    htable = HashTable(table_size)

    for word in vocab:
        htable.insert(word)

    for _ in range(M):
        line = input()
        words = split_words(line)
        for word in words:
            idx = htable.find_index(word)
            if idx == -1:
                print("Some words from the text are unknown.")
                return
            htable.mark_used(idx)

    if not htable.all_used():
        print("The usage of the vocabulary is not perfect.")
    else:
        print("Everything is going to be OK.")


if __name__ == "__main__":
    main()

#   url = r"https://eolymp.com/uk/problems/2195"
