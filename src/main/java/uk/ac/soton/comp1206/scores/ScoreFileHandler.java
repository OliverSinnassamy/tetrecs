package uk.ac.soton.comp1206.scores;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

/**
 * Reads and writes scores to/from file.
 */
public class ScoreFileHandler {

    /**
     * File to be read and written to.
     */
    private File scoresFile = new File("file.txt");

    private static Logger logger = LogManager.getLogger(ScoreFileHandler.class);

    /**
     * Constructor for ScoreFileHandler class.
     * Creates file and fills with default scores if it does not exist.
     */
    public ScoreFileHandler() {
        if (!scoresFile.exists()) {
            try {
                scoresFile.createNewFile();
                writeScores(defaultScores());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads scores from file.
     *
     * @return list of scores.
     * @throws IOException : handles file IO errors.
     */
    public SimpleListProperty<Pair<String, Integer>> loadScores() throws IOException {
        ArrayList<Pair<String, Integer>> localScores = new ArrayList<>();
        BufferedReader buffer = new BufferedReader(new FileReader(scoresFile));

        String line;

        logger.info("Reading file...");

        //Iterates through every line of the file and addes to score list.
        while ((line = buffer.readLine()) != null) {
            String[] lineSplit = line.split(":");
            localScores.add(new Pair<>(lineSplit[0], Integer.parseInt(lineSplit[1])));

        }

        logger.info("Reading complete...");
        return new SimpleListProperty<>(FXCollections.observableArrayList(localScores));
    }

    /**
     * Writes scores to file.
     *
     * @param scores : scores to be written to file.
     * @throws IOException : handles IO errors.
     */
    public void writeScores(ObservableList<Pair<String, Integer>> scores) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(scoresFile));
        for (Pair<String, Integer> p : scores) {
            writer.write(p.getKey() + ":" + p.getValue());
            writer.write("\n");
        }

        writer.close();
    }

    /**
     * Default scores to be added to file if it didn't exist.
     *
     * @return default scores.
     */
    private ObservableList<Pair<String, Integer>> defaultScores() {
        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
        scores.add(new Pair<>("Level 10", 10000));
        scores.add(new Pair<>("Level 9", 9000));
        scores.add(new Pair<>("Level 8", 8000));
        scores.add(new Pair<>("Level 7", 7000));
        scores.add(new Pair<>("Level 6", 6000));
        scores.add(new Pair<>("Level 5", 5000));
        scores.add(new Pair<>("Level 4", 4000));
        scores.add(new Pair<>("Level 3", 3000));
        scores.add(new Pair<>("Level 2", 2000));
        scores.add(new Pair<>("Level 1", 1000));

        return FXCollections.observableArrayList(scores);
    }
}
