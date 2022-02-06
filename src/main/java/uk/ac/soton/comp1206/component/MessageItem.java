package uk.ac.soton.comp1206.component;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalTime;

/**
 * Holds messages and displays them to the user.
 * Handles formatting of timestamps.
 */
public class MessageItem extends TextFlow {

    /**
     * Nodes to hold data for the message item.
     */
    private final Text name = new Text();
    private final Text message = new Text();
    private final Text time = new Text();

    /**
     * Constructor for MessageItem.
     * Splits original message string.
     * Sets data to Text nodes.
     *
     * @param message
     */
    public MessageItem(String message) {
        if (message.equals("")) {
            name.setText("");
            this.message.setText("");
            time.setText("");
        } else {
            String[] split = message.split(":");

            this.name.setText("<" + split[0] + ">");
            this.message.setText(split[1]);

            //Gets time of message.
            LocalTime now = LocalTime.now();
            time.setText(formatTime(now) + " ");
        }

        //Adds styling to Text nodes.
        this.name.getStyleClass().add("message-name");
        this.message.getStyleClass().add("message-text");
        time.getStyleClass().add("message-text");

        getChildren().addAll(time, name, this.message);

    }

    /**
     * Constructor for MessageItem.
     * <p>
     * Used to display in-game messages and prompt in-game to type messages at start.
     *
     * @param message : message to be displayed.
     * @param start   : displays start prompt.
     */
    public MessageItem(String message, Boolean start) {
        if (start) {
            this.message.setText(message);
        }
        this.message.getStyleClass().add("message-text");
        getChildren().add(this.message);
    }

    /**
     * Returns Text node containing the name.
     *
     * @return name.
     */
    public Text getName() {
        return name;
    }

    /**
     * Returns Text node containing the message.
     *
     * @return message.
     */
    public Text getMessage() {
        return message;
    }

    /**
     * Returns node containing the time of message.
     *
     * @return time.
     */
    public Text getTime() {
        return time;
    }

    /**
     * Formats the time to be displayed in messages in the lobby menu.
     *
     * @param time : time to be formatted.
     * @return finalTime : time string to be added to the text node.
     */
    public String formatTime(LocalTime time) {
        String finalTime = "[" + time.getHour() + ":";
        if (time.getMinute() < 10) {
            finalTime = " " + finalTime + "0" + time.getMinute() + "]";
        } else {
            finalTime = finalTime + time.getMinute() + "]";
        }
        return finalTime;
    }
}
