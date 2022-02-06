package uk.ac.soton.comp1206.event;

/**
 * Handles server errors such as user already in a channel.
 */
public interface ErrorAlert {

    /**
     * Fired when error occurs from server.
     * Triggers alert being sent.
     * @param message : error message to be displayed.
     */
    void onError(String message);
}
