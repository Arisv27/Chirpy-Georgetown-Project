package test.java.edu.georgetown.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Vector;

import edu.georgetown.dao.FollowDAO;
import edu.georgetown.service.FollowService;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private FollowDAO mockFollowDao;

    @InjectMocks
    private FollowService followService;

    private static final String TEST_FOLLOWER = "Alice";
    private static final String TEST_FOLLOWEE = "Bob";

    @Test
    public void testFollowUserSuccessfully() {
        when(mockFollowDao.createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE)).thenReturn(true);
        when(mockFollowDao.getAccountsUserFollows(TEST_FOLLOWER)).thenReturn(new Vector<>());

        followService.follow(TEST_FOLLOWER, TEST_FOLLOWEE);

        verify(mockFollowDao, times(1)).createRelationship(TEST_FOLLOWER, TEST_FOLLOWEE);
    }

    @Test
    public void testFollowUserThrowsExceptionWhenFollowingSelf() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            followService.follow(TEST_FOLLOWER, TEST_FOLLOWER);
        });

        assertEquals("A user cannot follow themselves.", exception.getMessage());
    }

    @Test
    public void testFollowUserThrowsExceptionWhenAlreadyFollowing() {
        Vector<String> following = new Vector<>();
        following.add(TEST_FOLLOWEE);
        when(mockFollowDao.getAccountsUserFollows(TEST_FOLLOWER)).thenReturn(following);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            followService.follow(TEST_FOLLOWER, TEST_FOLLOWEE);
        });

        assertEquals(TEST_FOLLOWER + " is already following " + TEST_FOLLOWEE, exception.getMessage());
    }

    @Test
    public void testUnfollowUserSuccessfully() {
        Vector<String> following = new Vector<>();
        following.add(TEST_FOLLOWEE);
        when(mockFollowDao.getAccountsUserFollows(TEST_FOLLOWER)).thenReturn(following);

        followService.unfollow(TEST_FOLLOWER, TEST_FOLLOWEE);

        verify(mockFollowDao, times(1)).deleteRelationship(TEST_FOLLOWER, TEST_FOLLOWEE);
    }

    @Test
    public void testUnfollowUserThrowsExceptionWhenUnfollowingSelf() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            followService.unfollow(TEST_FOLLOWER, TEST_FOLLOWER);
        });

        assertEquals("A user cannot unfollow themselves.", exception.getMessage());
    }

    @Test
    public void testUnfollowUserThrowsExceptionWhenNotFollowing() {
        when(mockFollowDao.getAccountsUserFollows(TEST_FOLLOWER)).thenReturn(new Vector<>());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            followService.unfollow(TEST_FOLLOWER, TEST_FOLLOWEE);
        });

        assertEquals(TEST_FOLLOWER + " is not following " + TEST_FOLLOWEE, exception.getMessage());
    }

    @Test
    public void testGetAccountsUserFollows() {
        Vector<String> following = new Vector<>();
        following.add(TEST_FOLLOWEE);
        when(mockFollowDao.getAccountsUserFollows(TEST_FOLLOWER)).thenReturn(following);

        Vector<String> result = followService.getAccountsUserFollows(TEST_FOLLOWER);

        assertEquals(1, result.size());
        assertTrue(result.contains(TEST_FOLLOWEE));
    }

    @Test
    public void testGetAccountsFollowingUser() {
        Vector<String> followers = new Vector<>();
        followers.add(TEST_FOLLOWER);
        when(mockFollowDao.getAccountsFollowingUser(TEST_FOLLOWEE)).thenReturn(followers);

        Vector<String> result = followService.getAccountsFollowingUser(TEST_FOLLOWEE);

        assertEquals(1, result.size());
        assertTrue(result.contains(TEST_FOLLOWER));
    }

    @Test
    public void testIsFollowingReturnsTrue() {
        Vector<String> following = new Vector<>();
        following.add(TEST_FOLLOWEE);
        when(mockFollowDao.getAccountsUserFollows(TEST_FOLLOWER)).thenReturn(following);

        boolean result = followService.isFollowing(TEST_FOLLOWER, TEST_FOLLOWEE);

        assertTrue(result);
    }

    @Test
    public void testIsFollowingReturnsFalse() {
        when(mockFollowDao.getAccountsUserFollows(TEST_FOLLOWER)).thenReturn(new Vector<>());

        boolean result = followService.isFollowing(TEST_FOLLOWER, TEST_FOLLOWEE);

        assertFalse(result);
    }
}
