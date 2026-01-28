package test.java.edu.georgetown.dao;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;

import edu.georgetown.dao.ChirperDAO;
import edu.georgetown.model.Chirper;
import edu.georgetown.persistence.Serializer;

@ExtendWith(MockitoExtension.class)
public class ChirperDAOTest {

    @Mock
    private Serializer<Chirper> mockChirperSerializer;
    
    @InjectMocks
    private ChirperDAO chirperDao;

    // Test constructor for ChirperDAO with valid logger and statePath
    @Test
    public void testChirperDaoConstructor() {
        assertNotNull(chirperDao, "ChirperDAO should be initialized");
        // Create a ChirperDAO object and check if it initializes correctly
    }

    // Test saveState() method when saving a Chirper object successfully
    @Test
    public void testSaveStateSuccess() {
        Chirper chirper = new Chirper("testUser", "password123", true);

        assertTrue(chirperDao.addChirper(chirper.getUsername(), chirper.getPassword(), chirper.getPublicStatus()),
                "Chirper should be added and saved successfully");
        // Create a ChirperDAO and Chirper, then call saveState() and verify the Chirper
        // state is saved
    }

    // Test chirperExists() method for an existing Chirper
    @Test
    public void testChirperExistsTrue() {
        chirperDao.addChirper("testUser", "password", true);
        assertTrue(chirperDao.chirperExists("testUser"), "Chirper should exist after being added");
        // Create a ChirperDAO, add a Chirper, and verify chirperExists() returns true
        // for the added user
    }

    // Test chirperExists() method for a non-existing Chirper
    @Test
    public void testChirperExistsFalse() {
        assertFalse(chirperDao.chirperExists("nonExistentUser"),
                "chirperExists() should return false for a user that does not exist");
        // Create a ChirperDAO and verify chirperExists() returns false for a
        // non-existing user
    }

    // Test passwordMatches() method when password matches
    @Test
    public void testPasswordMatchesTrue() {
        chirperDao.addChirper("secureUser", "securePass", true);
        assertTrue(chirperDao.passwordMatches("secureUser", "securePass"), "Correct password should match");

        // Create a ChirperDAO, add a Chirper, and verify passwordMatches() returns true
        // for the correct password
    }

    // Test passwordMatches() method when password does not match
    @Test
    public void testPasswordMatchesFalse() {
        chirperDao.addChirper("secureUser", "securePass", true);
        assertFalse(chirperDao.passwordMatches("secureUser", "wrongPass"), "Incorrect password should not match");
        // Create a ChirperDAO, add a Chirper, and verify passwordMatches() returns
        // false for an incorrect password
    }

    // Test setChirperPassword() method when updating password successfully
    @Test
    public void testSetChirperPasswordSuccess() {
        chirperDao.addChirper("testUser", "oldPass", true);
        assertTrue(chirperDao.setChirperPassword("testUser", "newPass"), "Password should be updated");
        assertTrue(chirperDao.passwordMatches("testUser", "newPass"), "New password should match");
        // Create a ChirperDAO, add a Chirper, change the password, and verify the
        // password is updated and saved
    }

    // Test setChirperPassword() method when the Chirper does not exist
    @Test
    public void testSetChirperPasswordFailure() {
        assertFalse(chirperDao.setChirperPassword("nonExistentUser", "newPass"),
                "Setting a password for a non-existent user should fail");
        // Verify setChirperPassword() returns false when trying to set the password for
        // a non-existing user
    }

    // Test getChirperPassword() method when the Chirper exists
    @Test
    public void testGetChirperPassword() {
        chirperDao.addChirper("testUser", "password123", true);
        assertEquals("password123", chirperDao.getChirperPassword("testUser"),
                "getChirperPassword() should return the correct password");

        // Create a ChirperDAO, add a Chirper, and verify getChirperPassword() returns
        // the correct password
    }

    // Test getChirperPublicStatus() method when Chirper exists
    @Test
    public void testGetChirperPublicStatusTrue() {
        chirperDao.addChirper("publicUser", "password", true);
        assertTrue(chirperDao.getChirperPublicStatus("publicUser"), "Public status should be true");

        // Create a ChirperDAO, add a Chirper, and verify getChirperPublicStatus()
        // returns the correct public status
    }

    // Test getChirperPublicStatus() method when Chirper does not exist
    @Test
    public void testGetChirperPublicStatusFalse() {
        assertFalse(chirperDao.getChirperPublicStatus("nonExistentUser"),
                "Public status should be false for non-existent users");
        // Verify getChirperPublicStatus() returns false for a non-existing user
    }

    // Test setChirperPublicStatus() method when successfully updating the public
    // status
    @Test
    public void testSetChirperPublicStatusSuccess() {
        chirperDao.addChirper("publicUser", "password", false);
        assertTrue(chirperDao.setChirperPublicStatus("publicUser", true), "Setting public status should succeed");
        assertTrue(chirperDao.getChirperPublicStatus("publicUser"), "Public status should now be true");
        // Create a ChirperDAO, add a Chirper, update public status, and verify the
        // change is successful
    }

    // Test setChirperPublicStatus() method when the Chirper does not exist
    @Test
    public void testSetChirperPublicStatusFailure() {
        assertFalse(chirperDao.setChirperPublicStatus("nonExistentUser", true),
                "Setting public status for a non-existent user should fail");
        // Verify setChirperPublicStatus() returns false when trying to set the public
        // status for a non-existing user
    }

    // Test addChirper() method when adding a new Chirper successfully
    @Test
    public void testAddChirperSuccess() {
        assertTrue(chirperDao.addChirper("newUser", "password", true), "New Chirper should be added successfully");
        // Create a ChirperDAO and verify addChirper() returns true when adding a new
        // user
    }

    // Test addChirper() method when adding an existing Chirper
    @Test
    public void testAddChirperFailure() {
        chirperDao.addChirper("existingUser", "password", true);
        assertFalse(chirperDao.addChirper("existingUser", "password", true), "Adding a duplicate user should fail");

        // Create a ChirperDAO, add a Chirper, and verify addChirper() returns false for
        // an existing user
    }

    // Test hashPassword() method to verify the password is correctly hashed
    @Test
    public void testHashPassword() {
        String hashedPassword = chirperDao.hashPassword("myPassword");
        assertNotNull(hashedPassword, "Hashed password should not be null");
        // Verify that hashPassword() correctly hashes the password
    }

    // Test deleteChirper() method when deleting a Chirper
    @Test
    public void testDeleteChirper() {
        chirperDao.addChirper("deleteUser", "password", true);
        assertFalse(chirperDao.deleteChirper("deleteUser"),
                "deleteChirper() should return false (since it does nothing currently)");

        // Verify that deleteChirper() behaves correctly, even if it currently does
        // nothing
    }

    // Test keySet() method to verify it returns the correct set of usernames
    @Test
    public void testKeySet() {
        chirperDao.addChirper("user1", "pass", true);
        chirperDao.addChirper("user2", "pass", true);
        assertEquals(2, chirperDao.keySet().size(), "keySet() should return the correct number of usernames");
        // Add multiple Chirpers to ChirperDAO, and verify keySet() returns the correct
        // set of usernames
    }

    // Test SaveUnsuccessfulException is thrown correctly when state saving fails
    @Test
    public void testSaveUnsuccessfulException() throws IOException {
        // Arrange
        Chirper chirper = new Chirper("testUser", "password", true);

        // Mock createState to throw the expected exception
        doThrow(new IOException("forced fail"))
                .when(mockChirperSerializer)
                .createState(any(Chirper.class), any(String.class));

        // Act & Assert
        assertFalse(chirperDao.addChirper(chirper.getUsername(), chirper.getPassword(), chirper.getPublicStatus()),
                "Expected addChirper to return false when state saving fails");
    }
}