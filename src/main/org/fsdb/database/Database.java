package main.org.fsdb.database;

import main.org.fsdb.FileSystem;
import main.org.fsdb.Util;
import main.org.fsdb.database.query.Query;
import main.org.fsdb.database.query.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public boolean create(String name) {
        return FileSystem.createDir(name);
    }



    public QueryResult executeQuery(Query query) {
        QueryResult result = new QueryResult();

        result.successful = true;
        result.action = query.action;

        // doesn't actually do anything at the moment (it just returns static data)
        result.data = new HashMap<>(Map.of("id", "42", "albumId", "86", "name", "Live forever"));

        return result;
    }

    public HashMap<String, String> parseData(String fileData) {
        List<String> lines = Util.stringToLines(fileData);
        if (lines == null) return null;

        HashMap<String, String> values = new HashMap<>();

        for (String line : lines) {
            String[] split = line.split("=");
            values.put(split[0], split[1]);
        }

        return values;
    }
}
