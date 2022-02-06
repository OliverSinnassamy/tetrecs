package uk.ac.soton.comp1206.component;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GameWindow;


/**
 * Holds game pieces in a grid for displaying in the Instructions scene.
 */
public class PieceGrid extends GridPane {

    private final GameWindow window;

    /**
     * Holds all the boards for the game pieces.
     */
    private final ArrayList<PieceBoard> pieces = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(PieceGrid.class);

    /**
     * Constructor for piece grid.
     * Calls methods to generate boards and place them on the grid.
     */
    public PieceGrid(GameWindow window) {
        super();
        this.window =  window;

        setPrefWidth(250);
        setPrefHeight(150);

        setPieces();
        setUpGrid();

        setAlignment(Pos.CENTER);
    }

    /**
     * Generates all the piece boards for the game pieces.
     */
    private void setPieces() {
        for (int i = 0; i < 15; i++) {
            logger.info("Creating piece boards...");
            PieceBoard piece = new PieceBoard(3, 3, 50, 50,window );
            piece.displayPiece(GamePiece.createPiece(i));
            pieces.add(piece);
        }
    }

    /**
     * Adds all piece to the grid.
     */
    private void setUpGrid() {
        int col = 0;
        int row = 0;

        //Adding boards to grid and setting indexes.
        logger.info("Adding boards to grid and setting indexes...");
        for (int i = 0; i < 15; i++) {
            if (col  >= 5) {
                col = 0;
                row++;
            }
            add(pieces.get(i), col, row);
            GridPane.setMargin(pieces.get(i), new Insets(10,10,10,10));
            col++;
        }

    }

}


