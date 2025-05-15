class Node:
    def __init__(self, expr, priority):
        self.expr = expr
        self.priority = priority


class Stack:
    def __init__(self):
        self._top = None

    def push(self, node):
        new_node = Node(node.expr, node.priority)
        new_node.next = self._top
        self._top = new_node

    def pop(self):
        if self._top is None:
            return None
        result = self._top
        self._top = self._top.next
        return result

    def empty(self):
        return self._top is None


def to_infix(expression):
    PRIORITY = {'+': 1, '-': 1, '*': 2, '/': 2}

    stack = Stack()

    for symbol in reversed(expression):
        if symbol.isalpha():
            stack.push(Node(symbol, 3))
        elif symbol in "+-*/":
            left = stack.pop()
            right = stack.pop()

            # Дужки потрібні, якщо пріоритет поточного оператора > пріоритет операнда
            left_expr = f'({left.expr})' if left.priority < PRIORITY[symbol] else left.expr
            right_expr = f'({right.expr})' if right.priority < PRIORITY[symbol] or \
                         (right.priority == PRIORITY[symbol] and symbol in '-/') else right.expr

            new_expr = f'{left_expr}{symbol}{right_expr}'
            stack.push(Node(new_expr, PRIORITY[symbol]))

    return stack.pop().expr


if __name__ == "__main__":
    with open("input.txt") as f:
        prefix_expr = f.readline().strip()
        result = to_infix(prefix_expr)
        print(result)
