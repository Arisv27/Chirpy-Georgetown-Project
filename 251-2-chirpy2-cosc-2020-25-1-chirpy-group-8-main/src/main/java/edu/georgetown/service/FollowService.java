package edu.georgetown.service;

import edu.georgetown.dao.*;

import java.util.Vector;
import java.util.logging.Logger;
import edu.georgetown.logging.LoggerFactory;

/**
 * The FollowService class provides methods to manage "following" relationships
 * between users in a social media application. It allows users to follow and
 * unfollow other users, retrieve lists of followers and followees, and check
 * if a user is following another user.
 * 
 * <p>
 * Features include:
 * <ul>
 * <li>Adding a following relationship between two users.</li>
 * <li>Removing a following relationship between two users.</li>
 * <li>Retrieving the list of users a specific user is following.</li>
 * <li>Retrieving the list of users following a specific user.</li>
 * <li>Checking if a user is following another user.</li>
 * </ul>
 * 
 * <p>
 * Exceptions:
 * <ul>
 * <li>{@link IllegalArgumentException} is thrown if a user attempts to follow
 * or unfollow themselves.</li>
 * <li>{@link IllegalStateException} is thrown if a user attempts to follow an
 * already followed user or unfollow a user they are not following.</li>
 * </ul>
 * 
 * <p>
 * Dependencies:
 * <ul>
 * <li>{@link FollowDAO} for managing the underlying data storage of follow
 * relationships.</li>
 * <li>{@link Logger} for logging operations and events.</li>
 * </ul>
 * 
 * <p>
 * Thread Safety: This class is not thread-safe and assumes single-threaded
 * access.
 */
public class FollowService {

    private FollowDAO followRelationships;
    private Logger logger = LoggerFactory.getLogger();

    public FollowService(FollowDAO followDAO) {
        followRelationships = followDAO;
        logger.info("FollowService started");
    }

    /**
     * Returns a vector of the usernames of Chirpers the follower follows.
     * 
     * @param username The username of the Chirper who is following others.
     * @return A vector of usernames that the follower follows.
     */
    public Vector<String> getAccountsUserFollows(String username) /* throws RelationshipDoesNotExist */ {
        return followRelationships.getAccountsUserFollows(username);
    }

    /**
     * Returns a vector of the usernames of Chirpers who follow the specified user.
     * 
     * @param followeeUsername The username of the Chirper being followed.
     * @return A vector of usernames that follow the given user.
     */
    public Vector<String> getAccountsFollowingUser(String username) {
        return followRelationships.getAccountsFollowingUser(username);
    }

    /**
     * Checks if a user is following another user.
     *
     * @param followingUser the username of the user who might be following
     * @param followedUser  the username of the user who might be followed
     * @return {@code true} if the followingUser is following the followedUser,
     *         {@code false} otherwise
     */
    public boolean isFollowing(String followingUser, String followedUser) {
        // Retrieve the list of users that followingUser is following
        Vector<String> accountsUserFollows = followRelationships.getAccountsUserFollows(followingUser);

        // Check if the followedUser is in the list of users followed by followingUser
        return accountsUserFollows.contains(followedUser);
    }

    /**
     * Adds the following relationship.
     * 
     * @param followingUser The username of the Chirper who is following.
     * @param targetUser    The username of the Chirper being followed.
     */
    public void follow(String followingUser, String targetUser) {
        logger.info(followingUser + " is attempting to follow " + targetUser);

        // Validation logic
        if (followingUser.equals(targetUser))
            throw new IllegalArgumentException("A user cannot follow themselves.");
        // Handle when relationship already exists. No need to add.
        if (isFollowing(followingUser, targetUser))
            throw new IllegalStateException(followingUser + " is already following " + targetUser);

        followRelationships.createRelationship(followingUser, targetUser);
        logger.info(followingUser + " successfully followed " + targetUser);
    }

    /**
     * Deletes the following relationship.
     * 
     * @param followingUser The username of the Chirper who follows the other.
     * @param targetUser    The username of the Chirper being followed.
     */
    public void unfollow(String followingUser, String targetUser) /* throws RelationshipDoesNotExist */ {
        logger.info(followingUser + " is attempting to unfollow " + targetUser);

        // Validation logic
        if (followingUser.equals(targetUser)) {
            throw new IllegalArgumentException("A user cannot unfollow themselves.");
        }
        // Relationship does not exist, nothing needs be done
        if (!isFollowing(followingUser, targetUser))
            throw new IllegalStateException(followingUser + " is not following " + targetUser);

        followRelationships.deleteRelationship(followingUser, targetUser);
        logger.info(followingUser + " successfully unfollowed " + targetUser);
    }

}