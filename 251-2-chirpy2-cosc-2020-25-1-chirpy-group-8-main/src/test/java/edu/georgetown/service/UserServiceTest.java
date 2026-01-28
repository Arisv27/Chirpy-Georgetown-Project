package test.java.edu.georgetown.service;

import java.util.Set;
import java.util.Vector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import edu.georgetown.dao.ChirperDAO;
import edu.georgetown.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private ChirperDAO mockChirperDao; // Mocked dependency

    @InjectMocks
    UserService userService;

    // Test constructor for UserService
    @Test
    public void testUserServiceConstructor() {
        assertNotNull(userService, "UserService should be initialized");
        // Create a UserService object and verify it initializes correctly
    }

    // Test getUsers() method to ensure it returns the correct list of users
    @Test
    public void testGetUsers() {
        assertTrue(userService.getUsers().isEmpty(), "User list should be empty initially");
        // Create a UserService, add users to ChirperDao, and verify getUsers() returns
        // the expected list of Chirpers
    }

    // Test registerUser() method when the username is not already taken
    @Test
    public void testRegisterUserSuccess() throws UserService.UsernameIsTaken {
        when(mockChirperDao.addChirper(anyString(), anyString(), anyBoolean())).thenReturn(true);
        assertTrue(userService.registerUser("testUser", "password"), "User should be registered successfully");
        // Create a UserService, call registerUser() for a new username, and verify the
        // user is added
    }

    // Test registerUser() method when the username is already taken
    @Test
    public void testRegisterUserUsernameTaken() {

        when(mockChirperDao.chirperExists(anyString())).thenReturn(true);
        assertThrows(UserService.UsernameIsTaken.class, () -> {
            userService.registerUser("testUser", "password");
        });
        // Create a UserService, add a user, and try registering the same username to
        // ensure the exception is thrown
    }

    // Test getUsernames() method to ensure it returns the correct list of usernames
    @Test
    public void testGetUsernames() {
        Set<String> mockKeySet = Set.of("user1", "user2");
        when(mockChirperDao.keySet()).thenReturn(mockKeySet);
        Vector<String> usernames = userService.getUsernames();
        assertTrue(usernames.contains("user1"), "Should contain 'user1'");
        assertTrue(usernames.contains("user2"), "Should contain 'user2'");
        // Create a UserService, add users, and verify getUsernames() returns the
        // expected list of usernames
    }

    // Test usernameExists() method for an existing username
    @Test
    public void testUsernameExistsTrue() {
        when(mockChirperDao.chirperExists(anyString())).thenReturn(true);
        assertTrue(userService.usernameExists("existingUser"), "Username should exist");
        // Create a UserService, add a user, and verify usernameExists() returns true
        // for the existing user
    }

    // Test usernameExists() method for a non-existing username
    @Test
    public void testUsernameExistsFalse() {
        assertFalse(userService.usernameExists("nonExistentUser"), "Username should not exist");
        // Create a UserService and verify usernameExists() returns false for a
        // non-existing user
    }

    // Test isValidUser() method when username exists and password is correct
    @Test
    public void testIsValidUserTrue() {
        when(mockChirperDao.chirperExists(anyString())).thenReturn(true);
        when(mockChirperDao.passwordMatches(anyString(), anyString())).thenReturn(true);
        assertTrue(userService.isValidUser("validUser", "correctPass"), "Should return true for valid credentials");
        // Create a UserService, add a user, and verify isValidUser() returns true for
        // valid username and password
    }

    // Test isValidUser() method when username exists but password is incorrect
    @Test
    public void testIsValidUserIncorrectPassword() {
        userService.registerUser("validUser", "correctPass");
        assertFalse(userService.isValidUser("validUser", "wrongPass"), "Should return false for incorrect password");
        // Create a UserService, add a user, and verify isValidUser() returns false for
        // incorrect password
    }

    // Test isValidUser() method when username does not exist
    @Test
    public void testIsValidUserUsernameNotExist() {
        assertFalse(userService.isValidUser("nonExistentUser", "password"),
                "Should return false for non-existing user");
        // Create a UserService and verify isValidUser() returns false for a
        // non-existing username
    }

    // Test the UsernameIsTaken exception is thrown correctly when trying to
    // register an already taken username
    @Test
    public void testUsernameIsTakenException() {
        when(mockChirperDao.chirperExists(anyString())).thenReturn(true);
        assertThrows(UserService.UsernameIsTaken.class, () -> {
            userService.registerUser("duplicateUser", "password");
        });
        // Create a UserService, add a user, and ensure the UsernameIsTaken exception is
        // thrown when trying to register the same username
    }

    // Test IncorrectPassword exception when an incorrect password is provided (in
    // methods like isValidUser())
    @Test
    public void testIncorrectPassword() {
        when(mockChirperDao.chirperExists(anyString())).thenReturn(true);
        when(mockChirperDao.passwordMatches(anyString(), anyString())).thenReturn(false);
        assertFalse(userService.isValidUser("validUser", "wrongPass"));
        // Simulate the situation where an incorrect password is provided and check for
        // IncorrectPassword exception (if applicable)
    }
}