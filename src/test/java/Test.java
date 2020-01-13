import database.TestDatabase;
import filesystem.TestFileSystem;

public class Test {
    public static void main(String[] args) {
        TestFileSystem.runTests();
        TestDatabase.runTests();
    }
}
