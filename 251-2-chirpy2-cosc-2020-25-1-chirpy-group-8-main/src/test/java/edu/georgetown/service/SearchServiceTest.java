package test.java.edu.georgetown.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.georgetown.service.SearchService;
import edu.georgetown.service.ChirpService;
import edu.georgetown.model.Chirp;

public class SearchServiceTest {

    private SearchService searchService;
    private ChirpService chirpServiceMock;

    @BeforeEach
    public void setUp() {
        chirpServiceMock = mock(ChirpService.class);
        searchService = new SearchService(chirpServiceMock);
    }

    @Test
    public void testSearchByTag() {
        // Arrange
        Vector<Chirp> chirps = new Vector<>();
        Chirp chirp1 = new Chirp("user1", "This is a #test chirp");
        Chirp chirp2 = new Chirp("user2", "Another chirp without the tag");
        Chirp chirp3 = new Chirp("user3", "Yet another #test chirp");
        chirps.add(chirp1);
        chirps.add(chirp2);
        chirps.add(chirp3);

        when(chirpServiceMock.getAllChirps()).thenReturn(chirps);

        // Act
        Vector<Chirp> result = searchService.searchByTag("#test");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(chirp1));
        assertFalse(result.contains(chirp2));
        assertTrue(result.contains(chirp3));
    }

    @Test
    public void testSearchByUser() {
        // Arrange
        Vector<Chirp> chirps = new Vector<>();
        Chirp chirp1 = new Chirp("user1", "This is a chirp");
        Chirp chirp2 = new Chirp("user2", "Another chirp");
        Chirp chirp3 = new Chirp("user1", "Yet another chirp");
        chirps.add(chirp1);
        chirps.add(chirp2);
        chirps.add(chirp3);

        when(chirpServiceMock.getAllChirps()).thenReturn(chirps);

        // Act
        Vector<Chirp> result = searchService.searchByUser("user1");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(chirp1));
        assertFalse(result.contains(chirp2));
        assertTrue(result.contains(chirp3));
    }
}
