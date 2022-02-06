package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.TimeListener;
import uk.ac.soton.comp1206.event.TimeZeroListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer to handle game loops.
 */
public class GameTimer {


    /**
     * Time property of timer.
     */
    public SimpleIntegerProperty time = new SimpleIntegerProperty();
    /**
     * Listener for changing time.
     */
    private TimeListener timeListener;
    /**
     * Listeners for triggering events when time is zero.
     */
    private ArrayList<TimeZeroListener> zeroListeners = new ArrayList<>();
    /**
     * Listener for starting a new game loop.
     */
    private GameLoopListener gameLoopListener;
    /**
     * Timer for game loop.
     */
    private Timer timer;

    private static Logger logger = LogManager.getLogger(GameTimer.class);


    /**
     * Constructor for game timer.
     * Starts the timer.
     */
    public GameTimer() {
        start();
    }


    /**
     * Sets time according to delay.
     * Starts new game loop.
     *
     * @param delay : time for turn.
     */
    public void reset(int delay) {
        time.set(delay);
        if (timeListener != null) {
            timeListener.onSecondChange();
        }
        gameLoopListener.onGameLoop();


    }

    /**
     * Starts timer at beginning of game.
     */
    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    //Changes time every second.
                    time.set(time.get() - 1000);
                    if (time.get() <= 0) {
                        for (TimeZeroListener z : zeroListeners) {
                            z.onZero();
                        }
                    }
                    //Fires method attached to listener.
                    if (timeListener != null) {
                        timeListener.onSecondChange();
                    }
                });
            }
        }, 0, 1000);

    }

    /**
     * Adds ZeroListener to object.
     *
     * @param listener : listener to be added.
     */
    public void addZeroListener(TimeZeroListener listener) {
        zeroListeners.add(listener);
    }

    /**
     * Adds TimeListener to object.
     *
     * @param listener : listener to be added.
     */
    public void addTimeListener(TimeListener listener) {
        timeListener = listener;
    }

    /**
     * Sets time property.
     *
     * @param time : time to be set.
     */
    public void setTime(int time) {
        this.time.set(time);
    }


    /**
     * Gets value of time.
     *
     * @return time value.
     */
    public int getTime() {
        return time.get();
    }


    /**
     * Gets time property.
     *
     * @return time.
     */
    public SimpleIntegerProperty timeProperty() {
        return time;
    }


    /**
     * Adds GameLoopListener to object.
     *
     * @param listener : listener to be added.
     */
    public void addGameLoopListener(GameLoopListener listener) {
        gameLoopListener = listener;
    }

    /**
     * Handles end of game, cancels timer.
     */
    public void end() {
        timer.cancel();
        timer.purge();
    }
}
