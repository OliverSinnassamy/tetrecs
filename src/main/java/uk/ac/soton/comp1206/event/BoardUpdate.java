package uk.ac.soton.comp1206.event;

/**
 * Handles board updates received from server.
 */
public interface BoardUpdate {

    /**
     * Used to display data to boards in spectate mode.
     * @param message : board representation as String.
     * @param player : player that the board represents.
     */
    void onUpdate(String message, String player);
}
