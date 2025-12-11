FROM python:3.9-slim

WORKDIR /app

# Встановлюємо конектор для MySQL
RUN pip install mysql-connector-python

COPY app.py .

CMD ["python", "app.py"]