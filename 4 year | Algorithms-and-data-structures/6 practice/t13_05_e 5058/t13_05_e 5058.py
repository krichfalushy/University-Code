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
            return None
        else:
            n = self._top.n
            self._top = self._top.next
            self._size -= 1
            return n

    def back(self):
        if self._top is None:
            return None
        else:
            return self._top.n

    def empty(self):
        return self._top is None


def brackets_checker(brackets_sequence):
    """
    Перевіряє чи brackets_sequence — правильна дужкова послідовність.
    :param brackets_sequence: рядок із дужками
    :return: "yes" або "no"
    """
    BRACKETS = {"(": ")", "[": "]", "{": "}"}
    stack = Stack()

    for bracket in brackets_sequence:
        if bracket in BRACKETS:
            stack.push(bracket)
        elif bracket in BRACKETS.values():
            top = stack.pop()
            if top is None or BRACKETS[top] != bracket:
                return "no"
        else:
            continue

    return "yes" if stack.empty() else "no"


if __name__ == "__main__":
    input_data = open("input.txt")

    for line in input_data:
        line = line.strip()
        if line:
            res = brackets_checker(line)
            print(res)

    input_data.close()
