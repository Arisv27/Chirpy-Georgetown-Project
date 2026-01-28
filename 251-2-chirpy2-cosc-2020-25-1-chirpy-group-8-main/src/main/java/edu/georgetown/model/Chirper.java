/**
 * A skeleton of a Chirper
 * 
 * Micah Sherr <msherr@cs.georgetown.edu>
 */

/** Guide to serialization */
// https://www.geeksforgeeks.org/serialization-in-java/
/** Guide to file path handling */
// https://medium.com/@AlexanderObregon/javas-paths-get-method-explained-9586c13f2c5c

package edu.georgetown.model;

import java.io.Serializable;

/**
 * Represents a Chirper (user) object
 * Each Chirper contains the username, password, and public status of the user
 * Public status is an unused variable
 */
public class Chirper implements Serializable {

    private String username;
    private String password;
    /** if true, the user's chirps are public */
    private boolean publicChirps; //Not part of current Chirpy design

    /**
     * Chirper constructor, creates a new Chirper with the specific
     * username/password
     * Public status is set to the value of publicChirps
     * 
     * @param username     the username of the Chirper
     * @param password     the password of the Chirper
     * @param publicChirps the public status of the Chirper
     */
    public Chirper(String username, String password, boolean publicChirps) {
        this.username = username;
        this.password = password;
        this.publicChirps = publicChirps;
        // this.followers = new Vector<String>();
    }

    /**
     * Chirper constructor, creates a new Chirper with the specific
     * username/password
     * Public status is set to true (the default)
     * 
     * @param username the username of the Chirper
     * @param password the password of the Chirper
     */
    public Chirper(String username, String password) {
        this(username, password, true);
    }

    /**
     * Gets the username of the Chirper
     * 
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username of the Chirper
     * 
     * @param username the new username
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password of the Chirper
     * 
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    // Will be useful in the future. Not part of current Chirpy design
    /**
     * Gets the public status of the Chirper
     * 
     * @return true if the Chirper's chirps are public, false otherwise
     */
    public boolean getPublicStatus() {
        return this.publicChirps;
    }

    /**
     * Sets the public status of the Chirper
     * 
     * @param newStatus the new public status
     */
    public void setPublicStatus(boolean newStatus) {
        this.publicChirps = newStatus;
    }
}