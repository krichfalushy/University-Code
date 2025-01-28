import json
from datetime import datetime
from wsgiref.simple_server import make_server
import cgi
from string import Template


OPTION = """<option>$pib</option>"""


class Vodiy:

    def __init__(self, vodiy, routelist):
        self.vodiy = vodiy
        self.routelist = routelist

    def open_routelist(self):
        with open(self.routelist, encoding="utf-8") as file:
            result = []
            lst = json.load(file)
            for dct in lst:
                name = dct["ПІБ"]
                date = dct["Дата"]
                distance = dct["Відстань (км)"]
                cort = (name, date, distance)
                result.append(cort)
        return result

    def count_payment(self, name, date1, date2):
        result = self.open_routelist()
        date1 = datetime.strptime(date1, "%d.%m.%y").date()
        date2 = datetime.strptime(date2, "%d.%m.%y").date()
        payment = 0
        with open(self.vodiy, encoding="utf-8") as file:
            lst = json.load(file)
            for dct in lst:
                for cort in result:
                    cort1, cort2, cort3 = cort
                    cort2 = datetime.strptime(cort2, "%d.%m.%y").date()
                    if name == cort1 and cort1 == dct["ПІБ"]:
                        if date1 <= cort2 <= date2:
                            payment += int(dct["Вантажопід'ємність (т)"]) * int(cort3)

        return payment

    def add_routelist(self, name, date, distance):
        addition = []
        info = {
            "ПІБ": name,
            "Дата": date,
            "Відстань (км)": distance
        }
        with open(self.routelist, "r", encoding="utf-8") as file:
            lst = self.open_routelist()
        with open(self.routelist, "w", encoding="utf-8") as file:
            for cort in lst:
                cort1, cort2, cort3 = cort
                if name == cort1 and date == cort2:
                    message = "Водій вже проїздив цього дня"
                    print(message)
                    break
                addition.append({
                    "ПІБ": cort1,
                    "Дата": cort2,
                    "Відстань (км)": cort3
                })
            addition.append(info)
            json.dump(addition, file, indent=2, ensure_ascii=False)

    def add_vodiy(self, name, weight):
        person = {
            "ПІБ": name,
            "Плата": 0,
            "Вантажопід'ємність (т)": weight
        }
        with open(self.vodiy, encoding="utf-8") as file:
            drivers = json.load(file)
        with open(self.vodiy, "w", encoding="utf-8") as file:
            d1, d2, d3 = drivers[-1].values()
            if name != d1:
                drivers.append(person)
            json.dump(drivers, file, indent=2, ensure_ascii=False)

    def __call__(self, environ, start_response):
        path = environ["PATH_INFO"].lstrip("/")
        form = cgi.FieldStorage(fp=environ["wsgi.input"], environ=environ)
        status = "200 OK"
        headers = [("Content-type", "text/html; charset=utf-8")]
        template = "/Users/user/PycharmProjects/pythonProject2/28_07_a/templates/drivers.html"
        # template_1 = "28_07_a/templates/count.html"
        params = {"drivers": ""}

        if path == "":
            driver = form.getfirst("name", "")
            weight = form.getfirst("weight", "")
            date = form.getfirst("date", "")
            distance = form.getfirst("distance", "")
            try:
                if driver and weight and date and distance:
                    self.add_vodiy(driver, weight)
                    self.add_routelist(driver, date, distance)
                    status = "303 SEE OTHER"
                    headers.append(("Location", "count_payment.json"))
                    # template = "28_07_a/templates/count.html"
                elif driver or weight or date or distance:
                    status = "303 SEE OTHER"
                    headers.append(("Location", "/"))
            except ValueError:
                status = "303 SEE OTHER"
                headers.append(("Location", "/"))

        elif path == "count_payment.json":
            with open(self.vodiy, encoding="utf-8") as file:
                lst = json.load(file)
            for dct in lst:
                params["drivers"] += Template(OPTION).substitute(pib=dct["ПІБ"])
            template = "/Users/user/PycharmProjects/pythonProject2/28_07_a/templates/count.html"
            driver = form.getfirst("drive", "")
            date1 = form.getfirst("from", "")
            date2 = form.getfirst("to", "")

            if driver and date1 and date2:
                info = []
                template = "/Users/user/PycharmProjects/pythonProject2/28_07_a/data/result.json"
                payment = self.count_payment(driver, date1, date2)
                for cort in self.open_routelist():
                    cort1, cort2, cort3 = cort
                    if cort1 == driver and date1 <= cort2 <= date2:
                        dct = {
                            "ПІБ": driver,
                            "Дата": cort2,
                        }
                        info.append(dct)
                info.append({"Плата": payment})

                with open(template, "w", encoding="utf-8") as file:
                    json.dump(info, file, indent=2, ensure_ascii=False)

                headers[0] = ("Content-type", "application/json; charset=utf-8")

            elif date1 or date2:
                status = "303 SEE OTHER"
                headers.append(("Location", "count_payment.json"))
            #
            # except ValueError:
            #     status = "303 SEE OTHER"
            #     headers.append(("Location", "count_payment.json"))

        with open(template, encoding="utf-8") as file:
            page = Template(file.read()).substitute(params)
            start_response(status, headers)

        return [bytes(page, encoding="utf-8")]


HOST = ""
PORT = 8008


if __name__ == "__main__":
    app = Vodiy("/Users/user/PycharmProjects/pythonProject2/28_07_a/data/vodiy.json", "/Users/user/PycharmProjects/pythonProject2/28_07_a/data/routelist.json")
    print(f"http://localhost:{PORT}")
    httpd = make_server(HOST, PORT, app)
    httpd.serve_forever()
