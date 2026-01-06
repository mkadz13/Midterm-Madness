/**
 *Author Makaato Serumaga
 * A place in the game world with objects, characters, and connections.
 */
public class Location {

    private String name;
    private String description;
    private String imagePath;

    // Access control and puzzle requirements
    private boolean accessible;
    private List<String> requiredItems = new ArrayList<>();
    private List<String> requiredInv = new ArrayList<>();

    private List<Connection> connections = new ArrayList<>();
    private List<GameObject> objects = new ArrayList<>();
    private List<NPC> characters = new ArrayList<>();

    /** Creates an empty location. */
    public Location() {}

    /** @return the location name. */
    public String getName() { return name; }

    /** @param name new location name. */
    public void setName(String name) { this.name = name; }

    /** @return the location description. */
    public String getDescription() { return description; }

    /** @param description new description text. */
    public void setDescription(String description) { this.description = description; }

    /** @return path to the location image. */
    public String getImagePath() { return imagePath; }

    /** @param imagePath resource path to the image. */
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    /** @return true if the location is currently accessible. */
    public boolean getAccessible() { return accessible; }

    /** @param accessible whether the location can be entered. */
    public void setAccessible(boolean accessible) { this.accessible = accessible; }

    /** @return item names required to enter this location. */
    public List<String> getRequiredItems() { return requiredItems; }

    /** @param requiredItems new list of required item names. */
    public void setRequiredItems(List<String> requiredItems) { this.requiredItems = requiredItems; }

    /** @return inventory item names that must be held to enter. */
    public List<String> getRequiredInv() { return requiredInv; }

    /** @param requiredInv new list of required inventory names. */
    public void setRequiredInv(List<String> requiredInv) { this.requiredInv = requiredInv; }

    /** @return outgoing connections. */
    public List<Connection> getConnections() { return connections; }

    /** @param connections new connections list. */
    public void setConnections(List<Connection> connections) { this.connections = connections; }

    /** @return objects currently in this location. */
    public List<GameObject> getObjects() { return objects; }

    /** @param objects new list of objects in this location. */
    public void setObjects(List<GameObject> objects) { this.objects = objects; }

    /** @return characters present in this location. */
    public List<NPC> getCharacters() { return characters; }

    /** @param characters new list of characters in this location. */
    public void setCharacters(List<NPC> characters) { this.characters = characters; }

    /** Adds an object to this location. */
    public void addObject(GameObject obj) { objects.add(obj); }

    /** Removes an object with the given name from this location. */
    public void removeObject(String name) {
        objects.removeIf(o -> o.getName().equalsIgnoreCase(name));
    }

    /**
     * Finds a connection by its label (case-insensitive).
     *
     * @param label label shown to the player (e.g., "Hallway").
     * @return matching connection, or null if none.
     */
    public Connection getConnection(String label) {
        return connections.stream()
                .filter(c -> c.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .orElse(null);
    }
}
