package app.server.factory;

import app.server.enums.ItemCategory;
import app.server.model.*;

public class ItemFactory {

    public static Item createItem(ItemCategory category) {

        return switch (category) {

            case ELECTRONICS -> new Electronics();

            case ART -> new Art();

            case VEHICLE -> new Vehicle();
        };
    }
}