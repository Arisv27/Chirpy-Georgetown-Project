package test.java.edu.georgetown.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
import edu.georgetown.model.Chirp;
import edu.georgetown.persistence.Serializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Vector;

import edu.georgetown.dao.ChirpDAO;

@ExtendWith(MockitoExtension.class)
public class ChirpDAOTest {

    @Mock
    private Serializer<Chirp> mockSerializer;
    
    @InjectMocks
    private ChirpDAO chirpDAO;

    @Test
    public void testLoadChirps() {
        Vector<Chirp> mockChirps = new Vector<>();
        mockChirps.add(new Chirp("user1", "Hello World!"));
        mockChirps.add(new Chirp("user2", "Another chirp"));

        when(mockSerializer.loadDirectory()).thenReturn(mockChirps);

        chirpDAO.loadChirps();

        assertEquals(1, chirpDAO.getChirpsByUser("user1").size());
        assertEquals(1, chirpDAO.getChirpsByUser("user2").size());
        assertEquals("Hello World!", chirpDAO.getChirpsByUser("user1").get(0).getContent());
    }

    @Test
    public void testAddChirp() throws IOException {
        chirpDAO.addChirp("user1", "This is a test chirp");

        Vector<Chirp> userChirps = chirpDAO.getChirpsByUser("user1");
        assertEquals(1, userChirps.size());
        assertEquals("This is a test chirp", userChirps.get(0).getContent());

        verify(mockSerializer, times(1)).createState(any(Chirp.class), contains("user1"));
    }

    @Test
    public void testAddChirpIOException() throws IOException {
        doThrow(new IOException()).when(mockSerializer).createState(any(Chirp.class), anyString());

        chirpDAO.addChirp("user1", "This chirp will fail to save");

        Vector<Chirp> userChirps = chirpDAO.getChirpsByUser("user1");
        assertEquals(1, userChirps.size());
        assertEquals("This chirp will fail to save", userChirps.get(0).getContent());
    }

    @Test
    public void testGetChirpsByUser() {
        chirpDAO.addChirp("user1", "Chirp 1");
        chirpDAO.addChirp("user1", "Chirp 2");

        Vector<Chirp> userChirps = chirpDAO.getChirpsByUser("user1");
        assertEquals(2, userChirps.size());
        assertEquals("Chirp 1", userChirps.get(0).getContent());
        assertEquals("Chirp 2", userChirps.get(1).getContent());
    }

    @Test
    public void testGetAllChirps() {
        chirpDAO.addChirp("user1", "User1 Chirp");
        chirpDAO.addChirp("user2", "User2 Chirp");

        Vector<Chirp> allChirps = chirpDAO.getAllChirps();
        assertEquals(2, allChirps.size());
    }
}
