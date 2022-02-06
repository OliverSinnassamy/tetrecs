package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.*;
import uk.ac.soton.comp1206.event.ErrorAlert;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

/**
 * Scene to display Multiplayer game.
 */
public class MultiplayerScene extends ChallengeScene {

    /**
     * Leaderboard component to display players scores.
     */
    private LeaderBoard leaderboard = new LeaderBoard("Leaderboard", 200);

    private static Logger logger = LogManager.getLogger(MultiplayerScene.class);

    /**
     * Communicator to send and receive requests.
     */
    private Communicator comm = gameWindow.getCommunicator();

    /**
     * Property of latestMessage.
     */
    private SimpleStringProperty latestMessage = new SimpleStringProperty();

    /**
     * Container of chat component of the multiplayer game.
     */
    private HBox chatContainer = new HBox();

    /**
     * Handles container of end game buttons.
     */
    private VBox endGameContainer;

    /**
     * Grid of boards for spectate mode.
     */
    private GridPane grid;
    /**
     * List of players in the game.
     */
    private ArrayList<String> players;
    /**
     * Container for grid to allow for scrolling.
     */
    private ScrollPane gridScroll;

    /**
     * Listener for server errors.
     */
    private ErrorAlert errorAlert;


    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(leaderboard);

        getBorderUI().getChildren().removeAll(getBorderUI().getMultiplier(), getBorderUI().getMultiplierLabel());
        getBorderUI().getChildren().removeAll(getBorderUI().getHighScore(), getBorderUI().getHighScoreLabel());

        getBorderUI().getChildren().add(2, scrollPane);

        Platform.runLater(() -> gameWindow.getScene().getWindow().setOnCloseRequest(windowEvent -> {
            comm.send("DIE");
            System.exit(0);
        }));

        errorAlert = message -> Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("ERROR");
            alert.setContentText(message);
            alert.initOwner(gameWindow.getScene().getWindow());
            alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> System.out.println("Alert dealt with"));
        });


        chatContainer.getChildren().add(new MessageItem(""));


    }

    /**
     * Sets up basic scene.
     */
    @Override
    public void setupGame() {
        logger.info("Setting up multiplayer game...");

        //Binds properties.
        game = new MultiplayerGame(5, 5, gameWindow.getCommunicator());
        leaderboard.scoreListProperty().bind(((MultiplayerGame) game).scoreListProperty());
        ((MultiplayerGame) game).addLeaderboardChangeListener(() -> leaderboard.display());
        latestMessageProperty().bind(((MultiplayerGame) game).latestMessageProperty());
        latestMessageProperty().addListener((observableValue, s, t1) -> {
            Platform.runLater(() -> {
                MessageItem message = new MessageItem(getLatestMessage());
                chatContainer.getChildren().clear();
                chatContainer.getChildren().add(message);
            });
        });

        boardContainer.getChildren().add(chatContainer);
        chatContainer.getChildren().add(new MessageItem("Type T to chat...", true));
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        super.build();

        ((MultiplayerGame) game).addChatListener(() -> Platform.runLater(() -> {
            boardContainer.getChildren().add(messageSenderContainer());

        }));

        ((MultiplayerGame) game).addBoardUpdateListener((message, player) -> {
            if (grid == null) return;
            Platform.runLater(() -> updatePieceBoards(message, player));
        });

        ((MultiplayerGame) game).addConnectionLostListener(() -> {
            errorAlert.onError("Technical difficulties have ended your game, we can only apologies for this.\n" +
                    "You may choose to spectate other players or end the game here.");
            Platform.runLater(() -> {
                handleEndGameContainer();
                pane.setBottom(null);
            });
        });

        chatContainer.setAlignment(Pos.CENTER);

    }

    /**
     * Gets latest message.
     *
     * @return value of latestMessage.
     */
    public String getLatestMessage() {
        return latestMessage.get();
    }

    /**
     * Gets latest message property.
     *
     * @return latestMessage.
     */
    public SimpleStringProperty latestMessageProperty() {
        return latestMessage;
    }

    /**
     * Handles the message sending container.
     * Allows user to send and receive messages in-game.
     *
     * @return container.
     */
    private HBox messageSenderContainer() {
        HBox container = new HBox();

        container.setAlignment(Pos.CENTER);


        TextField messageEntry = new TextField();
        messageEntry.setMaxHeight(50);

        Button send = new Button("Send");
        send.getStyleClass().add("lobby-button");
        send.setMaxHeight(50);

        send.setOnAction(actionEvent -> ((MultiplayerGame) game).sendMessage(messageEntry.getText()));
        messageEntry.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                ((MultiplayerGame) game).sendMessage(messageEntry.getText());
                boardContainer.getChildren().remove(container);
            }
        });

        container.getChildren().addAll(messageEntry, send);
        return container;
    }

    /**
     * Handles end game events.
     */
    @Override
    public void endGame() {
        game.addEndGameListener(() -> {
            handleEndGameContainer();
            pane.setCenter(endGameContainer);
            pane.setBottom(null);
        });
    }

    /**
     * Creates layout for the end of game.
     */
    private void handleEndGameContainer() {
        HBox buttonContainer = new HBox();
        endGameContainer = new VBox();

        Text text = new Text("Do you wish to spectate or end the game here?");

        Button spectate = new Button("Spectate");
        spectate.getStyleClass().add("lobby-button");

        Button endGame = new Button("End Game");
        endGame.getStyleClass().add("lobby-button");

        spectate.setOnAction(actionEvent -> {
            handleSpectate();
        });
        endGame.setOnAction(actionEvent -> {
            gameWindow.startMultiplayerScores(game);
            comm.send("DIE");
        });

        buttonContainer.getChildren().addAll(spectate, endGame);

        buttonContainer.setAlignment(Pos.CENTER);
        endGameContainer.setAlignment(Pos.CENTER);

        endGameContainer.getChildren().addAll(text, buttonContainer);
    }

    /**
     * Create spectate layout for specate mode.
     */
    private void handleSpectate() {
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        gridScroll = new ScrollPane();

        gridScroll.setContent(grid);
        gridScroll.setFitToWidth(true);

        gridScroll.getStyleClass().add("grid-scroll");

        players = leaderboard.getPlayers();

        int rowCount = 0;

        for (int i = 0; i < players.size(); i++) {
            if (i % 3 == 0) {
                rowCount++;
            }
            //Sets boards on grid.
            SpectateBoard board = new SpectateBoard(new PieceBoard(5, 5, 150, 150, gameWindow), players.get(i));
            grid.add(board, i % 3, rowCount, 1, 1);
            GridPane.setMargin(board, new Insets(10, 10, 10, 10));
            pane.setCenter(gridScroll);
        }

        //Adds end game button to go to score screen.
        Button endGame = new Button("End Game");
        endGame.getStyleClass().add("lobby-button");

        endGame.setOnAction(actionEvent -> {
            gameWindow.startMultiplayerScores(game);
            comm.send("DIE");
        });

        BorderPane.setAlignment(endGame, Pos.CENTER);
        pane.setBottom(endGame);

    }

    /**
     * Updates boards in spectate mode.
     *
     * @param board  board to display.
     * @param player player to which the board belongs.
     */
    private void updatePieceBoards(String board, String player) {
        for (Node n : grid.getChildren()) {
            if (((SpectateBoard) n).getName().equals(player)) {
                ((SpectateBoard) n).getBoard().displayBoard(board);
            }
        }
    }
}
