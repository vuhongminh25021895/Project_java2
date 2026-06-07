package Server.Factory;

import Server.Enums.ItemCategory;
import Server.Model.Art;
import Server.Model.Electronics;
import Server.Model.Item;
import Server.Model.Vehicle;

public class ItemFactory {
    public static Item createItem(ItemCategory category) {

        return switch (category) {

            case ELECTRONICS -> new Electronics();

            case ART -> new Art();

            case VEHICLE -> new Vehicle();
        };
    }
}
