package test.java.edu.georgetown.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.georgetown.dao.ChirpDAO;
import edu.georgetown.model.Chirp;
import edu.georgetown.service.ChirpService;

@ExtendWith(MockitoExtension.class)
public class ChirpServiceTest {

    @Mock
    private ChirpDAO chirpDAO;

    @InjectMocks
    private ChirpService chirpService;

    @Test
    public void testPostChirp() {
        String username = "testUser";
        String content = "This is a test chirp";

        chirpService.postChirp(username, content);

        verify(chirpDAO, times(1)).addChirp(username, content);
    }

    @Test
    public void testGetChirpsByUser() {
        String username = "testUser";
        Vector<Chirp> mockChirps = new Vector<>();
        mockChirps.add(new Chirp(username, "Chirp 1"));
        mockChirps.add(new Chirp(username, "Chirp 2"));

        when(chirpDAO.getChirpsByUser(username)).thenReturn(mockChirps);

        Vector<Chirp> chirps = chirpService.getChirpsByUser(username);

        assertEquals(2, chirps.size());
        assertEquals("Chirp 1", chirps.get(0).getContent());
        assertEquals("Chirp 2", chirps.get(1).getContent());
        verify(chirpDAO, times(1)).getChirpsByUser(username);
    }

    @Test
    public void testGetAllChirps() {
        Vector<Chirp> mockChirps = new Vector<>();
        mockChirps.add(new Chirp("user1", "Chirp 1"));
        mockChirps.add(new Chirp("user2", "Chirp 2"));

        when(chirpDAO.getAllChirps()).thenReturn(mockChirps);

        Vector<Chirp> chirps = chirpService.getAllChirps();

        assertEquals(2, chirps.size());
        assertEquals("Chirp 1", chirps.get(0).getContent());
        assertEquals("Chirp 2", chirps.get(1).getContent());
        verify(chirpDAO, times(1)).getAllChirps();
    }
}
