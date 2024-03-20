import os
import sqlite3


DICT = {}


def add_obj():
    print("Введіть вид, адресу, заг. площу, к-сть кімнат об'єкта через кому з пробілом: ")
    data = input()
    spysok1 = data.split(", ")
    DICT.update({"Лист1": spysok1})
    return DICT


def add_room():
    print("Введіть назву об'єкта, призначення та площу кімнати через кому з пробілом: ")
    data = input()
    spysok2 = data.split(", ")
    DICT.update({"Лист2": spysok2})
    return DICT


def to_database(db):
    if os.path.isfile(db):
        connection = sqlite3.connect(db)
        curs = connection.cursor()
        data_1 = add_obj()
        curs.execute("""
        INSERT INTO "Лист1"
            ("species", "address", "sum_square", "room_amount")
            VALUES (?, ?, ?, ?);
            """, data_1["Лист1"])
        data_2 = add_room()
        curs.execute("""
        INSERT INTO "Лист2"
            ("lyst1_id", "purpose", "room_square")
            VALUES (?, ?, ?);
        """, data_2["Лист2"])
        connection.commit()
        connection.close()

    else:
        connection = sqlite3.connect(db)
        curs = connection.cursor()

        curs.execute("""
        CREATE TABLE "Лист1" (
            "id"	INTEGER NOT NULL,
            "species"	TEXT,
            "address"	TEXT,
            "sum_square"	TEXT,
            "room_amount"	INTEGER,
            PRIMARY KEY("id" AUTOINCREMENT)
        ); """)

        curs.execute("""
        CREATE TABLE "Лист2" (
            "lyst1_id"	TEXT,
            "purpose"	TEXT,
            "room_square"	TEXT,
            FOREIGN KEY("lyst1_id") REFERENCES "Лист1"("species")
        ); """)

        connection.commit()
        connection.close()


if __name__ == "__main__":
    db_name = "new2901.db"
    to_database(db_name)
