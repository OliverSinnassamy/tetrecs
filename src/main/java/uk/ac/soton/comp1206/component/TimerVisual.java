package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Visual representation of in-game timer.
 */
public class TimerVisual extends HBox {

    /**
     * Time property to display.
     */
    SimpleIntegerProperty time = new SimpleIntegerProperty();
    /**
     * UI component representing time.
     */
    private final Rectangle timerBox;

    /**
     * Constructor of TimerVisual.
     * Handles display of components.
     */
    public TimerVisual() {
        timerBox = new Rectangle(680, 35);
        timerBox.setFill(Color.GREEN);

        setMaxHeight(35);
        setMaxWidth(690);

        setAlignment(Pos.CENTER);

        getChildren().add(timerBox);
        setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null)));
    }

    /**
     * Returns value of time property.
     * @return value of time.
     */
    public Integer getTime() {
        return time.get();
    }

    /**
     * Gets time property.
     * @return time.
     */
    public SimpleIntegerProperty timeProperty() {
        return time;
    }

    /**
     * Updates the colours of the timer depending on the time.
     */
    public void updateDisplay() {
        if (time.get() <= 3000) {
            timerBox.setFill(Color.RED);
        } else if (time.get() <= 6000) {
            timerBox.setFill(Color.ORANGE);
        }
    }

    /**
     * Gets rectangle providing representation of timer.
     * @return timerBox.
     */
    public Rectangle getTimerBox(){
        return timerBox;
    }
}


