package edu.georgetown.service;

import java.util.Vector;
import java.util.logging.Logger;

import edu.georgetown.dao.ChirpDAO;
import edu.georgetown.logging.LoggerFactory;
import edu.georgetown.model.Chirp;

/**
 * ChirpService is responsible for managing chirps. It provides methods to post
 * a chirp,
 * retrieve chirps by a specific user, and retrieve all chirps.
 */
public class ChirpService {

    private ChirpDAO chirpDAO;

    private Logger logger = LoggerFactory.getLogger();

    /**
     * Constructor for ChirpService.
     * 
     * @param chirpDAO The ChirpDAO instance used for data access.
     */
    public ChirpService(ChirpDAO chirpDAO) {
        this.chirpDAO = chirpDAO;
        logger.info("ChirpService started");
    }

    /**
     * Posts a chirp for a specific user.
     * 
     * @param username The username of the user posting the chirp.
     * @param content  The content of the chirp.
     */
    public void postChirp(String username, String content) {
        chirpDAO.addChirp(username, content);
    }

    /**
     * Retrieves all chirps posted by a specific user.
     * 
     * @param username The username of the user whose chirps are to be retrieved.
     * @return A vector of chirps posted by the user.
     */
    public Vector<Chirp> getChirpsByUser(String username) {
        return chirpDAO.getChirpsByUser(username);
    }

    /**
     * Retrieves all chirps from the database.
     * 
     * @return A vector of all Chirps.
     */
    public Vector<Chirp> getAllChirps() {
        return chirpDAO.getAllChirps();
    }
}