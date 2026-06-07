/**
 * Реалізатор (Implementor) мосту — спосіб приготування напою
 * (що додаємо: гарячу воду чи молоко) та його внесок у вартість.
 */
public interface Preparation {
    void addLiquid();          // додати рідину (вода/молоко)
    int extraCost();           // надбавка до вартості
    String description();      // короткий опис варіанту
}
