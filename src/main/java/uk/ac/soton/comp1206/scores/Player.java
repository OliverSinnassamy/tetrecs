package uk.ac.soton.comp1206.scores;

/**
 * Stores player data to be displayed in the leaderboard of multiplayer game.
 */
public class Player {

    /**
     * Player name.
     */
    private String name;
    /**
     * Player score.
     */
    private int score;
    /**
     * Player lives.
     */
    private int lives;

    /**
     * Constructor for Player class.
     * Sets values to attributes: name, score, lives.
     *
     * @param details : string of details.
     */
    public Player(String details) {
        String[] info = details.split(":");

        setName(info[0]);
        setScore(Integer.parseInt(info[1]));

        if (info[2].equals("DEAD")) {
            setLives(-1);
        } else {
            setLives(Integer.parseInt(info[2]));
        }
    }

    /**
     * Sets lives of player.
     *
     * @param lives : value to be set.
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * Gets lives of player.
     *
     * @return lives.
     */
    public int getLives() {
        return lives;
    }

    /**
     * Sets score of player.
     *
     * @param score : value to be set.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets score of player.
     *
     * @return score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets name of player.
     *
     * @param name : value to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets name of player.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Boolean value determining if player is dead.
     *
     * @return
     */
    public Boolean isDead() {
        return (getLives() == -1);
    }

    /**
     * ALlows name of player to be printed using toString() method.
     *
     * @return name of player.
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
