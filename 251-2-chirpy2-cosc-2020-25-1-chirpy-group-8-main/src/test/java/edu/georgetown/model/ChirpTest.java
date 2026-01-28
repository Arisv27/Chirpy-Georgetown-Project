package test.java.edu.georgetown.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

import edu.georgetown.model.Chirp;

public class ChirpTest {

    @Test
    public void testChirpConstructorAndGetters() {
        String ownerUsername = "testUser";
        String content = "This is a test chirp.";
        
        Chirp chirp = new Chirp(ownerUsername, content);

        assertEquals(ownerUsername, chirp.getOwnerUsername(), "Owner username should match the input.");
        assertEquals(content, chirp.getContent(), "Content should match the input.");
        assertNotNull(chirp.getTimestamp(), "Timestamp should not be null.");
        assertTrue(chirp.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)), "Timestamp should be set to the current time.");
    }

    @Test
    public void testChirpTimestampIsSet() {
        Chirp chirp = new Chirp("user123", "Another test chirp.");
        assertNotNull(chirp.getTimestamp(), "Timestamp should be initialized.");
    }
}
