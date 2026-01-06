package Main.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the collection of {@link GameObject} items a player carries.
 * @author Makaato
 */
public class Inventory {

    private List<GameObject> items = new ArrayList<>();

    /**
     * Adds an item to the inventory.
     *
     * @param item the object to add
     */
    public void addItem(GameObject item) { items.add(item); }

    /**
     * Removes the first item with the given name (case-insensitive).
     *
     * @param name the item name to remove
     */
    public void removeItem(String name) {
        items.removeIf(i -> i.getName().equalsIgnoreCase(name));
    }

    /**
     * Returns the list of items in the inventory.
     *
     * @return mutable list of items
     */
    public List<GameObject> listItems() { return items; }

    /**
     * Returns a list of item names currently in the inventory.
     *
     * @return list of item names
     */
    public List<String> invItemNames() {
        List<String> itemNames = new ArrayList<>();
        for (GameObject obj : items) {
            itemNames.add(obj.getName());
        }
        return itemNames;
    }

    /**
     * Finds the first item with the given name (case-insensitive).
     *
     * @param name the item name to search for
     * @return the matching item, or {@code null} if not found
     */
    public GameObject findItem(String name) {
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
