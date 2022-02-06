package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Scene to hold acknowledgements for assets.
 */
public class AcknowledgementsScene extends BaseScene{

    /**
     * Container for acknowledgements.
     */
    private VBox container;
    /**
     * BorderPane to hold scene components.
     */
    private BorderPane pane = new BorderPane();


    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public AcknowledgementsScene(GameWindow gameWindow) {
        super(gameWindow);

        container= new VBox();

        Text sounds = new Text("Sounds from zapsplat.com");
        sounds.getStyleClass().add("lobby-text");

        Text seaImageGame = new Text("Game sea image from https://www.vecteezy.com/free-vector/ocean-background");
        seaImageGame.getStyleClass().add("lobby-text");

        Text seaImageMenu = new Text("Menu sea image from https://www.vecteezy.com/free-vector/ocean-life");
        seaImageMenu.getStyleClass().add("lobby-text");

        Text seaImageMultiplayer = new Text("Multiplayer lobby sea image from https://www.vecteezy.com/free-vector/sea");
        seaImageMultiplayer.getStyleClass().add("lobby-text");

        Text seaTiles = new Text("Sea animals clipart PNG Designed By Ylivdesign from https://pngtree.com");
        seaTiles.getStyleClass().add("lobby-text");

        container.getChildren().addAll(sounds,seaImageGame,seaImageMenu,seaImageMultiplayer, seaTiles);

        container.setAlignment(Pos.CENTER);

        pane.setCenter(container);

    }
    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {

    }
    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        setUp(pane);
    }

    /**
     * Handles escape key being pressed.
     */
    @Override
    protected void handleKeyBoard() {
        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();

            }
        });
    }

    /**
     * Handles set up of Acknowledgements scene
     * @param pane : border pane to hold the contents.
     */
    private void setUp(BorderPane pane) {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var acknowledgePane = new StackPane();
        acknowledgePane.setMaxWidth(gameWindow.getWidth());
        acknowledgePane.setMaxHeight(gameWindow.getHeight());
        acknowledgePane.getStyleClass().add("settings-pane");
        root.getChildren().add(acknowledgePane);

        acknowledgePane.getChildren().add(pane);
    }
}
