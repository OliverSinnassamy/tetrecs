package uk.ac.soton.comp1206.event;

/**
 * Handles sound for placing piece.
 */
public interface PlaceSoundListener {

    /**
     * Fired when piece is played in a valid place on the board.
     */
    void onPlace();
}
