package test;

import main.org.fsdb.Database;
import main.org.fsdb.Query;
import main.org.fsdb.QueryResult;

public class TestDatabase {
    public static void runTests() {
        Database db = new Database();

        testQuery(db);
    }

    private static void testQuery(Database db) {
        QueryResult result = db.executeQuery(new Query()
                .from("songs").where("id", "0").update("name", "new value"));

        System.out.println(result);
    }
}
