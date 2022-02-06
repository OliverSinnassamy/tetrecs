package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Contains and updates list of games for the user to join in the lobby scene.
 */
public class LobbyMenu extends VBox {

    private static Logger logger = LogManager.getLogger(LobbyMenu.class);

    /**
     * Contains gamelist data.
     */
    private final ObservableList<String> observableList = FXCollections.observableArrayList(new ArrayList<>());
    private final SimpleListProperty<String> gameList = new SimpleListProperty<>(observableList);

    /**
     * Game list title.
     */
    private Text title;

    /**
     * Container for list items.
     */
    private VBox gameListContainer = new VBox();
    private ScrollPane scroll;

    /**
     * Button to create new game.
     */
    private Button startNew;

    /**
     * Constructor for lobby menu.
     * Holds list of games for user to join.
     * Initializes containers.
     */
    public LobbyMenu() {
        title = new Text("Game list");
        title.getStyleClass().addAll("gamelist-title");

        BorderPane.setMargin(this, new Insets(20, 0, 0, 0));
        getStyleClass().add("left-menu");

        scroll = new ScrollPane();

        scroll.setContent(gameListContainer);
        scroll.setMinWidth(100);
        scroll.setMinHeight(200);
        scroll.getStyleClass().add("lobby-pane");

        startNew = new Button("New Game");
        startNew.getStyleClass().add("lobby-button");


        setAlignment(Pos.CENTER);

        getChildren().addAll(title, scroll, startNew);
    }

    /**
     * Updates the buttons for the game list.
     */
    public void updateList() {
        gameListContainer.getChildren().clear();

        for (String s : gameList.get()) {
            Button b = new Button(s);
            b.getStyleClass().add("gamelist-button");

            gameListContainer.getChildren().add(b);
        }
    }

    /**
     * Gets the gameList property for list to be updated.
     * Bound to list property of the scene.
     *
     * @return gameList : list property.
     */
    public SimpleListProperty<String> gamelistProperty() {
        return gameList;
    }

    /**
     * Returns the container for the gameList.
     *
     * @return gameListContainer.
     */
    public VBox getGameListContainer() {
        return gameListContainer;
    }

    /**
     * Gets button to start new game for listener to be added in the scene.
     *
     * @return startNew : button.
     */
    public Button getStartNew() {
        return startNew;
    }
}
