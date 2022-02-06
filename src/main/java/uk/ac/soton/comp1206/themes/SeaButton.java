package uk.ac.soton.comp1206.themes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Button design for sea theme menu.
 */
public class SeaButton extends StackPane {

    /**
     * Holds porthole image.
     */
    private final ImageView image;
    /**
     * Button for menu event handling.
     */
    private final Button button;
    /**
     * Background image of SeaButton.
     */
    private final ImageView background;


    public SeaButton(Button b, double bWidth, double mWidth){
        button = b;

        button.setMaxWidth(bWidth);

        setMaxWidth(mWidth);

        //Sets image and size of button image.
        image = new ImageView(new Image(getClass().getResource("/images/sea/button.png").toExternalForm()));
        image.setFitHeight(75);
        image.setPreserveRatio(true);

        //Sets image and size of background.
        background = new ImageView(new Image(getClass().getResource("/images/sea/button-background.png").toExternalForm()));
        background.setFitWidth(200);
        background.setFitHeight(50);

        setAlignment(Pos.CENTER);

        setAlignment(image, Pos.CENTER_LEFT);

        getChildren().addAll(background, button,image);
    }
}
