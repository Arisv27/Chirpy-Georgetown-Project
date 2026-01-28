/**
 * This file contains the handler for the default (top-level) page.  
 */

package edu.georgetown.handler.authentication;

import java.io.IOException;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import edu.georgetown.display.BasePageHandler;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.http.ResponseUtils;
import edu.georgetown.service.UserService;

/**
 * The DefaultPageHandler class is responsible for handling requests to the
 * default
 * page of the Chirpy website. It extends the BasePageHandler and provides
 * specific
 * behavior for rendering the top page or redirecting users based on their login
 * status.
 *
 * <p>
 * This handler uses a FreeMarker template to render the default page and
 * interacts
 * with the UserService to determine the user's authentication state.
 * </p>
 *
 * <p>
 * Key responsibilities:
 * </p>
 * <ul>
 * <li>Redirects logged-in users to the timeline page.</li>
 * <li>Renders the default page for users who are not logged in.</li>
 * </ul>
 *
 * <p>
 * Dependencies:
 * </p>
 * <ul>
 * <li>{@link TemplateRenderer} - Used to render the FreeMarker template.</li>
 * <li>{@link UserService} - Provides user authentication and session
 * management.</li>
 * </ul>
 *
 * @see BasePageHandler
 * @see TemplateRenderer
 * @see UserService
 */
public class DefaultPageHandler extends BasePageHandler {

    /** the template that contains the top page for the Chirpy website */
    private final static String DEFAULT_PAGE = "authentication/toppage.ftl";

    public DefaultPageHandler(TemplateRenderer tr, UserService us) {
        super(tr, DEFAULT_PAGE, us);
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange, Map<String, Object> dataModel) throws IOException {
        // Redirects the user if they are logged in
        if (userService.isLoggedIn(exchange)) {
            ResponseUtils.sendRedirect(exchange, "/timeline/");
        } else {
            renderTemplate(exchange, dataModel);
        }
    }
}