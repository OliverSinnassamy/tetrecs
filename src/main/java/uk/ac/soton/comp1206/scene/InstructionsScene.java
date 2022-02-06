package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.PieceGrid;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Scene to display instructions of the game.
 * Accessed from menu scene.
 */
public class InstructionsScene extends BaseScene {

    /**
     * Container for scene.
     */
    private VBox container;

    private static Logger logger = LogManager.getLogger(InstructionsScene.class);

    private BorderPane pane = new BorderPane();

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);

    }


    /**
     * Sets up the new scene.
     * Creates container to hold all nodes.
     */
    private void setUp(BorderPane pane) {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-pane");
        root.getChildren().add(menuPane);

        menuPane.getChildren().add(pane);
    }

    /**
     * Makes title for sections.
     *
     * @param txt : text of the title.
     * @return title: Text object for to be added to container.
     */
    private Text title(String txt) {
        Text title = new Text(txt);
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Montserrat", 25));

        return title;
    }

    /**
     * Loads the instructions image and sizes it.
     *
     * @return image: imageview for the instructions.
     */
    public ImageView instructionImage() {
        ImageView image = new ImageView();
        Image img = new Image(getClass().getResource("/images/Instructions.png").toExternalForm());
        image.setImage(img);

        image.setFitHeight(300);
        image.preserveRatioProperty().set(true);

        return image;
    }


    /**
     * Creates the grid for the game pieces to be displayed.
     *
     * @return new PieceGrid.
     */
    public VBox piecesGrid() {
        VBox container = new VBox();

        container.setAlignment(Pos.CENTER);

        container.getChildren().addAll(title("Game Pieces"), new PieceGrid(gameWindow));
        return container;
    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
        container = new VBox();

        container.setAlignment(Pos.CENTER);


        container.getChildren().addAll(title("Instructions"), instructionImage(), piecesGrid());

        pane.setCenter(container);
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
                Platform.runLater(gameWindow::startMenu);
            }
        });
    }
}


