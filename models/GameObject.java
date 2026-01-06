package Main.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for objects in the game world
 * (items, fixed objects, containers, etc.).
 * @author Makaato
 */
public class GameObject {

    private String name;
    private String description;
    private String imagePath;
    private List<String> attributes = new ArrayList<>();
    private List<GameObject> containedObjects = new ArrayList<>();
    private boolean pickable;
    private boolean selectable;
    private boolean examinable;

    private boolean droppable = true;

    /**
     * Returns whether this object can be dropped.
     */
    public boolean isDroppable() { return droppable; }

    /**
     * Sets whether this object can be dropped.
     *
     * @param droppable true if the object may be dropped
     */
    public void setDroppable(boolean droppable) { this.droppable = droppable; }

    /**
     * Creates an empty game object.
     */
    public GameObject() {}

    /**
     * Returns the object's name.
     */
    public String getName() { return name; }

    /**
     * Sets the object's name.
     *
     * @param name the display name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the object's description.
     */
    public String getDescription() { return description; }

    /**
     * Sets the object's description text.
     *
     * @param description description to show to the player
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the image resource path for this object.
     */
    public String getImagePath() { return imagePath; }

    /**
     * Sets the image resource path for this object.
     *
     * @param imagePath classpath or relative image path
     */
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    /**
     * Returns attribute tags associated with this object.
     */
    public List<String> getAttributes() { return attributes; }

    /**
     * Replaces the object's attribute list.
     *
     * @param attributes new list of attribute strings
     */
    public void setAttributes(List<String> attributes) { this.attributes = attributes; }

    /**
     * Returns objects contained inside this object.
     */
    public List<GameObject> getContainedObjects() { return containedObjects; }

    /**
     * Replaces the list of contained objects.
     *
     * @param containedObjects new contained objects
     */
    public void setContainedObjects(List<GameObject> containedObjects) {
        this.containedObjects = containedObjects;
    }

    /**
     * Returns whether this object can be picked up.
     */
    public boolean isPickable() { return pickable; }

    /**
     * Sets whether this object can be picked up.
     *
     * @param pickable true if the object can be picked up
     */
    public void setPickable(boolean pickable) { this.pickable = pickable; }

    /**
     * Returns whether this object can be selected in the UI.
     */
    public boolean isSelectable() { return selectable; }

    /**
     * Sets whether this object can be selected in the UI.
     *
     * @param selectable true if the object is selectable
     */
    public void setSelectable(boolean selectable) { this.selectable = selectable; }

    /**
     * Returns whether this object can be examined.
     */
    public boolean isExaminable() { return examinable; }

    /**
     * Sets whether this object can be examined.
     *
     * @param examinable true if the object is examinable
     */
    public void setExaminable(boolean examinable) { this.examinable = examinable; }

    /**
     * Returns a description of this object.
     *
     * @return descriptive text
     */
    public String describe() { return description; }

    /**
     * Adds a contained object inside this object.
     *
     * @param obj the object to add
     */
    public void addObject(GameObject obj) {
        containedObjects.add(obj);
    }
}
