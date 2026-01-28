package edu.georgetown.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import java.util.logging.Logger;
import edu.georgetown.logging.LoggerFactory;

/**
 * Utility class for handling and parsing form data from HTTP requests.
 * 
 * <p>
 * This class provides methods to parse form data from an HTTP request body
 * into a map, as well as additional utility methods for filtering and
 * processing form data. The primary method, {@code parseResponse}, is used to
 * extract key-value pairs from a URL-encoded form submission.
 * </p>
 * 
 * <p>
 * Usage Example:
 * </p>
 * 
 * <pre>{@code
 * HttpExchange exchange = ...; // Obtain the HttpExchange object
 * Map<String, String> formData = FormUtils.parseResponse(exchange);
 * }</pre>
 */
public class FormUtils {

    private static final Logger logger = LoggerFactory.getLogger();

    private FormUtils() {
        // Prevent instantiation
    }

    /**
     * This is a helper function which parses the response from a HTML form and puts
     * the results into a Map. This code was adopted from the code at
     * https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection.
     * See `TestFormHandler.java` for an example of how to use this function.
     * You probably don't want to change this function.
     * 
     * @param query the query string to parse
     * @return the form data as a Map
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> parseResponse(HttpExchange exchange) {
        Map<String, String> myMap = new HashMap<String, String>();

        // the data sent via the HTML form ends up in the request body
        byte[] b;
        try {
            b = exchange.getRequestBody().readAllBytes();
        } catch (IOException e) {
            logger.warning("Cannot get request body: " + e);
            return myMap;
        }
        String formData = new String(b);
        if (formData.equals("")) {
            return myMap;
        }

        try {
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                myMap.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        } catch (IOException e) {
            logger.warning("IOException: " + e.getMessage());
        }
        return myMap;
    }
}
