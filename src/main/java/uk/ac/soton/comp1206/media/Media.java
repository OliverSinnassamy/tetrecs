package uk.ac.soton.comp1206.media;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.MediaPlayer;

/**
 * Handles all sounds and music played.
 */
public class Media {
    /**
     * MediaPlayer to play music.
     */
    private static MediaPlayer music;
    /**
     * MediaPlayer to handle sound effects.
     */
    private static MediaPlayer audio;

    /**
     * Property for controlling volumes.
     */
    private static SimpleDoubleProperty volumeProperty = new SimpleDoubleProperty(100);



    /**
     * Handles music being played.
     *
     * @param theme : theme of game.
     * @param file  : file of music to be played.
     */
    public void playMusic(String theme, String file) {
        file = "/sounds/" + theme + "/" + file;


        javafx.scene.media.Media backgroundMusic = new javafx.scene.media.Media(Media.class.getResource(file).toExternalForm());
        music = new MediaPlayer(backgroundMusic);


        volumeProperty.addListener((observableValue, number, t1) -> music.setVolume(volumeProperty.get() / 100));

        music.setVolume(volumeProperty.get()/100);

        music.setCycleCount(MediaPlayer.INDEFINITE);

        music.play();

    }

    /**
     * Handles sound effect to be played.
     *
     * @param file : file of sound effect to be played.
     */
    public void playSound(String file) {
        javafx.scene.media.Media sound = new javafx.scene.media.Media(Media.class.getResource("/sounds/default/" + file).toExternalForm());
        audio = new MediaPlayer(sound);

        volumeProperty.addListener((observableValue, number, t1) -> audio.setVolume(volumeProperty.get() / 100));
        audio.setVolume(volumeProperty.get()/100);

        audio.play();
    }

    /**
     * Handles specialised sound effect being played depending on theme of game.
     *
     * @param theme : theme of game.
     * @param file  : file of music to be played.
     */
    public void playThemeSound(String theme, String file) {

        file = "/sounds/" + theme + "/" + file;

        javafx.scene.media.Media sound = new javafx.scene.media.Media(Media.class.getResource(file).toExternalForm());
        audio = new MediaPlayer(sound);

        volumeProperty.addListener((observableValue, number, t1) -> audio.setVolume(volumeProperty.get() / 100));

        audio.setVolume(volumeProperty.get()/100);

        audio.play();
    }

    /**
     * Gets volume property.
     *
     * @return volumeProperty.
     */
    public SimpleDoubleProperty getVolumeProperty() {
        return volumeProperty;
    }

    /**
     * Stops music being played.
     */
    public void stopMusic() {
        music.stop();
    }

    /**
     * Gets music media player.
     *
     * @return music.
     */
    public MediaPlayer getMusic() {
        return music;
    }
}
