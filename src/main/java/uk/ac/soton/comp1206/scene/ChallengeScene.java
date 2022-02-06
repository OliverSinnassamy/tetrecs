package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.*;
import uk.ac.soton.comp1206.event.ClearSoundListener;
import uk.ac.soton.comp1206.event.FailSoundListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GameTimer;
import uk.ac.soton.comp1206.media.Animator;
import uk.ac.soton.comp1206.media.Media;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * Game object.
     */
    protected Game game;

    /**
     * BorderUI component to hold in-game data.
     */
    private final BorderUI border = new BorderUI();
    /**
     * PieceBoards for holding current and next piece.
     */
    private PieceBoard current, next;

    /**
     * Container for gameBoard and chat for multiplayer.
     */
    protected VBox boardContainer = new VBox();
    /**
     * Timer to handle game loop.
     */
    protected GameTimer timer = new GameTimer();

    /**
     * Main container for the scene.
     */
    protected BorderPane pane = new BorderPane();

    /**
     * Stores object to handle sounds.
     */
    private Media media = gameWindow.getMedia();

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");

    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();


        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-pane");
        root.getChildren().add(challengePane);


        challengePane.getChildren().add(pane);

        boardContainer.setAlignment(Pos.CENTER);

        //Creates game board.
        var board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2, gameWindow);
        boardContainer.getChildren().add(0, board);
        pane.setCenter(boardContainer);

        //Handle block on gameboard grid being clicked
        board.setOnRightClicked(() -> game.rotatePiece(false));

        //Handles block clicked.
        board.setOnBlockClick(this::blockClicked);

        //Changes current square when keyboard is used.
        game.addCurrentSquareListener(toggle -> board.changeBlock(game.getCurrentSquare(), toggle));

        //Adds listener for enter key being pressed to play game piece.
        game.addKeyBoardEnterListener(place -> game.blockClicked(board.getBlock(place)));

        //Adds sound effect listeners.
        game.addClearSoundListener(() -> gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "clear.mp3"));
        game.addFailSoundListener(() -> gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "fail.mp3"));
        game.addPlaceSoundListener(() -> gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "place.mp3"));
        game.getLevelProperty().addListener((observableValue, number, t1) -> media.playSound("level.wav"));


        //Container for holding border.
        VBox borderHolder = new VBox();
        borderHolder.getChildren().add(border);
        logger.info("Adding border to pane...");
        pane.setRight(borderHolder);

        //Binds BorderUI properties.
        bindBorderProperties(border);

        game.setTimer(timer);

        //Sets timer visual to scene.
        TimerVisual timerVisual = new TimerVisual();

        timerVisual.timeProperty().bind(timer.timeProperty());

        timer.addTimeListener(timerVisual::updateDisplay);
        pane.setBottom(timerVisual);

        Animator animator = new Animator();

        //Adds game loop listener to handle animations.
        timer.addGameLoopListener(() -> {
            animator.timerBar(timerVisual.getTimerBox(), game.getTimerDelay());
            timerVisual.getTimerBox().setFill(Color.GREEN);
        });

        //Adds listener for lives animation and sound.
        game.getLivesProperty().addListener((observableValue, s, t1) -> {
            if(game.getLives() == 3) return;
            media.playSound("lifelose.wav");
            animator.shakeButton(border.getLives());
            animator.shakeButton(border.getLivesLabel());
        });


        current = handleCurrentPieceBoards();
        next = handleNextPieceBoard();
        handlePieceBoardListeners(current, next, board);

        borderHolder.getChildren().addAll(handleLabel("Incoming"), current, handleLabel("Future"), next);

        borderHolder.setAlignment(Pos.CENTER);

        endGame();
    }

    /**
     * Handles escape key being pressed.
     */
    @Override
    protected void handleKeyBoard() {
        game.addEscapeListener(() -> {
            logger.info("Pressed");
            Platform.runLater(() -> {
                gameWindow.startMenu();
                timer.end();
                game = null;
            });
        });

        Platform.runLater(() -> {
            game.keyboardSupport(gameWindow.getScene());
        });
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }


    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");

        getScene().getStylesheets().add(getClass().getResource("/style/" + gameWindow.themeProperty().get() + ".css").toExternalForm());
        handleKeyBoard();
        game.start();
    }

    /**
     * Binds BorderUI properties to game.
     *
     * @param ui : BorderUI containing properties.
     */
    public void bindBorderProperties(BorderUI ui) {
        ui.getLevel().textProperty().bind(game.getLevelProperty().asString());
        ui.getScore().textProperty().bind(game.getScoreProperty().asString());
        ui.getLives().textProperty().bind(game.getLivesProperty().asString());
        ui.getMultiplier().textProperty().bind(game.getMultiplierProperty().asString());
        ui.getHighScore().textProperty().bind(game.getHighScoreProperty().asString());
    }

    /**
     * Adds listeners to piece boards.
     *
     * @param currentPiece : PieceBoard of current piece.
     * @param nextPiece    : PieceBoard of next piece.
     * @param board        : gameboard for right click handling.
     */
    public void handlePieceBoardListeners(PieceBoard currentPiece, PieceBoard nextPiece, GameBoard board) {
        board.setOnRightClicked(() -> {
            logger.info("Rotate received...");
            currentPiece.displayPiece(game.getCurrentPiece());
        });

        currentPiece.setOnMouseClicked(e -> {
            if (!(e.getButton() == MouseButton.PRIMARY)) {
                return;
            }
            game.rotatePiece(false);
            media.playSound("rotate.wav");
            currentPiece.displayPiece(game.getCurrentPiece());
        });

        nextPiece.setOnMouseClicked(e -> {
            game.swapCurrentPiece();
        });

        game.addSwapListener(() -> {
            media.playSound("transition.wav");
            currentPiece.displayPiece(game.getCurrentPiece());
            nextPiece.displayPiece(game.getNextPiece());
        });

        game.addNextPieceListener(piece -> {
            currentPiece.displayPiece(piece[0]);
            nextPiece.displayPiece(piece[1]);
        });

        game.addOnRotateListener(piece -> {
            currentPiece.displayPiece(game.getCurrentPiece());
            media.playSound("rotate.wav");
        });
    }

    /**
     * Creates piece board to display the current piece.
     */

    public PieceBoard handleCurrentPieceBoards() {
        PieceBoard currentPiece = new PieceBoard(3, 3, 75, 75, gameWindow);
        VBox.setMargin(currentPiece, new Insets(0, 0, 10, 0));
        return currentPiece;
    }

    /**
     * Creates labels.
     *
     * @param text : text to be added to labels.
     * @return node.
     */
    private Text handleLabel(String text) {
        Text node = new Text(text);
        node.setFill(Color.WHITE);
        node.setFont(Font.font("Montserrat", 20));
        VBox.setMargin(node, new Insets(10, 0, 0, 0));
        return node;
    }

    /**
     * Creates piece board to display the next piece.
     */

    public PieceBoard handleNextPieceBoard() {
        return new PieceBoard(3, 3, 75, 75, gameWindow);
    }

    /**
     * Gets BorderUI of scene.
     *
     * @return border.
     */
    public BorderUI getBorderUI() {
        return border;
    }

    /**
     * Handles end of game events.
     */
    public void endGame() {
        game.addEndGameListener(() -> {
            gameWindow.startScores(game);
            timer.end();
        });
    }
}
