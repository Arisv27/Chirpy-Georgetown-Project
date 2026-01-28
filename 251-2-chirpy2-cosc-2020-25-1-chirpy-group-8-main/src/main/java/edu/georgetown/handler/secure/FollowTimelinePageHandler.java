package edu.georgetown.handler.secure;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import edu.georgetown.display.BasePageHandler;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.model.Chirp;
import edu.georgetown.service.ChirpService;
import edu.georgetown.service.FollowService;
import edu.georgetown.service.UserService;
import edu.georgetown.http.CookieUtils;

/**
 * The FollowTimelinePageHandler class is responsible for handling HTTP GET
 * requests
 * for the "Follow Timeline" page. It filters and displays chirps (posts) from
 * users
 * that the currently logged-in user is following.
 * 
 * <p>
 * This class extends the BasePageHandler and utilizes services such as
 * ChirpService,
 * FollowService, and UserService to fetch and filter data for rendering the
 * page.
 * 
 * <p>
 * Key functionalities include:
 * <ul>
 * <li>Fetching all chirps from the ChirpService.</li>
 * <li>Identifying the logged-in user and the accounts they follow using
 * FollowService.</li>
 * <li>Filtering out chirps from users that the logged-in user does not
 * follow.</li>
 * <li>Rendering the filtered chirps using the provided template renderer.</li>
 * </ul>
 * 
 * <p>
 * Dependencies:
 * <ul>
 * <li>TemplateRenderer: Used to render the HTML template for the page.</li>
 * <li>ChirpService: Provides access to chirps data.</li>
 * <li>FollowService: Provides information about user follow relationships.</li>
 * <li>UserService: Provides user-related utilities.</li>
 * </ul>
 * 
 * <p>
 * Exceptions are logged and printed to the console in case of errors during
 * request handling.
 * 
 * @see BasePageHandler
 * @see ChirpService
 * @see FollowService
 * @see UserService
 */
public class FollowTimelinePageHandler extends BasePageHandler {
    private final static String FOLLOW_TIMELINE_TEMPLATE = "secure/followtimeline.ftl";
    private final static String USERNAME_FIELD = "username";
    private final ChirpService chirpService;
    private final FollowService followService;

    public FollowTimelinePageHandler(TemplateRenderer tr, ChirpService cs, FollowService fs, UserService us) {
        super(tr, FOLLOW_TIMELINE_TEMPLATE, us);
        this.chirpService = cs;
        this.followService = fs;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange, Map<String, Object> dataModel) throws IOException {

        try {
            // Get all chirps that currently exist
            Vector<Chirp> chirps = chirpService.getAllChirps();

            // Setup for filtering posts
            String loggedInUser = CookieUtils.getCookies(exchange).get(USERNAME_FIELD);
            HashSet<String> userFollowing = new HashSet<String>(followService.getAccountsUserFollows(loggedInUser));

            // Filtering posts
            // Big thanks to Jonathan for pointing out the bug
            int chirpsSize = chirps.size();
            logger.info("Fetched " + Integer.valueOf(chirpsSize).toString() + " chirps.");

            for (int i = chirpsSize - 1; i >= 0; i--) {
                Chirp chirp = chirps.elementAt(i);
                String chirpAuthor = chirp.getOwnerUsername();

                // Remove the chirp
                if (!userFollowing.contains(chirpAuthor)) {
                    logger.info("Removing chirp by " + chirpAuthor + ".");
                    chirps.remove(i);
                } else {
                    logger.info("Keeping chirp by " + chirpAuthor + ".");
                }
            }

            // Finally, render template with chirps
            dataModel.put("chirps", chirps);
            renderTemplate(exchange, dataModel);
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }
}
