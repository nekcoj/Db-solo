package test.filesystem;
import main.org.fsdb.FileSystem;
import java.io.File;

public class TestFileSystem {
    public static void runTests() {
        String filePath = "src/test/filesystem/data.txt";

        //Test Exist before file is created
        testExists(filePath);

        // Test FileSystem read and write methods
        if (testReadWrite(filePath)) System.out.println("[Test][FileSystem] Read/Write to file passed ✓");
        else System.out.println("[Test][FileSystem] Read/Write to file failed ❌");

        //Test Exist after file is created
        testExists(filePath);

        new File(filePath).deleteOnExit();


    }

    private static boolean testReadWrite(String filePath) {
        String outputText = "Hello World!\ntesting 123\n9832";

        FileSystem.writeFile(filePath, outputText);
        String inputText = FileSystem.readFile(filePath);

        return inputText.equals(outputText);
    }

    private static void testExists(String filePath) {
        if (FileSystem.exists(filePath)) System.out.println("File Exists");
        else System.out.println("File Doesn't Exists");

    }

}
