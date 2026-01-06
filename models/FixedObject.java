package Main.model;

/**
 * Author : Makaato Serumaga
 * A non-pickable game object that is fixed in place.
 */
public class FixedObject extends GameObject {

    /**
     * Creates a fixed object that cannot be picked up.
     */
    public FixedObject() {
        this.setPickable(false);
    }

    /**
     * Returns a basic interaction message for this object.
     *
     * @return interaction text mentioning the object's name
     */
    public String interact() {
        return "You interact with the " + getName() + ".";
    }
}
