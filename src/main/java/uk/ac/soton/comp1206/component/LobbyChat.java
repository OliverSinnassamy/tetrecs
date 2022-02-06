package uk.ac.soton.comp1206.component;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.media.Media;
import java.util.ArrayList;

/**
 * Handles the chat element of the lobby menu.
 * Displays controls for starting and leaving games.
 * Allows user to send and receive messages.
 */
public class LobbyChat extends BorderPane {

    private static Logger logger = LogManager.getLogger(LobbyChat.class);

    /**
     * Titles of sections
     */
    private Text lobbyName = new Text("Title Holder");
    private Text userName = new Text("Name Holder");

    /**
     * Data arrays for chat and user list.
     */
    private ArrayList<String> users = new ArrayList<>();
    private ArrayList<String> messagesRaw = new ArrayList<>();
    private ObservableList<String> messages = FXCollections.observableArrayList(messagesRaw);


    private String name = "";

    /**
     * Buttons for scene control.
     */
    private Button send;
    private TextField messageEntry;
    private Button leave;
    private Button start;

    /**
     * Containers for interface.
     */
    private VBox userList = new VBox();
    private HBox titleContainer;
    private VBox rightHolder = new VBox();
    private VBox messageHolder;

    /**
     * Property for determining whether user is host of lobby.
     * Used to display start button if true.
     */
    public SimpleBooleanProperty hostProperty = new SimpleBooleanProperty();

    /**
     * Handles message sounds.
     */
    private final Media media = new Media();

    /**
     * Constructor for lobby chat.
     * Sets up containers and buttons.
     */
    public LobbyChat() {
        setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null)));

        BorderPane.setMargin(this, new Insets(20, 20, 0, 50));

        titleContainer = new HBox();
        titleContainer.getChildren().addAll(lobbyName, userName);

        HBox messageContainer = new HBox();

        lobbyName.setText(name);

        //Adds styling to titles.
        userName.getStyleClass().add("lobby-text");
        lobbyName.getStyleClass().add("lobby-text");

        HBox.setMargin(lobbyName, new Insets(0, 20, 0, 20));


        ScrollPane scroll = new ScrollPane();
        messageHolder = new VBox();
        messageHolder.setAlignment(Pos.TOP_LEFT);

        //Contains messages, allows for scrolls.
        scroll.setContent(messageHolder);
        scroll.setFitToWidth(true);


        //TextField for typing message.
        messageEntry = new TextField();
        HBox.setHgrow(messageEntry, Priority.ALWAYS);
        messageEntry.setMaxHeight(50);

        //Button to send message.
        send = new Button("Send");
        send.getStyleClass().add("lobby-button");
        send.setMaxHeight(50);

        //Start button to begin multiplayer game if the user is the host.
        start = new Button("Start game");
        start.getStyleClass().add("lobby-button-right");
        start.setMaxWidth(150);

        //Adds listener for host property to display start button.
        hostProperty.addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                System.out.println("Change");
                Platform.runLater(() -> {
                    rightHolder.getChildren().add(0, start);
                });
            }
        });

        //Adds button to leave games.
        leave = new Button("Leave game");
        leave.getStyleClass().add("lobby-button-right");
        leave.setFont(new Font(15));
        leave.setMaxWidth(150);
        rightHolder.getChildren().add(leave);

        //Adds title.
        setTop(titleContainer);

        messageContainer.getChildren().addAll(messageEntry, send);
        setBottom(messageContainer);

        //Listens for new messages and displays them.
        messages.addListener((ListChangeListener<String>) change -> {
            Platform.runLater(() -> {
                logger.info(messages.get(0));
                displayMessage(new MessageItem(messages.get(messages.size() - 1)));
                media.playSound("message.wav");
            });
        });

        //Adds user list to interface.
        userList.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null)));
        rightHolder.getChildren().add(userList);
        setRight(rightHolder);

        //Adds message board to interface.
        setCenter(scroll);
        scroll.getStyleClass().add("message-holder");
    }

    /**
     * Creates and formats list of users.
     */
    private void handleUserList() {
        Text title = new Text("Users in lobby:");
        title.getStyleClass().add("user-list-title");
        VBox.setMargin(title, new Insets(0, 0, 20, 0));

        userList.setMaxWidth(150);

        logger.info(users.get(0));

        userList.getChildren().clear();
        userList.getChildren().add(title);

        for (String s : users) {
            Text user = new Text(s);
            user.getStyleClass().add("user-list");
            VBox.setMargin(user, new Insets(0, 0, 10, 0));
            userList.getChildren().add(user);
        }
    }

    /**
     * Adds message to message array.
     *
     * @param message : message to be added.
     */
    public void addMessage(String message) {
        messages.add(message);
    }

    /**
     * Displays message on screen.
     *
     * @param item : new message to be displayed.
     */
    private void displayMessage(MessageItem item) {
        messageHolder.getChildren().addAll(item);
    }

    /**
     * Gets name of user.
     *
     * @return userName.
     */
    public Text getUserName() {
        return userName;
    }

    /**
     * Gets name of lobby.
     *
     * @return lobbyName.
     */
    public Text getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets message TextField.
     * Used to send message from user.
     *
     * @return messageEntry.
     */
    public TextField getMessage() {
        return messageEntry;
    }

    /**
     * Gets send button to add onAction listener.
     *
     * @return send : button.
     */
    public Button getSend() {
        return send;
    }

    /**
     * Gets host property.
     *
     * @return hostProperty.
     */
    public SimpleBooleanProperty hostProperty() {
        return hostProperty;
    }

    /**
     * Adds users to user-list.
     *
     * @param usrs
     */
    public void addUsers(ArrayList<String> usrs) {
        users = usrs;
        handleUserList();
    }

    /**
     * Returns users list.
     *
     * @return users.
     */
    public ArrayList<String> getUsers() {
        return users;
    }

    /**
     * Gets start button for onAction listener to be set.
     *
     * @return start : button.
     */
    public Button getStart() {
        return start;
    }

    /**
     * Gets leave button for onAction listener to be set.
     *
     * @return leave : button.
     */
    public Button getLeave() {
        return leave;
    }
}
