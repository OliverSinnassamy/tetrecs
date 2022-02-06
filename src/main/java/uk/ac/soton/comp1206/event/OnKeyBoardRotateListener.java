package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Handles rotate by keyboard.
 */
public interface OnKeyBoardRotateListener {

    /**
     * Fired when keyboard controls for rotate are pressed.
     * @param piece
     */
    void OnKeyBoardRotate(GamePiece piece);
}
