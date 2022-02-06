package uk.ac.soton.comp1206.event;

/**
 * Handles fail sound being played.
 */
public interface FailSoundListener {

    /**
     * Fired when user tries to play piece in invalid place.
     */
    void onFail();
}