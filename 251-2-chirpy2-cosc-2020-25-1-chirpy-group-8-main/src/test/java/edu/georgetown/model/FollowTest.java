package test.java.edu.georgetown.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.georgetown.model.Follow;

public class FollowTest {
  
  private Follow follow;

  private static final String TEST_FOLLOWER = "JohnDoe";
  private static String TEST_FOLLOWEE = "JaneDoe";

  @BeforeEach
  void setUp() {
    follow = new Follow(TEST_FOLLOWER, TEST_FOLLOWEE);
  }

  @Test
  void Creates_instance() {
    assertNotNull(follow);
  }

  @Test
  void Gets_follower() {
    assertEquals(follow.getFollower(), TEST_FOLLOWER);
  }

  @Test
  void Gets_followee() {
    assertEquals(follow.getFollowee(), TEST_FOLLOWEE);
  }
}
