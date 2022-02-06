package uk.ac.soton.comp1206.media;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class Animator {

    /**
     * Counter for array handling in timers.
     */
    private int counter = 0;

    private static Logger logger = LogManager.getLogger(Animator.class);

    /**
     * Handles display of score list.
     * @param node node to be faded in.
     */
    public void scoreListFade(Node node) {
        FadeTransition fade = new FadeTransition();

        fade.setFromValue(0);
        fade.setToValue(100);
        fade.setNode(node);

        fade.play();
    }

    /**
     * Fades the introduction image in.
     * @param node node to be faded in.
     */
    public void introFade(Node node) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (node.getOpacity() == 1) {
                    stop();
                }

                node.setOpacity(node.getOpacity() + 0.003);
            }
        };
        timer.start();
    }

    /**
     * Handles animation of timer rectangle.
     * @param node node to be animated.
     * @param dur duration of animation.
     */
    public void timerBar(Rectangle node, int dur) {
        node.setWidth(680);
        var duration = Duration.millis(dur);
        Timeline timeline = new Timeline(new KeyFrame(duration, new KeyValue(node.widthProperty(), 0, Interpolator.EASE_BOTH)));
        timeline.play();
    }

    /**
     * Animates menu screen title.
     * @param container : holds title letters.
     */
    public void titleDisplay(HBox container) {
        FillTransition[] transitions = new FillTransition[container.getChildren().size()];

        for (int i = 0; i < container.getChildren().size(); i++) {
            Text t = (Text) container.getChildren().get(i);
            FillTransition trans = new FillTransition(Duration.millis(1000), t, Color.WHITE, Color.TRANSPARENT);
            trans.setAutoReverse(true);
            transitions[i] = trans;
        }

        //Timer to start transition.
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (counter == 7) {
                    counter = 0;
                }
                transitions[counter].play();
                counter++;
            }
        }, 0, 25);
    }

    /**
     * Animates shake of button
     * @param n node to be animated.
     */
    public void shakeButton(Node n){
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(n.rotateProperty(), -15)), new KeyFrame(Duration.millis(100), new KeyValue(n.rotateProperty(), 15)));
        timeline.setCycleCount(2);

        timeline.setOnFinished(actionEvent -> n.rotateProperty().set(0));
        timeline.play();
    }
}
