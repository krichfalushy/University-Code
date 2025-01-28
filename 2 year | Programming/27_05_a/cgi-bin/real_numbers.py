#!/usr/bin/env python3
import cgi
# import html
from string import Template


def max_min(s):
    s = s.split(", ")
    result = []
    punct = " /\|'@£$%^&*()_-+="
    for i in s:
        if i.isalpha() or i in punct:
            return None
        else:
            i = float(i)
            result.append(i)
    return max(result), min(result)


form = cgi.FieldStorage()
string = form.getfirst("string", "")
sentence = f"From '{string}' max & min: {max_min(string)}"


with open("/27_05_a/result.html", encoding="utf-8") as file:
    if max_min(string) is None:
        sentence = f"From '{string}' max & min: {max_min(string)}"
        res = Template(file.read()).substitute(result=sentence, num_1="", sign="", num_2="")
    else:
        num_1, num_2 = max_min(string)
        sentence = f"From '{string}' max & min: "
        res = Template(file.read()).substitute(result=sentence, num_1=num_1, sign=", ", num_2=num_2)


print("Content-type: text/html; charset=utf-8\n")
print(res)
