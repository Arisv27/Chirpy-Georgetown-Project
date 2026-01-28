package test.java.edu.georgetown.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.georgetown.model.Chirper;

class ChirperTest {
  
  private Chirper chirper;
  
  private static final String TEST_USERNAME = "JohnDoe";
  private static final String TEST_PASSWORD = "password123";
  private static final boolean TEST_PUBLIC_STATUS = true;
  
  @BeforeEach
  void setUp() {
    chirper = new Chirper(TEST_USERNAME, TEST_PASSWORD, TEST_PUBLIC_STATUS);
  }

  @Test
  void Gets_username() {
    String realUsername = chirper.getUsername();
    assertEquals(TEST_USERNAME, realUsername);
  }

  @Test
  void Gets_password() {
    String realPassword = chirper.getPassword();
    assertEquals(TEST_PASSWORD, realPassword);
  }

  @Test
  void Sets_password() {
    String newPassword = "newPassword123";
    chirper.setPassword(newPassword);
    String realPassword = chirper.getPassword();
    assertEquals(newPassword, realPassword);
  }

  @Test
  void Gets_public_status() {
    boolean realStatus = chirper.getPublicStatus();
    assertEquals(TEST_PUBLIC_STATUS, realStatus);
  }

  @Test
  void Sets_public_status() {
    boolean newStatus = !TEST_PUBLIC_STATUS;
    chirper.setPublicStatus(newStatus);
    boolean realStatus = chirper.getPublicStatus();
    assertEquals(newStatus, realStatus);
  }

  @Test
  void Public_status_is_true_by_default() {
    Chirper defaultChirper = new Chirper(TEST_USERNAME, TEST_PASSWORD);
    boolean realStatus = defaultChirper.getPublicStatus();
    assertTrue(realStatus, "Default public status should be true.");
  }
}