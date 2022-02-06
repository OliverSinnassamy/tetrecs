package uk.ac.soton.comp1206.scores;


import javafx.util.Pair;

import java.util.Comparator;

/**
 * Sorts leaderboard to display best player first in game.
 */
public class Sorter implements Comparator<Pair<String,Integer>> {

    /**
     * Compares objects and returns values depending on comparisons.
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        if(o1.getValue() > o2.getValue()){
            return -1;
        }
        else if(o1.getValue()< o2.getValue()){
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * Returns false.
     * @param obj : object.
     * @return false.
     */
    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
