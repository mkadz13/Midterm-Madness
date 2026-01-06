package Main.model;

/**
 * A pickable game object that represents an item.
 * @author Makaato
 */
public class Item extends GameObject {

    /**
     * Creates an item that can be picked up.
     */
    public Item() {
        this.setPickable(true);
    }

    /**
     * Returns a basic message for using this item.
     *
     * @return use text mentioning the item's name
     */
    public String use() {
        return "You use the " + getName() + ".";
    }
}
