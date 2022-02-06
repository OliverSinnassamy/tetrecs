package uk.ac.soton.comp1206.component;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Component that holds all the UI elements for score, level, lives and multiplier in-game.
 * Updated via properties.
 */
public class BorderUI extends VBox {

    private Text score, scoreLabel;
    private Text multiplier, multiplierLabel;
    private Text lives, livesLabel;
    private Text level, levelLabel;
    private Text highScore, highScoreLabel;

    private final int DATA_SIZE = 29, LABEL_SIZE = 15;

    private Color TEXT_COLOUR = Color.WHITE;

    private static Logger logger = LogManager.getLogger(BorderUI.class);

    /**
     * Constructor for BorderUI.
     * Creates all the nodes for the different parts of the border.
     * (Score, multiplier, lives, level and next pieces.)
     */
    public BorderUI() {
        logger.info("Setting up nodes for border...");
        handleScore();
        handleMultiplier();
        handleLevel();
        handleLives();
        handleHighScore();


        logger.info("Adding nodes to border...");

        setAlignment(Pos.CENTER);

        getChildren().addAll(scoreLabel, score, multiplierLabel, multiplier, highScoreLabel, highScore, livesLabel, lives,
                levelLabel, level);
    }

    /**
     * Initializes and formats score text and score label.
     */
    private void handleScore() {
        score = new Text();
        score.setFill(TEXT_COLOUR);
        score.setFont(Font.font("Montserrat", FontWeight.BOLD, DATA_SIZE));

        scoreLabel = new Text("SCORE:");
        scoreLabel.getStyleClass().add("border-titles");
    }

    /**
     * Initializes and formats multiplier text and multiplier label.
     */
    private void handleMultiplier() {
        multiplier = new Text();
        multiplier.setFill(TEXT_COLOUR);
        multiplier.setFont(Font.font("Montserrat", FontWeight.BOLD, DATA_SIZE));

        multiplierLabel = new Text("Multiplier");
        multiplierLabel.getStyleClass().add("border-titles");
    }

    /**
     * Initializes and formats level text and level label.
     */
    private void handleLevel() {
        level = new Text();
        level.setFill(TEXT_COLOUR);
        level.setFont(Font.font("Montserrat", FontWeight.BOLD, DATA_SIZE));

        levelLabel = new Text("Level");
        levelLabel.getStyleClass().add("border-titles");
    }

    /**
     * Initializes and formats lives text and lives label.
     */
    private void handleLives() {
        lives = new Text();
        lives.setFill(TEXT_COLOUR);
        lives.setFont(Font.font("Montserrat", FontWeight.BOLD, DATA_SIZE));

        livesLabel = new Text("Lives");
        livesLabel.getStyleClass().add("border-titles");
    }

    /**
     * Initializes and formats high score text and high score label.
     */
    public void handleHighScore() {
        highScore = new Text();
        highScore.setFill(TEXT_COLOUR);
        highScore.setFont(Font.font("Montserrat", FontWeight.BOLD, 30));

        highScoreLabel = new Text("High Score");
        highScoreLabel.setFont(Font.font(LABEL_SIZE));
        highScoreLabel.getStyleClass().add("border-titles");
    }


    /**
     * Gets 'lives' text node.
     *
     * @return lives.
     */
    public Text getLives() {
        return lives;
    }

    /**
     * Gets 'livesLabel' text node.
     *
     * @return livesLabel.
     */
    public Text getLivesLabel() {
        return livesLabel;
    }

    /**
     * Gets 'score' text node.
     *
     * @return score.
     */
    public Text getScore() {
        return score;
    }

    /**
     * Gets 'multiplier' text node.
     *
     * @return multiplier.
     */
    public Text getMultiplier() {
        return multiplier;
    }

    /**
     * Gets 'multiplierLabel' text node.
     *
     * @return multiplierLabel.
     */
    public Text getMultiplierLabel() {
        return multiplierLabel;
    }

    /**
     * Gets 'level' text node.
     *
     * @return level.
     */
    public Text getLevel() {
        return level;
    }

    /**
     * Gets 'highScore' text node.
     *
     * @return highScore.
     */
    public Text getHighScore() {
        return highScore;
    }

    /**
     * Gets 'highScoreLabel' text node.
     *
     * @return highScoreLabel.
     */
    public Text getHighScoreLabel() {
        return highScoreLabel;
    }
}

