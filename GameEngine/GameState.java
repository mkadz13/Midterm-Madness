package Main.GameEngine;

import Main.model.World;
import Main.model.Location;
import Main.model.Inventory;
/**
 * Represents what is currently going on in the game at any given moment
 * It's functions include:
 * Creating a fresh state when a new game begins
 * Tracking player progress
 * Managing the inventory
 * Checking if the game should end 
 * @author Terence 
 */
public class GameState {

    private World world;
    private Location currentLocation;
    private Inventory inventory;

    private int turnCount;
    private boolean gameOver;
    private boolean win;
    
    /** 
     * Constructor initializes a new GameState at the start of a new game 
     * @param world
     * @param startingLocation
     * @param startingInventory
     */
    public GameState(World world, Location startingLocation, Inventory startingInventory) { 
        this.world = world;
        this.currentLocation = startingLocation;
        this.inventory = startingInventory;
        this.turnCount = 0;
        this.gameOver = false;
        this.win = false;
    }
    
    /** 
     * Getters and setters
     * @return
     */
    public World getWorld() {
        return world;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Tracks turn count and increments it (after an action occurs, like the player moving to a new room)
     * @return
     */
    public int getTurnCount() {
        return turnCount;
    }

    public void incrementTurn() {
        turnCount++;
    }
    
    /**
     * Determines whether the player had achieved the good ending or the bad ending 
     * @return
     */
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWin() {
        return win;
    }

    public void endGame(boolean winFlag) {
        this.gameOver = true;
        this.win = winFlag;
    }

    public boolean hasReachedTurnLimit() {
        return world.getTurnLimit() > 0 && turnCount >= world.getTurnLimit();
    }
}
