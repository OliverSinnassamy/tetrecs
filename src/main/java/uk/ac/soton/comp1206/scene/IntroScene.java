package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.media.Animator;
import uk.ac.soton.comp1206.media.Media;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Displays introduction animation.
 */
public class IntroScene extends BaseScene {

    /**
     * ImageView containing animated image.
     */
    private ImageView imageView;
    /**
     * Main container of the scene.
     */
    private BorderPane pane;

    /**
     * Time value for timer.
     */
    private int time = 0;

    /**
     * Multimedia object for handling sound effects.
     */
    private Media media = gameWindow.getMedia();

    private Logger logger = LogManager.getLogger(IntroScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
        handleTimer();

        Animator animator = new Animator();
        media.playSound("intro.mp3");
        animator.introFade(imageView);
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        pane = new BorderPane();
        setUp(pane);

        imageView = new ImageView();

        Image image = new Image(getClass().getResource("/images/ECSGames.png").toExternalForm());

        imageView.setImage(image);
        imageView.setOpacity(0);

        imageView.setFitHeight(250);
        imageView.preserveRatioProperty().set(true);

        pane.setCenter(imageView);
    }

    /**
     * Handles Escape event.
     */
    @Override
    protected void handleKeyBoard() {
        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                System.exit(0);
            }
        });
    }

    /**
     * Handles timer for changing to the menu screen.
     */
    private void handleTimer() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;

                if (time == 8) {
                    Platform.runLater(gameWindow::startMenu);
                }
            }
        }, 0, 1000);
    }

    /**
     * Creates the basic scene.
     *
     * @param pane
     */
    private void setUp(BorderPane pane) {
        logger.info("Set up lobby scene...");

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        root.getChildren().add(lobbyPane);

        lobbyPane.getChildren().add(pane);
    }

}
