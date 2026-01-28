package edu.georgetown.handler.authentication;

import java.io.IOException;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;

import edu.georgetown.display.BasePageHandler;
import edu.georgetown.display.TemplateRenderer;
import edu.georgetown.http.CookieUtils;
import edu.georgetown.http.ResponseUtils;
import edu.georgetown.service.UserService;

public class RegisterPageHandler extends BasePageHandler {

    private final static String REGISTER_TEMPLATE = "authentication/register.ftl";
    private final static String USERNAME_FIELD = "username";
    private final static String PASSWORD_FIELD = "password";
    private final static String CONFIRM_PASSWORD_FIELD = "confirmPassword";

    public RegisterPageHandler(TemplateRenderer tr, UserService us) {
        super(tr, REGISTER_TEMPLATE, us);
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
     * Updates the data model with feedback information for a specific form field.
     * This method logs the feedback message, sets a helper message for the input
     * field,
     * and updates the field's `aria-invalid` attribute to indicate its validity.
     *
     * @param dataModel   The data model to update with feedback information.
     * @param fieldName   The name of the form field to associate with the feedback.
     * @param feedbackMsg The feedback message to display for the form field.
     * @param isInvalid   A boolean indicating whether the field is invalid (true)
     *                    or valid (false).
     */
    private void putFormFeedback(Map<String, Object> dataModel, String fieldName, String feedbackMsg,
            boolean isInvalid) {
        // Log the message
        logger.info("Registration " + (isInvalid ? "Error" : "Success") + ": " + feedbackMsg);
        // Set input field helper message
        dataModel.put(fieldName + "HelperMessage", feedbackMsg);
        // Finally, set the input field's aria-invalid attribute
        dataModel.put(fieldName + "AriaInvalid", String.valueOf(isInvalid));
    }

    /**
     * Validates the provided username and updates the data model with feedback.
     *
     * @param dataModel A map representing the data model to store feedback and
     *                  validated data.
     * @param username  The username to validate.
     * @return {@code true} if the username is valid; {@code false} otherwise.
     *
     *         <p>
     *         Validation criteria:
     *         <ul>
     *         <li>The username must not be blank. If blank, an error message is
     *         added to the data model.</li>
     *         <li>The username must not already exist in the system. If it exists,
     *         an error message is added to the data model.</li>
     *         <li>If valid, a success message is added to the data model, and the
     *         username is stored.</li>
     *         </ul>
     */
    private boolean validateUsername(Map<String, Object> dataModel, String username) {
        if (username.isBlank()) {
            putFormFeedback(dataModel, USERNAME_FIELD, "Username cannot be empty.", true);
            return false;
        }

        if (userService.usernameExists(username)) {
            putFormFeedback(dataModel, USERNAME_FIELD, "Username already exists.", true);
            return false;
        }

        putFormFeedback(dataModel, USERNAME_FIELD, "Looks good!", false);
        dataModel.put(USERNAME_FIELD, username);
        return true;
    }

    /**
     * Validates the provided password and updates the data model with feedback.
     *
     * @param dataModel A map representing the data model to store feedback and the
     *                  password.
     * @param password  The password string to validate.
     * @return {@code true} if the password is valid; {@code false} otherwise.
     *
     *         <p>
     *         Validation rules:
     *         <ul>
     *         <li>If the password is blank, an error message is added to the data
     *         model, and the method returns {@code false}.</li>
     *         <li>If the password is valid, a success message is added to the data
     *         model, and the password is stored in the data model.</li>
     *         </ul>
     *         </p>
     */
    private boolean validatePassword(Map<String, Object> dataModel, String password) {
        if (password.isBlank()) {
            putFormFeedback(dataModel, PASSWORD_FIELD, "Password cannot be empty.", true);
            return false;
        }

        putFormFeedback(dataModel, PASSWORD_FIELD, "Looks good!", false);
        dataModel.put(PASSWORD_FIELD, password);
        return true;
    }

    /**
     * Validates the confirmation password against the provided password.
     * 
     * @param dataModel       A map representing the data model to store feedback
     *                        messages.
     * @param password        The original password entered by the user.
     * @param confirmPassword The confirmation password entered by the user.
     * @return {@code true} if the confirmation password is valid and matches the
     *         original password,
     *         {@code false} otherwise.
     * 
     *         This method performs the following checks:
     *         <ul>
     *         <li>If the confirmation password is blank, it adds an error message
     *         to the data model
     *         and returns {@code false}.</li>
     *         <li>If the confirmation password does not match the original
     *         password, it adds an error
     *         message to the data model and returns {@code false}.</li>
     *         <li>If the confirmation password is valid, it adds a success message
     *         to the data model,
     *         stores the confirmation password in the data model, and returns
     *         {@code true}.</li>
     *         </ul>
     */
    private boolean validateConfirmPassword(Map<String, Object> dataModel, String password, String confirmPassword) {
        if (confirmPassword.isBlank()) {
            putFormFeedback(dataModel, CONFIRM_PASSWORD_FIELD, "Please confirm your password.", true);
            return false;
        }

        if (!confirmPassword.equals(password)) {
            putFormFeedback(dataModel, CONFIRM_PASSWORD_FIELD, "Passwords do not match.", true);
            return false;
        }

        putFormFeedback(dataModel, CONFIRM_PASSWORD_FIELD, "Looks good!", false);
        dataModel.put(CONFIRM_PASSWORD_FIELD, confirmPassword);
        return true;
    }

    /**
     * Handles HTTP GET requests for the register page.
     * If the user is already logged in, they are redirected to the timeline page.
     * Otherwise, the registration page template is rendered.
     *
     * @param exchange  The HttpExchange object containing the request and response.
     * @param dataModel A map containing data to be passed to the template for
     *                  rendering.
     * @throws IOException If an I/O error occurs during the handling of the
     *                     request.
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
     * Handles the HTTP POST request for the registration page.
     * </p>
     * 
     * <p>
     * This method extracts the username, password, and confirm password fields from
     * the raw form data. It validates the username, password, and checks if the
     * passwords match.
     * </p>
     * <ul>
     * <li>If all validations pass, the user is registered, a cookie is added, and
     * the user is redirected to the timeline page.</li>
     * <li>Otherwise, an error modal is displayed by rendering the appropriate
     * template.</li>
     * </ul>
     *
     * @param exchange    The HttpExchange object that contains the request and
     *                    response.
     * @param dataModel   A map representing the data model used for rendering
     *                    templates.
     * @param rawFormData A map containing the raw form data submitted by the user.
     * @throws IOException If an I/O error occurs during the handling of the
     *                     request.
     */
    @Override
    protected void handlePostRequest(HttpExchange exchange, Map<String, Object> dataModel,
            Map<String, String> rawFormData) throws IOException {

        // Extract values from rawFormData
        String username = extractField(rawFormData, USERNAME_FIELD);
        String password = extractField(rawFormData, PASSWORD_FIELD);
        String confirmPassword = extractField(rawFormData, CONFIRM_PASSWORD_FIELD);

        // Validate fields and update data model
        boolean validUsername = validateUsername(dataModel, username);
        boolean validatePassword = validatePassword(dataModel, password);
        boolean passwordsMatch = validateConfirmPassword(dataModel, password, confirmPassword);

        if (validUsername && validatePassword && passwordsMatch) {
            // Register user, create active session cookie, and redirect to timeline
            logger.info("Registration successful");
            userService.registerUser(username, password);
            CookieUtils.addCookie(exchange, USERNAME_FIELD, username);
            ResponseUtils.sendRedirect(exchange, "/timeline/");
        } else {
            // Render errors
            dataModel.put("showErrorModal", true);
            renderTemplate(exchange, dataModel);
        }

    }
}