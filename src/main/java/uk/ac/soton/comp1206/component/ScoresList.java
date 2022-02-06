package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.media.Animator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Component containing list of scores for leaderboard and score screen.
 */
public class ScoresList extends VBox {

    private static final Logger logger = LogManager.getLogger(ScoresList.class);

    /**
     * List property containing scores.
     */
    protected final SimpleListProperty<Pair<String, Integer>> scoresList = new SimpleListProperty<>();
    protected ArrayList<ScoreItem> items = new ArrayList<>();

    /**
     * Container of score items.
     */
    protected VBox itemContainer = new VBox();

    /**
     * Counter for timer.
     */
    int i = 0;


    /**
     * Constructor for new custom ScoresList component.
     *
     * @param text : provides text for the title.
     */
    public ScoresList(String text) {
        Text title = new Text(text);
        title.getStyleClass().add("scorelist-title");
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        setMaxWidth(400);
        setMaxHeight(400);

        itemContainer.setMaxHeight(350);

        getChildren().addAll(title, itemContainer);
    }

    /**
     * Constructor for new custom ScoresList component.
     *
     * @param text  : provides text for the title.
     * @param width : width of scoreList.
     */
    public ScoresList(String text, int width) {
        Text title = new Text(text);
        title.getStyleClass().add("scorelist-title");
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        setMaxWidth(width);
        setMaxHeight(width);

        getChildren().addAll(title, itemContainer);
    }

    /**
     * Displays score items of the list.
     */
    public void display() {
        logger.info("Displaying scores...");

        int colourCounter = 0;

        logger.info("Setting up score items...");
        for (Pair<String, Integer> l : scoresList) {
            //Creating item.
            ScoreItem item;
            if (l.getKey().startsWith(":")) {
                item = new ScoreItem(l.getKey().replace(":", ""), l.getValue());
            } else {
                item = new ScoreItem(l.getKey(), l.getValue());
            }

            //Setting colours
            if (colourCounter == 10) {
                colourCounter = 0;
            }
            item.getName().setFill(colours[colourCounter]);
            item.getScore().setFill(colours[colourCounter]);
            colourCounter++;

            item.setOpacity(0);
            items.add(item);
            itemContainer.getChildren().add(item);
        }

        logger.info("Revealing score items...");
        //Reveal
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (i == items.size()) {
                    timer.purge();
                    timer.cancel();
                } else {
                    reveal(items.get(i));
                    i++;
                }

            }
        }, 0, 250);
        logger.info("All scores displayed...");
    }

    /**
     * Returns list property for score data.
     *
     * @return scoresList.
     */
    public SimpleListProperty<Pair<String, Integer>> scoreListProperty() {
        return scoresList;
    }

    /**
     * Animates the reveal of scoreItems.
     *
     * @param item
     */
    public void reveal(Node item) {
        Animator anim = new Animator();
        anim.scoreListFade(item);
    }

    /**
     * Contains colours of score items.
     */
    protected static final Color[] colours = {
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.LIME,
            Color.GREEN,
            Color.DARKTURQUOISE,
            Color.AQUA,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };
}
