package uk.ac.soton.comp1206.event;

/**
 * Used to handle chat request from user in-game.
 */
public interface ChatListener {

    /**
     * Fired when chat request is received.
     */
    void onChat();
}
