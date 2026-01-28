package edu.georgetown.handler.secure;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map; // REMOVE DURING PRODUCTION
import java.io.IOException;
import java.util.Vector;

import edu.georgetown.model.Chirp;

import edu.georgetown.display.BasePageHandler;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.http.CookieUtils;
import edu.georgetown.http.ResponseUtils;
import edu.georgetown.service.SearchService;
import edu.georgetown.service.UserService;

/**
 * <p>
 * The SearchPageHandler class handles HTTP GET and POST requests for the search
 * page.
 * It extends the BasePageHandler and provides functionality for rendering the
 * search page
 * and processing search queries submitted by users.
 * </p>
 * 
 * <p>
 * This class uses the following services:
 * </p>
 * <ul>
 * <li>{@link TemplateRenderer} for rendering templates.</li>
 * <li>{@link SearchService} for executing search queries.</li>
 * <li>{@link UserService} for user authentication and session management.</li>
 * </ul>
 * 
 * <p>
 * Key Features:
 * </p>
 * <ul>
 * <li>Handles GET requests to display the search page for logged-in users.</li>
 * <li>Handles POST requests to process search queries and display results.</li>
 * <li>Validates search queries to ensure they start with '@' (user search) or
 * '#' (tag search).</li>
 * <li>Provides error handling and feedback for invalid queries.</li>
 * </ul>
 * 
 * <p>
 * Note:
 * </p>
 * <li>Queries must start with '@' for user searches or '#' for tag
 * searches.</li>
 * <li>If the query is invalid, an error message is displayed on the search
 * page.</li>
 */
public class SearchPageHandler extends BasePageHandler {
    private final static String SEARCH_TEMPLATE = "secure/search.ftl";
    private final static String QUERY_FIELD = "query";
    private final SearchService searchService;

    public SearchPageHandler(TemplateRenderer tr, SearchService ss, UserService us) {
        super(tr, SEARCH_TEMPLATE, us);
        this.searchService = ss;
    }

    /**
     * Handles HTTP GET requests for the search page.
     * 
     * <p>
     * This method checks if the user is logged in by verifying the session
     * cookies. If the user is logged in, their username is retrieved from the
     * cookies and added to the data model. The search page template is then
     * rendered with the provided data model. If the user is not logged in, they
     * are redirected to the home page.
     * </p>
     * 
     * @param exchange  The HttpExchange object that contains the request and
     *                  response
     *                  details.
     * @param dataModel A map containing the data to be passed to the template for
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

    /**
     * Handles POST requests for the search page.
     *
     * @param exchange    The HttpExchange object that contains the request and
     *                    response.
     * @param dataModel   A map representing the data model to be used for rendering
     *                    templates.
     * @param rawFormData A map containing the raw form data submitted in the POST
     *                    request.
     * @throws IOException If an I/O error occurs during processing.
     *
     *                     This method processes the search query submitted via the
     *                     POST request. It extracts the query
     *                     from the form data, validates it, and updates the data
     *                     model with the appropriate results or
     *                     error messages. If the query is valid, it retrieves the
     *                     search results and renders the template
     *                     with the results. If the query is invalid or blank, it
     *                     updates the data model with an error
     *                     message and renders the template with the error.
     */
    @Override
    protected void handlePostRequest(HttpExchange exchange, Map<String, Object> dataModel,
            Map<String, String> rawFormData) throws IOException {
        // Extract values from rawFormData
        String query = extractField(rawFormData, QUERY_FIELD);

        // verify that query is valid
        boolean queryIsValid = validateQuery(dataModel, query);

        // Stop processing if query is not provided
        if (query.isBlank()) {
            dataModel.put("errorModalMessage", "Query field was left blank.");
        }
        // Return list of chirps
        if (queryIsValid) {
            logger.info("Searched for query: " + query);
            dataModel.put("results", executeQuery(query));
            renderTemplate(exchange, dataModel);
        } else {
            dataModel.put("errorModalMessage",
                    "The query was not valid. Start your query with an at symbol ('@') to search for a Chirper, or with a pound symbol ('#') to search for a tag.");
            renderTemplate(exchange, dataModel);
        }
    }

    /**
     * Extracts the value of a specified field from the provided form data map.
     * If the field is not present in the map, an empty string is returned.
     * The returned value is trimmed of any leading or trailing whitespace.
     *
     * @param rawFormData A map containing form data with field names as keys and
     *                    their values as map values.
     * @param field       The name of the field to extract from the form data.
     * @return The trimmed value of the specified field, or an empty string if the
     *         field is not present.
     */
    private String extractField(Map<String, String> rawFormData, String field) {
        return rawFormData.getOrDefault(field, "").trim();
    }

    /**
     * Adds an error message to the data model for a specific field, logs the error,
     * and marks the field as invalid for accessibility purposes.
     *
     * @param dataModel The map representing the data model where error details will
     *                  be added.
     * @param fieldName The name of the field associated with the error.
     * @param errorMsg  The error message to be logged and displayed.
     */
    private void putError(Map<String, Object> dataModel, String fieldName, String errorMsg) {
        // Log the error message
        logger.info("Login Error: " + errorMsg);
        // Set helper message for field error
        dataModel.put(fieldName + "HelperMessage", errorMsg);
        // Finally, set the input field to display invalid
        dataModel.put(fieldName + "AriaInvalid", "true");
    }

    /**
     * Updates the data model to indicate a successful validation for a specific
     * input field.
     * This method sets the field's ARIA invalid attribute to "false" and ensures
     * the input value
     * is retained in the data model so the user does not need to re-enter valid
     * data.
     *
     * @param dataModel  The map representing the data model to be updated.
     * @param fieldName  The name of the input field being validated.
     * @param inputValue The value of the input field to be retained in the data
     *                   model.
     */
    private void putSuccess(Map<String, Object> dataModel, String fieldName, String inputValue) {
        // Set the input field to display valid
        dataModel.put(fieldName + "AriaInvalid", "false");
        // Finally, resend the input so the user doesn't have to redo valid fields
        dataModel.put(fieldName, inputValue);
    }

    /**
     * <p>
     * Validates the given query string and updates the data model with appropriate
     * success or error messages.
     * </p>
     * 
     * <p>
     * Validation Rules:
     * </p>
     * <ul>
     * <li>The query must not be blank and must have a length of at least 2
     * characters.</li>
     * <li>The query must start with an '@' for a user query or a '#' for a tag
     * query.</li>
     * </ul>
     * 
     * <p>
     * If the query is valid, a success message is added to the data model.
     * </p>
     * <p>
     * If the query is invalid, an error message is added to the data model.
     * </p>
     *
     * @param dataModel A map representing the data model to store validation
     *                  messages.
     * @param query     The query string to validate.
     * @return {@code true} if the query is valid; {@code false} otherwise.
     */
    private boolean validateQuery(Map<String, Object> dataModel, String query) {
        if (query.isBlank() || query.length() < 2) {
            putError(dataModel, "query", "Query must not be blank.");
            return false;
        }
        if (query.charAt(0) == '@'
                || query.charAt(0) == '#') {
            putSuccess(dataModel, QUERY_FIELD, query);
            return true;
        } else {
            putError(dataModel, "query",
                    "Query must start with an at sign '@' for a user query, or a pound sign '#' for a tag query.");
            return false;
        }
    }

    /**
     * Executes a search query based on the specified query string.
     * The query string must begin with either '@' (to search by user) or '#' (to
     * search by tag),
     * followed by the content to search for.
     *
     * @param query The search query string. It must start with '@' or '#' and
     *              contain at least one additional character.
     * @return A Vector of Chirp objects matching the search criteria, or null if
     *         the query is malformed.
     */
    private Vector<Chirp> executeQuery(String query) {
        // Assumes query begins with # or @ and contains at least one more character
        // after that
        char queryType = query.charAt(0);
        String queryContent = query.substring(1);
        switch (queryType) {
            case '@':
                return searchService.searchByUser(queryContent);
            case '#':
                return searchService.searchByTag("#"+queryContent);
            default:
                // this should never happen
                logger.warning("Logic error: query was malformed. Query: " + query);
                return null;
        }
    }
}
