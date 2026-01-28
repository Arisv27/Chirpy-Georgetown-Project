package edu.georgetown.handler.authentication;

import java.io.IOException;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;

import edu.georgetown.display.BasePageHandler;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.http.CookieUtils;
import edu.georgetown.http.ResponseUtils;
import edu.georgetown.service.UserService;

/**
 * Handles the login page functionality, including rendering the login page,
 * validating user input, and processing login requests.
 * 
 * <p>
 * This class extends {@link BasePageHandler} and provides specific
 * implementations for handling GET and POST requests related to user
 * authentication. It uses a {@link TemplateRenderer} to render the login page
 * and a {@link UserService} to validate user credentials.
 * 
 * <p>
 * Key features include:
 * <ul>
 * <li>Validation of username and password fields.</li>
 * <li>Providing user feedback for invalid input via helper messages and ARIA
 * attributes.</li>
 * <li>Redirecting authenticated users to the timeline page.</li>
 * <li>Handling invalid login attempts with appropriate error messages.</li>
 * </ul>
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * LoginPageHandler loginHandler = new LoginPageHandler(templateRenderer, userService);
 * </pre>
 * 
 * @see BasePageHandler
 * @see TemplateRenderer
 * @see UserService
 */
public class LoginPageHandler extends BasePageHandler {

    private final static String LOGIN_PAGE = "authentication/login.ftl";
    private final static String USERNAME_FIELD = "username";
    private final static String PASSWORD_FIELD = "password";

    public LoginPageHandler(TemplateRenderer tr, UserService us) {
        super(tr, LOGIN_PAGE, us);
    }

    /**
     * Extracts the value of a specified field from the provided form data map.
     * If the field is not present in the map, an empty string is returned.
     * The returned value is trimmed of leading and trailing whitespace.
     *
     * @param rawFormData A map containing form data where keys are field names and
     *                    values are field values.
     * @param field       The name of the field to extract from the form data.
     * @return The trimmed value of the specified field, or an empty string if the
     *         field is not present.
     */
    private String extractField(Map<String, String> rawFormData, String field) {
        return rawFormData.getOrDefault(field, "").trim();
    }

    /**
     * Adds an error message to the data model for a specific input field, logs the
     * error,
     * and marks the input field as invalid for accessibility purposes.
     *
     * @param dataModel The data model map where error messages and attributes are
     *                  stored.
     * @param fieldName The name of the input field associated with the error.
     * @param errorMsg  The error message to be displayed for the input field.
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
     * Updates the data model to indicate that a specific input field is valid and
     * retains its value.
     *
     * @param dataModel  The map representing the data model to be updated.
     * @param fieldName  The name of the input field to mark as valid.
     * @param inputValue The value of the input field to retain in the data model.
     */
    private void putSuccess(Map<String, Object> dataModel, String fieldName, String inputValue) {
        // Set the input field to display valid
        dataModel.put(fieldName + "AriaInvalid", "false");
        // Finally, resend the input so the user doesn't have to redo valid fields
        dataModel.put(fieldName, inputValue);
    }

    /**
     * Validates the provided username and updates the data model with the result.
     *
     * @param dataModel A map representing the data model to store validation
     *                  results.
     * @param username  The username to validate.
     * @return {@code true} if the username is valid; {@code false} otherwise.
     *         A username is considered valid if it is not blank.
     *         If invalid, an error message is added to the data model.
     *         If valid, the username is added to the data model as a success.
     */
    private boolean validateUsername(Map<String, Object> dataModel, String username) {
        if (username.isBlank()) {
            putError(dataModel, "username", "Username cannot be empty.");
            return false;
        }

        putSuccess(dataModel, USERNAME_FIELD, username);
        return true;
    }

    /**
     * Validates the provided password and updates the data model with the result.
     *
     * @param dataModel A map representing the data model to store validation
     *                  results.
     * @param password  The password string to validate.
     * @return {@code true} if the password is valid; {@code false} otherwise.
     *         If the password is invalid, an error message is added to the data
     *         model.
     *         If the password is valid, the password is added to the data model as
     *         a success.
     */
    private boolean validatePassword(Map<String, Object> dataModel, String password) {
        if (password.isBlank()) {
            putError(dataModel, PASSWORD_FIELD, "Password cannot be empty.");
            return false;
        }

        putSuccess(dataModel, PASSWORD_FIELD, password);
        return true;
    }

    /**
     * Validates the provided username and password against the user service.
     * If the credentials are valid, the method returns true. Otherwise, it logs
     * an error message, updates the data model with accessibility attributes
     * for invalid input fields, and returns false.
     *
     * @param dataModel A map representing the data model to update with error
     *                  attributes.
     * @param username  The username to validate.
     * @param password  The password to validate.
     * @return true if the username and password are valid; false otherwise.
     */
    private boolean validateAccount(Map<String, Object> dataModel, String username, String password) {
        if (userService.isValidUser(username, password)) {
            return true;
        }

        logger.info("Login Error: Credentials do not match.");
        dataModel.put("usernameAriaInvalid", "true");
        dataModel.put("passwordAriaInvalid", "true");
        return false;
    }

    /**
     * Handles HTTP GET requests for the login page.
     *
     * @param exchange  The HttpExchange object that contains the request and
     *                  response details.
     * @param dataModel A map containing data to be passed to the template for
     *                  rendering.
     * @throws IOException If an I/O error occurs during the handling of the
     *                     request.
     *
     *                     <p>
     *                     This method checks if the user is already logged in. If
     *                     the user is logged in,
     *                     they are redirected to the timeline page. Otherwise, the
     *                     login page template
     *                     is rendered using the provided data model.
     *                     </p>
     */
    protected void handleGetRequest(HttpExchange exchange, Map<String, Object> dataModel) throws IOException {
        if (userService.isLoggedIn(exchange)) {
            ResponseUtils.sendRedirect(exchange, "/timeline/");
        } else {
            renderTemplate(exchange, dataModel);
        }
    }

    /**
     * <p>
     * Handles POST requests for the login page. This method processes the form data
     * submitted by the user, validates the input, and performs authentication.
     * </p>
     * 
     * <p>
     * The method performs the following steps:
     * </p>
     * 
     * <ol>
     * <li>Extracts the username and password from the raw form data.</li>
     * <li>Validates that both the username and password fields are provided.
     * <ul>
     * <li>If either field is missing, an error message is added to the data model,
     * and the login page is re-rendered.</li>
     * </ul>
     * </li>
     * <li>Checks if the provided credentials match an existing account.
     * <ul>
     * <li>If the login is successful, a cookie is added for the username, and the
     * user is redirected to the timeline page.</li>
     * <li>If the login fails, an error message is added to the data model, and the
     * login page is re-rendered.</li>
     * </ul>
     * </li>
     * </ol>
     * 
     * @param exchange    The HttpExchange object that contains the request and
     *                    response
     *                    details.
     * @param dataModel   A map representing the data model used for rendering
     *                    templates.
     * @param rawFormData A map containing the raw form data submitted by the user.
     * 
     * @throws IOException If an I/O error occurs during the handling of the
     *                     request.
     */
    @Override
    protected void handlePostRequest(HttpExchange exchange, Map<String, Object> dataModel,
            Map<String, String> rawFormData) throws IOException {

        // Extract values from rawFormData
        String username = extractField(rawFormData, USERNAME_FIELD);
        String password = extractField(rawFormData, PASSWORD_FIELD);

        // validate passwords and update data model
        boolean isUsernameEntered = validateUsername(dataModel, username);
        boolean isPasswordEntered = validatePassword(dataModel, password);

        // Stop processing if both username and password are not provided
        if (!isUsernameEntered || !isPasswordEntered) {
            dataModel.put("errorModalMessage", "One or more fields were left blank.");
            renderTemplate(exchange, dataModel);
            return;
        }

        // Check if login matches existing account and update data model
        boolean isLoginSuccessful = validateAccount(dataModel, username, password);

        if (isLoginSuccessful) {
            // Create active session and redirect user
            logger.info("Login successful");
            CookieUtils.addCookie(exchange, USERNAME_FIELD, username);
            ResponseUtils.sendRedirect(exchange, "/timeline/");
        } else {
            // Render errors
            dataModel.put("errorModalMessage",
                    "The account credentials you provided does not match one in our systems.");
            renderTemplate(exchange, dataModel);
        }
    }
}