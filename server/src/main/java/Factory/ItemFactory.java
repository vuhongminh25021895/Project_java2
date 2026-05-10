package Factory;

import Model.Art;
import Model.Electronics;
import Model.Item;
import Model.Vehicle;

public class ItemFactory {
    public static Art createArt() {
        return new Art();
    }

    public static Vehicle createVehicle() {
        return new Vehicle();
    }

    public static Electronics createElectronic() {
        return new Electronics();
    }
}
