package test.filesystem;

import main.org.fsdb.FileSystem;

import java.io.File;
import java.io.IOException;

public class TestFileSystem {
    public static void runTests() {
        String filePath = "src/test/filesystem/data.txt";

        // Test FileSystem read and write methods
        if (testReadWrite(filePath)) System.out.println("[Test][FileSystem] Read/Write to file passed ✓");
        else System.out.println("[Test][FileSystem] Read/Write to file failed ❌");


        //Test Exist before deletion of test file
        if (FileSystem.exists(filePath)) System.out.println("[Test][FileSystem] Exists is true, passed ✓");
        else System.out.println("[Test][FileSystem] Exists is false, failed ❌");


        //Test to delete of test file.
        if (FileSystem.delete(filePath)) System.out.println("[Test][FileSystem] File deleted, passed ✓");
        else System.out.println("[Test][FileSystem] File not deleted, failed ❌");


        //Test Exist after file is deleted
        if (!FileSystem.exists(filePath)) System.out.println("[Test][FileSystem] Exists is false, passed ✓");
        else System.out.println("[Test][FileSystem] Exists is true, failed ❌");


    }

    private static boolean testReadWrite(String filePath) {
        String outputText = "Hello World!\ntesting 123\n9832";

        FileSystem.writeFile(filePath, outputText);
        String inputText = FileSystem.readFile(filePath);

        return inputText.equals(outputText);
    }


}
