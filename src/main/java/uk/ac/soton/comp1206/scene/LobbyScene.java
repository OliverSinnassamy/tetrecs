package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.LobbyChat;
import uk.ac.soton.comp1206.component.LobbyMenu;
import uk.ac.soton.comp1206.event.ErrorAlert;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.*;

/**
 * Scene for displaying pre-game lobby for multiplayer.
 */
public class LobbyScene extends BaseScene {

    private static Logger logger = LogManager.getLogger(LobbyScene.class);

    /**
     * Timer to send requests to server.
     */
    private Timer timer;

    /**
     * Timer to send requests for userlist to server.
     */
    private Timer usersTimer;
    /**
     * Communicator to send requests to server.
     */
    private Communicator comm;

    /**
     * Main container of the scene.
     */
    private BorderPane pane;

    /**
     * LobbyChat object when game is joined.
     */
    private LobbyChat chat = new LobbyChat();

    /**
     * Username property of user.
     */
    public SimpleStringProperty userNameProperty = new SimpleStringProperty();
    /**
     * LobbyName Property.
     */
    public SimpleStringProperty lobbyName = new SimpleStringProperty();

    /**
     * Boolean property identifying host status of user.
     */
    public SimpleBooleanProperty hostProperty = new SimpleBooleanProperty();

    /**
     * Property containing list of games.
     */
    public SimpleListProperty<String> gameListProperty = new SimpleListProperty<>();

    /**
     * Error alert listener.
     */
    private ErrorAlert errorAlert;

    /**
     * List of games component.
     */
    private LobbyMenu lobbyMenu = new LobbyMenu();

    /**
     * Constructor to create new Lobby Scene.
     *
     * @param gameWindow
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);

        pane = new BorderPane();

        //Binds lobby name component of chat object.
        chat.getLobbyName().textProperty().bind(lobbyName);
        //Initializes communicator.
        comm = gameWindow.getCommunicator();

        //Handles quitting from server.
        Platform.runLater(() -> getScene().getWindow().setOnCloseRequest(windowEvent -> comm.send("QUIT")));


    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {

        //Binds properties
        chat.getUserName().textProperty().bind(userNameProperty());
        chat.hostProperty().bind(hostProperty());
        gameListProperty.bind(lobbyMenu.gamelistProperty());

        //Fires alert if error listener is triggered.
        errorAlert = message -> Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("ERROR");
            alert.setContentText(message);
            alert.initOwner(gameWindow.getScene().getWindow());
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.out.println("Alert dealt with"));
        });

        getScene().getStylesheets().add(getClass().getResource("/style/" + gameWindow.getTheme() + ".css").toExternalForm());


        gameWindow.themeProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                getScene().getStylesheets().clear();
                getScene().getStylesheets().add(getClass().getResource("/style/default.css").toExternalForm());
                getScene().getStylesheets().add(getClass().getResource("/style/" + gameWindow.getTheme() + ".css").toExternalForm());

            }
        });

    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        setUp(pane);

        handleTimers();
        handleListeners();

        pane.setTop(title());
        pane.setLeft(lobbyMenu);

        lobbyMenu.getStartNew().setOnAction(actionEvent -> {
            logger.info("New game clicked...");
            pane.setCenter(newChannel());
        });

        //Handles enter.
        chat.getMessage().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (chat.getMessage().getText().startsWith("/nick ")) {
                    String newName = chat.getMessage().getText().replace("/nick ", "");
                    comm.send("NICK " + newName);
                } else {
                    comm.send("MSG " + chat.getMessage().getText());
                }
                chat.getMessage().clear();
            }
        });
        //Handles send button pressed.
        chat.getSend().setOnAction(actionEvent -> {
            gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            if (chat.getMessage().getText().startsWith("/nick ")) {
                String newName = chat.getMessage().getText().replace("/nick ", "");
                comm.send("NICK " + newName);
            } else {
                comm.send("MSG " + chat.getMessage().getText());
            }
            chat.getMessage().clear();
        });

        //Handles leave button pressed.
        chat.getLeave().setOnAction(actionEvent -> {
            gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            comm.send("PART");
        });

        //Handles start button pressed.
        chat.getStart().setOnAction(actionEvent -> {
            gameWindow.getMedia().playThemeSound(gameWindow.getTheme(), "button-select.mp3");
            comm.send("START");
        });
    }

    /**
     * Sets up basic scene
     *
     * @param pane : main container.
     */
    private void setUp(BorderPane pane) {
        logger.info("Set up lobby scene...");

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("settings-pane");
        root.getChildren().add(lobbyPane);

        lobbyPane.getChildren().add(pane);
    }

    /**
     * Handles title of screen.
     *
     * @return : title of screen.
     */
    private Text title() {
        Text title = new Text("Multiplayer");
        title.getStyleClass().add("lobby-title");
        title.getStyleClass().add("lobby-text");

        BorderPane.setAlignment(title, Pos.CENTER);

        return title;
    }

    /**
     * Gets user name.
     *
     * @return value of userNameProperty.
     */
    public String getName() {
        return userNameProperty.get();
    }

    /**
     * Gets username property.
     *
     * @return userNameProperty.
     */
    public SimpleStringProperty userNameProperty() {
        return userNameProperty;
    }

    /**
     * Property to determine if user can start game
     *
     * @return host property.
     */
    public SimpleBooleanProperty hostProperty() {
        return hostProperty;
    }

    /**
     * Sets boolean value regarding the status of the user.
     *
     * @param host : true if user is host.
     */
    public void setHostProperty(Boolean host) {
        hostProperty.set(host);
    }

    /**
     * Gets boolean value regarding the status of the user.
     *
     * @return host : true if user is host.
     */
    public Boolean getHost() {
        return hostProperty.get();
    }


    /**
     * Handles listeners on the communicator.
     */
    private void handleListeners() {

        comm.addListener(communication -> {
            if (communication.startsWith("CHANNELS ")) {

                communication = communication.replace("CHANNELS ", "");
                String[] split = communication.split("\n");


                gameListProperty.clear();

                gameListProperty.addAll(Arrays.asList(split));


                Platform.runLater(() -> {
                    lobbyMenu.updateList();

                    for (Node b : lobbyMenu.getGameListContainer().getChildren()) {
                        ((Button) b).setOnAction(actionEvent -> {
                            comm.send("JOIN " + ((Button) b).getText());
                        });
                    }
                });

            } else if (communication.startsWith("HOST")) {
                setHostProperty(true);

                System.out.println(communication);
            } else if (communication.startsWith("USERS ")) {
                communication = communication.replace("USERS ", "");

                String[] split = communication.split("\n");

                logger.info("USERS.... " + communication);

                ArrayList<String> usrs = new ArrayList<>(Arrays.asList(split));
                if (usrs.equals(chat.getUsers())) return;

                Platform.runLater(() -> chat.addUsers(usrs));

            } else if (communication.startsWith("JOIN ")) {
                communication = communication.replace("JOIN ", "");
                lobbyName.set(communication);
                Platform.runLater(() -> pane.setCenter(chat));


                usersTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        comm.send("USERS");
                    }
                }, 0, 2000);


            } else if (communication.startsWith("ERROR ")) {
                communication = communication.replace("ERROR ", "");
                logger.error("ERROR ERROR ERROR...");

                if (communication.contains("You are already in a channel")) {
                    logger.info("Yes");
                    Platform.runLater(() -> pane.setCenter(chat));
                }
                errorAlert.onError(communication);
            } else if (communication.startsWith("MSG ")) {
                communication = communication.replace("MSG ", "");
                logger.info("Message " + communication + " received...");
                chat.addMessage(communication + "\n");
            } else if (communication.startsWith("NICK ") && !communication.contains(":")) {
                userNameProperty.set(communication.replace("NICK ", ""));
            } else if (communication.startsWith("NICK ") && communication.contains(":")) {
                comm.send("USERS");
            } else if (communication.startsWith("PARTED")) {
                logger.info("####PARTED####");
                usersTimer.purge();
                usersTimer.cancel();
                Platform.runLater(() -> pane.setCenter(null));
            } else if (communication.startsWith("START")) {
                logger.info("Start multiplayer name...");
                timer.purge();
                timer.cancel();
                usersTimer.purge();
                usersTimer.cancel();
                Platform.runLater(gameWindow::startMultiplayerGame);
            }

        });
    }

    /**
     * Sends message to server to request list of users.
     */
    private void handleTimers() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                comm.send("LIST");
            }
        }, 0, 1000);

        usersTimer = new Timer();

    }

    /**
     * Allows user to set up new game.
     *
     * @return
     */
    private VBox newChannel() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);

        Text title = new Text("Enter name of game");
        title.getStyleClass().add("lobby-text");

        //TextField to enter game name.
        TextField entry = new TextField();
        VBox.setMargin(entry, new Insets(20, 0, 0, 0));
        entry.setMaxWidth(400);

        //Allows enter key to submit new game name.
        entry.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                comm.send("CREATE " + entry.getText());
            }
        });

        //Submits new game name.
        Button submit = new Button("Submit");
        VBox.setMargin(submit, new Insets(20, 0, 0, 0));
        submit.getStyleClass().add("lobby-button");


        submit.setOnAction(actionEvent -> {
            comm.send("CREATE " + entry.getText());
        });

        container.getChildren().addAll(title, entry, submit);

        return container;
    }

    /**
     * Handles Escape event.
     */
    @Override
    public void handleKeyBoard() {
        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
                comm.send("PART");
                usersTimer.purge();
                usersTimer.cancel();
            }

        });
    }


}
