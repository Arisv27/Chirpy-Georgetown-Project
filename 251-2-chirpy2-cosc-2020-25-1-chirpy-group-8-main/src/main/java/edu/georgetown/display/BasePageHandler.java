package edu.georgetown.display;

import com.sun.net.httpserver.HttpHandler;

import edu.georgetown.http.CookieUtils;
import edu.georgetown.http.FormUtils;
import edu.georgetown.http.ResponseUtils;
import edu.georgetown.logging.LoggerFactory;
import edu.georgetown.model.Chirper;
import edu.georgetown.service.UserService;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>
 * Abstract base class for handling HTTP requests in a web application.
 * </p>
 * <p>
 * This class implements the {@link HttpHandler} interface and provides
 * common functionality for handling GET and POST requests, logging requests,
 * and rendering templates. Subclasses can extend this class to define specific
 * behavior for handling HTTP requests.
 * </p>
 *
 * <p>
 * Key features of this class include:
 * <ul>
 * <li>Logging incoming HTTP requests.</li>
 * <li>Rendering templates using a provided {@link TemplateRenderer}.</li>
 * <li>Handling GET requests with a default implementation.</li>
 * <li>Providing a no-op implementation for handling POST requests, which can
 * be overridden by subclasses.</li>
 * <li>Adding user login information and a list of users to the data model
 * for debugging purposes.</li>
 * </ul>
 *
 * <p>
 * Subclasses are expected to override the {@code handlePostRequest} method
 * to provide specific behavior for POST requests, if needed.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * {@code
 * public class MyPageHandler extends BasePageHandler {
 *     public MyPageHandler(TemplateRenderer tr, String templateString, UserService us) {
 *         super(tr, templateString, us);
 *     }
 *
 *     &#64;Override
 *     protected void handlePostRequest(HttpExchange exchange, Map<String, Object> dataModel,
 *                                      Map<String, String> rawFormData) throws IOException {
 *         // Custom POST request handling logic
 *     }
 * }
 * </pre>
 *
 * @see HttpHandler
 * @see TemplateRenderer
 * @see UserService
 */
public abstract class BasePageHandler implements HttpHandler {

    protected static final Logger logger = LoggerFactory.getLogger();
    protected final TemplateRenderer templateRenderer;
    protected final String templateString;
    protected final UserService userService;

    public BasePageHandler(TemplateRenderer tr, String templateString, UserService us) {
        this.templateRenderer = tr;
        this.templateString = templateString;
        this.userService = us;
    }

    /**
     * Logs incoming requests.
     * 
     * @param exchange the HTTP exchange object containing request details
     */
    protected void logRequest(HttpExchange exchange) {
        logger.info(this.getClass().getSimpleName() + " called for path: " + exchange.getRequestURI().getPath());
        logger.info("Request method: " + exchange.getRequestMethod());
    }

    /**
     * Handles HTTP GET requests by rendering a template with the provided data
     * model.
     *
     * @param exchange  The HttpExchange object that contains the request and
     *                  response
     *                  information.
     * @param dataModel A map containing the data to be used for rendering the
     *                  template.
     * @throws IOException If an input or output exception occurs during the
     *                     handling
     *                     of the request.
     */
    protected void handleGetRequest(HttpExchange exchange, Map<String, Object> dataModel) throws IOException {
        renderTemplate(exchange, dataModel);
    }

    /**
     * Handles HTTP POST requests. This method is intended to be overridden by
     * subclasses to provide specific behavior for handling POST requests.
     * By default, this method performs no operation (No-op).
     *
     * @param exchange    The HttpExchange object that contains the request and
     *                    response information.
     * @param dataModel   A map containing data to be used for rendering or
     *                    processing
     *                    the request.
     * @param rawFormData A map containing raw form data extracted from the POST
     *                    request.
     * @throws IOException If an I/O error occurs during the handling of the
     *                     request.
     */
    protected void handlePostRequest(HttpExchange exchange, Map<String, Object> dataModel,
            Map<String, String> rawFormData) throws IOException {
        // No-op by default
    }

    /**
     * Renders the template with the given dataModel.
     * 
     * @param exchange  the HTTP exchange object containing request details
     * @param dataModel the data model to use for rendering the template
     * @throws IOException if an I/O error occurs
     */
    protected void renderTemplate(HttpExchange exchange, Map<String, Object> dataModel) throws IOException {
        String parsedTemplate = templateRenderer.parseTemplate(templateString, dataModel);
        ResponseUtils.sendHtmlResponse(exchange, parsedTemplate);
    }

    /**
     * Handles incoming HTTP requests.
     * This method is called by the HTTP server when a request is received.
     * It determines the request method (GET or POST) and calls the appropriate
     * handler method.
     * 
     * @param exchange the HTTP exchange object containing request details
     * 
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        logRequest(exchange);

        // Set headers to prevent caching
        exchange.getResponseHeaders().set("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        exchange.getResponseHeaders().set("Pragma", "no-cache");
        exchange.getResponseHeaders().set("Expires", "0");

        // Create empty data model for request
        Map<String, Object> dataModel = new HashMap<>();

        // Add login info to data model
        if (userService.isLoggedIn(exchange)) {
            String username = CookieUtils.getCookies(exchange).get("username");
            dataModel.put("username", username);
            dataModel.put("isLoggedIn", true);
        }

        // Add list of users to footer for debugging
        Map<String, String> users = new HashMap<>();
        for (Chirper user : userService.getUsers()) {
            users.put(user.getUsername(), user.getPassword()); // Extract actual username
        }
        dataModel.put("users", users);

        // Direct request based on method
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGetRequest(exchange, dataModel);
        } else if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> rawFormData = FormUtils.parseResponse(exchange);
            handlePostRequest(exchange, dataModel, rawFormData);
        }
    }

}
