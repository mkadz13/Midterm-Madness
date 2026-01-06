package Main.model;

import java.util.ArrayList;
import java.util.List;
/** 
 * Represents the NPCs in the game that the player will have to interact with to complete the game
 * Stores all the information about them that is relevant to the game 
 * @author Terence
 */
public class NPC {
	/**
	 * Store and return their dialogue
	 * Track which objects they want in array lists  
	 * get image path in directory so they can be represented in game 
	 */
    private String name;
    private String description;
    private String imagePath;
    /** 
     * Getters and setters + talk function that cycles through the NPCs dialogue options until they run out
     */
    private List<String> phrases = new ArrayList<>();
    private List<String> wantedObjects = new ArrayList<>();
    private List<String> givenItems = new ArrayList<>(); 

    private int dialogueIndex = 0;

    public NPC() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getPhrases() { return phrases; }
    public void setPhrases(List<String> phrases) { this.phrases = phrases; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public List<String> getWantedObjects() { return wantedObjects; }
    public void setWantedObjects(List<String> wantedObjects) { this.wantedObjects = wantedObjects; }

    public List<String> getGivenItems() { return givenItems; }
    public void setGivenItems(List<String> givenItems) { this.givenItems = givenItems; }

    public String talk() {
        if (dialogueIndex < phrases.size()) {
            return phrases.get(dialogueIndex++);
        }
        return name + " has nothing more to say.";
    }
}
