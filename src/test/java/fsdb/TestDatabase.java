package fsdb;

import org.fsdb.Database;
import org.fsdb.query.Query;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestDatabase {
    private static final String dbName = System.getenv("TEST_DB");
    private static final String subDir = "test";
    private static final Database db = new Database();

    @BeforeClass
    public static void setup() {
        assertTrue(db.create(dbName));
    }

    @Test
    public void parseData() {
        String testData = "field1=123\nfield2=test\nfield3=[3,2,1]";
        var values = db.deserializeData(testData);

        assertEquals(values.get("field1"), "123");
        assertEquals(values.get("field2"), "test");
        assertEquals(values.get("field3"), "[3,2,1]");
    }

    @Test
    public void query() {
        var values = new HashMap<>(Map.of("id", "0", "name", "test"));

        var createResult = db.executeQuery(new Query().from(subDir).create(values));
        assertTrue(createResult.success);

        var fetchResult = db.executeQuery(new Query().from(subDir).where("id", "0").fetch());
        assertTrue(fetchResult.success);
        assertEquals("test", fetchResult.data.get("name"));

        var deleteResult = db.executeQuery(new Query().from(subDir).where("id", "0").delete());
        assertTrue(deleteResult.success);
    }

    @AfterClass
    public static void clean() {
        boolean deleted = new File(dbName + "/" + subDir).delete() && new File(dbName).delete();
        assertTrue(deleted);
    }
}
