/**
 * ВАРІАНТ 3 — знімок із підвищеним захистом.
 * Memento — окремий клас, але доступ до його стану обмежено package-private
 * методами. Тож лише класи того ж пакету (originator Triangle) можуть читати
 * стан, а зовнішній код — ні. Публічно доступні лише безпечні дані (опис).
 */
public class Memento {
    // package-private поля: видимі Triangle, але не зовнішньому коду інших пакетів
    final float a, b, c;

    Memento(float a, float b, float c) {
        this.a = a; this.b = b; this.c = c;
    }

    /** Безпечна публічна інформація без розкриття внутрішнього стану. */
    public String getDescription() {
        return "Memento of a triangle";
    }
}
