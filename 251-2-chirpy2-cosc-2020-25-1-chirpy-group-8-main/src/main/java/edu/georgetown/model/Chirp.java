package edu.georgetown.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa a Chirp object (a post, like a Tweet)
 * Each Chirp contains the username of the owner,
 * the content of the message, and its creation timestamp.
 */
public class Chirp implements Serializable {

    private String ownerUsername;
    private String content;
    private LocalDateTime timestamp;
    private String formattedTime;

    /**
     * Chirp constructor, creates a new Chirp with the specific user/content
     * Timestamp is automatically created
     * 
     * @param ownerUsername the username of the owner of the chirp
     * @param content       the content (text) of the chirp, less than 280
     *                      characters
     */
    public Chirp(String ownerUsername, String content) {
        this.ownerUsername = ownerUsername;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Set the current timestamp

        // Formats the timestamp
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MMM d, h:mm a");
        this.formattedTime = this.timestamp.format(dateFormatter);
    }

    /**
     * Gets the username of the Chirp's owner (poster)
     * 
     * @return the username of the owner
     */
    public String getOwnerUsername() {
        return this.ownerUsername;
    }

    /**
     * Gets the content (text) of the Chirp
     * 
     * @return the content of the chirp
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Gets the raw timestamp (created) of the Chirp
     * 
     * @return the raw timestamp of the chirp
     */
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * Gets the formatted timestamp (created) of the Chirp
     * 
     * @return the formatted timestamp of the chirp
     */
    public String getFormattedTime() {
        return this.formattedTime;
    }
}