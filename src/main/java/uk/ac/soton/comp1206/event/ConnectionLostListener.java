package uk.ac.soton.comp1206.event;

/**
 * Handles server issues during multiplayer game.
 */
public interface ConnectionLostListener {

    /**
     * Fired if blocks aren't received in time.
     * Ends the game.
     */
    void onConnectionLost();
}
