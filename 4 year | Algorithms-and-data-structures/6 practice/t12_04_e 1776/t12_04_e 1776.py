import sys


class Node:
    def __init__(self, n):
        self.n = n
        self.next = None


class Stack:
    def __init__(self):
        self._top = None
        self._size = 0

    def push(self, n):
        node = Node(n)
        node.next = self._top
        self._top = node
        self._size += 1

    def pop(self):
        if self._top is None:
            return "error"
        else:
            n = self._top.n
            self._top = self._top.next
            self._size -= 1
            return n

    def back(self):
        if self._top is None:
            return "error"
        else:
            return self._top.n

    def size(self):
        return self._size

    def clear(self):
        self.__init__()

    def empty(self):
        return self._top is None


def can_achieve_permutation(n, target):
    stack = Stack()
    current = 1

    for number in target:
        while current <= n and (stack.empty() or stack.back() != number):
            stack.push(current)
            current += 1
        if stack.back() == number:
            stack.pop()
        else:
            return "No"
    return "Yes"


def main():
    input_lines = sys.stdin.read().splitlines()

    i = 0
    while i < len(input_lines):
        line = input_lines[i].strip()
        if line == "0":
            break
        n = int(line)
        i += 1
        while i < len(input_lines):
            perm_line = input_lines[i].strip()
            i += 1
            if perm_line == "0":
                print()
                break
            target = list(map(int, perm_line.split()))
            print(can_achieve_permutation(n, target))


if __name__ == "__main__":
    main()

#    url = r"https://eolymp.com/uk/problems/1776"
