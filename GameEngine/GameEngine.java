package Main.GameEngine;

import Main.model.World;
import Main.model.Location;
import Main.model.Inventory;
import Main.model.Connection;
import Main.model.GameObject;
import Main.model.Item;
import Main.model.NPC;
import java.util.List;

/**
 * The GameEngine class handles all player actions and game logic execution.
 * It maintains the current game state and processes commands such as
 * movement, item interaction, and NPC interaction.
 *
 * It acts as the main gameplay controller between the UI and the underlying model.
 * @author Arthur
 */

public class GameEngine {

    private GameState state;

    /**
     * Creates a new GameEngine using a world and a starting inventory.
     *
     * @param world the world data containing locations, connections, and game objects
     * @param startingInventory the player's initial inventory
     */
    public GameEngine(World world, Inventory startingInventory) {
        this(new GameState(world, world.getStartLocation(), startingInventory));
    }

    /**
     * Creates a GameEngine with a predefined game state.
     *
     * @param state an existing GameState instance
     */
    public GameEngine(GameState state) {
        this.state = state;
    }

    /**
     * Returns the current active game state.
     *
     * @return the GameState instance for this game
     */
    public GameState getState() {
        return state;
    }

    /**
     * Main command processor for player inputs.
     * Supported verbs: go, pick/pickup, drop, examine, talk, give, use.
     *
     * @param verb the command verb entered by the user
     * @param args additional command arguments (e.g., item names or NPC names)
     * @return result message and state flags wrapped in a CommandResult
     */
    public CommandResult processCommand(String verb, String... args) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        if (verb == null || verb.trim().isEmpty()) {
            return new CommandResult("No command provided.", false, false);
        }

        switch (verb.toLowerCase()) {
            case "go":
                if (args.length == 0) {
                    return new CommandResult("Go where?", false, false);
                }
                return go(args[0]);
            case "pickup":
            case "pick":
                if (args.length == 0) {
                    return new CommandResult("Pick up what?", false, false);
                }
                return pickUp(args[0]);
            case "drop":
                if (args.length == 0) {
                    return new CommandResult("Drop what?", false, false);
                }
                return drop(args[0]);
            case "examine":
                if (args.length == 0) {
                    return new CommandResult("Examine what?", false, false);
                }
                return examine(args[0]);
            case "talk":
                if (args.length == 0) {
                    return new CommandResult("Talk to whom?", false, false);
                }
                return talk(args[0]);
            case "give":
                if (args.length < 2) {
                    return new CommandResult("Give what to whom?", false, false);
                }
                return give(args[0], args[1]);
            case "use":
                if (args.length < 1) {
                    return new CommandResult("Use what?", false, false);
                }
                if (args.length == 1) {
                    return use(args[0], null);
                }
                return use(args[0], args[1]);
            default:
                return new CommandResult("Command '" + verb + "' is not implemented yet.", false, false);
        }
    }

    /**
     * Attempts to move the player to another location using a connection label.
     * Handles inaccessible areas, required items, and game completion logic.
     *
     * @param label the label identifying the connection to travel through (ex: "north")
     * @return command feedback result
     */
    public CommandResult go(String label) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        Location current = state.getCurrentLocation();
        Connection connection = current.getConnection(label);
        state.incrementTurn();
        if (connection == null) {
            return postTurnCheck(new CommandResult("You cannot go that way.", false, false));
        }
        World world = state.getWorld();
        Location destination = world.getLocation(connection.getTargetLocation());
        if (destination == null) {
            return postTurnCheck(new CommandResult("You try to go " + label + ", but something feels wrong.", false, false));
        }
        
        if (!destination.getAccessible()) {

            List<String> invNames = state.getInventory().invItemNames();

            //check required inventory items
            if (destination.getRequiredInv() != null &&
                !invNames.containsAll(destination.getRequiredInv())) {
                return postTurnCheck(new CommandResult(
                        "You need certain items before accessing " + label + ".", false, false));
            }

            //check required usable items (like Bobby Pin for Washroom)
            if (destination.getRequiredItems() != null &&
                !invNames.containsAll(destination.getRequiredItems())) {
                return postTurnCheck(new CommandResult(
                        "You need to use something first to unlock " + label + ".", false, false));
            }
            
            //this is for if we want items used when they are needed for areas
            if (destination.getRequiredItems() != null) {
                for (String used : destination.getRequiredItems()) {
                    state.getInventory().removeItem(used);
                }
            }

            
            destination.setAccessible(true);
        }
        
        state.setCurrentLocation(destination);
        if (world.isEndLocation(destination.getName())) {
            state.endGame(true);
            return new CommandResult(destination.getDescription() + "\n\nYou have reached your destination. Game over.", true, true);
        }
        String msg = "You go to " + destination.getName() + ".\n" + destination.getDescription();
        return postTurnCheck(new CommandResult(msg, false, false));
    }

    /**
     * Attempts to pick up an item from the current location.
     *
     * @param objectName the item name the player wishes to pick up
     * @return result explaining success or failure
     */
    public CommandResult pickUp(String objectName) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        Location current = state.getCurrentLocation();
        List<GameObject> objects = current.getObjects();
        GameObject found = findByName(objects, objectName);
        if (found == null) {
            return new CommandResult("You don't see that here.", false, false);
        }
        if (!found.isPickable()) {
            return new CommandResult("You can't pick that up.", false, false);
        }
        current.removeObject(found.getName());
        state.getInventory().addItem(found);
        state.incrementTurn();
        return postTurnCheck(new CommandResult("You pick up the " + found.getName() + ".", false, false));
    }
    /**
     * Drops an item from inventory into the current location.
     *
     * @param objectName the name of the item to drop
     * @return result explaining the drop outcome
     */
    public CommandResult drop(String objectName) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        Inventory inv = state.getInventory();
        GameObject item = inv.findItem(objectName);
        if (item == null) {
            return new CommandResult("You are not carrying that.", false, false);
        }
        if (!item.isDroppable()) {
            return new CommandResult("You can't drop that.", false, false);
        }
        inv.removeItem(item.getName());
        state.getCurrentLocation().addObject(item);
        state.incrementTurn();
        return postTurnCheck(new CommandResult("You drop the " + item.getName() + ".", false, false));
    }
    
    /**
     * Lists all items currently stored in the player's inventory.
     *
     * @return a formatted inventory listing
     */
    public CommandResult inventory() {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }

        var inv = state.getInventory();
        if (inv == null || inv.listItems().isEmpty()) {
            return new CommandResult("You have nothing in your inventory.", false, false);
        }

        StringBuilder sb = new StringBuilder("You are carrying:\n");
        inv.listItems().forEach(item -> sb.append(" - ").append(item.getName()).append("\n"));

        return postTurnCheck(new CommandResult(sb.toString(), false, false));
    }

    /**
     * Examines an object either in the inventory or the current location.
     * May reveal hidden items inside containers.
     *
     * @param name the name of the object to examine
     * @return result with the object's description or error message
     */
    public CommandResult examine(String name) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        Location current = state.getCurrentLocation();
        GameObject obj = state.getInventory().findItem(name);
        if (obj == null) {
            obj = findByName(current.getObjects(), name);
        }
        if (obj == null) {
            return new CommandResult("You don't see that here.", false, false);
        }
        state.incrementTurn();
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getDescription());
        List<GameObject> contained = obj.getContainedObjects();
        if (contained != null && !contained.isEmpty()) {
            for (GameObject hidden : contained) {
                current.addObject(hidden);
            }
            contained.clear();
            sb.append("\nYou discover something hidden!");
        }
        return postTurnCheck(new CommandResult(sb.toString(), false, false));
    }

    /**
     * Talks to an NPC in the current location if they exist.
     *
     * @param npcName the name of the NPC the player wishes to speak with
     * @return dialogue or error if NPC not present
     */
    public CommandResult talk(String npcName) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        Location current = state.getCurrentLocation();
        NPC npc = findNpcByName(current, npcName);
        if (npc == null) {
            return new CommandResult("There is no one by that name here.", false, false);
        }
        state.incrementTurn();
        String line = npc.talk();
        return postTurnCheck(new CommandResult(line, false, false));
    }

    /**
     * Attempts to give an item to an NPC. May trigger NPC responses and rewards.
     *
     * @param itemName the item to give
     * @param npcName the NPC to give the item to
     * @return result describing the outcome
     */
    public CommandResult give(String itemName, String npcName) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        Inventory inv = state.getInventory();
        GameObject item = inv.findItem(itemName);
        if (item == null) {
            return new CommandResult("You don't have that item.", false, false);
        }
        Location current = state.getCurrentLocation();
        NPC npc = findNpcByName(current, npcName);
        if (npc == null) {
            return new CommandResult("There is no one by that name here.", false, false);
        }
        List<String> wanted = npc.getWantedObjects();
        if (wanted != null) {
            for (String want : wanted) {
                if (want != null && want.equalsIgnoreCase(item.getName())) {
                    inv.removeItem(item.getName());
                    state.incrementTurn();
                    
                    StringBuilder msg = new StringBuilder();
                    msg.append(npc.getName()).append(" gladly accepts the ").append(item.getName()).append(".\n\n");
                    
                    // Get the next dialogue phrase (important dialogue)
                    String dialogue = npc.talk();
                    if (dialogue != null && !dialogue.equals(npc.getName() + " has nothing more to say.")) {
                        msg.append(dialogue);
                    }
                    
                    // Give items back if NPC has any
                    List<String> givenItems = npc.getGivenItems();
                    if (givenItems != null && !givenItems.isEmpty()) {
                        World world = state.getWorld();
                        for (String itemNameToGive : givenItems) {
                            GameObject itemToGive = findItemInWorld(world, itemNameToGive);
                            if (itemToGive != null) {
                                inv.addItem(itemToGive);
                                msg.append("\n\n").append(npc.getName()).append(" gives you ").append(itemToGive.getName()).append(".");
                            } else {
                                // Item not found in world or inventoryItems - this means JSON is misconfigured
                                System.err.println("WARNING: NPC " + npc.getName() + " tried to give item '" + itemNameToGive + 
                                    "' but it's not defined in inventoryItems or any location. Please add it to your JSON file.");
                                msg.append("\n\n").append(npc.getName()).append(" tries to give you ").append(itemNameToGive)
                                    .append(", but something went wrong...");
                            }
                        }
                    }
                    
                    return postTurnCheck(new CommandResult(msg.toString(), false, false));
                }
            }
        }
        return new CommandResult(npc.getName() + " does not seem interested in that.", false, false);
    }

    /**
     * Attempts to use an item on another object in the current location.
     * Currently placeholder logic for future rules.
     *
     * @param itemName the item to use
     * @param targetName optional object to use the item on (may be null)
     * @return result describing the interaction outcome
     */
    public CommandResult use(String itemName, String targetName) {
        if (state.isGameOver()) {
            return new CommandResult("The game is already over.", true, state.isWin());
        }
        Inventory inv = state.getInventory();
        GameObject item = inv.findItem(itemName);
        if (item == null) {
            item = findByName(state.getCurrentLocation().getObjects(), itemName);
        }
        if (item == null) {
            return new CommandResult("You don't have that, and it's not here.", false, false);
        }
        if (targetName == null) {
            state.incrementTurn();
            return postTurnCheck(new CommandResult("Nothing happens.", false, false));
        }
        Location current = state.getCurrentLocation();
        GameObject target = findByName(current.getObjects(), targetName);
        if (target == null) {
            return new CommandResult("You don't see that here.", false, false);
        }
        state.incrementTurn();
        return postTurnCheck(new CommandResult("You try to use " + item.getName() + " on " + target.getName() + ", but nothing special happens (TODO rules).", false, false));
    }

    /**
     * Helper method to find an object by name in a list of GameObjects.
     *
     * @param objects list of objects to search
     * @param name target name
     * @return matching GameObject or null if not found
     */
    private GameObject findByName(List<GameObject> objects, String name) {
        if (objects == null) {
            return null;
        }
        for (GameObject obj : objects) {
            if (obj.getName() != null && obj.getName().equalsIgnoreCase(name)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Finds an NPC with a matching name in a given location.
     *
     * @param location the current location to search
     * @param npcName the target NPC name
     * @return the NPC or null if not found
     */
    private NPC findNpcByName(Location location, String npcName) {
        if (location.getCharacters() == null) {
            return null;
        }
        for (NPC npc : location.getCharacters()) {
            if (npc.getName() != null && npc.getName().equalsIgnoreCase(npcName)) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Checks game-over conditions after any turn-based action.
     *
     * @param base the initial command result
     * @return same result if still playing, or override if turn limit reached
     */
    private CommandResult postTurnCheck(CommandResult base) {
        if (state.hasReachedTurnLimit() && !state.isGameOver()) {
            state.endGame(false);
            return new CommandResult("You ran out of time. Game over.", true, false);
        }
        return base;
    }
    
     /**
     * Searches for a named item in inventory items or locations in the world.
     * Removes and returns the object when found.
     *
     * @param world global world containing items and locations
     * @param itemName name of item to retrieve
     * @return the removed GameObject or null if not found
     */
    private GameObject findItemInWorld(World world, String itemName) {
        // First check inventory items (items that can be given by NPCs)
        if (world.getInventoryItems() != null) {
            GameObject found = findByName(world.getInventoryItems(), itemName);
            if (found != null) {
                // Remove from inventory items and return a copy
                world.getInventoryItems().remove(found);
                return found;
            }
        }
        
        // Then search all locations for the item
        for (Location loc : world.getLocations()) {
            if (loc.getObjects() != null) {
                GameObject found = findByName(loc.getObjects(), itemName);
                if (found != null) {
                    // Remove from location and return it
                    loc.removeObject(itemName);
                    return found;
                }
            }
        }
        return null;
    }
    
}
