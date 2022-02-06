package uk.ac.soton.comp1206.component;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * UI Component for displaying boards in Spectate mode.
 */
public class SpectateBoard extends VBox {

    /**
     * PieceBoard to display players boards.
     */
    private PieceBoard board;

    /**
     * Handles name of player as title for the board.
     */
    private String name;
    private Text title;

    /**
     * Constructor of SpectateBoard.
     * @param board : board to display.
     * @param name : name of user.
     */
    public SpectateBoard(PieceBoard board, String name){
        this.board = board;
        this.name = name;

        title = new Text(name);
        title.getStyleClass().add("spectate-text");

        setAlignment(Pos.CENTER);

        getChildren().addAll(title, this.board);
    }

    /**
     * Returns PieceBoard of component.
     * @return board.
     */
    public PieceBoard getBoard(){
        return board;
    }

    /**
     * Gets name of player.
     * @return name.
     */
    public String getName(){
        return name;
    }


}
