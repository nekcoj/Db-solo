package main.org.fsdb.database.query;

import java.util.HashMap;

public class QueryResult {
    public boolean successful;
    public QueryAction action;
    public HashMap<String, String> data;

    @Override
    public String toString() {
        return "QueryResult{" +
                "successful=" + successful +
                ", action=" + action +
                ", data='" + data + '\'' +
                '}';
    }
}
