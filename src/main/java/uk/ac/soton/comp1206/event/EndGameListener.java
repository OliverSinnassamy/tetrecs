package uk.ac.soton.comp1206.event;

/**
 * Handles end of game events.
 */
public interface EndGameListener {

    /**
     * Fired when lives reaches zero or server problems occur.
     */
    void endGame();
}
