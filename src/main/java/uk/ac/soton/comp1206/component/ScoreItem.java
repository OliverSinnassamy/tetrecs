package uk.ac.soton.comp1206.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * UI representation of score elements in the scoreList.
 */
public class ScoreItem extends HBox {

    /**
     * Text nodes to display name and score of player.
     */
    private Text name = new Text();
    private Text score = new Text();

    /**
     * Constructor for ScoreItem.
     * Sets styles and alignment of UI nodes.
     *
     * @param n
     * @param s
     */
    public ScoreItem(String n, Integer s) {
        this.name.setText(n + ": ");
        this.score.setText(String.valueOf(s));

        name.setFill(Color.WHITE);
        score.setFill(Color.WHITE);

        name.getStyleClass().add("score-list");
        score.getStyleClass().add("score-list");

        HBox.setMargin(score, new Insets(0, 0, 0, 10));

        setAlignment(Pos.CENTER);

        getChildren().addAll(name, score);
    }

    /**
     * Returns name Text Node.
     *
     * @return name.
     */
    public Text getName() {
        return name;
    }

    /**
     * Returns score Text Node.
     *
     * @return score.
     */
    public Text getScore() {
        return score;
    }
}
