package uk.ac.soton.comp1206.event;

/**
 * Handles changes to the leaderboard.
 */
public interface LeaderboardChange {

    /**
     * Fired when new scores are received from server.
     */
    void onChange();
}
