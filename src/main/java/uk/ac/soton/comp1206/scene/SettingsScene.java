package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.media.Media;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Scene to display and control settings of game.
 */
public class SettingsScene extends BaseScene {

    /**
     * Main container of the class.
     */
    private BorderPane pane;

    /**
     * Container of the theme setting.
     */
    private VBox themeContainer;

    /**
     * Container of all settings.
     */
    private VBox settingContainer;

    /**
     * Container of volume settings.
     */
    private VBox volumeContainer;

    /**
     * Container of tile settings.
     */
    private VBox tileContainer = new VBox();

    /**
     * Multimedia object for handling sounds.
     */
    private Media media = gameWindow.getMedia();

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public SettingsScene(GameWindow gameWindow) {
        super(gameWindow);

    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
        getScene().getStylesheets().add(getClass().getResource("/style/default.css").toExternalForm());
        getScene().getStylesheets().add(getClass().getResource("/style/" + gameWindow.themeProperty().get() + ".css").toExternalForm());
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        pane = new BorderPane();
        setUp(pane);

        if (!gameWindow.themeProperty().get().equals("sea")) {
            handleTiles();
        }

        handleThemeContainer();
        handleSettingsContainer();

        pane.setCenter(settingContainer);
    }

    /**
     * Handles Escape event.
     */
    @Override
    protected void handleKeyBoard() {
        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                //     multimedia.stopMusic();
                gameWindow.startMenu();

            }
        });
    }

    /**
     * Handles set up of basic scene.
     *
     * @param pane : main container of scene.
     */
    private void setUp(BorderPane pane) {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var settingsPane = new StackPane();
        settingsPane.setMaxWidth(gameWindow.getWidth());
        settingsPane.setMaxHeight(gameWindow.getHeight());
        settingsPane.getStyleClass().add("settings-pane");
        root.getChildren().add(settingsPane);

        settingsPane.getChildren().add(pane);
    }

    /**
     * Initializes and sets up theme container.
     */
    private void handleThemeContainer() {
        HBox themeButtonContainer = new HBox();
        themeContainer = new VBox();

        //Creates title.
        Text title = new Text("Themes");
        title.getStyleClass().add("setting-titles");
        themeContainer.getChildren().add(title);

        //Creates buttons.
        Button minimal = new Button("Minimal");
        Button sea = new Button("Sea");
        Button space = new Button("Space");

        //Adds style to buttons.
        minimal.getStyleClass().add("menu-buttons");
        sea.getStyleClass().add("menu-buttons");
        space.getStyleClass().add("menu-buttons");

        //Adds listeners to buttons.
        minimal.setOnAction(actionEvent -> {
            gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            gameWindow.setTheme("minimal");
        });

        sea.setOnAction(actionEvent -> {
            gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            gameWindow.setTheme("sea");
        });

        space.setOnAction(actionEvent -> {
            gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            gameWindow.setTheme("space");
        });


        themeButtonContainer.getChildren().add(minimal);
        themeButtonContainer.getChildren().add(sea);
        themeButtonContainer.getChildren().add(space);


        themeContainer.setAlignment(Pos.CENTER);
        themeButtonContainer.setAlignment(Pos.CENTER);

        themeContainer.getChildren().add(themeButtonContainer);
    }

    /**
     * Initializes settings container.
     */
    private void handleSettingsContainer() {
        settingContainer = new VBox();
        settingContainer.getStyleClass().add("settings-container");

        handleVolumeContainer();

        settingContainer.getChildren().addAll(themeContainer, volumeContainer, tileContainer, acknowledgements());

        VBox.setMargin(themeContainer, new Insets(25, 0, 0, 0));
        VBox.setMargin(volumeContainer, new Insets(20, 0, 0, 0));
        VBox.setMargin(tileContainer, new Insets(20, 0, 20, 0));

        settingContainer.setAlignment(Pos.CENTER);

    }

    /**
     * Initializes volume setting.
     */
    private void handleVolumeContainer() {
        volumeContainer = new VBox();

        Text title = new Text("Volume");
        title.getStyleClass().add("setting-titles");

        //Controls volume property of multimedia.
        Slider slider = new Slider();
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(10);

        slider.setValue(media.getVolumeProperty().get());

        slider.setMaxWidth(300);
        slider.getStyleClass().add("volume-slider");

        volumeContainer.setAlignment(Pos.CENTER);

        volumeContainer.getChildren().addAll(title, slider);

        media.getVolumeProperty().bind(slider.valueProperty());
    }

    /**
     * Handles tile settings.
     */
    private void handleTiles() {
        HBox container = new HBox();

        Text title = new Text("Tiles");
        title.getStyleClass().add("setting-titles");

        PieceBoard tileTriangle = new PieceBoard(3, 3, 150, 150, gameWindow, false);
        PieceBoard tileSquare = new PieceBoard(3, 3, 150, 150, gameWindow, false);

        gameWindow.tileProperty().set(0);
        tileTriangle.displayPiece(GamePiece.createPiece(3));
        gameWindow.tileProperty().set(1);
        tileSquare.displayPiece(GamePiece.createPiece(3));

        if (!gameWindow.themeProperty().get().equals("sea")) {
            tileTriangle.setOnBlockClick(block -> {
                gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
                gameWindow.tileProperty().set(0);
            });
            tileSquare.setOnBlockClick(block -> {
                gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
                gameWindow.tileProperty().set(1);
            });
        }


        container.getChildren().addAll(tileSquare, tileTriangle);

        container.setAlignment(Pos.CENTER);
        tileContainer.setAlignment(Pos.CENTER);

        tileContainer.getChildren().addAll(title, container);
    }

    /**
     * Creates acknowledgements button.
     *
     * @return acknowledgements container.
     */
    private VBox acknowledgements() {
        VBox container = new VBox();

        Button button = new Button("Acknowledgements");

        button.setOnAction(actionEvent -> {
            gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            gameWindow.startAcknowledgements();
        });

        button.getStyleClass().add("lobby-button");

        container.getChildren().add(button);
        container.setAlignment(Pos.BOTTOM_CENTER);

        return container;
    }
}
