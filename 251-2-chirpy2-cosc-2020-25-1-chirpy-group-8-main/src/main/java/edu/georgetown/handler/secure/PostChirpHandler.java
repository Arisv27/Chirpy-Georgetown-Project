package edu.georgetown.handler.secure;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

import edu.georgetown.display.BasePageHandler;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.http.CookieUtils;
import edu.georgetown.http.ResponseUtils;
import edu.georgetown.service.ChirpService;
import edu.georgetown.service.UserService;

/**
 * The PostChirpHandler class handles HTTP requests for posting chirps in a
 * secure manner.
 * It extends the BasePageHandler and provides functionality for rendering the
 * post chirp page
 * and processing chirp submissions.
 * 
 * <p>
 * This handler supports both GET and POST requests:
 * <ul>
 * <li>GET requests render the post chirp page if the user is logged in,
 * otherwise redirects to the home page.</li>
 * <li>POST requests process the chirp submission, ensuring the content is not
 * empty and the user is authenticated.</li>
 * </ul>
 * 
 * <p>
 * Dependencies:
 * <ul>
 * <li>{@link TemplateRenderer} for rendering the HTML templates.</li>
 * <li>{@link ChirpService} for handling chirp-related operations.</li>
 * <li>{@link UserService} for managing user authentication and session
 * information.</li>
 * </ul>
 * 
 * <p>
 * Behavior:
 * <ul>
 * <li>If the user is not logged in, they are redirected to the home page.</li>
 * <li>If the chirp content is empty, an error message is displayed on the post
 * chirp page.</li>
 * <li>Successful chirp submissions redirect the user to the timeline page.</li>
 * </ul>
 * 
 * @see BasePageHandler
 * @see TemplateRenderer
 * @see ChirpService
 * @see UserService
 */
public class PostChirpHandler extends BasePageHandler {

    private final static String POST_CHIRP_TEMPLATE = "secure/postchirp.ftl";
    private final static String CONTENT_FIELD = "content";
    private final ChirpService chirpService;

    public PostChirpHandler(TemplateRenderer tr, ChirpService cs, UserService us) {
        super(tr, POST_CHIRP_TEMPLATE, us);
        this.chirpService = cs;
    }

    /**
     * Extracts the value of a specified field from the provided form data map.
     * If the field is not present in the map, an empty string is returned.
     * The returned value is trimmed of leading and trailing whitespace.
     *
     * @param rawFormData A map containing form data as key-value pairs.
     * @param field       The key of the field to extract from the form data.
     * @return The trimmed value of the specified field, or an empty string if the
     *         field is not present.
     */
    private String extractField(Map<String, String> rawFormData, String field) {
        return rawFormData.getOrDefault(field, "").trim();
    }

    /**
     * Handles a GET request by checking if the user is logged in.
     * If the user is logged in, retrieves the username from the cookies,
     * adds it to the data model, and renders the appropriate template.
     * If the user is not logged in, redirects them to the home page.
     *
     * @param exchange  The HttpExchange object representing the HTTP request and
     *                  response.
     * @param dataModel A map containing data to be passed to the template for
     *                  rendering.
     * @throws IOException If an I/O error occurs during the handling of the
     *                     request.
     */
    protected void handleGetRequest(HttpExchange exchange, Map<String, Object> dataModel) throws IOException {
        if (userService.isLoggedIn(exchange)) {
            String username = CookieUtils.getCookies(exchange).get("username");
            dataModel.put("username", username);
            renderTemplate(exchange, dataModel);
        } else {
            ResponseUtils.sendRedirect(exchange, "/");
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange, Map<String, Object> dataModel,
            Map<String, String> rawFormData) throws IOException {
        // Extract data from form and cookies
        String content = extractField(rawFormData, CONTENT_FIELD);
        String username = CookieUtils.getCookies(exchange).get("username");

        if (username != null && !content.isBlank()) {
            // Post chirp and redirect to timeline
            chirpService.postChirp(username, content);
            ResponseUtils.sendRedirect(exchange, "/timeline/");
        } else {
            // Render error
            dataModel.put("error", "Content cannot be empty.");
            renderTemplate(exchange, dataModel);
        }
    }
}