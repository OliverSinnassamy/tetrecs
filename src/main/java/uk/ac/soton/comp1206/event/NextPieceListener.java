package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Handles changes to UI when next piece is spawned.
 */
public interface NextPieceListener {

    /**
     * Fired to trigger new display to PieceBoard in the game UI.
     * @param piece : piece to be displayed on the piece board.
     */
    void nextPiece(GamePiece[] piece);
}
