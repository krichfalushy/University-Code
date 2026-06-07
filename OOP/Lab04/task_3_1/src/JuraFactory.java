public class JuraFactory implements CoffeeMachineFactory {
    public CoffeeMachine createMachine()  { return new JuraMachine(); }
    public Beverage createEspresso()      { return new JuraEspresso(); }
    public Beverage createCappuccino()    { return new JuraCappuccino(); }
    public Beverage createLatte()         { return new JuraLatte(); }
    public String getManufacturer()       { return "Jura"; }
}
