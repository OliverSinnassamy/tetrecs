package uk.ac.soton.comp1206.game;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.media.Media;
import uk.ac.soton.comp1206.scores.ScoreFileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */

    /**
     * Stores current game piece.
     */
    protected GamePiece currentPiece;

    /**
     * Stores next game piece.
     */
    protected GamePiece nextPiece;



    /**
     * Property storing player score.
     */
    public SimpleIntegerProperty scoreProperty = new SimpleIntegerProperty();
    /**
     * Property storing multiplier for game.
     */
    public SimpleIntegerProperty multiplierProperty = new SimpleIntegerProperty();
    /**
     * Property storing player level.
     */
    public SimpleIntegerProperty levelProperty = new SimpleIntegerProperty();
    /**
     * Property storing player lives.
     */
    public SimpleIntegerProperty livesProperty = new SimpleIntegerProperty();
    /**
     * Property storing player high score.
     */
    public SimpleIntegerProperty highScoreProperty = new SimpleIntegerProperty();

    /**
     * Listener for next piece.
     */
    protected NextPieceListener nextPieceListener;
    /**
     * Listener for swap event.
     */
    private SwapListener swapListener;

    /**
     * Current square for keyboard support.
     */
    private final int[][] currentSquare = {{0, 0}};
    /**
     * Listener for rotate event.
     */
    private OnKeyBoardRotateListener rotateListener;
    /**
     * Listener for changing current square with keyboard.
     */
    private CurrentSquareListener currentSquareListener;
    /**
     * Listener for playing piece with keyboard.
     */
    private KeyBoardEnterListener keyBoardEnterListener;
    /**
     * Listener for handling end of game events.
     */
    protected EndGameListener endGameListener;
    /**
     * Listener for escape key being pressed.
     */
    protected EscapeListener escapeListener;
    /**
     * Listener for triggering fail sound.
     */
    private FailSoundListener failSoundListener;
    /**
     * Listener for triggering clear sound.
     */
    private ClearSoundListener clearSoundListener;
    /**
     * Listener for triggering place sound.
     */
    private PlaceSoundListener placeSoundListener;

    /**
     * Game timer for game loop control.
     */
    protected GameTimer timer;

    /**
     * Listener for handling chat event in multiplayer.
     */
    protected ChatListener chatListener;

    /**
     * Constructor for game.
     * Sets colums nad rows and other objects for the game.
     *
     * @param cols
     * @param rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");

        nextPiece(true
);

        setScoreProperty(5600);
        setMultiplierProperty(1);
        setLevelProperty(0);
        setLivesProperty(0);
        setHighScoreProperty(getHighScore());

        timer.reset(getTimerDelay());
        gameLoop();
    }

    /**
     * Game loop control for handling timer and lives.
     */
    public void gameLoop() {
        timer.addZeroListener(() -> {
            if (livesProperty.get() - 1 == -1) {
                timer.end();
                endGame();

            } else {
                System.out.println("##GAME LOOP##");
                livesProperty.set(livesProperty.get() - 1);
                multiplierProperty.set(1);
                nextPiece(false);
            }
        });
    }

    /**
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();


        if (!grid.canPlayPiece(x, y, currentPiece)) {
            failSoundListener.onFail();
            return;
        }
        grid.playPiece(x, y, currentPiece);
        placeSoundListener.onPlace();
        afterPlay();
        nextPiece(false);

    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Spawns a new piece using random number generation.
     *
     * @return : new game piece.
     */
    public GamePiece spawnPiece() {
        Random rand = new Random();
        GamePiece piece = GamePiece.createPiece(rand.nextInt(15));
        logger.info("Current piece: " + piece.toString());
        return piece;
    }

    /**
     * Returns the current game piece.
     *
     * @return : currentPiece.
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Returns the next game piece.
     *
     * @return : nextPiece.
     */
    public GamePiece getNextPiece() {
        return nextPiece;
    }


    /**
     * Sets timer for game to handle lives and time.
     */
    public void setTimer(GameTimer timer) {
        this.timer = timer;
    }

    /**
     * Puts the next piece as the current piece and generates a new piece.
     */
    public void nextPiece(Boolean start) {
        if (start) {
            currentPiece = spawnPiece();
        } else {
            currentPiece = nextPiece;
        }
        nextPiece = spawnPiece();
        nextPieceListener.nextPiece(new GamePiece[]{currentPiece, nextPiece});
        timer.reset(getTimerDelay());
    }


    /**
     * Adds listener for when pieces change.
     *
     * @param listener: listener to be implemented.
     */
    public void addNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    /**
     * Swaps current and next piece.
     */
    public void swapCurrentPiece() {
        GamePiece tmp = currentPiece;
        currentPiece = nextPiece;
        nextPiece = tmp;


        swapListener.onSwap();
    }

    /**
     * Allows swap listener to be added to scene and implemented elsewhere.
     */
    public void addSwapListener(SwapListener listener) {
        swapListener = listener;
    }

    /**
     * Creates arraylist of rows and columns.
     * Adds indexes rows and columns to arraylists.
     * Iterates through the columns and rows, removing any from the arrayList that have blank values.
     * Clears any full rows.
     */
    public void afterPlay() {
        ArrayList<Integer> clearCol = new ArrayList<>();
        ArrayList<Integer> clearRow = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clearRow.add(i);
            clearCol.add(i);
        }

        for (int i = 0; i < 5; i++) {
            for (int z = 0; z < 5; z++) {
                if (grid.getGridProperty(i, z).get() == 0) {
                    clearRow.remove(Integer.valueOf(z));
                }
            }

        }
        for (int i = 0; i < 5; i++) {
            for (int z = 0; z < 5; z++) {
                if (grid.getGridProperty(z, i).get() == 0) {
                    clearCol.remove(Integer.valueOf(z));
                }
            }

        }

        //Clears columns.
        for (int i : clearCol) {
            logger.info("Clearing columns");
            for (int z = 0; z < 5; z++) {
                clearSoundListener.onClear();
                grid.getGridProperty(i, z).set(-1);
            }
        }
        //Clears rows.
        for (int i : clearRow) {
            logger.info("Clearing rows");
            for (int z = 0; z < 5; z++) {
                clearSoundListener.onClear();
                grid.getGridProperty(z, i).set(-1);
            }
        }
        //Changes score.
        setScoreProperty(getScore() + score(clearCol, clearRow));
        if (scoreProperty.get() >= highScoreProperty.get()) {
            highScoreProperty.bind(scoreProperty);
        }

        //Handles multiplier changes.
        if (clearCol.size() == 0 && clearRow.size() == 0) {
            setMultiplierProperty(1);
        } else {
            setMultiplierProperty(getMultiplier() + 1);
        }
        if (getScore() >= 1000) {
            setLevelProperty(getLevel() + 1);
        }

    }

    /**
     * Calculates score when pieces are cleared.
     *
     * @param cols
     * @param rows
     * @return
     */
    public int score(ArrayList<Integer> cols, ArrayList<Integer> rows) {
        int lines = cols.size() + rows.size();

        int blocks = cols.size() * 5;

        if(cols.size()==0){
            blocks = rows.size() * 5;
        }
        else {
            for (int i : rows) {
                blocks += 5 - cols.size();
            }
        }

        logger.info("cols: " + cols.size() + " rows: " + rows.size());
        logger.info("lines: "+ lines + " blocks: " + blocks + " multiplier: " + getMultiplier());

        return blocks * lines * 10* getMultiplier();
    }

    /**
     * Rotates current piece.
     *
     * @param left : direction of rotation.
     * @return rotated piece.
     */
    public GamePiece rotatePiece(boolean left) {
        if (left) {
            currentPiece.rotate(3);
        } else {
            currentPiece.rotate();
        }
        logger.info("Piece rotated...");

        return currentPiece;
    }

    /**
     * Gets value of score property.
     *
     * @return value of score.
     */
    public int getScore() {
        return scoreProperty.get();
    }

    /**
     * Gets score property.
     *
     * @return
     */
    public SimpleIntegerProperty getScoreProperty() {
        return scoreProperty;
    }

    /**
     * Sets score property
     *
     * @param scoreProperty: new value to be set.
     */
    public void setScoreProperty(int scoreProperty) {
        this.scoreProperty.set(scoreProperty);
    }

    /**
     * Sets multiplier property.
     *
     * @param multiplierProperty: new value to be set.
     */
    public void setMultiplierProperty(int multiplierProperty) {
        this.multiplierProperty.set(multiplierProperty);
    }

    /**
     * Gets multiplier value.
     *
     * @return value of the multiplier.
     */
    public int getMultiplier() {
        return multiplierProperty.get();
    }

    /**
     * Gets multiplier property.
     *
     * @return multiplier property.
     */
    public SimpleIntegerProperty getMultiplierProperty() {
        return multiplierProperty;
    }

    /**
     * Gets level property.
     *
     * @return level property.
     */
    public SimpleIntegerProperty getLevelProperty() {
        return levelProperty;
    }

    /**
     * Gets the current level.
     *
     * @return value of level property.
     */
    public int getLevel() {
        return levelProperty.get();
    }

    /**
     * Sets value to level property.
     *
     * @param levelProperty : new value to be set.
     */
    public void setLevelProperty(int levelProperty) {
        this.levelProperty.set(levelProperty);
    }

    /**
     * Gets lives property.
     *
     * @return lives property.
     */
    public SimpleIntegerProperty getLivesProperty() {
        return livesProperty;
    }

    /**
     * Gets number of lives left.
     *
     * @return value of lives property.
     */
    public int getLives() {
        return livesProperty.get();
    }

    /**
     * Sets value of lives property.
     *
     * @param livesProperty: new value to be set.
     */
    public void setLivesProperty(int livesProperty) {
        this.livesProperty.set(livesProperty);
    }

    /**
     * Gets high score property.
     *
     * @return highScoreProperty.
     */
    public SimpleIntegerProperty getHighScoreProperty() {
        return highScoreProperty;
    }

    /**
     * Sets value of highScoreProperty.
     *
     * @param score : value to be set.
     */
    public void setHighScoreProperty(int score) {
        highScoreProperty.set(score);
    }

    /**
     * Adds keyboard support to the game by calling appropriate methods.
     */
    public void keyboardSupport(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case R -> {
                    logger.info("R pressed");
                    swapCurrentPiece();
                }
                case SPACE -> swapCurrentPiece();
                case UP, W -> {
                    if (currentSquare[0][1] == 0) break;
                    currentSquareListener.onChange(false);
                    currentSquare[0][1]--;
                    currentSquareListener.onChange(true);
                    logger.info(currentSquare[0][0] + " " + currentSquare[0][1]);
                }
                case DOWN, S -> {
                    if (currentSquare[0][1] == 4) break;
                    currentSquareListener.onChange(false);
                    currentSquare[0][1]++;
                    currentSquareListener.onChange(true);
                    logger.info(currentSquare[0][0] + " " + currentSquare[0][1]);
                }
                case LEFT, A -> {
                    if (currentSquare[0][0] == 0) break;
                    currentSquareListener.onChange(false);
                    currentSquare[0][0]--;
                    currentSquareListener.onChange(true);
                    logger.info(currentSquare[0][0] + " " + currentSquare[0][1]);
                }
                case RIGHT, D -> {
                    if (currentSquare[0][0] == 4) break;
                    currentSquareListener.onChange(false);
                    currentSquare[0][0]++;
                    currentSquareListener.onChange(true);
                    logger.info(currentSquare[0][0] + " " + currentSquare[0][1]);
                }
                case X, ENTER -> keyBoardEnterListener.onEnter(currentSquare);
                case CLOSE_BRACKET, E, Q -> {
                    rotatePiece(true);
                    rotateListener.OnKeyBoardRotate(currentPiece);
                }
                case OPEN_BRACKET, C, Z -> {
                    rotatePiece(false);
                    rotateListener.OnKeyBoardRotate(currentPiece);
                }
                case ESCAPE -> escapeListener.onEscape();
                case T -> {
                    if (chatListener == null) return;
                    chatListener.onChat();
                }
            }
        });
    }

    /**
     * Gets current square.
     *
     * @return : currentSquare.
     */
    public int[][] getCurrentSquare() {
        return currentSquare;
    }

    /**
     * Adds rotate listener.
     *
     * @param listener : listener to be added.
     */
    public void addOnRotateListener(OnKeyBoardRotateListener listener) {
        rotateListener = listener;
    }

    /**
     * Adds currentSquare listener.
     *
     * @param listener : listener to be added.
     */
    public void addCurrentSquareListener(CurrentSquareListener listener) {
        currentSquareListener = listener;
    }

    /**
     * Adds KeyBoardEnter listener.
     *
     * @param listener : listener to be added.
     */
    public void addKeyBoardEnterListener(KeyBoardEnterListener listener) {
        keyBoardEnterListener = listener;
    }

    /**
     * Adds EndGame listener.
     *
     * @param listener : listener to be added.
     */
    public void addEndGameListener(EndGameListener listener) {
        endGameListener = listener;
    }

    /**
     * Adds Escape listener.
     *
     * @param listener : listener to be added.
     */
    public void addEscapeListener(EscapeListener listener) {
        escapeListener = listener;
    }

    /**
     * Adds ClearSound listener.
     *
     * @param listener : listener to be added.
     */
    public void addClearSoundListener(ClearSoundListener listener) {
        clearSoundListener = listener;
    }

    /**
     * Adds FailSound listener.
     *
     * @param listener : listener to be added.
     */
    public void addFailSoundListener(FailSoundListener listener) {
        failSoundListener = listener;
    }

    /**
     * Adds PlaceSound listener.
     *
     * @param listener : listener to be added.
     */
    public void addPlaceSoundListener(PlaceSoundListener listener) {
        placeSoundListener = listener;
    }

    /**
     * Adds ChatSound listener.
     *
     * @param listener : listener to be added.
     */
    public void addChatListener(ChatListener listener) {
        chatListener = listener;
    }

    /**
     * Handles end of game events.
     */
    public void endGame() {
        //Show high score screen.
        logger.info("Open high score screen");
        endGameListener.endGame();

    }

    /**
     * Calculates time for each turn.
     *
     * @return calculated time.
     */
    public int getTimerDelay() {
        if (12000 - (500 * getLevel()) < 2500) {
            return 2500;
        }
        return 12000 - (500 * getLevel());
    }

    /**
     * Fetches high score from file.
     *
     * @return score : highScore.
     */
    protected int getHighScore() {
        ScoreFileHandler handler = new ScoreFileHandler();
        int score = 0;
        try {
            score = handler.loadScores().get(0).getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }
}
