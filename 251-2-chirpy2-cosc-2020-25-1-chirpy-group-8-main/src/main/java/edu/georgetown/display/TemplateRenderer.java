package edu.georgetown.display;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import edu.georgetown.logging.LoggerFactory;
import freemarker.core.ParseException;
import freemarker.template.*;

/**
 * The TemplateRenderer class is responsible for rendering templates using the
 * FreeMarker template engine. It provides functionality to initialize the
 * template engine with a specified template directory and parse templates
 * with a given data model.
 * 
 * <p>
 * Usage:
 * <ul>
 * <li>Initialize the TemplateRenderer with the default or custom template
 * path.</li>
 * <li>Use the {@code parseTemplate} method to render templates with a data
 * model.</li>
 * </ul>
 * 
 * <p>
 * Features:
 * <ul>
 * <li>Supports UTF-8 encoding for templates.</li>
 * <li>Handles various exceptions related to template processing.</li>
 * <li>Logs initialization and error details for debugging purposes.</li>
 * </ul>
 * 
 * <p>
 * Dependencies:
 * <ul>
 * <li>FreeMarker library for template processing.</li>
 * <li>SLF4J for logging.</li>
 * </ul>
 * 
 * <p>
 * Note: Templates should be stored in the "resources/templates" directory
 * or a custom directory specified during initialization.
 */
public class TemplateRenderer {

    private static final String DEFAULT_TEMPLATE_PATH = "resources/templates";
    private static final Logger logger = LoggerFactory.getLogger();
    private Configuration cfg;

    public TemplateRenderer() throws IOException {
        this(DEFAULT_TEMPLATE_PATH);
    }

    /**
     * Initializes the template renderer. Specifically, it sets up the template
     * engine.
     * You probably don't want to change much of this code, if any.
     * 
     * @param templatePath the path to the template directory to use
     * @throws IOException
     */
    public TemplateRenderer(String templatePath) throws IOException {

        /* Create and adjust the configuration singleton */
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDirectoryForTemplateLoading(new File(templatePath));

        // Recommended settings for new projects:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        logger.info("Disply logic initialized");
    }

    /**
     * Parses a template given the provided `dataModel`, and writes the output to
     * `out`. Templates should be stored in the resources/templates directory.
     * 
     * @param templateName the name of the template to use
     * @param dataModel    the variables to use in the template
     * @return the rendered template output
     */
    public String parseTemplate(String templateName, Map<String, Object> dataModel) {
        Template template;

        // sw will hold the output of parsing the template
        StringWriter sw = new StringWriter();

        try {
            template = cfg.getTemplate(templateName);

            // now we call the method to parse the template and write the output
            template.process(dataModel, sw);
            return sw.toString();
        } catch (TemplateNotFoundException e) {
            logger.warning(templateName + " not found");
        } catch (MalformedTemplateNameException e) {
            logger.warning("malformed template: " + templateName);
        } catch (ParseException e) {
            logger.warning(templateName + " parse exception: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("IO exception: " + e.getMessage());
        } catch (TemplateException e) {
            e.printStackTrace();
            logger.warning(templateName + " template exception: " + e.getMessage());
        }
        return ""; // Return empty string on failure
    }
}
