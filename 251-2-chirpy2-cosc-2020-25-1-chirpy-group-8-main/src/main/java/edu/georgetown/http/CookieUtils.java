package edu.georgetown.http;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import edu.georgetown.logging.LoggerFactory;

/**
 * Utility class for handling HTTP cookies in an HttpExchange context.
 * 
 * <p>
 * This class provides methods to add, delete, and retrieve cookies
 * from HTTP requests and responses. It ensures proper encoding and decoding
 * of cookie values and supports setting cookies with attributes such as
 * HttpOnly and Max-Age.
 * </p>
 * 
 * <p>
 * Methods:
 * </p>
 * <ul>
 * <li>{@link #addCookie(HttpExchange, String, String)}: Adds a cookie to the
 * response.</li>
 * <li>{@link #deleteCookie(HttpExchange, String)}: Deletes a cookie by setting
 * it with an expired max-age.</li>
 * <li>{@link #getCookies(HttpExchange)}: Retrieves all cookies from the request
 * as a map of name-value pairs.</li>
 * </ul>
 * 
 * <p>
 * Note: This utility assumes that cookies are managed in a simple key-value
 * format
 * and does not handle advanced cookie attributes beyond those explicitly set in
 * the methods.
 * </p>
 * 
 * <p>
 * Dependencies:
 * </p>
 * <ul>
 * <li>{@link com.sun.net.httpserver.HttpExchange}: Represents the HTTP exchange
 * context.</li>
 * <li>{@link java.net.URLEncoder} and {@link java.net.URLDecoder}: For encoding
 * and decoding cookie values.</li>
 * <li>{@link java.nio.charset.StandardCharsets}: For specifying UTF-8
 * encoding.</li>
 * </ul>
 */
public class CookieUtils {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * Adds a cookie to the response.
     * 
     * @param exchange the HttpExchange object representing the current exchange
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     */
    public static void addCookie(HttpExchange exchange, String name, String value) {
        exchange.getResponseHeaders().add("Set-Cookie",
                name + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8) + "; Path=/; HttpOnly");
        logger.info("Cookie added with name: '" + name + "', and value '" + value + "'.");
    }

    /**
     * Deletes a cookie by setting it with an expired max-age.
     * 
     * @param exchange the HttpExchange object representing the current exchange
     * @param name     the name of the cookie to delete
     */
    public static void deleteCookie(HttpExchange exchange, String name) {
        exchange.getResponseHeaders().add("Set-Cookie",
                name + "=; Path=/; HttpOnly; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
        logger.info("Cookie deleted with name: '" + name + "'.");
    }

    /**
     * Gets the value of a cookie from the request.
     * 
     * @param exchange the HttpExchange object representing the current exchange
     * @param var      the name of the cookie
     * @return the value of the cookie, or null if the cookie is not set
     */
    public static Map<String, String> getCookies(HttpExchange exchange) {

        // Create an empty cookies map
        Map<String, String> cookies = new HashMap<String, String>();
        // Get the list of raw cookie data
        List<String> cookieList = exchange.getRequestHeaders().get("Cookie");

        // End early if there are no cookies
        if (cookieList == null) {
            return cookies;
        }

        // Parse each string in the raw data list
        for (String cookieStr : cookieList) {
            String[] cookiesStr = cookieStr.split(";");
            for (String cookie : cookiesStr) {
                String[] parts = cookie.split("=");
                cookies.put(parts[0], URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
            }
        }

        // Return the parsed cookie map
        logger.info("Retrieved " + cookies.size() + " cookies.");
        return cookies;
    }
}
