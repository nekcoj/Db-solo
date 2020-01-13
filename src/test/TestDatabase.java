package test;

import main.org.fsdb.Database;
import main.org.fsdb.FileSystem;
import main.org.fsdb.Query;
import main.org.fsdb.QueryResult;

public class TestDatabase {
    public static void runTests() {
        System.out.println("----- [ DATABASE TESTING START ] -----");
        Database db = new Database();

        // Test parsing data
        String testData = "id=999\nalbumId=432\nname=Soldiers of the Wastelands";
        var values = db.parseData(testData);
        System.out.println(values);

        testQuery(db);
        System.out.println("----- [ DATABASE TESTING END ] -----");
    }

    private static void testQuery(Database db) {
        QueryResult result = db.executeQuery(new Query()
                .from("songs").where("id", "0").update("name", "new value"));

        System.out.println(result);
    }
}
