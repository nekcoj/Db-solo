package fsdb;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.fsdb.FileSystem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFileSystem {
    String dbName = System.getenv("TEST_DB");
    String testFile = "test.txt";

    String outputText = "testing\n12345\ncontent";

    @Test
    public void readWrite() {
        FileSystem.writeFile(testFile, outputText);
        String content = FileSystem.readFile(testFile);

        assertNotNull(content);
        assertEquals(content, outputText);
    }

    @Test
    public void existsAndDelete() {
        assumeTrue(Files.exists(Paths.get(testFile)));
        assertTrue("File exists", FileSystem.exists(testFile));
        assertTrue("File deletion", FileSystem.delete(testFile) && !FileSystem.exists(testFile));
    }

    @Test
    public void getDirFiles() {
        File[] files = FileSystem.getDirFiles("./");
        assertNotNull(files);
        assertTrue("Should not be empty", files.length != 0);
    }
}
