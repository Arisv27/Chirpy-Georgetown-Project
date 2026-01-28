/**
 * Chirpy -- a really basic social networking site
 * 
 * Micah Sherr <msherr@cs.georgetown.edu>
 */

package edu.georgetown;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import com.sun.net.httpserver.HttpServer;

import edu.georgetown.service.ChirpService;
import edu.georgetown.service.FollowService;
import edu.georgetown.dao.ChirpDAO;
import edu.georgetown.dao.ChirperDAO;
import edu.georgetown.dao.FollowDAO;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.handler.LogoutHandler;
import edu.georgetown.handler.authentication.DefaultPageHandler;
import edu.georgetown.handler.authentication.LoginPageHandler;
import edu.georgetown.handler.authentication.RegisterPageHandler;
import edu.georgetown.handler.secure.FollowTimelinePageHandler;
import edu.georgetown.handler.secure.PostChirpHandler;
import edu.georgetown.handler.secure.SearchPageHandler;
import edu.georgetown.handler.secure.TimelinePageHandler;
import edu.georgetown.logging.LoggerFactory;
import edu.georgetown.model.Chirp;
import edu.georgetown.model.Chirper;
import edu.georgetown.model.Follow;
import edu.georgetown.persistence.Serializer;
import edu.georgetown.persistence.Serializer.NonSerializableClassException;
import edu.georgetown.service.UserService;
import edu.georgetown.service.SearchService;

public class Chirpy {

  final static int PORT = 8080;

  private static final Logger logger = LoggerFactory.getLogger();
  private TemplateRenderer templateRenderer;

  public Chirpy() {

    try {
      FileHandler fileHandler = new FileHandler("/tmp/log.txt");
      logger.addHandler(fileHandler);
      fileHandler.setFormatter(new SimpleFormatter());
    } catch (IOException e) {
      e.printStackTrace();
    }
    ConsoleHandler consoleHandler = new ConsoleHandler();
    logger.addHandler(consoleHandler);
    logger.setUseParentHandlers(false); // Remove default handlers
    // Set desired log level (e.g., Level.INFO, Level.WARNING, etc.)
    logger.setLevel(Level.ALL);

    try {
      templateRenderer = new TemplateRenderer();
    } catch (IOException e) {
      logger.warning("failed to initialize display logic: " + e.getMessage());
      System.exit(1);
    }

    logger.info("Starting chirpy web service");

  }

  /**
   * Start the web service
   */

  private void startService(UserService userService, FollowService followService, ChirpService chirpService, SearchService searchService) {
    try {
      // initialize the web server
      HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

      // each of these "contexts" below indicates a URL path that will be handled by
      // the service. The top-level path is "/", and that should be listed last.
      server.createContext("/register/", new RegisterPageHandler(templateRenderer, userService));
      server.createContext("/login/", new LoginPageHandler(templateRenderer, userService));
      server.createContext("/timeline/", new TimelinePageHandler(templateRenderer, chirpService, userService, followService));
      server.createContext("/followtimeline/", new FollowTimelinePageHandler(templateRenderer, chirpService, followService, userService));
      server.createContext("/postchirp/", new PostChirpHandler(templateRenderer, chirpService, userService));
      server.createContext("/search/", new SearchPageHandler(templateRenderer, searchService, userService));
      server.createContext("/logout/", new LogoutHandler());
      server.createContext("/", new DefaultPageHandler(templateRenderer, userService));
      // you will need to add to the above list to add new functionality to the web
      // service. Just make sure that the handler for "/" is listed last.

      server.setExecutor(null); // Use the default executor

      // this next line effectively starts the web service and waits for requests. The
      // above "contexts" (created via `server.createContext`) will be used to handle
      // the requests.
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    logger.info("Server started on port " + PORT);
  }

  public static void main(String[] args) throws IOException, NonSerializableClassException {

    Chirpy ws = new Chirpy();

    // let's start up the various business logic services
    Serializer<Chirper> chirperSerializer = new Serializer<Chirper>(Chirper.class, "data/users");
    ChirperDAO chirperDao = new ChirperDAO(chirperSerializer);
    chirperDao.loadChirpers();
    UserService userService = new UserService(chirperDao);

    Serializer<Follow> followSerializer = new Serializer<Follow>(Follow.class, "data/follows");
    FollowDAO followDao = new FollowDAO(followSerializer);
    followDao.loadFollows();
    FollowService followService = new FollowService(followDao);

    Serializer<Chirp> chirpSerializer = new Serializer<Chirp>(Chirp.class, "data/posts");
    ChirpDAO chirpDAO = new ChirpDAO(chirpSerializer);
    chirpDAO.loadChirps();
    ChirpService chirpService = new ChirpService(chirpDAO);

    SearchService searchService = new SearchService(chirpService);
    
    // finally, let's begin the web service so that we can start handling requests
    ws.startService(userService, followService, chirpService, searchService);
  }

}
