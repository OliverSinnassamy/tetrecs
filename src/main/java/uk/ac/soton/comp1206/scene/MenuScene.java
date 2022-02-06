package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.media.Animator;
import uk.ac.soton.comp1206.themes.SeaButton;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.media.Media;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Theme property.
     */
    private final SimpleStringProperty theme = new SimpleStringProperty();

    /**
     * Holds title of scene.
     */
    private HBox titleContainer;

    /**
     * Multimedia object to handle music and sounds.
     */
    private Media media = new Media();


    /**
     * Create a new menu scene
     *
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");

        theme.bind(gameWindow.themeProperty());
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        BorderPane pane = new BorderPane();
        setUp(pane);

        //Left panel - title and buttons
        pane.setCenter(titleButtonsContainer());
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        getScene().getWindow().setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });

        logger.error(theme.get());
        getScene().getStylesheets().add(getClass().getResource("/style/" + theme.get() + ".css").toExternalForm());

        Animator animator = new Animator();

        animator.titleDisplay(titleContainer);

    }

    /**
     * Handle when the Start Game button is pressed
     */
    private void startGame() {
        gameWindow.startChallenge();
    }

    /**
     * Handle when the Instructions button is pressed
     */
    private void startInstructions() {
        gameWindow.startInstructions();
    }

    /**
     * Handle when the Multiplayer button is pressed
     */
    private void startMultiplayer() {
        gameWindow.startMultiplayer();
    }

    /**
     * Handle when the Settings button is pressed
     */
    private void startSettings() {
        gameWindow.startSettings();
    }

    /**
     * Handles set up of MenuScene.
     * Creates stack pane for background and content.
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
     * Creates center container for MenuScene.
     *
     * @return
     */
    private VBox titleButtonsContainer() {
        var container = new VBox();

        titleContainer = title();

        container.getChildren().addAll(titleContainer, buttons());

        container.setAlignment(Pos.CENTER);
        return container;
    }

    /**
     * Create title for the screen.
     * Separate text nodes used for animation purposes.
     *
     * @return title.
     */
    private HBox title() {
        HBox title = new HBox();
        Text t1 = new Text("T");
        Text e1 = new Text("e");
        Text t2 = new Text("t");
        Text r = new Text("r");
        Text e2 = new Text("E");
        Text c = new Text("C");
        Text s = new Text("S");

        title.getChildren().addAll(t1, e1, t2, r, e2, c, s);

        for (Node n : title.getChildren()) {
            n.getStyleClass().add("menu-title");
        }

        title.setAlignment(Pos.CENTER);

        VBox.setMargin(title, new Insets(0, 20, 40, 0));

        return title;
    }

    /**
     * Creates buttons for the menu screen.
     *
     * @return
     */
    private VBox buttons() {
        VBox buttonContainer = new VBox();
        Animator animator = new Animator();

        //Creates buttons.
        Button single = new Button("Single Player");
        Button multi = new Button("Multiplayer");
        Button instr = new Button("How To Play");
        Button settings = new Button("Settings");

        //Styling for buttons.
        single.getStyleClass().add("menu-buttons");
        multi.getStyleClass().add("menu-buttons");
        instr.getStyleClass().add("menu-buttons");
        settings.getStyleClass().add("menu-buttons");

        //Adds listeners for shake animation.
        single.setOnMouseEntered(mouseEvent -> animator.shakeButton(single));
        multi.setOnMouseEntered(mouseEvent -> animator.shakeButton(multi));
        instr.setOnMouseEntered(mouseEvent -> animator.shakeButton(instr));
        settings.setOnMouseEntered(mouseEvent -> animator.shakeButton(settings));

        //Adds listeners for action event.
        single.setOnAction(event -> {
            media.playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            startGame();
        });
        multi.setOnAction(event -> {
            media.playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            startMultiplayer();
        });
        instr.setOnAction(event -> {
            media.playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            startInstructions();
        });
        settings.setOnAction(event -> {
            media.playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            startSettings();
        });

        buttonContainer.setAlignment(Pos.CENTER);

        //Creates sea buttons if theme is sea.
        if (theme.get().equals("sea")) {
            buttonContainer.getChildren().add(new SeaButton(single, 250, 320));
            buttonContainer.getChildren().add(new SeaButton(multi, 250, 320));
            buttonContainer.getChildren().add(new SeaButton(instr, 250, 320));
            buttonContainer.getChildren().add(new SeaButton(settings, 250, 320));
        } else {
            buttonContainer.getChildren().addAll(single, multi, instr, settings);
        }

        return buttonContainer;
    }

    /**
     * Handles Escape event.
     */
    @Override
    protected void handleKeyBoard() {
        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.getCommunicator().send("QUIT");
                System.exit(0);
            }
        });
    }
}
