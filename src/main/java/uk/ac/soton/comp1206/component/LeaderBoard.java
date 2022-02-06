package uk.ac.soton.comp1206.component;

import javafx.application.Platform;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Sub class of Scorelist.
 * Displays users and scores during multiplayer game.
 * Updated when scores are received from servers.
 */
public class LeaderBoard extends ScoresList {

    private static final Logger logger = LogManager.getLogger(LeaderBoard.class);

    /**
     * Creates new custom ScoresList component.
     *
     * @param text : provides text for the title.
     */
    public LeaderBoard(String text, int width) {
        super(text, width);
    }


    /**
     * Displays leaderboard items.
     */
    @Override
    public void display() {
        logger.info("Displaying scores...");

        items.clear();

        int colourCounter = 0;


        logger.info("Setting up score items...");
        for (Pair<String, Integer> l : scoresList) {
            ScoreItem item;
            // Creating item, handling deaths.
            if (l.getKey().startsWith(":")) {
                item = new ScoreItem(l.getKey().replace(":", ""), l.getValue());
                item.getName().getStyleClass().add("dead-player");
                item.getScore().getStyleClass().add("dead-player");

            } else {
                item = new ScoreItem(l.getKey(), l.getValue());

                item.getName().getStyleClass().clear();
                item.getScore().getStyleClass().clear();
            }

            //Adds leaderboard styling.
            item.getName().getStyleClass().add("leaderboard-item");
            item.getScore().getStyleClass().add("leaderboard-item");

            //Setting colours
            item.getName().setFill(colours[colourCounter]);
            item.getScore().setFill(colours[colourCounter]);
            colourCounter++;

            items.add(item);
        }
        addAll();
        logger.info("All scores displayed...");
    }

    /**
     * Adds all items to the leaderboard.
     */
    public void addAll() {
        Platform.runLater(() -> {
                    if (itemContainer.getChildren().size() != 0) {
                        itemContainer.getChildren().clear();
                    }
                    itemContainer.getChildren().addAll(items);
                }
        );
    }

    /**
     * Returns list of inital players to be used for specating the end of a multiplayer game.
     *
     * @return users: list of users.
     */
    public ArrayList<String> getPlayers() {
        ArrayList<String> users = new ArrayList<>();

        for (Pair<String, Integer> l : scoresList) {
            users.add(l.getKey());
        }

        return users;
    }
}
