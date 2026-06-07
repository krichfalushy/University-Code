public class DeLonghiFactory implements CoffeeMachineFactory {
    public CoffeeMachine createMachine()  { return new DeLonghiMachine(); }
    public Beverage createEspresso()      { return new DeLonghiEspresso(); }
    public Beverage createCappuccino()    { return new DeLonghiCappuccino(); }
    public Beverage createLatte()         { return new DeLonghiLatte(); }
    public String getManufacturer()       { return "DeLonghi"; }
}
