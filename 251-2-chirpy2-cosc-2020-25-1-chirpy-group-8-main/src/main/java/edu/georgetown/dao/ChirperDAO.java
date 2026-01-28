package edu.georgetown.dao;

import java.util.HashMap;
import java.util.logging.Logger;

import java.util.Set;
import java.util.Vector;
import java.io.IOException;

import edu.georgetown.logging.LoggerFactory;
import edu.georgetown.model.Chirper;
import edu.georgetown.persistence.Serializer;

/**
 * Chirper Data Access Object
 * This class is responsible for managing the chirpers of users.
 * It provides methods to add, retrieve, and load chirpers from persistent
 * storage.
 * It uses a HashMap to store chirpers by username and a Serializer for state
 * persistence.
 */
public class ChirperDAO {
    private HashMap<String, Chirper> userList;
    private Logger logger = LoggerFactory.getLogger();
    private Serializer<Chirper> serializer;

    /**
     * ChirperDAO constructor
     * Initializes the userList HashMap and the serializer.
     * 
     * @param chirpSerializer: Serializer object for Chirper objects
     */
    public ChirperDAO(Serializer<Chirper> chirpSerializer) {
        userList = new HashMap<String, Chirper>();
        this.serializer = chirpSerializer;
        logger.info("ChirperDao object created successfully.");

        // TO DO: iterate through .ser files in statePath directory, deserializing them
        // and storing the result in userList
    }

    /**
     * This method loads chirpers from the persistent storage using the serializer.
     * It iterates through the saved chirpers and adds them to the userList HashMap.
     * If no saved chirpers exist, it returns early.
     */
    public void loadChirpers() {
        Vector<Chirper> savedChirpers = serializer.loadDirectory();

        // Exit early if no saved files exist
        if (savedChirpers == null)
            return;

        for (Chirper chirper : savedChirpers) {
            // This will need to change if we add password hashing
            addChirper(chirper.getUsername(), chirper.getPassword(), chirper.getPublicStatus());
        }
    }

    /**
     * Retrieves a Chirper object from the userList HashMap.
     * 
     * @param username The username of the chirper to retrieve
     * 
     * @return Chirper object if found, null otherwise
     */
    public boolean chirperExists(String username) {
        return userList.containsKey(username);
    }

    /**
     * Checks if the provided plaintext password matches the stored
     * password for a given chirper.
     * 
     * @param username           The username of the chirper to check
     * 
     * @param plaintext_password The plaintext password to check
     * 
     * @return true if the passwords match, false otherwise
     */
    public boolean passwordMatches(String username, String plaintext_password) {
        String password = hashPassword(plaintext_password);

        Chirper user = userList.get(username);
        if (user != null) {
            return (user.getPassword().equals(password));
        } else
            return false;
    }

    /**
     * Sets the password for a given chirper.
     * It hashes the plaintext password and updates the Chirper object in the
     * userList.
     * 
     * @param username           The username of the chirper to update
     * 
     * @param plaintext_password The plaintext password to set
     * 
     * @return true if the password was set successfully, false otherwise
     */
    public boolean setChirperPassword(String username, String plaintext_password) {
        // more design needed
        String password = hashPassword(plaintext_password);

        Chirper user = userList.get(username);
        if (user != null) {
            user.setPassword(password);
            try {
                serializer.createState(user, username);
            } catch (IOException e) {
                logger.warning("ChirperDao.setChirperPassword: Could not save the state of chirper " + username
                        + "upon update.");
                // Suggestion: add to list of unsaved Chirpers for future reattempts?
            }
            return true;
        } else
            return false;
    }

    // For debug purposes, might be taken out later
    /**
     * getChirperPassword method
     * This method retrieves the password for a given chirper.
     * It returns the stored password if the chirper exists, null otherwise.
     * 
     * @param username The username of the chirper to retrieve
     * 
     * @return The stored password if the chirper exists, null otherwise
     */
    public String getChirperPassword(String username) {
        if (chirperExists(username)) {
            return userList.get(username).getPassword();
        } else
            return null;
    }

    // these two do actually deserve to exist
    /**
     * Retrieves the public status of a given chirper.
     * It returns the public status if the chirper exists, false otherwise.
     * 
     * @param username The username of the chirper to retrieve
     * 
     * @return The public status of the chirper if it exists, false otherwise
     */
    public boolean getChirperPublicStatus(String username) {
        if (chirperExists(username)) {
            return userList.get(username).getPublicStatus();
        } else {
            logger.warning("ChirperDao.getChirperPublicStatus: Tried to access public status of user " + username
                    + " which doesn't exist.");
            return false;
        }
    }

    /**
     * Sets the public status for a given chirper.
     * It updates the Chirper object in the userList and attempts to save the state
     * using the serializer.
     * If the save operation is unsuccessful, it logs a warning.
     * 
     * @param username The username of the chirper to update
     * 
     * @param status   The public status to set
     * 
     * @return true if the public status was set successfully, false otherwise
     */
    public boolean setChirperPublicStatus(String username, boolean status) {
        if (chirperExists(username)) {
            userList.get(username).setPublicStatus(status);
            return true;
        } else {
            logger.warning("ChirperDao.getChirperPublicStatus: Tried to set public status of user " + username
                    + " which doesn't exist.");
            return false;
        }
    }

    /**
     * Adds a new chirper to the userList HashMap.
     * It creates a new Chirper object and attempts to save the state using the
     * serializer.
     * If the save operation is unsuccessful, it logs a warning.
     * 
     * @param username           The username of the chirper to add
     * 
     * @param plaintext_password The plaintext password to set
     * 
     * @param publicChirps       The public status of the chirper
     * 
     * @return true if the chirper was added successfully, false if the chirper
     *         already exists
     */
    public boolean addChirper(String username, String plaintext_password, boolean publicChirps) {

        String password = hashPassword(plaintext_password);
        if (!chirperExists(username)) {
            Chirper newChirper = new Chirper(username, password, publicChirps);
            userList.put(username, newChirper);
            logger.info("ChirperDao.addChirper: user " + username + "successfully created.");
            try {
                serializer.createState(newChirper, username);
                return true;
            } catch (IOException e) {
                logger.warning(
                        "ChirperDao.addChirper: Could not save the state of chirper " + username + "upon creation.");
                return false;
                // Suggestion: add to list of unsaved Chirpers for future reattempts?
            }
        } else {
            logger.info("ChirperDao.addChirper: Chirper " + username + "already exists.");
            return false;
        }

    }

    /**
     * Hashes the plaintext password for a given chirper.
     * It currently uses a trivial hashing algorithm (identity function).
     * 
     * @param plaintext_password The plaintext password to hash
     * 
     * @return The hashed password
     */
    public String hashPassword(String plaintext_password) {
        // trivial hashing for now
        return plaintext_password;
    }

    /**
     * Deletes a chirper from the userList HashMap.
     * It attempts to remove the chirper and save the state using the serializer.
     * If the save operation is unsuccessful, it logs a warning.
     * 
     * @deprecated Not implemented yet
     * 
     * @param username The username of the chirper to delete
     * 
     * @return true if the chirper was deleted successfully, false otherwise
     */
    public boolean deleteChirper(String username) {
        // does nothing for now
        return false;
    }

    /**
     * Retrieves the set of usernames from the userList HashMap.
     * 
     * @return A set of usernames
     */
    public Set<String> keySet() {
        return userList.keySet();
    }

    /** Inner exception classes */
    public static class SaveUnsuccessfulException extends IOException {
        public SaveUnsuccessfulException(String message) {
            super(message);
        }

        public SaveUnsuccessfulException() {
            super();
        }
    }
}
