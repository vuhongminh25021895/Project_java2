package Factory;

import Model.Art;
import Model.Item;

public class ItemFactory {
    public static Item createArt() {
        return new Art();
    }
}
