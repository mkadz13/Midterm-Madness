package Main;

import Main.ui.MenuUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * The entry point for the Midterm Madness JavaFX application.
 * <p>
 * This class initializes and displays the main menu UI by loading the
 * {@code MenuUI.fxml} file and setting up the primary application window.
 * </p>
 * @author Arthur
 */
public class Main extends Application {


    /**
     * Called automatically when the JavaFX application starts.
     * <p>
     * Loads the main menu from FXML, initializes its controller, and
     * displays the primary application window.
     * </p>
     *
     * @param primaryStage
     *        The main stage provided by the JavaFX runtime.
     * @throws Exception
     *         If the FXML resource cannot be loaded or initialization fails.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/Main/ui/MenuUI.fxml"));
        Parent menuRoot = menuLoader.load();
        
        MenuUI menuUI = menuLoader.getController();
        menuUI.init(primaryStage);
        
        primaryStage.setScene(new Scene(menuRoot, 600, 400));
        primaryStage.setTitle("Midterm Madness");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
