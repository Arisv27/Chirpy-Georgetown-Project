package edu.georgetown.model;

import java.io.Serializable;

/**
 * Represents a Follow object (a relationship between two Chirpers)
 * Each Follow contains the username of the follower and the followee.
 */
public class Follow implements Serializable {

    private String follower;
    private String followee;

    /**
     * Follow constructor, creates a new Follow with the specific follower/followee
     * 
     * @param follower the username of the follower
     * @param followee the username of the followee
     */
    public Follow(String follower, String followee) {
        this.follower = follower;
        this.followee = followee;
    }

    /**
     * Gets the username of the follower
     * 
     * @return the username of the follower
     */
    public String getFollower() {
        return follower;
    }

    /**
     * Gets the username of the followee
     * 
     * @return the username of the followee
     */
    public String getFollowee() {
        return followee;
    }
}