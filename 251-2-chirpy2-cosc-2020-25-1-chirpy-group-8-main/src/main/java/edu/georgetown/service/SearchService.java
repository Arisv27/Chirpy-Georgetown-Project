package edu.georgetown.service;

import java.util.logging.Logger;
import java.util.Vector;

import edu.georgetown.model.Chirp;
import edu.georgetown.logging.LoggerFactory;

/**
 * The SearchService class provides functionality to search for Chirps
 * based on specific criteria such as tags or the username of the Chirper.
 * It interacts with the ChirpService to retrieve and filter Chirps.
 * 
 * <p>
 * Features:
 * </p>
 * <ul>
 * <li>Search for Chirps containing a specific tag.</li>
 * <li>Search for Chirps created by a specific user.</li>
 * </ul>
 */
public class SearchService {
    private ChirpService chirpService;
    private Logger logger = LoggerFactory.getLogger();

    /**
     * Initializes SearchService
     * 
     * @param cs reference to ChirpService object
     */
    public SearchService(ChirpService cs) {
        this.chirpService = cs;
        logger.info("SearchService successfully initiated");
    }

    /**
     * Searches for chirps that contain a certain tag
     * 
     * @param tag the tag of interest
     * @return a list of chirp IDs of Chirps that contain the tag
     */
    public Vector<Chirp> searchByTag(String tag) {
        Vector<Chirp> posts = chirpService.getAllChirps();
        Vector<Chirp> matches = new Vector<>();
        for (Chirp post : posts) {
            if (post.getContent().contains(tag)) {
                matches.add(post);
            }
        }
        return matches;
    }

    /**
     * Searches for chirps made by a specific Chirper.
     * 
     * @param username username of the Chirper
     * @return a list of chirp IDs of Chirps made by the user
     */
    public Vector<Chirp> searchByUser(String username) {
        Vector<Chirp> posts = chirpService.getAllChirps();
        Vector<Chirp> matches = new Vector<>();
        for (Chirp post : posts) {
            if (post.getOwnerUsername().equals(username)) {
                matches.add(post);
            }
        }
        return matches;
    }
}
