/**
 * CS2212A Group 27 Point and Click Adventure Game
 * 
 * This class is the UI controller for the main menu screen.
 * This class loads and displays the menu image and handles the start and exit buttons
 * Handles creating the game state, and engine, and world
 * Switches from menu to the game
 * 
 * @author Mohammed Kadri
 */

package Main.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MenuUI {

    @FXML
    private Button startButton;
    
    @FXML
    private Button exitButton;
    
    @FXML
    private ImageView backgroundImage;
    
    private Stage stage;

    @FXML
    /**
     * This method is called by JavaFX after FXML have been injected
     * Loads the background image
     * Error checks to see if there is a background image
     */
    private void initialize() {
        // Load background image
        try {
            Image bg = new Image(getClass().getResourceAsStream("/images/menu_background.png"));
            backgroundImage.setImage(bg);
            backgroundImage.setFitWidth(600);
            backgroundImage.setFitHeight(400);
            backgroundImage.setPreserveRatio(false);
        } catch (Exception e) {
            System.err.println("Menu background image not found");
        }
    }
    
    /**
     * Handles the start and exit buttons
     * @param stage
     */
    public void init(Stage stage) {
        this.stage = stage;
        
        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> exitGame());
    }
    
    /**
     * Loads the GameUI
     * Creates the game engine, state, and world
     * Switches to the game
     */
    private void startGame() {
        try {
            // Load the game UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main/ui/GameUI.fxml"));
            Parent root = loader.load();
            
            GameUI gameUI = loader.getController();
            
            // Create objects
            Main.model.World world = Main.data.JsonWorldLoader.loadWorld("/games/midterm_madness.json");
            Main.model.Inventory startingInventory = new Main.model.Inventory();
            Main.GameEngine.GameState gameState = new Main.GameEngine.GameState(world, world.getStartLocation(), startingInventory);
            Main.GameEngine.GameEngine engine = new Main.GameEngine.GameEngine(gameState);
            
            gameUI.init(stage, engine, gameState);
            
            stage.setScene(new Scene(root, 600, 400));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method to close the game
     */
    private void exitGame() {
        stage.close();
    }
}

