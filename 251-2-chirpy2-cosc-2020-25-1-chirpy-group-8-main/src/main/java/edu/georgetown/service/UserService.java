package edu.georgetown.service;

import java.util.Vector;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import java.nio.file.Path;

import edu.georgetown.dao.*;
import edu.georgetown.http.CookieUtils;
import edu.georgetown.logging.LoggerFactory;
import edu.georgetown.model.Chirper;

/**
 * UserService is responsible for managing users. It provides methods to
 * register a user,
 * check if a username exists, and validate user credentials.
 */
public class UserService {

    ChirperDAO users;
    static Path userStateDir;

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * Constructor for UserService.
     * 
     * @param chirperDAO The ChirperDAO instance used for data access.
     */
    public UserService(ChirperDAO chirperDAO) {
        this.users = chirperDAO;
        logger.info("UserService started");
    }

    /**
     * Retrieves all users from the database.
     * 
     * @return A vector of all Chirpers.
     */
    public Vector<Chirper> getUsers() {
        Vector<Chirper> myUserList = new Vector<Chirper>();
        for (String username : users.keySet()) {
            Chirper newUser = new Chirper(username, users.getChirperPassword(username),
                    users.getChirperPublicStatus(username));
            myUserList.add(newUser);
        }
        return myUserList;
    }

    /**
     * Registers a new user with the given username and password.
     * Checks if the username is already taken before adding the new user.
     * 
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @throws UsernameIsTaken if the username is already taken.
     * @return true if the user was successfully registered, false otherwise.
     */
    public boolean registerUser(String username, String password) throws UsernameIsTaken {
        // stores password in plain text so far

        // Checks whether user exists already
        if (users.chirperExists(username)) { // Check for duplicates before adding a new user
            throw new UsernameIsTaken("UserService: The username " + username + " is already taken.");
        }

        // Store username and password into a new Chirper
        return users.addChirper(username, password, true);
    }

    /**
     * Retrieves a vector of all usernames.
     * 
     * @return A vector of usernames.
     */
    public Vector<String> getUsernames() {
        Vector<String> userList = new Vector<String>();
        for (String username : users.keySet()) {
            userList.add(username);
        }
        return userList;
    }

    /**
     * Checks if a username already exists in the database.
     * 
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean usernameExists(String username) {
        return users.chirperExists(username);
    }

    /**
     * Validates user credentials by checking if the username exists and if the
     * password matches.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @return true if the credentials are valid, false otherwise.
     */
    public boolean isValidUser(String username, String password) {
        if (!users.chirperExists(username)) {
        } else if (!users.passwordMatches(username, password)) {
        }
        return users.chirperExists(username) && users.passwordMatches(username, password);
    }

    /**
     * Checks if a user is logged in based on the presence and validity of a
     * "username" cookie.
     * 
     * @param exchange The HttpExchange object containing the request and response
     *                 details.
     * @return {@code true} if the "username" cookie exists and corresponds to a
     *         valid username;
     *         {@code false} otherwise. If the cookie is invalid, it will be
     *         deleted.
     */
    public boolean isLoggedIn(HttpExchange exchange) {
        String username = CookieUtils.getCookies(exchange).get("username");
        if (usernameExists(username)) {
            return true;
        } else {
            CookieUtils.deleteCookie(exchange, "username");
            return false;
        }
    }

    /** Inner exception classes */

    /**
     * UsernameIsTaken is thrown when a user tries to register with a username that
     * already exists.
     * 
     * @param message The error message.
     */
    public static class UsernameIsTaken extends IllegalArgumentException {
        public UsernameIsTaken(String message) {
            super(message);
        }
    }

    /**
     * UserDoesNotExist is thrown when a user tries to log in with a username that
     * does not exist.
     * 
     * @param message The error message.
     */
    public static class UserDoesNotExist extends IllegalArgumentException {
        public UserDoesNotExist(String message) {
            super(message);
        }
    }

    /**
     * IncorrectPassword is thrown when a user tries to log in with an incorrect
     * password.
     * 
     * @param message The error message.
     */
    public static class IncorrectPassword extends IllegalArgumentException {
        public IncorrectPassword(String message) {
            super(message);
        }
    }
}
