package edu.georgetown.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import edu.georgetown.logging.LoggerFactory;
import edu.georgetown.model.Chirp;
import edu.georgetown.persistence.Serializer;

/**
 * Chirp Data Access Object
 * This class is responsible for managing the chirps of users.
 * It provides methods to add, retrieve, and load chirps from persistent
 * storage.
 * It uses a HashMap to store chirps by user and a Serializer for state
 * persistence.
 */
public class ChirpDAO {
    private HashMap<String, Vector<Chirp>> chirpsByUser;
    private Logger logger = LoggerFactory.getLogger();
    private Serializer<Chirp> serializer;

    /**
     * ChirpDAO constructor.
     * Initializes the chirpsByUser HashMap and the serializer.
     * 
     * @param chirpSerializer Serializer object for Chirp objects
     * 
     */
    public ChirpDAO(Serializer<Chirp> chirpSerializer) {
        chirpsByUser = new HashMap<String, Vector<Chirp>>();
        this.serializer = chirpSerializer;
        logger.info("ChirpDAO object created successfully.");
    }

    /**
     * Loads chirps from the persistent storage using the serializer.
     * It iterates through the saved chirps and adds them to the chirpsByUser
     * HashMap.
     * If no saved chirps exist, it returns early.
     */
    public void loadChirps() {
        Vector<Chirp> savedChirps = serializer.loadDirectory();

        if (savedChirps == null)
            return;

        for (Chirp chirp : savedChirps) {
            chirpsByUser.computeIfAbsent(chirp.getOwnerUsername(), k -> new Vector<>()).add(chirp);
        }
    }

    /**
     * Adds a new chirp to the chirpsByUser HashMap.
     * It creates a new Chirp object and adds it to the vector of chirps for the
     * given username.
     * It also attempts to save the chirp state using the serializer.
     * If the save operation is unsuccessful, it logs a warning.
     * 
     * @param username The username of the user who created the chirp
     * @param content  The content of the chirp
     */
    public void addChirp(String username, String content) {
        Chirp newChirp = new Chirp(username, content);
        chirpsByUser.computeIfAbsent(username, k -> new Vector<>()).add(newChirp);
        try {
            serializer.createState(newChirp, username + "_" + System.currentTimeMillis());
        } catch (IOException e) {
            logger.warning("ChirpDAO.addChirp: Could not save the state of chirp for user " + username);
        }
    }

    /**
     * Retrieves all chirps for a given user from the chirpsByUser HashMap.
     * If the user has no chirps, it returns an empty vector.
     * 
     * @param username The username of the user whose chirps are to be retrieved
     * @return A vector of Chirp objects for the given user
     */
    public Vector<Chirp> getChirpsByUser(String username) {
        return chirpsByUser.getOrDefault(username, new Vector<>());
    }

    /**
     * Retrieves all chirps from all users.
     * It iterates through the chirpsByUser HashMap and adds all chirps to a single
     * vector.
     * 
     * @return A vector of all Chirp objects from all users
     */
    public Vector<Chirp> getAllChirps() {
        Vector<Chirp> allChirps = new Vector<>();
        for (Vector<Chirp> userChirps : chirpsByUser.values()) {
            allChirps.addAll(userChirps);
        }
        return allChirps;
    }
}