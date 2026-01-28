package edu.georgetown.dao;

import java.util.Vector;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import edu.georgetown.logging.LoggerFactory;
import edu.georgetown.model.Follow;
import edu.georgetown.persistence.Serializer;

/**
 * Responsible for managing the follow relationships between users.
 * It provides methods to add, retrieve, and delete follow relationships.
 * It uses a double adjacency list (two HashMaps) to store the relationships.
 * It also uses a Serializer for state persistence.
 */
public class FollowDAO {
    // Double adjacency list
    private HashMap<String, Vector<String>> accountsFollowingUser = new HashMap<String, Vector<String>>();
    private HashMap<String, Vector<String>> accountsUserFollows = new HashMap<String, Vector<String>>();

    private Logger logger = LoggerFactory.getLogger();
    private Serializer<Follow> serializer;

    /**
     * Initializes statePath and logger
     * Sets up state persistence resources
     * Recovers follow relationships from secondary memory.
     * Initializes the userFollowers and userFollowees HashMaps and the serializer.
     * 
     * @param followSerializer Serializer object for Follow objects
     */
    public FollowDAO(Serializer<Follow> followSerializer) {
        this.serializer = followSerializer;
        logger.info("FollowDao object created successfully.");
    }

    /**
     * This method loads follow relationships from the persistent storage using the
     * serializer.
     * It iterates through the saved follow relationships and adds them to the
     * userFollowers and userFollowees HashMaps.
     * If no saved follow relationships exist, it returns early.
     */
    public boolean loadFollows() {
        // Load Follow objects from file
        Vector<Follow> savedFollows = serializer.loadDirectory();
        // Exit early if no saved files exist
        if (savedFollows == null || savedFollows.isEmpty())
            return false;
        // Add relationship´to adjacency lists
        for (Follow follow : savedFollows)
            createRelationship(follow.getFollower(), follow.getFollowee());

        return true;
    }

    /**
     * Retrieves a list of followers for a specific user by their username.
     * 
     * @param username the unique identifier of the Chirper being followed
     * @return a new Vector containing the usernames of all the followers;
     *         returns an empty Vector if the user has no followers.
     * 
     * @implNote This method returns a **copy** of the followers list to
     *           prevent external modification of internal data.
     */
    public Vector<String> getAccountsFollowingUser(String username) {
        Vector<String> followers = accountsFollowingUser.getOrDefault(username, new Vector<>());
        return new Vector<>(followers); // Return a copy of the followers list
    }

    /**
     * Retrieves a list of users this user follows
     * 
     * @param username unique identifier of the Chirper who follows others
     * @return a new Vector with the username of all the users the Chirper follows,
     *         or
     *         Null if the user does not exist.
     * 
     * @implNote This method returns a **copy** of the followers list to
     *           prevent external modification of internal data.
     */
    public Vector<String> getAccountsUserFollows(String username) {
        logger.info("Retrieving followees of " + username);
        Vector<String> followees = accountsUserFollows.getOrDefault(username, new Vector<>());
        return new Vector<>(followees);
    }

    /**
     * Generates a filename by concatenating the follower's username and the
     * followee's username
     * with a space in between.
     *
     * @param followerUsername the username of the follower
     * @param followeeUsername the username of the followee
     * @return a string representing the generated filename
     */
    public String generateFilename(String followerUsername, String followeeUsername) {
        return followerUsername + " " + followeeUsername;
    }

    /**
     * Updates adjacency lists and handles state persistence to add a follow
     * relationship, if it doesn't exist
     * 
     * @param followerUsername username of the prospective follower
     * @param followeeUsername username of the prospective followee
     */
    public boolean createRelationship(String followerUsername, String followeeUsername) {

        logger.info("Attempting to create relationship: " + followerUsername + " follows " + followeeUsername);

        // Update double-adjacency lists
        accountsUserFollows.computeIfAbsent(followerUsername, username -> new Vector<String>()).add(followeeUsername);
        accountsFollowingUser.computeIfAbsent(followeeUsername, username -> new Vector<String>()).add(followerUsername);

        // Attempt to serialize follow relationship
        try {
            Follow follow = new Follow(followerUsername, followeeUsername);
            String fileName = generateFilename(followerUsername, followeeUsername);
            serializer.createState(follow, fileName);

            return true;
        } catch (IOException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    /**
     * Updates adjacency lists and handles state persistence to remove a follow
     * relationship if it exists
     * 
     * @param followerUsername username of the proposed follower
     * @param followeeUsername username of the proposed followee
     */
    public boolean deleteRelationship(String followerUsername, String followeeUsername) {

        // Update double-adjacency lists
        accountsUserFollows.get(followerUsername).remove(followeeUsername);
        accountsFollowingUser.get(followeeUsername).remove(followerUsername);

        // Attempt to delete file
        try {
            String fileName = generateFilename(followerUsername, followeeUsername);
            serializer.deleteState(fileName);

            return true;
        } catch (IOException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

}