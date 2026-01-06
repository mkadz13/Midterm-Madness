/**
 * CS2212A Group 27 Point and Click Adventure Game
 * 
 * This class is the UI controller for our game.
 * It connects the interface with the game engine and handles the interactions.
 * Displays all of the connected locations, NPCS, and items.
 * Processes all of the commands.
 * 
 * @author Mohammed Kadri
 */

package Main.ui;

// Imports
import Main.GameEngine.CommandResult;
import Main.GameEngine.GameEngine;
import Main.GameEngine.GameState;
import Main.model.Connection;
import Main.model.GameObject;
import Main.model.Location;
import Main.model.NPC;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameUI {
	

	// FXML controls
    @FXML private Button goButton;
    @FXML private Button examineButton;
    @FXML private Button useButton;
    @FXML private Button inventoryButton;
    @FXML private Button pickUpButton;
    @FXML private Button dropButton;
    @FXML private Button giveButton;
    @FXML private Button talkButton;
    @FXML private ImageView sceneImage;
    @FXML private AnchorPane arrowContainer;
    @FXML private Button locationButton;
    @FXML private Button turnCountButton;
    @FXML private TextArea infoBox;   


    private Stage stage;
    private GameEngine engine;
    private GameState state;

    // dynamically created arrows
    private List<Polyline> arrows = new ArrayList<>();
    private String selectedExitLabel = null;
    
    // dynamically created NPC images
    private List<ImageView> npcImages = new ArrayList<>();
    private String selectedNPCName = null;
    
    // dynamically created item images
    private List<ImageView> itemImages = new ArrayList<>();
    private String selectedItemName = null;

    // Check if the users inventory is shown
    private boolean showingInventory = false;

    public GameUI() {
    }

    /**
     * Method called by JavaFX to make the info box not editable and enable text wrapping
     */
    @FXML
    private void initialize() {
        if (infoBox != null) {
            infoBox.setEditable(false);
            infoBox.setWrapText(true);
        }
    }


    /**
     * Method to initialize the interface with the stage, engine, and state
     * @param stage
     * @param engine
     * @param state
     */
    public void init(Stage stage, GameEngine engine, GameState state) {
        this.stage = stage;
        this.engine = engine;
        this.state = state;

        wireButtonHandlers();
        refreshUI();
        if (state != null && state.getCurrentLocation() != null) {
            infoBox.setText(state.getCurrentLocation().getDescription());
        }
    }
    
    /**
     * Method to update the images based off the locations image path
     * @param loc
     */
    private void updateSceneImage(Location loc) {
        if (sceneImage == null || loc == null) return;

        String path = loc.getImagePath();
        if (path == null || path.isBlank()) {
            sceneImage.setImage(null);
            return;
        }

        try {
        	// Set the scene images dimensions
            Image img = new Image(getClass().getResourceAsStream(path));
            sceneImage.setImage(img);
            sceneImage.setPreserveRatio(false);
            sceneImage.setFitWidth(603);
            sceneImage.setFitHeight(300);
            sceneImage.setMouseTransparent(true);
        } catch (Exception e) {
            System.err.println("Could not load image for " + loc.getName() + " from " + path);
            sceneImage.setImage(null);
        }
    }

    /**
     * Handles all of the buttons 
     */
    private void wireButtonHandlers() {
        goButton.setOnAction(e -> handleGo());
        pickUpButton.setOnAction(e -> handlePickUp());
        dropButton.setOnAction(e -> handleDrop());
        examineButton.setOnAction(e -> handleExamine());
        talkButton.setOnAction(e -> handleTalk());
        giveButton.setOnAction(e -> handleGive());
        useButton.setOnAction(e -> handleUse());
        inventoryButton.setOnAction(e -> toggleInventoryView());
    }

    /**
     * Method to select the arrows of the connected locations
     * 
     * @param connectionIndex
     */
    private void selectArrow(int connectionIndex) {
        Location loc = state.getCurrentLocation();
        if (loc == null || loc.getConnections() == null) return;

        List<Connection> cons = loc.getConnections();
        if (connectionIndex >= 0 && connectionIndex < cons.size()) {
            selectedExitLabel = cons.get(connectionIndex).getLabel();
        }
    }
    
    /**
     * Method to select an NPC in a scene
     * @param npcName
     */
    private void selectNPC(String npcName) {
        selectedNPCName = npcName;
        selectedItemName = null; 
        showMessage("Selected " + npcName + ". Click 'Talk' or 'Give' to interact.");
    }
    
    /**
     * Method to select an item in a scene
     * @param itemName
     */
    private void selectItem(String itemName) {
        selectedItemName = itemName;
        selectedNPCName = null;
        showMessage("Selected " + itemName + ". Click 'Pick Up' or 'Examine' to interact.");
    }
    
    /**
     * Method to configure the ImageView with the correct settings
     * @param iv
     * @param name
     * @param onClick
     */
    private void setupClickable(ImageView iv, String name, Runnable onClick) {
        iv.setOnMouseClicked(e -> { onClick.run(); e.consume(); });
        iv.setOnMouseEntered(e -> iv.setOpacity(0.8));
        iv.setOnMouseExited(e -> iv.setOpacity(1.0));
        Tooltip t = new Tooltip(name + " (Click to select)");
        t.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        Tooltip.install(iv, t);
    }
    /**
     * Method for error catching, if an image is not working then replace it with a label
     * @param label
     * @param name
     * @param onClick
     */
    private void setupClickableLabel(javafx.scene.control.Label label, String name, Runnable onClick) {
        label.setOnMouseClicked(e -> { onClick.run(); e.consume(); });
        label.setOnMouseEntered(e -> label.setOpacity(0.8));
        label.setOnMouseExited(e -> label.setOpacity(1.0));
        Tooltip t = new Tooltip(name + " (Click to select)");
        t.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        Tooltip.install(label, t);
    }
    
    /**
     * Method to create an image for the given image path with the specific dimensions
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    private ImageView createImage(String imagePath, double width, double height) {
        if (imagePath == null || imagePath.isBlank()) return null;
        try {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setPreserveRatio(true);
            iv.setPickOnBounds(true);
            iv.setMouseTransparent(false);
            iv.setStyle("-fx-cursor: hand;");
            return iv;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Method to handle the go command
     */
    private void handleGo() {
        if (selectedExitLabel == null) {
            showMessage("Select an arrow (exit) first.");
            return;
        }
        CommandResult res = engine.processCommand("go", selectedExitLabel);
        selectedExitLabel = null;
        showingInventory = false;
        handleResult(res);
    }

    /**
     * Method to handle the pickup command
     */
    private void handlePickUp() {
        Location loc = state.getCurrentLocation();
        if (loc == null || loc.getObjects() == null || loc.getObjects().isEmpty()) {
            showMessage("There is nothing to pick up here.");
            return;
        }

        String target = selectedItemName;
        if (target == null) {
            List<String> names = loc.getObjects().stream()
                    .map(GameObject::getName).toList();
            target = pickOne("Pick up which item?", names);
            if (target == null) return;
        }

        CommandResult res = engine.processCommand("pick", target);
        selectedItemName = null; 
        showingInventory = false;
        handleResult(res);
    }

    /**
     * Method to handle the drop command
     */
    private void handleDrop() {
        List<String> names = state.getInventory().listItems().stream()
                .map(GameObject::getName).toList();
        if (names.isEmpty()) {
            showMessage("You have nothing to drop.");
            return;
        }
        String target = pickOne("Drop which item?", names);
        if (target == null) return;

        CommandResult res = engine.processCommand("drop", target);
        showingInventory = false;
        handleResult(res);
    }

    /**
     * Method to handle the examine command
     */
    private void handleExamine() {
        Location loc = state.getCurrentLocation();

        String target = selectedItemName;
        if (target == null) {
            List<String> names = loc.getObjects().stream()
                    .map(GameObject::getName)
                    .collect(Collectors.toList());
            names.addAll(state.getInventory().listItems().stream()
                    .map(GameObject::getName)
                    .toList());

            if (names.isEmpty()) {
                showMessage("There is nothing to examine.");
                return;
            }

            target = pickOne("Examine what?", names);
            if (target == null) return;
        }

        CommandResult res = engine.processCommand("examine", target);
        selectedItemName = null; 
        showingInventory = false;
        handleResult(res);
    }

    /**
     * Method to handle the talk command
     */
    private void handleTalk() {
        Location loc = state.getCurrentLocation();
        if (loc.getCharacters() == null || loc.getCharacters().isEmpty()) {
            showMessage("There is no one here.");
            return;
        }

        String npcName = selectedNPCName;
        if (npcName == null) {
            List<String> names = loc.getCharacters().stream()
                    .map(NPC::getName).toList();
            npcName = pickOne("Talk to whom?", names);
            if (npcName == null) return;
        }

        CommandResult res = engine.processCommand("talk", npcName);
        selectedNPCName = null; 
        showingInventory = false;
        handleResult(res);
    }

    /**
     * Method to handle the give command
     */
    private void handleGive() {
        Location loc = state.getCurrentLocation();
        if (loc.getCharacters() == null || loc.getCharacters().isEmpty()) {
            showMessage("There is no one here.");
            return;
        }

        String npc = selectedNPCName;
        if (npc == null) {
            List<String> npcNames = loc.getCharacters().stream()
                    .map(NPC::getName).toList();
            npc = pickOne("Give to whom?", npcNames);
            if (npc == null) return;
        }

        List<String> itemNames = state.getInventory().listItems().stream()
                .map(GameObject::getName).toList();
        if (itemNames.isEmpty()) {
            showMessage("You have nothing to give.");
            return;
        }

        String item = pickOne("Give which item to " + npc + "?", itemNames);
        if (item == null) return;

        CommandResult res = engine.processCommand("give", item, npc);
        selectedNPCName = null; 
        showingInventory = false;
        handleResult(res);
    }

    /**
     * Method to handle the use command
     */
    private void handleUse() {
        Location loc = state.getCurrentLocation();

        List<String> itemNames = state.getInventory().listItems().stream()
                .map(GameObject::getName)
                .collect(Collectors.toList());
        itemNames.addAll(loc.getObjects().stream().map(GameObject::getName).toList());

        if (itemNames.isEmpty()) {
            showMessage("There is nothing to use.");
            return;
        }

        String item = pickOne("Use what?", itemNames);
        if (item == null) return;

        List<String> targetNames = loc.getObjects().stream()
                .map(GameObject::getName).toList();
        String target = null;
        if (!targetNames.isEmpty()) {
            target = pickOne("Use " + item + " on what? (Cancel for none)", targetNames);
        }

        CommandResult res = (target == null)
                ? engine.processCommand("use", item)
                : engine.processCommand("use", item, target);

        showingInventory = false;
        handleResult(res);
    }

    /**
     * Method to handle inventory
     */
    private void toggleInventoryView() {
        showingInventory = !showingInventory;

        if (showingInventory) {
            List<String> items = state.getInventory().listItems().stream()
                    .map(GameObject::getName).toList();
            if (items.isEmpty()) {
                infoBox.setText("Inventory is empty.");
            } else {
                infoBox.setText("Inventory:\n" + String.join("\n", items));
            }
        } else {
            Location loc = state.getCurrentLocation();
            if (loc != null) {
                infoBox.setText(loc.getDescription());
            }
        }
    }


    /**
     * Method that processes the result of a command from the game engine
     * @param result
     */
    private void handleResult(CommandResult result) {
        if (result == null) return;

        showMessage(result.getMessage());
        refreshUI();

        if (result.isGameOver()) {
            String endText = result.isWin()
                    ? "\n*** You win! ***"
                    : "\n*** Game over. ***";
            infoBox.appendText(endText);
            
            showGameOverDialog(result.isWin());
        }
    }
    
    /**
     * Method to let the user restart or exit game once a condition has been reached
     * @param won
     */
    private void showGameOverDialog(boolean won) {
        String title = won ? "You Win!" : "Game Over";
        String message = won 
            ? "Congratulations! You've completed the game!\n\nWould you like to play again?"
            : "Game Over!\n\nWould you like to try again?";
        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        javafx.scene.control.ButtonType restartButton = new javafx.scene.control.ButtonType("Restart");
        javafx.scene.control.ButtonType exitButton = new javafx.scene.control.ButtonType("Exit");
        alert.getButtonTypes().setAll(restartButton, exitButton);
        
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == restartButton) {
            restartGame();
        } else {
            stage.close();
        }
    }
    
    /**
     * Method to handle restarting the game
     */
    private void restartGame() {
        try {
            Main.model.World world = Main.data.JsonWorldLoader.loadWorld("/games/midterm_madness.json");
            Main.model.Inventory startingInventory = new Main.model.Inventory();
            Main.GameEngine.GameState newGameState = new Main.GameEngine.GameState(world, world.getStartLocation(), startingInventory);
            Main.GameEngine.GameEngine newEngine = new Main.GameEngine.GameEngine(newGameState);
            
            this.engine = newEngine;
            this.state = newGameState;
            
            selectedExitLabel = null;
            selectedNPCName = null;
            selectedItemName = null;
            showingInventory = false;
            
            refreshUI();
            if (state != null && state.getCurrentLocation() != null) {
                infoBox.setText(state.getCurrentLocation().getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error restarting game: " + e.getMessage());
        }
    }

    /**
     * Method to refresh the dynamic UI 
     */
    private void refreshUI() {
        if (state == null) return;
        Location loc = state.getCurrentLocation();
        if (loc == null) return;

        locationButton.setText(loc.getName());
        turnCountButton.setText("Turns: " + state.getTurnCount());
        updateSceneImage(loc);
        List<Connection> cons = loc.getConnections();
        selectedExitLabel = null;
        selectedNPCName = null;
        selectedItemName = null;
        arrowContainer.getChildren().clear();
        arrows.clear();
        npcImages.clear();
        itemImages.clear();
        arrowContainer.setMouseTransparent(false);
        
        // Create NPC images
        List<NPC> npcs = loc.getCharacters();
        if (npcs != null && !npcs.isEmpty()) {
            for (int i = 0; i < npcs.size(); i++) {
                NPC npc = npcs.get(i);
                ImageView iv = createImage(npc.getImagePath(), 100, 150);
                if (iv != null) {
                	// NPC positions (100.5 is the x value)
                    iv.setLayoutX(100.5 + (i - (npcs.size() - 1) / 2.0) * 120);
                    iv.setLayoutY(80);
                    setupClickable(iv, npc.getName(), () -> selectNPC(npc.getName()));
                    arrowContainer.getChildren().add(iv);
                    npcImages.add(iv);
                }
            }
        }
        
        // Create item images
        List<GameObject> items = loc.getObjects();
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                GameObject item = items.get(i);
                ImageView iv = createImage(item.getImagePath(), 80, 80);
                if (iv != null) {
                	// Item positions (603 is the x value)
                    iv.setLayoutX((603 - (items.size() - 1) * 100) / 2.0 + i * 100);
                    iv.setLayoutY(220);
                    setupClickable(iv, item.getName(), () -> selectItem(item.getName()));
                    arrowContainer.getChildren().add(iv);
                    itemImages.add(iv);
                } else {
                    javafx.scene.control.Label label = new javafx.scene.control.Label(item.getName());
                    label.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 5px; -fx-cursor: hand; -fx-border-color: #9612c7; -fx-border-width: 2px;");
                    label.setLayoutX((603 - (items.size() - 1) * 100) / 2.0 + i * 100);
                    label.setLayoutY(200);
                    setupClickableLabel(label, item.getName(), () -> selectItem(item.getName()));
                    arrowContainer.getChildren().add(label);
                }
            }
        }

        if (cons != null && !cons.isEmpty()) {
            String[] dirs = {"up", "down", "left", "right"};
            for (int i = 0; i < cons.size(); i++) {
                String dir = cons.size() == 1 ? "right" : 
                            cons.size() == 2 ? (i == 0 ? "left" : "right") :
                            cons.size() == 3 ? (i == 0 ? "left" : i == 1 ? "right" : "up") :
                            dirs[i % 4];
                Polyline arrow = createDirectionalArrow(dir);
                arrow.setMouseTransparent(false);
                arrow.setPickOnBounds(true);
                Tooltip t = new Tooltip(cons.get(i).getLabel());
                t.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
                Tooltip.install(arrow, t);
                positionArrow(arrow, dir, 603, 300);
                final int idx = i;
                arrow.setOnMouseClicked(e -> { selectArrow(idx); e.consume(); });
                arrowContainer.getChildren().add(arrow);
                arrows.add(arrow);
            }
        }

    }


    /**
     * Creates the arrows that represent connected locations
     * @param direction
     * @return
     */
    private Polyline createDirectionalArrow(String direction) {
        Polyline arrow = new Polyline();
        arrow.setFill(Color.web("#ad00ff"));
        arrow.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        arrow.setStyle("-fx-cursor: hand;");
        switch (direction) {
            case "up": arrow.getPoints().addAll(0.0, -25.0, -18.0, 12.0, 18.0, 12.0, 0.0, -25.0); break;
            case "down": arrow.getPoints().addAll(0.0, 25.0, -18.0, -12.0, 18.0, -12.0, 0.0, 25.0); break;
            case "left": arrow.getPoints().addAll(-25.0, 0.0, 12.0, -18.0, 12.0, 18.0, -25.0, 0.0); break;
            default: arrow.getPoints().addAll(25.0, 0.0, -12.0, -18.0, -12.0, 18.0, 25.0, 0.0);
        }
        return arrow;
    }
    
    /**
     * Method to handle the position of the arrows
     * @param arrow
     * @param direction
     * @param w
     * @param h
     */
    private void positionArrow(Polyline arrow, String direction, double w, double h) {
        switch (direction) {
            case "up": arrow.setLayoutX(w/2); arrow.setLayoutY(25); break;
            case "down": arrow.setLayoutX(w/2); arrow.setLayoutY(h-25); break;
            case "left": arrow.setLayoutX(25); arrow.setLayoutY(h/2); break;
            default: arrow.setLayoutX(w-25); arrow.setLayoutY(h/2);
        }
    }

    /**
     * Displays the message in the text box
     * @param msg
     */
    private void showMessage(String msg) {
        infoBox.setText(msg);
    }

    /**
     * Method to handle the options you have 
     * @param title
     * @param options
     * @return
     */
    private String pickOne(String title, List<String> options) {
        if (options == null || options.isEmpty()) return null;
        if (options.size() == 1) return options.get(0);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(title);
        return dialog.showAndWait().orElse(null);
    }
}
