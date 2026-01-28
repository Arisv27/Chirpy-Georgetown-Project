package edu.georgetown.persistence;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.georgetown.logging.LoggerFactory;

/**
 * The {@code Serializer} class provides a generic mechanism for serializing and
 * deserializing objects of a specified type {@code T} that implements the
 * {@link Serializable} interface. It supports basic CRUD operations for
 * managing
 * serialized objects stored in a specified directory.
 * 
 * <p>
 * The class ensures type safety during deserialization and provides detailed
 * logging for all operations. It also handles directory creation and file
 * management, making it a convenient utility for persistent storage of objects.
 * </p>
 * 
 * <p>
 * Key features include:
 * </p>
 * <ul>
 * <li>Serialization and storage of objects to a specified directory</li>
 * <li>Deserialization of objects from files</li>
 * <li>Updating existing serialized objects</li>
 * <li>Deleting serialized object files</li>
 * <li>Loading all serialized objects from the directory</li>
 * </ul>
 * 
 * <p>
 * The class also defines custom exceptions to handle specific error scenarios:
 * </p>
 * <ul>
 * <li>{@link NonSerializableClassException}: Thrown when the specified class
 * does not implement {@link Serializable}</li>
 * <li>{@link ObjectTypeMismatchException}: Thrown when a deserialized object
 * does not match the expected type</li>
 * </ul>
 * 
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * {@code
 * // Instantiating Serializer and a Serializable class
 * Serializer<MySerializableClass> serializer = new Serializer<>(MySerializableClass.class, "myDirectory");
 * MySerializableClass obj = new MySerializableClass();
 * // CRUD functionality
 * serializer.createState(obj, "myFile");
 * MySerializableClass deserializedObj = serializer.readFile("myFile");
 * serializer.updateState(updatedObj, "myFile");
 * serializer.deleteState("myFile");
 * // Loading all serializables from the Serializer's stored directory
 * Vector<MySerializableClass> allObjects = serializer.loadDirectory();
 * }
 * </pre>
 * 
 * @param <T> The type of objects to be serialized and deserialized. Must
 *            implement {@link Serializable}.
 */
public class Serializer<T extends Serializable> {

    private final Logger logger = LoggerFactory.getLogger();
    /** Path to the directory where serialized objects will be stored */
    private final Path directoryPath;
    private final Class<T> classType;
    private static final String FILE_EXTENSION = ".ser";

    /**
     * Constructs a Serializer object and initializes the target directory.
     * 
     * <p>
     * This constructor sets the directory path relative to the current working
     * directory. If the specified directory already exists, it logs a message and
     * continues without creating a new directory. Otherwise, it attempts to create
     * the directory along with any necessary parent directories. If directory
     * creation fails, an error is logged and the exception is propagated.
     * 
     * @param targetDirectory The name of the target directory to be created or
     *                        used.
     * @throws NonSerializableClassException
     * @throws IOException                   If an error occurs while creating the
     *                                       directory.
     */
    public Serializer(Class<T> classType, String targetDirectory) throws NonSerializableClassException, IOException {
        // Check if the class implements Serializable
        if (!Serializable.class.isAssignableFrom(classType))
            throw new NonSerializableClassException(
                    "Class " + classType.getName() + " does not implement Serializable.");

        // Set object properties
        this.classType = classType;
        this.directoryPath = Paths.get(System.getProperty("user.dir"), targetDirectory);

        // Create directory if it does not exist
        if (Files.notExists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                logger.info("Created directory: " + directoryPath.toAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create directory: " + directoryPath.toAbsolutePath(), e);
                throw e;
            }
        } else {
            logger.info("Directory already exists: " + directoryPath.toAbsolutePath());
        }
    }

    // =============== //
    // CRUD Operations //
    // =============== //

    /**
     * Creates a serialized state of the given object and saves it to a file with
     * the specified name.
     *
     * @param object   The object to be serialized and saved.
     * @param fileName The name of the file (without extension) where the serialized
     *                 object will be stored.
     * @throws FileAlreadyExistsException If a file with the specified name already
     *                                    exists.
     * @throws IOException                If an I/O error occurs during the creation
     *                                    or writing of the file.
     */
    public void createState(T object, String fileName) throws FileAlreadyExistsException, IOException {

        // Create path to file
        Path fileToCreate = directoryPath.resolve(fileName + FILE_EXTENSION);

        // Throw error if file already exists
        if (Files.exists(fileToCreate)) {
            throw new FileAlreadyExistsException("The file already exists: " + fileToCreate);
        }

        // Attempt to create file
        try (OutputStream outStr = Files.newOutputStream(fileToCreate);
                ObjectOutputStream objOutStr = new ObjectOutputStream(outStr)) {
            objOutStr.writeObject(object);
            logger.info("Successfully created File " + fileToCreate + " in storage.");
        } catch (IOException ex) {
            logger.warning("IOException while creating object at " + fileToCreate + ": " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Reads an object of type T from a file with the specified name.
     *
     * @param fileName the name of the file (without extension) to read the object
     *                 from.
     * @return the deserialized object of type T read from the file.
     * @throws NoSuchFileException         if the file does not exist.
     * @throws ObjectTypeMismatchException if the object type in the file does not
     *                                     match the expected type.
     * @throws IOException                 if an I/O error occurs while reading the
     *                                     file.
     * @throws ClassNotFoundException      if the class of the serialized object
     *                                     cannot be found.
     */
    public T readFile(String fileName)
            throws NoSuchFileException, ObjectTypeMismatchException, IOException, ClassNotFoundException {
        Path filePath = directoryPath.resolve(fileName + FILE_EXTENSION);
        return readFile(filePath);
    }

    /**
     * <p>
     * This method attempts to read a serialized object from the given file path.
     * </p>
     * 
     * <ul>
     * <li>If the file does not exist, a {@code NoSuchFileException} is thrown.</li>
     * <li>If the deserialized object is not of the expected type, an
     * {@code ObjectTypeMismatchException} is thrown.</li>
     * <li>The method uses a try-with-resources block to ensure that the input
     * streams are closed properly after use.</li>
     * <li>Logging is performed to provide information about the success or failure
     * of the deserialization process.</li>
     * </ul>
     * 
     * @param filePath The path to the file containing the serialized object.
     * @return The deserialized object of type {@code T}.
     * @throws NoSuchFileException         If the file does not exist at the
     *                                     specified path.
     * @throws ObjectTypeMismatchException If the deserialized object is not of the
     *                                     expected type {@code T}.
     * @throws IOException                 If an I/O error occurs while reading the
     *                                     file.
     * @throws ClassNotFoundException      If the class of the serialized object
     *                                     cannot be found.
     */
    public T readFile(Path filePath)
            throws NoSuchFileException, ObjectTypeMismatchException, IOException, ClassNotFoundException {

        // Throw error if the file does not exist
        // NOTE: could instead handle gracefully by returning null
        if (Files.notExists(filePath))
            throw new NoSuchFileException("File not found: " + filePath);

        // Attepmt to deserialize object from file
        try (InputStream inStr = Files.newInputStream(filePath);
                ObjectInputStream objOutStr = new ObjectInputStream(inStr)) {

            // Read the object from file and cast it safely
            Object obj = objOutStr.readObject();

            // Method for deserialization of object
            if (classType.isInstance(obj)) {
                // Return successfully deserialized object
                T newObject = classType.cast(obj); // Safe casting
                logger.info("Successfully deserialized object from " + filePath);
                return newObject;
            } else {
                // The class' type does not match
                logger.warning("Type mismatch during deserialization for file: " + filePath);
                throw new ObjectTypeMismatchException("Deserialization failed: Object is not of the expected type "
                        + classType.getName());
            }
        } catch (IOException ex) {
            logger.warning("IOException while deserializing object from " + filePath + ": " + ex.getMessage());
            throw ex;
        } catch (ClassNotFoundException ex) {
            logger.warning("ClassNotFoundException while deserializing object from " + filePath + ": "
                    + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Updates the state of the specified object by serializing it and writing it
     * to a file with the given file name in the designated directory.
     *
     * @param object   The object to be serialized and written to the file.
     * @param fileName The name of the file (without extension) where the object
     *                 will be stored.
     * @throws NoSuchFileException If the file does not exist in the directory.
     * @throws IOException         If an I/O error occurs during the update process.
     */
    public void updateState(T object, String fileName) throws NoSuchFileException, IOException {

        // Create path to file
        Path fileToUpdate = directoryPath.resolve(fileName + FILE_EXTENSION);

        // Throw error if file does not exist
        if (Files.notExists(fileToUpdate)) {
            throw new NoSuchFileException("The file does not exist: " + fileToUpdate);
        }

        // Attempt to overwrite file
        try (OutputStream outStr = Files.newOutputStream(fileToUpdate);
                ObjectOutputStream objOutStr = new ObjectOutputStream(outStr)) {
            objOutStr.writeObject(object);
            logger.info("Successfully updated File " + fileToUpdate + " in storage.");
        } catch (IOException ex) {
            logger.warning("IOException while updating object at " + fileToUpdate + ": " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Deletes a serialized state file with the specified name from the directory.
     *
     * @param fileName the name of the file (without the ".ser" extension) to be
     *                 deleted
     * @throws NoSuchFileException if the file does not exist in the directory
     * @throws IOException         if an I/O error occurs while attempting to delete
     *                             the file
     * @throws SecurityException   if the deletion is denied due to insufficient
     *                             permissions
     */
    public void deleteState(String fileName) throws NoSuchFileException, IOException {

        // Create path to file
        Path fileToDelete = directoryPath.resolve(fileName + FILE_EXTENSION);

        // Check if the file exists before attempting to delete
        if (Files.notExists(fileToDelete))
            throw new NoSuchFileException("File not found: " + fileToDelete);

        // Attempt to delete file
        try {
            boolean isFileDeleted = Files.deleteIfExists(fileToDelete);

            if (isFileDeleted) {
                logger.info("File " + fileName + " deleted successfully.");
            } else {
                logger.warning("File " + fileName + " does not exist or could not be deleted.");
            }
        } catch (SecurityException ex) {
            logger.warning(
                    "SecurityException: Insufficient permissions to delete file " + fileName + ": " + ex.getMessage());
            throw ex;
        } catch (IOException ex) {
            logger.warning("IOException while deleting object at " + fileToDelete + ": " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Loads and deserializes all objects from the specified directory.
     * 
     * <p>
     * This method iterates through all files in the directory specified by
     * {@code directoryPath}, attempts to deserialize each file into an object of
     * type {@code T}, and adds the successfully deserialized objects to a
     * {@link Vector}.
     * </p>
     * 
     * <p>
     * If a file cannot be deserialized due to a {@link ClassNotFoundException},
     * {@link IOException}, or {@link ObjectTypeMismatchException}, a warning is
     * logged, and the method continues processing the remaining files.
     * </p>
     * 
     * <p>
     * If the directory itself cannot be read due to an {@link IOException}, a
     * warning is logged, and an empty {@link Vector} is returned.
     * </p>
     * 
     * @return A {@link Vector} containing all successfully deserialized objects
     *         from the directory. If no objects are successfully deserialized or
     *         the directory cannot be read, an empty {@link Vector} is returned.
     */
    public Vector<T> loadDirectory() {

        // Create empty list of objects to return
        Vector<T> deserializedObjects = new Vector<T>();

        // Create directory stream
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            // Attempt to deserialize each file
            for (Path childPath : stream) {
                Path relativePath = directoryPath.relativize(childPath);

                try {
                    T newObject = readFile(childPath);
                    deserializedObjects.add(newObject);
                    logger.info("Successfully deserialized object from " + relativePath);
                } catch (ClassNotFoundException | IOException | ObjectTypeMismatchException ex) {
                    logger.warning("Failed to recover Object state from " + relativePath + ": " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            logger.warning("Failed to read directory " + directoryPath + ": " + ex.getMessage());
        }

        // Finally, return list of deserialized objects
        return deserializedObjects;
    }

    // ========== //
    // Exceptions //
    // ========== //

    public static class NonSerializableClassException extends Exception {
        public NonSerializableClassException(String message) {
            super(message);
        }
    }

    public static class ObjectTypeMismatchException extends Exception {
        public ObjectTypeMismatchException(String message) {
            super(message);
        }
    }
}