package uk.ac.soton.comp1206.event;

/**
 * Used to handle keyboard support for changing overlays.
 */
public interface CurrentSquareListener {

    /**
     * Fired when the player aims at different square.
     * @param toggle : toggles overlay.
     */
    void onChange(boolean toggle);
}
