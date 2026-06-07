/**
 * Абстрактна фабрика: створює кавову машину та сімейство напоїв
 * одного виробника.
 */
public interface CoffeeMachineFactory {
    CoffeeMachine createMachine();
    Beverage createEspresso();
    Beverage createCappuccino();
    Beverage createLatte();
    String getManufacturer();
}
