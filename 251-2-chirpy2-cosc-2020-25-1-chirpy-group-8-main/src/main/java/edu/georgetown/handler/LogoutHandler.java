package edu.georgetown.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.georgetown.http.CookieUtils;
import edu.georgetown.http.ResponseUtils;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * The {@code LogoutHandler} class is responsible for handling user logout
 * requests.
 * It implements the {@link HttpHandler} interface to process HTTP exchanges.
 * 
 * <p>
 * This handler performs the following actions:
 * <ul>
 * <li>Invalidates the user's session by deleting the "username" cookie.</li>
 * <li>Logs the logout event for auditing purposes.</li>
 * <li>Redirects the user to the login page after logout.</li>
 * </ul>
 * 
 * <p>
 * Usage:
 * 
 * <pre>{@code
 * HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
 * server.createContext("/logout", new LogoutHandler());
 * server.start();
 * }</pre>
 * 
 * <p>
 * Dependencies:
 * <ul>
 * <li>{@link CookieUtils} for cookie management.</li>
 * <li>{@link ResponseUtils} for handling HTTP responses.</li>
 * <li>{@link Logger} for logging events.</li>
 * </ul>
 * 
 * @see HttpHandler
 * @see CookieUtils
 * @see ResponseUtils
 */
public class LogoutHandler implements HttpHandler {

    private static final Logger logger = Logger.getLogger(LogoutHandler.class.getName());

    /**
     * Handles the HTTP exchange for logging out a user.
     * <p>
     * This method invalidates the user's session by deleting the "username" cookie
     * and logs the logout event. After invalidating the session, it redirects the
     * user to the login page.
     *
     * @param exchange the {@link HttpExchange} object representing the HTTP request
     *                 and response
     * @throws IOException if an I/O error occurs during the handling of the
     *                     exchange
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Invalidate the user session by deleting the cookie
        CookieUtils.deleteCookie(exchange, "username");
        logger.info("User logged out successfully.");

        // Redirect to the login page
        ResponseUtils.sendRedirect(exchange, "/");
    }
}