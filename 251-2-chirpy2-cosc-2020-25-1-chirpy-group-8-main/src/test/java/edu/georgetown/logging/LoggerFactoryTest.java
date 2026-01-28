package test.java.edu.georgetown.logging;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import edu.georgetown.logging.LoggerFactory;

public class LoggerFactoryTest {
  @Test
    void testGetLoggerNotNull() {
        Logger logger = LoggerFactory.getLogger();
        assertNotNull(logger, "Logger should not be null");
    }
    
    @Test
    void testGetLoggerReturnsSameInstance() {
        Logger logger1 = LoggerFactory.getLogger();
        Logger logger2 = LoggerFactory.getLogger();
        assertSame(logger1, logger2, "LoggerFactory should return the same logger instance");
    }
}
