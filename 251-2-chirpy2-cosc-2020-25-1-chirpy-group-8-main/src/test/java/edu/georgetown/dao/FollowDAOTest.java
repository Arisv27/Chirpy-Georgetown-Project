package test.java.edu.georgetown.dao;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Vector;

import edu.georgetown.dao.FollowDAO;
import edu.georgetown.model.Follow;
import edu.georgetown.persistence.Serializer;


@ExtendWith(MockitoExtension.class)
public class FollowDAOTest {

    @Mock
    private Serializer<Follow> mockSerializer;

    @InjectMocks
    private FollowDAO followDao;

    private static final String TEST_FOLLOWER = "Alice";
    private static final String TEST_FOLLOWEE = "Bob";

    @Test
    public void testCreateRelationshipSuccess() {
        assertTrue(followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE));
        assertEquals(1, followDao.getAccountsUserFollows(TEST_FOLLOWER).size());
        assertEquals(1, followDao.getAccountsFollowingUser(TEST_FOLLOWEE).size());
    }

    @Test
    public void testCreateRelationshipFailureDueToSerialization() throws IOException {
        doThrow(new IOException()).when(mockSerializer).createState(any(Follow.class), anyString());
        assertFalse(followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE));
    }

    @Test
    public void testDeleteRelationshipSuccess() {
        followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE);
        assertTrue(followDao.deleteRelationship(TEST_FOLLOWER, TEST_FOLLOWEE));
        assertEquals(0, followDao.getAccountsUserFollows(TEST_FOLLOWER).size());
        assertEquals(0, followDao.getAccountsFollowingUser(TEST_FOLLOWEE).size());
    }

    @Test
    public void testDeleteRelationshipFailureDueToSerialization() throws IOException {
        followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE);
        doThrow(new IOException()).when(mockSerializer).deleteState(anyString());
        assertFalse(followDao.deleteRelationship(TEST_FOLLOWER, TEST_FOLLOWEE));
    }

    @Test
    public void testGetAccountsFollowingUser() {
        followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE);
        Vector<String> followers = followDao.getAccountsFollowingUser(TEST_FOLLOWEE);
        assertEquals(1, followers.size());
        assertEquals(TEST_FOLLOWER, followers.get(0));
    }

    @Test
    public void testGetAccountsUserFollows() {
        followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE);
        Vector<String> followees = followDao.getAccountsUserFollows(TEST_FOLLOWER);
        assertEquals(1, followees.size());
        assertEquals(TEST_FOLLOWEE, followees.get(0));
    }

    @Test
    public void testLoadFollowsSuccess() {
        Vector<Follow> follows = new Vector<>();
        follows.add(new Follow(TEST_FOLLOWER, TEST_FOLLOWEE));
        when(mockSerializer.loadDirectory()).thenReturn(follows);

        assertTrue(followDao.loadFollows());
        assertEquals(1, followDao.getAccountsUserFollows(TEST_FOLLOWER).size());
        assertEquals(1, followDao.getAccountsFollowingUser(TEST_FOLLOWEE).size());
    }

    @Test
    public void testLoadFollowsEmpty() {
        when(mockSerializer.loadDirectory()).thenReturn(new Vector<>());
        assertFalse(followDao.loadFollows());
    }

    @Test
    public void testLoadFollowsNull() {
        when(mockSerializer.loadDirectory()).thenReturn(null);
        assertFalse(followDao.loadFollows());
    }

    @Test
    public void testGenerateFilename() {
        String filename = followDao.generateFilename(TEST_FOLLOWER, TEST_FOLLOWEE);
        assertEquals("Alice Bob", filename);
    }

    @Test
    public void testCreateDuplicateRelationship() throws IOException {
        followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE);
        doThrow(new IOException()).when(mockSerializer).createState(any(Follow.class), anyString());
        assertFalse(followDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE));
        assertEquals(1, followDao.getAccountsUserFollows(TEST_FOLLOWER).size());
    }

    @Test
    public void testDeleteNonExistentRelationship() throws IOException {
        doThrow(new IOException()).when(mockSerializer).deleteState(anyString());
        assertFalse(followDao.deleteRelationship(TEST_FOLLOWER, TEST_FOLLOWEE));
    }
}
