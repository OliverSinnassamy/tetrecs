package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scores.ScoreFileHandler;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Scene to display scores at the end of the game.
 */
public class ScoresScene extends BaseScene {


    private static Logger logger = LogManager.getLogger(ScoresScene.class);
    /**
     * Local scores list property.
     */

    private ArrayList<Pair<String, Integer>> observableRemoteScores = new ArrayList<>();


    private SimpleListProperty<Pair<String, Integer>> localScores = new SimpleListProperty<>();
    /**
     * Remote scores list property.
     */

    private SimpleListProperty<Pair<String, Integer>> remoteScores = new SimpleListProperty<>(FXCollections.observableArrayList(observableRemoteScores));


    /**
     * File containing high scores.
     */

    private HBox scoreListContainer = new HBox();

    /**
     * Displays local names and scores on screen.
     */
    private ScoresList scoreListLocal;

    /**
     * Displays online names and scores on screen.
     */
    private ScoresList scoreListOnline = new ScoresList("Online Scores");


    /**
     * Game object containing score data.
     */
    private Game game;

    /**
     * Main container of the scene.
     */
    private BorderPane pane;

    /**
     * Gets scores from file.
     */
    private ScoreFileHandler handler = new ScoreFileHandler();
    /**
     * Boolean value to determine whether to display local scores or multiplayer scores.
     */
    private Boolean multiplayer = false;

    /**
     * Handles timer to exit the screen automatically.
     */
    private Timer timer = new Timer();
    /**
     * Time value for the timer.
     */
    private int time;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;

        scoreListLocal = new ScoresList("Local Scores");

        scoreListOnline = new ScoresList("Online Scores");


    }

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow  the game window
     * @param game        game to take scores from.
     * @param multiplayer determines whether the game was a single or multiplayer game.
     */
    public ScoresScene(GameWindow gameWindow, Game game, Boolean multiplayer) {
        super(gameWindow);
        this.multiplayer = multiplayer;
        this.game = game;

        scoreListOnline = new ScoresList("Online Scores");
        scoreListLocal = new ScoresList("This game");

    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
        logger.info("Initialising scene...");

        getScene().getStylesheets().add(getClass().getResource("/style/" + gameWindow.getTheme() + ".css").toExternalForm());

        //Add listener to change the theme.
        gameWindow.themeProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                getScene().getStylesheets().clear();
                getScene().getStylesheets().add(getClass().getResource("/style/default.css").toExternalForm());
                getScene().getStylesheets().add(getClass().getResource("/style/" + gameWindow.getTheme() + ".css").toExternalForm());

            }
        });

        //Display multiplayer scores.
        if (multiplayer) {
            localScores = ((MultiplayerGame) game).scoreListProperty();
            scoreListLocal.scoreListProperty().bind(localScores);

        } else {
            try {
                localScores = handler.loadScores();
                scoreListLocal.scoreListProperty().bind(localScores);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Checks if score is a new high score.
        logger.info("Checking scores..." + game.getScore());
        if (checkScore(game.getScore()) && !multiplayer) {
            logger.info("True");
            pane.setCenter(nameEntry());
        } else {
            loadOnlineScores();
            pane.setCenter(scoreListContainer);

            scoreListLocal.display();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (time == 15) {
                        Platform.runLater(gameWindow::startMenu);
                        timer.purge();
                        timer.cancel();
                        game = null;

                    } else {
                        time++;
                    }
                }
            }, 0, 1000);
        }

        //Displays online scores.
        scoreListOnline.scoreListProperty().bind(remoteScores);



    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building scene...");

        pane = new BorderPane();
        setUp(pane);

        pane.setTop(title());

        scoresList();




    }

    /**
     * Handles Escape event.
     */
    @Override
    protected void handleKeyBoard() {
        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                timer.purge();
                timer.cancel();
                Platform.runLater(gameWindow::startMenu);
                game = null;
            }
        });
    }

    /**
     * Sets up basic scene.
     *
     * @param pane main container of the scene.
     */
    private void setUp(BorderPane pane) {
        logger.info("Set up...");
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var scorePane = new StackPane();
        scorePane.setMaxWidth(gameWindow.getWidth());
        scorePane.setMaxHeight(gameWindow.getHeight());
        scorePane.getStyleClass().add("score-pane");
        root.getChildren().add(scorePane);

        scorePane.getChildren().add(pane);
    }

    /**
     * Initalizes and formats title.
     *
     * @return
     */
    private Text title() {
        Text title = new Text("High Scores");
        title.getStyleClass().add("scores-title");
        BorderPane.setMargin(title, new Insets(20, 0, 0, 0));
        BorderPane.setAlignment(title, Pos.CENTER);

        return title;
    }

    /**
     * Handles scorelist components.
     */
    private void scoresList() {
        logger.info("Scorelist...");

        scoreListContainer.setAlignment(Pos.CENTER);
        scoreListContainer.setMaxWidth(400);
        Platform.runLater(() -> scoreListContainer.setMaxHeight(scoreListLocal.getHeight() + 200));
        HBox.setMargin(scoreListLocal, new Insets(0, 10, 0, 0));
        HBox.setMargin(scoreListOnline, new Insets(0, 0, 0, 10));

        scoreListContainer.getStyleClass().add("scorelist-background");

        scoreListContainer.getChildren().addAll(scoreListLocal, scoreListOnline);

    }

    /**
     * Checks if score is new high score.
     *
     * @param score : score to be checked.
     * @return true if new high score / false if not.
     */
    private Boolean checkScore(int score) {
        for (Pair<String, Integer> l : localScores) {
            if (score >= l.getValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Container to enter new name if high score is achieved.
     *
     * @return container.
     */
    private HBox nameEntry() {
        HBox container = new HBox();

        TextField nameEntry = new TextField();
        nameEntry.setPromptText("Enter nickname...");
        nameEntry.setMaxHeight(50);

        Button submit = new Button("Submit");
        submit.getStyleClass().add("lobby-button");
        submit.setMaxHeight(50
        );
        submit.setOnAction(actionEvent -> {
                    String name = nameEntry.getText();
                    int score = game.getScore();
                    //Insert into local scores.
                    insertInto(name, score, localScores);
                    try {
                        handler.writeScores(localScores.get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Send to server.
                    writeOnlineScore(name, score);
                    loadOnlineScores();

                    pane.setCenter(scoreListContainer);
                    scoreListLocal.display();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (time == 15) {
                        Platform.runLater(gameWindow::startMenu);
                        timer.purge();
                        timer.cancel();
                        game = null;

                    } else {
                        time++;
                    }
                }
            }, 0, 1000);
                }
        );

        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(nameEntry, submit);

        return container;
    }

    /**
     * Inserts score into list if high enough,
     *
     * @param name  name of player.
     * @param score score of player.
     * @param list  list to add to.
     * @return list.
     */
    private SimpleListProperty<Pair<String, Integer>> insertInto(String name, int score, SimpleListProperty<Pair<String, Integer>> list) {
        for (int i = 0; i < list.size(); i++) {
            if (score >= list.get(i).getValue()) {
                list.add(i, new Pair<>(name, score));
                break;
            }
        }
        list.remove(list.size() - 1);

        System.out.println(list.size());
        return list;
    }

    /**
     * Loads scores from server.
     */
    private void loadOnlineScores() {
        Communicator comm = gameWindow.getCommunicator();
        logger.info("Loading online scores...");

        //Sends request to server.
        comm.send("HISCORES UNIQUE");

        //Adds listener to server.
        comm.addListener(communication -> {

            if (!(communication.startsWith("HISCORES"))) return;

            communication = communication.replace("HISCORES", "");

            String[] scores = communication.split("\n");

            remoteScores.clear();
            //Adds scores to list.
            for (int i = 0; i < 10; i++) {
                String name = scores[i].substring(0, scores[i].indexOf(":"));
                String score = scores[i].substring(scores[i].indexOf(":") + 1);
                remoteScores.add(new Pair<>(name, Integer.parseInt(score)));
            }
            logger.info("DISPLAY ONLINE SCORES...");

            System.out.println(remoteScores.size());

            Platform.runLater(() -> scoreListOnline.display());
        });
    }

    /**
     * Sends new high score to server.
     *
     * @param name  name of player.
     * @param score score of player.
     */
    private void writeOnlineScore(String name, int score) {
        Communicator comm = gameWindow.getCommunicator();

        comm.send("HISCORE " + name + ":" + score);

    }

}
