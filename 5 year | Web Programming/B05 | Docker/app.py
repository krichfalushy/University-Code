import mysql.connector
import os
import time


def get_db_connection():
    return mysql.connector.connect(
        host=os.environ.get('DB_HOST'),
        user=os.environ.get('MYSQL_USER'),
        password=os.environ.get('MYSQL_PASSWORD'),
        database=os.environ.get('MYSQL_DATABASE')
    )


def main():
    print("Очікування запуску MySQL...", flush=True)
    conn = None
    for i in range(10):
        try:
            conn = get_db_connection()
            print("Успішне підключення до бази даних!", flush=True)
            break
        except mysql.connector.Error:
            print(f"База ще не готова, спроба {i+1}/10...", flush=True)
            time.sleep(3)

    if not conn:
        print("Не вдалося підключитися до БД.", flush=True)
        return

    cursor = conn.cursor()

    # 1. Створення таблиці
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS students (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255),
            score INT
        )
    """)
    print("Таблиця 'students' перевірена/створена.", flush=True)

    # 2. Додавання записів
    sql = "INSERT INTO students (name, score) VALUES (%s, %s)"
    val = [
        ('Олександр', 95),
        ('Марія', 88),
        ('Дмитро', 92)
    ]
    cursor.executemany(sql, val)
    conn.commit()
    print(f"Додано {cursor.rowcount} записів.", flush=True)

    print("\n--- ВМІСТ ТАБЛИЦІ STUDENTS ---", flush=True)
    cursor.execute("SELECT * FROM students")
    rows = cursor.fetchall()
    for row in rows:
        print(row, flush=True)
    print("------------------------------\n", flush=True)

    cursor.close()
    conn.close()


if __name__ == "__main__":
    main()
