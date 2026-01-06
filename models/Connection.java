package Main.model;
/**
 * Represents the pathways between the rooms in the game, which restrict the player from being able to move into any adjacent rooms
 * This helps simulate a realistic house and enrich the consumer's gaming experience 
 * @author Terence
 */
public class Connection {
    /**
     * label stores the name of the connection  
     * tagretLocation stores where the connection leads to
     */
    private String label;
    private String targetLocation;

    public Connection() {}
    /**
     * Getters and setters
     * @return
     */
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getTargetLocation() { return targetLocation; }
    public void setTargetLocation(String targetLocation) { this.targetLocation = targetLocation; }
}
