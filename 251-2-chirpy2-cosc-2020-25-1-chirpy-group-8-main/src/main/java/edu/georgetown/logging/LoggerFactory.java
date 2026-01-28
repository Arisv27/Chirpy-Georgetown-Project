package edu.georgetown.logging;

import java.util.logging.Logger;

/**
 * The LoggerFactory class provides a centralized mechanism for obtaining
 * a logger instance. This implementation ensures that all logging within
 * the application is directed to a single global logger, simplifying
 * log management and configuration.
 * 
 * <p>
 * Key Features:
 * <ul>
 * <li>Singleton pattern: Ensures a single instance of the logger is used
 * throughout the application.</li>
 * <li>Global logging: Provides a shared logger instance that can be
 * accessed by all classes.</li>
 * <li>Future extensibility: Includes a commented-out method for
 * per-class logging, allowing for more granular logging in the
 * future.</li>
 * </ul>
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * {@code
 * Logger logger = LoggerFactory.getLogger();
 * logger.info("This is a log message.");
 * }
 * </pre>
 * 
 * <p>
 * Note: This implementation currently uses a single global logger.
 * For more advanced use cases, such as per-class logging, the commented-out
 * method {@code getLogger(Class<?> clazz)} can be implemented.
 * 
 * <p>
 * Advantages of using a logger over System.out.println():
 * <ul>
 * <li>Log levels: Control which messages are logged (e.g., INFO, WARNING,
 * ERROR).</li>
 * <li>Flexibility: Redirect logs to different outputs (e.g., console,
 * file).</li>
 * <li>Consistency: Centralized logging configuration for the entire
 * application.</li>
 * </ul>
 */
public class LoggerFactory {

    // Single instance of Logger
    private static final Logger logger = Logger.getLogger("MyLogger");

    // Private constructor to prevent instantiation
    private LoggerFactory() {
    }

    // Temporary: Single global logger
    public static Logger getLogger() {
        return logger;
    }

    // Future refactor: Per-class logging
    // public static Logger getLogger(Class<?> clazz) {
    // return Logger.getLogger(clazz.getName());
    // }
}
