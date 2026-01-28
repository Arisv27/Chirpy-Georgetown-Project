package edu.georgetown.http;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

/**
 * Utility class for handling HTTP responses.
 * 
 * This class provides methods to send HTML responses and handle HTTP redirects
 * in a simplified manner using the HttpExchange object.
 * 
 * <p>
 * Methods included:
 * <ul>
 * <li>{@link #sendHtmlResponse(HttpExchange, String)}: Sends an HTML response
 * to the client.</li>
 * <li>{@link #sendRedirect(HttpExchange, String)}: Redirects the client to a
 * specified URL.</li>
 * </ul>
 * 
 * <p>
 * Usage example:
 * 
 * <pre>
 * {@code
 * HttpExchange exchange = ...; // Obtain HttpExchange from the HTTP server
 * ResponseUtils.sendHtmlResponse(exchange, "<html><body>Hello, World!</body></html>");
 * 
 * // Or redirect to another URL
 * ResponseUtils.sendRedirect(exchange, "https://www.example.com");
 * }
 * </pre>
 */
public class ResponseUtils {
    public static void sendHtmlResponse(HttpExchange exchange, String responseBody) throws IOException {
        // set the type of content (in this case, we're sending back HTML)
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        // send the HTTP headers
        exchange.sendResponseHeaders(200, responseBody.length());
        // finally, write the actual response (the contents of the template)
        OutputStream os = exchange.getResponseBody();
        os.write(responseBody.getBytes());
        os.close();
    }

    /**
     * Redirects the client to a different URL.
     * 
     * @param exchange The HttpExchange object for the request.
     * @param location The URL to redirect to.
     * @throws IOException If an I/O error occurs.
     */
    public static void sendRedirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1); // 302 Found (Redirect) with no response body
        exchange.close();
    }
}
