package test.java.edu.georgetown.persistence;

import java.io.IOException;
import java.io.Serializable;
import java.io.File;
import java.util.Vector;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import edu.georgetown.persistence.Serializer;

class SerializerTest {

    private static final String TEST_DIRECTORY = "testserializer";
    private static final Path TEST_DIRECTORY_PATH = Path.of(System.getProperty("user.dir"), TEST_DIRECTORY);
    private Serializer<TestObject> serializer;

    @BeforeEach
    void setUp() throws Serializer.NonSerializableClassException, IOException {
        serializer = new Serializer<>(TestObject.class, TEST_DIRECTORY);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up test directory
        if (Files.exists(TEST_DIRECTORY_PATH)) {
            for (File file : TEST_DIRECTORY_PATH.toFile().listFiles()) {
                file.delete();
            }
            Files.delete(TEST_DIRECTORY_PATH);
        }
    }

    @Test
    void testCreateState() throws Exception {
        TestObject obj = new TestObject("Test", 123);
        serializer.createState(obj, "testFile");

        Path filePath = Path.of(System.getProperty("user.dir"), TEST_DIRECTORY, "testFile.ser");
        assertTrue(Files.exists(filePath), "File should be created");
    }

    @Test
    void testCreateState_FileAlreadyExists() throws Exception {
        TestObject obj = new TestObject("Test", 123);
        serializer.createState(obj, "testFile");

        assertThrows(FileAlreadyExistsException.class, () -> {
            serializer.createState(obj, "testFile");
        });
    }

    @Test
    void testReadFile() throws Exception {
        TestObject obj = new TestObject("Test", 123);
        serializer.createState(obj, "testFile");

        TestObject deserializedObj = serializer.readFile("testFile");
        assertEquals(obj, deserializedObj, "Deserialized object should match the original");
    }

    @Test
    void testReadFile_FileNotFound() {
        assertThrows(NoSuchFileException.class, () -> {
            serializer.readFile("nonExistentFile");
        });
    }

    @Test
    void testUpdateState() throws Exception {
        TestObject obj = new TestObject("Test", 123);
        serializer.createState(obj, "testFile");

        TestObject updatedObj = new TestObject("Updated", 456);
        serializer.updateState(updatedObj, "testFile");

        TestObject deserializedObj = serializer.readFile("testFile");
        assertEquals(updatedObj, deserializedObj, "Updated object should match the deserialized object");
    }

    @Test
    void testUpdateState_FileNotFound() {
        TestObject obj = new TestObject("Test", 123);
        assertThrows(NoSuchFileException.class, () -> {
            serializer.updateState(obj, "nonExistentFile");
        });
    }

    @Test
    void testDeleteState() throws Exception {
        TestObject obj = new TestObject("Test", 123);
        serializer.createState(obj, "testFile");

        serializer.deleteState("testFile");

        Path filePath = Path.of(System.getProperty("user.dir"), TEST_DIRECTORY, "testFile.ser");
        assertFalse(Files.exists(filePath), "File should be deleted");
    }

    @Test
    void testDeleteState_FileNotFound() {
        assertThrows(NoSuchFileException.class, () -> {
            serializer.deleteState("nonExistentFile");
        });
    }

    @Test
    void testLoadDirectory() throws Exception {
        TestObject obj1 = new TestObject("Test1", 123);
        TestObject obj2 = new TestObject("Test2", 456);

        serializer.createState(obj1, "file1");
        serializer.createState(obj2, "file2");

        Vector<TestObject> objects = serializer.loadDirectory();
        assertEquals(2, objects.size(), "Directory should contain 2 objects");
        assertTrue(objects.contains(obj1), "Directory should contain obj1");
        assertTrue(objects.contains(obj2), "Directory should contain obj2");
    }

    // Helper class for testing
    private static class TestObject implements Serializable {
        private String name;
        private int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            TestObject that = (TestObject) obj;
            return value == that.value && name.equals(that.name);
        }
    }
}
