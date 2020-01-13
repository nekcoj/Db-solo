package org.fsdb.database;

import org.fsdb.FileSystem;
import org.fsdb.Util;
import org.fsdb.database.query.Query;
import org.fsdb.database.query.QueryResult;

import java.util.HashMap;
import java.util.List;

public class Database {
    public boolean create(String name) {
        return FileSystem.createDir(name);
    }

    public QueryResult executeQuery(Query query) {
        QueryResult result;

        switch (query.action) {
            case FETCH:
            case DELETE:
            case CREATE: {
                result = new QueryResult(true, query.action);
                break;
            }
            case UPDATE: {
                result = new QueryResult(true, query.action, query.values);
                break;
            }
            default: {
                result = null;
                break;
            }
        }

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
