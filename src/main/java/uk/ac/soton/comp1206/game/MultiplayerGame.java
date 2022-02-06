package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BoardUpdate;
import uk.ac.soton.comp1206.event.ConnectionLostListener;
import uk.ac.soton.comp1206.event.LeaderboardChange;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scores.Player;
import uk.ac.soton.comp1206.scores.Sorter;

import java.util.ArrayList;


/**
 * Multiplayer game class.
 * Handles differences from base game class.
 */
public class MultiplayerGame extends Game {

    /**
     * Communicator to send and receive requests to server.
     */
    private Communicator comm;
    /**
     * Data structure to store pieces.
     */
    private ArrayList<GamePiece> pieces = new ArrayList<>();

    private static Logger logger = LogManager.getLogger(MultiplayerGame.class);

    /**
     * Players list.
     */
    private ArrayList<Player> players = new ArrayList<>();

    /**
     * Scores list.
     */
    private ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
    /**
     * Score list property.
     */
    private SimpleListProperty<Pair<String, Integer>> scoreListProperty = new SimpleListProperty<>(FXCollections.observableArrayList(scores));

    /**
     * Property containing latest message received.
     */
    public SimpleStringProperty latestMessage = new SimpleStringProperty();

    /**
     * Listener for leaderboard changes.
     */
    private LeaderboardChange leaderboardChangeListener;
    /**
     * Listener for board updates.
     */
    private BoardUpdate boardUpdateListener;
    /**
     * Listener for loss of connection / server issues.
     */
    private ConnectionLostListener connectionLostListener;

    /**
     * Constructor for game.
     * Sets columns nad rows and other objects for the game.
     *
     * @param cols : rows of grid.
     * @param rows : rows of grid.
     */
    public MultiplayerGame(int cols, int rows, Communicator comm) {
        super(cols, rows);

        this.comm = comm;


        handleCommListeners();
        initializeQueue();

        scoreProperty.addListener((observableValue, number, t1) -> comm.send("SCORE " + getScore()));

        livesProperty.addListener((observableValue, number, t1) -> comm.send("LIVES " + getLives()));

    }

    /**
     * Handles next piece being added to UI and game.
     *
     * @param start : checks if the request is the initial request at the start of the game.
     */
    @Override
    public void nextPiece(Boolean start) {
        logger.info("Multiplayer next piece");

        comm.send(sendBoard());

        if (start) {
            currentPiece = newCurrentPiece();

        } else {
            currentPiece = nextPiece;
            dequeue();
        }
        Platform.runLater(() -> {
            nextPiece = newNextPiece();
            nextPieceListener.nextPiece(new GamePiece[]{currentPiece, nextPiece});
        });
        comm.send("PIECE");
        comm.send("SCORES");


        timer.reset(getTimerDelay());

    }

    /**
     * Handles listeners for the communicator object.
     */
    private void handleCommListeners() {
        comm.addListener(communication -> {
            //Handles new piece being sent.
            if (communication.startsWith("PIECE ")) {
                communication = communication.replace("PIECE ", "");
                // logger.info("##PIECE##  " + communication);
                enqueue(GamePiece.createPiece(Integer.parseInt(communication) - 1));
                System.out.println(pieces.size());
                logger.info("Adding ##PIECE## " + communication);

            }
            //Handles new scores being received.
            else if (communication.startsWith("SCORES ")) {
                communication = communication.replace("SCORES ", "");
                String[] playerSplit = communication.split("\n");

                players.clear();
                scoreListProperty.clear();

                for (String s : playerSplit) {
                    players.add(new Player(s));
                }

                for (Player p : players) {
                    if (p.isDead()) {
                        p.setName(":" + p.getName());
                    }
                    scoreListProperty.add(new Pair<>(p.getName(), p.getScore()));

                    scoreListProperty.sort(new Sorter());
                }

                handleDeaths();

                leaderboardChangeListener.onChange();

                logger.info("##Scores##\t" + communication);
            }
            //Handles messages being received.
            else if (communication.startsWith("MSG ")) {
                communication = communication.replace("MSG ", "");

                latestMessage.set(communication);

            }
            //Handles new boards being received.
            else if (communication.startsWith("BOARD ")) {
                logger.info(communication);
                String player = communication.substring(6, communication.indexOf(":"));
                communication = communication.replace(communication.substring(0, communication.indexOf(":") + 1), "");

                boardUpdateListener.onUpdate(communication, player);

            }
        });
    }

    /**
     * Initializes queue at the start.
     * Preloads 5 piece.
     */
    public void initializeQueue() {
        for (int i = 0; i < 5; i++) {
            comm.send("PIECE");
        }
    }

    /**
     * Dequeues a piece when played.
     *
     * @return : piece to be played.
     */
    private GamePiece dequeue() {
        return pieces.remove(0);
    }

    /**
     * Adds new piece to queue.
     *
     * @param piece : piece to be added.
     */
    private void enqueue(GamePiece piece) {
        pieces.add(piece);
    }

    /**
     * Gets current piece in-game.
     *
     * @return piece.
     */
    public GamePiece newCurrentPiece() {
        return pieces.get(0);
    }

    /**
     * Gets next piece in-game.
     *
     * @return piece or null (if server issues occur).
     */
    public GamePiece newNextPiece() {
        try {
            return pieces.get(1);
        } catch (Exception e) {
            connectionLostListener.onConnectionLost();
            endGameListener.endGame();
        }
        return null;
    }


    @Override
    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");


        setScoreProperty(0);
        setMultiplierProperty(1);
        setLevelProperty(0);
        setLivesProperty(3);

        Platform.runLater(() -> {
            nextPiece(true);
            timer.reset(getTimerDelay());
            gameLoop();
        });
    }

    /**
     * Adds identifier to dead players.
     */
    private void handleDeaths() {
        for (int i = 0; i < scoreListProperty.size(); i++) {
            for (int z = 0; z < players.size(); z++) {
                if (scoreListProperty.get(i).getKey().equals(players.get(z).getName()) && players.get(z).isDead()) {
                    Pair<String, Integer> newItem = new Pair<>(":" + scoreListProperty.get(i).getKey(), scoreListProperty.get(i).getValue());
                    scoreListProperty.remove(scoreListProperty.get(i));
                    scoreListProperty.add(i, newItem);
                }
            }
        }
    }


    /**
     * Gets score list property.
     *
     * @return scoreListProperty.
     */
    public SimpleListProperty<Pair<String, Integer>> scoreListProperty() {
        return scoreListProperty;
    }

    /**
     * Sends user's board to server.
     *
     * @return board message to be sent.
     */
    private String sendBoard() {
        StringBuilder builder = new StringBuilder();
        builder.append("BOARD ");

        for (int i = 0; i < rows; i++) {
            for (int z = 0; z < cols; z++) {
                builder.append((getGrid().get(i, z))).append(" ");
            }
        }
        return builder.toString();
    }

    /**
     * Handles end of game events.
     */
    @Override
    public void endGame() {
        //Show high score screen.
        logger.info("Open high score screen");
        endGameListener.endGame();
    }

    /**
     * Gets latest message property.
     *
     * @return latestMessagePropery.
     */
    public SimpleStringProperty latestMessageProperty() {
        return latestMessage;
    }


    /**
     * Sends message to server.
     *
     * @param message : message to send.
     */
    public void sendMessage(String message) {
        comm.send("MSG " + message);
    }


    /**
     * Adds LeaderboardChangeListener to object.
     *
     * @param listener : listener to be added.
     */
    public void addLeaderboardChangeListener(LeaderboardChange listener) {
        leaderboardChangeListener = listener;
    }

    /**
     * Adds BoardUpdateListener to object.
     *
     * @param listener : listener to be added.
     */
    public void addBoardUpdateListener(BoardUpdate listener) {
        boardUpdateListener = listener;
    }

    /**
     * Adds ConnectionLostListener to object.
     *
     * @param listener : listener to be added.
     */
    public void addConnectionLostListener(ConnectionLostListener listener) {
        connectionLostListener = listener;
    }
}


