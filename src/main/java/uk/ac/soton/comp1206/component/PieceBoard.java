package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Board created to handle the display of game pieces and user boards in spectate mode.
 */
public class PieceBoard extends GameBoard {

    private Boolean circle = true;

    /**
     * Constructor for PieceBoard class.
     *
     * @param cols   : number of columns in grid.
     * @param rows   : number of rows in grid.
     * @param width  : width of component.
     * @param height : height of component.
     */
    public PieceBoard(int cols, int rows, double width, double height, GameWindow window) {
        super(cols, rows, width, height, window);

    }

    /**
     * Constructor for PieceBoard class.
     *
     * @param cols   : number of columns in grid.
     * @param rows   : number of rows in grid.
     * @param width  : width of component.
     * @param height : height of component.
     * @param circle : boolean for displaying center circle.
     * @param window : GameWindow to be passed to superclass to handle themes.
     */
    public PieceBoard(int cols, int rows, double width, double height, GameWindow window, boolean circle) {
        super(cols, rows, width, height, window);
        this.circle = circle;

    }

    /**
     * Sets new value to piece.
     * Displays piece on grid using PlaceHandler object.
     *
     * @param piece: new game piece to display.
     */

    public void displayPiece(GamePiece piece) {
        getGrid().clearGrid();
        getGrid().playPiece(1, 1, piece);

        if (circle) {
            getBlock(1, 1).addCenterCircle();
        }

        //Removes hover capabilities.
        for (int i = 0; i < 3; i++) {
            for (int z = 0; z < 3; z++) {
                getBlock(i, z).setAllowHover(false);
            }
        }
    }

    /**
     * Displays piece board in spectate mode.
     *
     * @param board : board from server to display.
     */
    public void displayBoard(String board) {
        String[] values = board.split(" ");

        getGrid().clearGrid();

        int counter = 0;

        for (int i = 0; i < 5; i++) {
            for (int z = 0; z < 5; z++) {
                getGrid().set(i, z, Integer.parseInt(values[counter]));
                counter++;
            }
        }
    }
}
