import org.fsdb.FileSystem;
import org.fsdb.database.Database;
import org.fsdb.database.query.Query;
import org.fsdb.database.query.QueryAction;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class TestDatabase {
    static final String dbName = System.getenv("TEST_DB");
    static final String subDir = "sub_dir";
    final Database db = new Database();

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
        var result = db.executeQuery(new Query().from("test").where("id", "0").fetch());
        assertNotNull(result);
        assertEquals(result.action, QueryAction.FETCH);
    }

    @Test
    public void createDatabaseAndSubDir() {
        assumeNotNull(db);
        assertTrue(db.create(dbName));
        assertTrue(FileSystem.createDir(dbName + "/" + subDir, true));
    }

    @AfterClass
    public static void clean() {
        boolean deleted = new File(dbName + "/" + subDir).delete() && new File(dbName).delete();
        assertTrue(deleted);
    }
}
