package uk.ac.soton.comp1206.event;

/**
 * Handles the game loop timer updates for animation.
 */
public interface GameLoopListener {

     /**
      * Fired when timer is reset.
      */
     void onGameLoop();
}
