package uk.ac.soton.comp1206.event;

/**
 * Handles 'enter' keyboard support.
 */
public interface KeyBoardEnterListener {

    /**
     * Fired when user presses enter in-game.
     * @param place : location to place piece.
     */
    void onEnter(int[][] place);
}
