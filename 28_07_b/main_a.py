import xml.etree.ElementTree as Et
from datetime import datetime
from wsgiref.simple_server import make_server
import cgi
from string import Template


OPTION = """<option>$pib</option>"""


class Vodiy:

    def __init__(self, data):
        self.data = data

    def open_routelist(self):
        tree = Et.parse(self.data)
        root = tree.getroot()
        routelist_node = root.find("routelist")
        result = []
        for route in routelist_node:
            name = route.text
            date = route.get("date")
            distance = route.get("distance")
            cort = (name, date, distance)
            result.append(cort)
        return result

    def count_payment(self, name, date1, date2):
        result = self.open_routelist()
        date1 = datetime.strptime(date1, "%d.%m.%y").date()
        date2 = datetime.strptime(date2, "%d.%m.%y").date()
        payment = 0
        tree = Et.parse(self.data)
        driver_node = tree.find("vodiy")
        for driver in driver_node:
            for cort in result:
                cort1, cort2, cort3 = cort
                cort2 = datetime.strptime(cort2, "%d.%m.%y").date()
                if name == cort1 and cort1 == driver.text:
                    if date1 <= cort2 <= date2:
                        payment += int(driver.get("weight")) * int(cort3)

        return payment

    def add_routelist(self, name, date, distance):
        dct = {"date": date, "distance": str(distance)}
        tree = Et.parse(self.data)
        routelist = tree.find("routelist")
        for i in routelist:
            if date == i.get("date") and name == i.text:
                return None
        new_name = Et.SubElement(routelist, "name", dct)
        new_name.text = str(name)
        tree.write(self.data, encoding="utf-8", xml_declaration=True)

    def add_vodiy(self, name, weight):
        drivers = []
        tree = Et.parse(self.data)
        root = tree.getroot()
        vodiy = root.find("vodiy")
        for driver in vodiy:
            drivers.append(driver.text)
        if name not in drivers:
            new_driver = Et.SubElement(vodiy, "name", weight=str(weight))
            new_driver.text = str(name)
            tree.write(self.data, encoding="utf-8", xml_declaration=True)

    def __call__(self, environ, start_response):
        path = environ["PATH_INFO"].lstrip("/")
        form = cgi.FieldStorage(fp=environ["wsgi.input"], environ=environ)
        status = "200 OK"
        headers = [("Content-type", "text/html; charset=utf-8")]
        template = "28_07_b/templates/drivers.html"
        # template_1 = "28_07_b/templates/count.html"
        params = {"drivers": ""}

        if path == "":
            try:
                driver = form.getfirst("name", "")
                weight = form.getfirst("weight", "")
                date = form.getfirst("date", "")
                distance = form.getfirst("distance", "")
                if driver and weight and date and distance:
                    self.add_vodiy(driver, weight)
                    self.add_routelist(driver, date, distance)
                    status = "303 SEE OTHER"
                    headers.append(("Location", "count_payment.xml"))
                    # template = "28_07_b/templates/count.html"
                elif driver or weight or date or distance:
                    status = "303 SEE OTHER"
                    headers.append(("Location", "/"))

            except ValueError:
                status = "303 SEE OTHER"
                headers.append(("Location", "/"))

        elif path == "count_payment.xml":
            tree = Et.parse(self.data)
            vodiy_node = tree.find("vodiy")
            for driver in vodiy_node:
                params["drivers"] += Template(OPTION).substitute(pib=driver.text)
            template = "28_07_b/templates/count.html"
            driver = form.getfirst("drive", "")
            date1 = form.getfirst("from", "")
            date2 = form.getfirst("to", "")

            if driver and date1 and date2:
                template = "28_07_b/data/result.xml"
                counted = self.count_payment(driver, date1, date2)
                root = Et.Element("data")
                for cort in self.open_routelist():
                    cort1, cort2, cort3 = cort
                    if cort1 == driver and date1 <= cort2 <= date2:
                        routelist = Et.Element("routelist", date=cort2)
                        routelist.text = str(driver)
                        root.append(routelist)
                        payment = Et.Element("payment")
                        payment.text = str(counted)
                        root.append(payment)
                        tree = Et.ElementTree(root)
                        tree.write("28_07_b/data/result.xml", encoding="utf-8", xml_declaration=True)

                headers[0] = ("Content-type", "application/json; charset=utf-8")

            elif date1 or date2:
                status = "303 SEE OTHER"
                headers.append(("Location", "count_payment.xml"))

        with open(template, encoding="utf-8") as file:
            page = Template(file.read()).substitute(params)
            start_response(status, headers)

        return [bytes(page, encoding="utf-8")]


HOST = ""
PORT = 8008


if __name__ == "__main__":
    app = Vodiy("28_07_b/data/data.xml")
    print(f"http://localhost:{PORT}")
    httpd = make_server(HOST, PORT, app)
    httpd.serve_forever()