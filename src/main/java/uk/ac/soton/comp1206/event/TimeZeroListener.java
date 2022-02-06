package uk.ac.soton.comp1206.event;

/**
 * Handles events when timer reaches zero.
 */
public interface TimeZeroListener {

    /**
     * Fired when time reaches zero in a game loop.
     */
    void onZero();
}
