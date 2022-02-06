package uk.ac.soton.comp1206.event;

/**
 * Handles rotating of a piece when right click is pressed.
 */
public interface RightClickedListener {

    /**
     * Fired when right click is pressed.
     * Handles the rotating of a piece.
     */
    void rotatePiece();
}
