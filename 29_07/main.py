import os
import sqlite3
from datetime import datetime
from wsgiref.simple_server import make_server
import cgi
from string import Template


OPTION = """<option>$pib</option>"""


class Vodiy:

    def __init__(self, db):
        self.db = db
        self.connection = sqlite3.connect(self.db)

    def __del__(self):
        self.connection.close()

    def open_routelist(self):
        curs = self.connection.cursor()
        curs.execute("""SELECT * FROM "routelists";""")
        return curs.fetchall()

    def count_payment(self, name, date1, date2):
        result = self.open_routelist()
        date1 = datetime.strptime(date1, "%d.%m.%y").date()
        date2 = datetime.strptime(date2, "%d.%m.%y").date()
        payment = 0
        curs = self.connection.cursor()
        query = """SELECT * FROM "drivers";"""
        curs.execute(query)
        drivers = curs.fetchall()
        for driver in drivers:
            d1, d2 = driver
            for cort in result:
                cort1, cort2, cort3 = cort
                cort2 = datetime.strptime(cort2, "%d.%m.%y").date()
                if name == cort1 and cort1 == d1:
                    if date1 <= cort2 <= date2:
                        payment += int(d2) * int(cort3)
        return payment

    def add_routelist(self, name, date, distance):
        info = (name, date, distance)
        routelist = self.open_routelist()
        for cort in routelist:
            cort1, cort2, cort3 = cort
            if date == cort2 and name == cort1:
                return None
        curs = self.connection.cursor()
        curs.execute(
            """INSERT INTO "routelists" ("name", "date", "distance")
            VALUES (?, ?, ?);""", info)

        self.connection.commit()
        curs.close()

    def add_vodiy(self, name, weight):
        curs = self.connection.cursor()
        try:
            curs.execute(
                """INSERT INTO "drivers" ("name", "weight")
                VALUES (?, ?);""", (name, weight))
            self.connection.commit()
        except sqlite3.IntegrityError:
            return None
        finally:
            curs.close()

    def delete_route(self, name, date):
        curs = self.connection.cursor()
        curs.execute(
            """DELETE FROM "routelists" WHERE "name"=? AND "date"=?;""",
            (name, date)
        )
        self.connection.commit()

    def change_route_date(self, name, date, new_date):
        curs = self.connection.cursor()
        curs.execute(
            """UPDATE "routelists" SET "date"=? WHERE "name"=? AND "date"=?;""",
            (new_date, name, date)
        )
        self.connection.commit()

    def change_route_distance(self, name, date, distance, new_distance):
        curs = self.connection.cursor()
        curs.execute(
            """UPDATE "routelists" SET "distance"=? WHERE "name"=? AND "date"=? AND "distance"=?;""",
            (new_distance, name, date, distance)
        )
        self.connection.commit()

    def __call__(self, environ, start_response):
        path = environ["PATH_INFO"].lstrip("/")
        form = cgi.FieldStorage(fp=environ["wsgi.input"], environ=environ)
        status = "200 OK"
        headers = [("Content-type", "text/html; charset=utf-8")]
        template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/drivers.html"
        params = {"drivers": "", "result": "", "date": "", "distance": "", "result1": ""}

        if path == "":
            curs = self.connection.cursor()
            routes = curs.execute("""SELECT * FROM "routelists";""")
            try:
                driver = form.getfirst("name", "")
                weight = form.getfirst("weight", "")
                date = form.getfirst("date", "")
                distance = form.getfirst("distance", "")

                driver1 = form.getfirst("drive", "")
                date1 = form.getfirst("date1", "")
                date2 = form.getfirst("date2", "")
                distance1 = form.getfirst("distance1", "")
                distance2 = form.getfirst("distance2", "")

                driver3 = form.getfirst("drive1", "")
                date3 = form.getfirst("date3", "")
                for r in routes:
                    params["drivers"] += Template(OPTION).substitute(pib=r[0])
                    params["date"] += Template(OPTION).substitute(pib=r[1])
                    params["distance"] += Template(OPTION).substitute(pib=r[2])

                if driver and weight:
                    self.add_vodiy(driver, weight)
                    params["result1"] = f"Водія було додано"
                    # status = "303 SEE OTHER"
                    # headers.append(("Location", "count_payment"))
                    template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"

                elif driver and date and distance:
                    self.add_routelist(driver, date, distance)
                    params["result1"] = f"Маршрутний лист водія було додано"
                    # status = "303 SEE OTHER"
                    # headers.append(("Location", "count_payment"))
                    template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"

                elif driver and weight and date and distance:
                    self.add_vodiy(driver, weight)
                    self.add_routelist(driver, date, distance)
                    template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"

                elif driver1 and date1 and date2:
                    lst = self.open_routelist()
                    for i in lst:
                        if i[0] == driver1 and i[1] == date1:
                            self.change_route_date(driver1, date1, date2)
                            params["result1"] = f"Дата {date1} змінена на {date2}"
                        elif i[0] == driver1 and i[1] != date1:
                            params["result1"] = f"У цього водія немає такої дати"
                    template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"

                elif driver1 and date1 and distance1 and distance2:
                    lst = self.open_routelist()
                    for i in lst:
                        if i[0] == driver1 and i[1] == date1 and str(i[2]) == distance1:
                            self.change_route_distance(driver1, date1, distance1, distance2)
                            params["result1"] = f"Відстань {distance1} за {date1} змінена на {distance2}"
                        elif i[0] == driver1 and i[1] == date1 and str(i[2]) != distance1:
                            params["result1"] = f"Цей водій проїхав іншу кількість кілометрів"
                        elif i[0] == driver1 and i[1] != date1 and str(i[2]) == distance1:
                            params["result1"] = f"Цей водій проїхав іншого дня"
                    template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"

                elif driver1 and date1 and date2 and distance1 and distance2:
                    lst = self.open_routelist()
                    for i in lst:
                        if i[0] == driver1 and i[1] == date1 and str(i[2]) == distance1:
                            self.change_route_date(driver1, date1, date2)
                            self.change_route_distance(driver1, date1, distance1, distance2)
                            params["result1"] = f"'{date1}' і '{distance1} км' змінено на '{date2}' і '{distance2} км'"
                        elif i[0] == driver1 and i[1] != date1 and str(i[2]) == distance1:
                            params["result1"] = "Для редагування даних були введені некоректні значення"
                        elif i[0] == driver1 and i[1] == date1 and str(i[2]) != distance1:
                            params["result1"] = "Для редагування даних були введені некоректні значення"
                        elif i[0] != driver1 and i[1] == date1 and str(i[2]) == distance1:
                            params["result1"] = "Для редагування даних були введені некоректні значення"
                    template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"

                elif driver3 and date3:
                    self.delete_route(driver3, date3)
                    lst = self.open_routelist()
                    for i in lst:
                        if i[0] == driver3 and i[1] == date3:
                            params["result1"] = f"Маршрут водія {driver3} за {date3} був видалений"
                        elif i[0] == driver3 and i[1] != date3:
                            params["result1"] = f"У цього водія немає маршруту за цю дату"
                    template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"
                    # status = "303 SEE OTHER"
                    # headers.append(("Location", "count_payment"))

                elif driver or weight or date or distance or driver1 or date1 or distance1 or driver3:
                    status = "303 SEE OTHER"
                    headers.append(("Location", "/"))

            except ValueError:
                status = "303 SEE OTHER"
                headers.append(("Location", "/"))

        elif path == "count_payment":
            curs = self.connection.cursor()
            drivers = curs.execute("""SELECT * FROM "drivers";""")
            for driver in drivers:
                params["drivers"] += Template(OPTION).substitute(pib=driver[0])
            template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/count.html"
            driver = form.getfirst("drive", "")
            date1 = form.getfirst("from", "")
            date2 = form.getfirst("to", "")

            if driver and date1 and date2:
                counted = self.count_payment(driver, date1, date2)
                params["result"] = counted
                template = "/Users/user/PycharmProjects/pythonProject2/29_07/templates/result.html"
                headers[0] = ("Content-type", "text/html; charset=utf-8")

            elif date1 or date2:
                status = "303 SEE OTHER"
                headers.append(("Location", "count_payment"))

        with open(template, encoding="utf-8") as file:
            page = Template(file.read()).substitute(params)
            start_response(status, headers)

        return [bytes(page, encoding="utf-8")]


DICT = {
    "drivers": [
        ['Ігорович Василь Павлович', 4],
        ['Шухевич Віктор Сергійович', 3],
        ['Семенко Іван Степанович', 5],
        ['Огієнко Микола Миколайович', 25]],
    "routelists": [
        ['Ігорович Василь Павлович', "21.02.23", 20],
        ['Шухевич Віктор Сергійович', "19.03.23", 10],
        ['Ігорович Василь Павлович', "20.04.23", 15],
        ['Семенко Іван Степанович', "10.05.23", 50],
        ['Огієнко Микола Миколайович', "23.05.23", 15],
        ['Шухевич Віктор Сергійович', "14.05.23", 30]]
    }


def restore_db(db):
    connection = sqlite3.connect(db)
    curs = connection.cursor()
    if os.path.isfile(db):
        pass
    else:
        curs.execute(
            """
            CREATE TABLE "drivers" (
                "name"	TEXT NOT NULL UNIQUE,
                "weight"	INTEGER NOT NULL CHECK("weight">0),
                PRIMARY KEY("name")
            );
            """)
        curs.execute(
            """
            CREATE TABLE "routelists" (
                "name"	TEXT NOT NULL,
                "date"	TEXT NOT NULL CHECK(LENGTH("date") = 8),
                "distance"	INTEGER NOT NULL,
                FOREIGN KEY("name") REFERENCES "drivers"("name")
            );
            """)
        curs.executemany(
            """INSERT INTO "drivers" VALUES (?, ?);""", DICT["drivers"])
        curs.executemany(
            """INSERT INTO "routelists" VALUES (?, ?, ?);""", DICT["routelists"])

    connection.commit()
    connection.close()


HOST = ""
PORT = 8008


if __name__ == "__main__":
    db_file = "/homeworks/29_07/data/data.db"
    # xl_file = "/Users/user/PycharmProjects/pythonProject2/29_07/data/vodiy.xlsx"
    restore_db(db_file)
    app = Vodiy(db_file)
    print(f"http://localhost:{PORT}")
    httpd = make_server(HOST, PORT, app)
    httpd.serve_forever()