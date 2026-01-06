package Main.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Stores all the static information about the world 
 * @author Terence
 */
public class World {
	/**
	 * Stores all in-game locations, 
	 * the player's starting location,
	 * rooms that triggers the end of the game, and
	 * the items in the players inventory
	 * + how many turns the player has to beat the game
	 */
    private List<Location> locations = new ArrayList<>();
    private String startLocation;
    private List<String> endLocations = new ArrayList<>();
    private int turnLimit;
    private List<GameObject> inventoryItems = new ArrayList<>(); 

    public World() {}
    /** 
     * Getters and setters for the world's attributes 
     * @return
     */
    public List<Location> getLocations() { return locations; }
    public void setLocations(List<Location> locations) { this.locations = locations; }
    
    public List<GameObject> getInventoryItems() { return inventoryItems; }
    public void setInventoryItems(List<GameObject> inventoryItems) { this.inventoryItems = inventoryItems; }

    public String getStartLocationName() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

    public List<String> getEndLocationNames() { return endLocations; }
    public void setEndLocations(List<String> endLocations) { this.endLocations = endLocations; }

    public int getTurnLimit() { return turnLimit; }
    public void setTurnLimit(int turnLimit) { this.turnLimit = turnLimit; }
    /**
     * Returns the location Object of the specified location
     * Used in the go method in GameEngine.java
     **/
    public Location getLocation(String name) {
        return locations.stream()
                .filter(l -> l.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    /** Gets player's starting position (the bedroom)
     * @return
     */
    public Location getStartLocation() {
        return getLocation(startLocation);
    }
    
    /**
     * Check if a location triggers the end of the game
     * @param name
     * @return
     */
    public boolean isEndLocation(String name) {
        return endLocations.stream()
                .anyMatch(n -> n.equalsIgnoreCase(name));
    }
}
