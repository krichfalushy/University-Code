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
        return "ok"

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
            n = self._top.n
            return n

    def size(self):
        return self._size

    def clear(self):
        self.__init__()
        return "ok"

    def exit(self):
        return "bye"

    def main(self, command):
        method, *args = command.split()
        return getattr(self, method)(*args)


if __name__ == "__main__":
    input_data = open("input.txt")

    stack = Stack()
    for line in input_data:
        output = stack.main(line)
        print(output)
        if output == "bye":
            break

    input_data.close()

#    url = r"https://eolymp.com/uk/problems/6123"
