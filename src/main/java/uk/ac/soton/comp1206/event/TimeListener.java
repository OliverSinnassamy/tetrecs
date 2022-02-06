package uk.ac.soton.comp1206.event;

/**
 * Handles changes to time in timer.
 */
public interface TimeListener {

    /**
     * Fired every second of the timer when the time changes.
     */
    void onSecondChange();
}
