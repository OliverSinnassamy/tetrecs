package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 * <p>
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 * <p>
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    private final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    private ArrayList<RightClickedListener> rightClickedListeners = new ArrayList<>();

    public SimpleStringProperty theme = new SimpleStringProperty();
    public SimpleIntegerProperty tileProperty = new SimpleIntegerProperty();

    private GameWindow window;

    private Image[] SEA_TILES;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     *
     * @param grid   linked grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height, GameWindow window) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.window = window;


        theme.addListener((observableValue, s, t1) -> {
            logger.info("tile load " + theme.get());
            if (theme.get().equals("sea")) {
                loadTiles();
            } else {
                logger.error("undefined value");
            }
        });

        //Build the GameBoard
        build();

    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols   number of columns for internal grid
     * @param rows   number of rows for internal grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height, GameWindow window) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols, rows);
        this.window = window;

        theme.addListener((observableValue, s, t1) -> {
            logger.info("tile load " + theme.get());
            if (theme.get().equals("sea")) {
                loadTiles();
            } else {
                logger.error("undefined value");
            }
        });

        //Build the GameBoard
        build();


    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     *
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}", cols, rows);

        theme.bind(window.themeProperty());
        tileProperty.bind(window.tileProperty());

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x, y);
            }
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     *
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block, x, y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x, y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));

        //Add mouse hover for current position
        block.setOnMouseEntered((e) -> block.toggleOverlay(true));

        block.setOnMouseExited((e) -> block.toggleOverlay(false));

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     *
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     *
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);
        logger.info(block.getGraphicsContext2D().getFill());

        if (event.getButton() == MouseButton.SECONDARY) {
            for (RightClickedListener l : rightClickedListeners) {
                l.rotatePiece();
            }
            return;
        }

        if (blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Returns grid.
     *
     * @return grid.
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Adds listener for right click event.
     */
    public void setOnRightClicked(RightClickedListener listener) {
        rightClickedListeners.add(listener);
    }

    /**
     * Changes the overlay of the blocks.
     *
     * @param xy     : block to be changed.
     * @param toggle : the value of for the overlay.
     */
    public void changeBlock(int[][] xy, boolean toggle) {
        blocks[xy[0][0]][xy[0][1]].toggleOverlay(toggle);
    }

    /**
     * Returns block based on coordinates on the board.
     *
     * @param gbc : coordinates of required block.
     * @return : block.
     */
    public GameBlock getBlock(int[][] gbc) {
        return blocks[gbc[0][0]][gbc[0][1]];
    }


    /**
     * Theme property for the design of the tiles.
     *
     * @return theme.
     */
    public SimpleStringProperty getTheme() {
        return theme;
    }


    /**
     * Loads the images to be displayed on the sea tiles.
     */
    private void loadTiles() {
        SEA_TILES = new Image[]{

                new Image(getClass().getResource("/images/sea/tiles/fish_01.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_02.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_04.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_05.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_06.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_07.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_08.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_09.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_10.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_11.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_12.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_13.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_14.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_15.png").toExternalForm(), width, height, true, true),
                new Image(getClass().getResource("/images/sea/tiles/fish_16.png").toExternalForm(), width, height, true, true)
        };
    }

    /**
     * Returns the image to be displayed depeneding on the value given.
     *
     * @param i : value of block.
     * @return : tile image.
     */
    public Image getTile(int i) {
        return SEA_TILES[i - 1];
    }

    /**
     * Property for handling square or triangle tile design.
     *
     * @return : tile design property.
     */
    public SimpleIntegerProperty tileProperty() {
        return tileProperty;
    }
}
