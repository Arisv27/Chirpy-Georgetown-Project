package edu.georgetown.handler.secure;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import edu.georgetown.display.BasePageHandler;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.http.CookieUtils;
import edu.georgetown.http.ResponseUtils;
import edu.georgetown.model.Chirp;
import edu.georgetown.service.ChirpService;
import edu.georgetown.service.FollowService;
import edu.georgetown.service.UserService;

/**
 * The TimelinePageHandler class is responsible for handling HTTP requests
 * related to the
 * timeline page of the application. It extends the BasePageHandler and provides
 * specific
 * implementations for handling GET and POST requests.
 * 
 * <p>
 * This handler interacts with the ChirpService, FollowService, and UserService
 * to
 * manage user timelines, including displaying chirps and managing
 * follow/unfollow actions.
 * 
 * <p>
 * Key responsibilities:
 * <ul>
 * <li>Render the timeline page with chirps and followed users for logged-in
 * users.</li>
 * <li>Redirect unauthenticated users to the home page.</li>
 * <li>Handle follow and unfollow actions via POST requests.</li>
 * </ul>
 */
public class TimelinePageHandler extends BasePageHandler {

    private final static String TIMELINE_TEMPLATE = "secure/timeline.ftl";
    private final ChirpService chirpService;
    private final FollowService followService;

    public TimelinePageHandler(TemplateRenderer tr, ChirpService cs, UserService us, FollowService fs) {
        super(tr, TIMELINE_TEMPLATE, us);
        this.chirpService = cs;
        this.followService = fs;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange, Map<String, Object> dataModel) throws IOException {
        if (!userService.isLoggedIn(exchange)) {
            ResponseUtils.sendRedirect(exchange, "/");
            return;
        }
        String username = CookieUtils.getCookies(exchange).get("username");
        dataModel.put("username", username);
        logger.info("Got username: " + username);
        Vector<String> followedUsers = followService.getAccountsUserFollows(username);
        dataModel.put("followedUsers", followedUsers);
        logger.info("Followed Users: " + followedUsers);

        Vector<Chirp> chirps = chirpService.getAllChirps();
        dataModel.put("chirps", chirps);
        renderTemplate(exchange, dataModel);
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange, Map<String, Object> dataModel,
            Map<String, String> rawFormData) throws IOException {
        if (!userService.isLoggedIn(exchange)) {
            ResponseUtils.sendRedirect(exchange, "/");
            return;
        }
        String activeUser = CookieUtils.getCookies(exchange).get("username");
        String targetUser = rawFormData.get("targetUser");
        String action = rawFormData.get("action");

        logger.info("Attempting to " + action + " " + targetUser);

        if ("follow".equals(action) && targetUser != null) {
            followService.follow(activeUser, targetUser);
        } else if ("unfollow".equals(action) && targetUser != null) {
            followService.unfollow(activeUser, targetUser);
        }

        logger.info("Action successful: " + action + " " + targetUser);

        handleGetRequest(exchange, dataModel); // Reuse GET logic after modifying state
    }
}