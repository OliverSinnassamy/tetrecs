package uk.ac.soton.comp1206.component;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    private final Color BORDER_COLOUR = Color.GREY;

    private Color EMPTY_COLOUR = Color.rgb(0, 0, 0, 0);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE,
            Color.WHITE
    };

    private final uk.ac.soton.comp1206.component.GameBoard gameBoard;

    private final double width;
    private final double height;


    /**
     * Allows for hover effect on gameBlocks.
     */
    private Boolean allowHover = true;


    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     *
     * @param gameBoard the board this block belongs to
     * @param x         the column the block exists in
     * @param y         the row the block exists in
     * @param width     the width of the canvas to render
     * @param height    the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        value.bind(this.gameBoard.getGrid().getGridProperty(this.x, this.y));

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);


    }

    /**
     * When the value of this block is updated,
     *
     * @param observable what was updated
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();

    }

    /**
     * Handle painting of the block canvas.
     * Paints according to theme.
     */
    public void paint() {
        if (value.get() == 0) {
            //If the block is empty, paint as empty
            paintEmpty();

        } else if (value.get() == -1) {
            Platform.runLater(this::flash);
        } else {
            if (gameBoard.getTheme().get().equals("sea")) {
                //Tiles set if the theme is sea.
                setTile(gameBoard.getTile(value.get()));

            } else {
                EMPTY_COLOUR = Color.rgb(82, 82, 82, 0.6);

                //If the block is not empty, paint with the colour represented by the value
                if (gameBoard.tileProperty().get() == 1) {
                    paintSquare(COLOURS[value.get()]);
                } else {
                    paintTriangle(COLOURS[value.get()]);
                }
            }

        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();
        setEmpty();
        //Clear
        gc.clearRect(0, 0, width, height);

        //Fill
        gc.setFill(EMPTY_COLOUR);
        gc.fillRect(0, 0, width, height);

        //Border
        gc.setStroke(BORDER_COLOUR);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Paint this canvas with the given colour
     * Paints triangle design on the blocks.
     *
     * @param colour the colour to paint
     */
    private void paintTriangle(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Lightest triangle
        gc.setFill(modifyColor((Color) colour, 1));
        gc.fillPolygon(new double[]{0, getWidth() / 2, 0}, new double[]{0, getHeight() / 2, getHeight()}, 3);

        //Second lightest triangle
        gc.setFill(modifyColor((Color) colour, 2));
        gc.fillPolygon(new double[]{0, getWidth() / 2, getWidth()}, new double[]{0, getHeight() / 2, 0}, 3);

        //Third lightest triangle
        gc.setFill(modifyColor((Color) colour, 2));
        gc.fillPolygon(new double[]{0, getWidth() / 2, getWidth()}, new double[]{getHeight(), getHeight() / 2, getHeight()}, 3);

        //Fourth largest triangle
        gc.setFill(modifyColor((Color) colour, 3));
        gc.fillPolygon(new double[]{getWidth(), getWidth() / 2, getWidth()}, new double[]{0, getHeight() / 2, getHeight()}, 3);
        //Border
        gc.setStroke(BORDER_COLOUR);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Paint this canvas with the given colour
     * Paints square design on the blocks.
     *
     * @param colour the colour to paint
     */
    public void paintSquare(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Lightest triangle
        gc.setFill(modifyColor((Color) colour, 1));
        gc.fillPolygon(new double[]{0, getWidth() / 2, 0}, new double[]{0, getHeight() / 2, getHeight()}, 3);

        //Second lightest triangle
        gc.setFill(modifyColor((Color) colour, 2));
        gc.fillPolygon(new double[]{0, getWidth() / 2, getWidth()}, new double[]{0, getHeight() / 2, 0}, 3);

        //Third lightest triangle
        gc.setFill(modifyColor((Color) colour, 2));
        gc.fillPolygon(new double[]{0, getWidth() / 2, getWidth()}, new double[]{getHeight(), getHeight() / 2, getHeight()}, 3);

        //Fourth largest triangle
        gc.setFill(modifyColor((Color) colour, 3));
        gc.fillPolygon(new double[]{getWidth(), getWidth() / 2, getWidth()}, new double[]{0, getHeight() / 2, getHeight()}, 3);


        gc.setFill(modifyColor((Color) colour, 4));
        gc.fillRect(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);

        //Border
        gc.setStroke(BORDER_COLOUR);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Get the column of this block
     *
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     *
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     *
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     *
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }


    /**
     * Draws overlay on block.
     * Used for hover and keyboard control.
     */
    public void drawOverlay() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.rgb(120, 122, 125, 0.7));
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Set boolean for overlay feature.
     * Used for different uses of gameblocks.
     *
     * @param toggle : adds or removes overlay.
     */
    public void toggleOverlay(boolean toggle) {
        if (!allowHover) return;
        if (toggle) {
            drawOverlay();
        } else {
            paint();
        }
    }

    public void addCenterCircle() {
        GraphicsContext gc = getGraphicsContext2D();

        if (gameBoard.getTheme().get().equals("sea")) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
        } else {
            gc.setFill(Color.rgb(227, 230, 228, 0.7));
        }
        gc.fillOval(getWidth() / 2 - getWidth() / 4, getHeight() / 2 - getHeight() / 4, getWidth() / 2, getHeight() / 2);
    }

    /**
     * Creates different shades of main colour for the block designs.
     *
     * @param colour : base colour for the variants.
     * @param value  : variant to be returned.
     * @return result of createNewColour : colour to be used.
     */
    public Color modifyColor(Color colour, int value) {
        return switch (value) {
            case 1 -> createNewColour(colour, 0.5f);
            case 3 -> createNewColour(colour, 0.75f);
            case 4 -> createNewColour(colour, 0.875f);
            default -> createNewColour(colour, 0.675f);
        };
    }

    /**
     * Creates new colour based upon value given.
     *
     * @param colour : base colour.
     * @param value  : scale factor to alter rgb composition.
     * @return new colour created.
     */
    private Color createNewColour(Color colour, float value) {
        double blue = colour.getBlue();
        double red = colour.getRed();
        double green = colour.getGreen();

        return Color.color(red * value, blue * value, green * value);
    }

    /**
     * Handles flash animation called when clearing rows and columns in the game.
     */
    public void flash() {
        var gc = getGraphicsContext2D();
        //Initial white flash.
        gc.setFill(Color.rgb(255, 255, 255, 1));
        gc.fillRect(0, 0, width, height);

        //Timer to handle fade out.
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                gc.clearRect(0, 0, width, height);


                gc.setFill(((Color) gc.getFill()).deriveColor(0, 1, 1, 0.955));
                gc.fillRect(0, 0, width, height);

                gc.setStroke(BORDER_COLOUR);
                gc.strokeRect(0, 0, width, height);


                if (((Color) gc.getFill()).getOpacity() <= 0.1) {
                    gameBoard.getGrid().set(x, y, 0);
                    stop();
                }
            }
        };

        timer.start();
    }

    /**
     * Sets value of allowHover.
     *
     * @param allow : new value of allowHover.
     */
    public void setAllowHover(Boolean allow) {
        allowHover = allow;
    }

    /**
     * Draws image onto sea tiles.
     *
     * @param i : image ot be drawn.
     */
    private void setTile(Image i) {
        var gc = getGraphicsContext2D();

        gc.drawImage(i, 0, 0, width, height);
    }

    /**
     * Sets value of empty block.
     */
    private void setEmpty() {
        if (gameBoard.getTheme().get().equals("sea")) {
            EMPTY_COLOUR = Color.rgb(120, 208, 255, 0.4);
        } else if (gameBoard.getTheme().get().equals("minimal")) {
            EMPTY_COLOUR = Color.rgb(82, 82, 82, 0.6);

        }
    }

}
