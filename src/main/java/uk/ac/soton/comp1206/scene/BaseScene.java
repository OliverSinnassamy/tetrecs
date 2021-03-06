package uk.ac.soton.comp1206.scene;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * A Base Scene used in the game. Handles common functionality between all scenes.
 */
public abstract class BaseScene {
    /**
     * GameWindow of scene.
     */
    protected final GameWindow gameWindow;

    /**
     * Root of scene.
     */
    protected GamePane root;
    /**
     * Scene for UI display.
     */
    protected Scene scene;


    private static Logger logger = LogManager.getLogger(BaseScene.class);


    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public BaseScene(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * Initialise this scene. Called after creation
     */
    public abstract void initialise();

    /**
     * Build the layout of the scene
     */
    public abstract void build();

    /**
     * Create a new JavaFX scene using the root contained within this scene
     *
     * @return JavaFX scene
     */
    public Scene setScene() {
        var previous = gameWindow.getScene();
        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight(), Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
        this.scene = scene;

        handleKeyBoard();

        return scene;
    }


    /**
     * Get the JavaFX scene contained inside
     *
     * @return JavaFX scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Handles Escape event.
     */
    protected abstract void handleKeyBoard();


}
