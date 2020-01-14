package org.fsdb.database.query;

import java.util.HashMap;

public class QueryResult {
    public boolean success;
    public QueryAction action;
    public HashMap<String, String> data;

    public QueryResult(boolean wasSuccessful, QueryAction queryAction) {
        success = wasSuccessful;
        action = queryAction;
    }

    public QueryResult(boolean wasSuccessful, QueryAction queryAction, HashMap<String, String> queryData) {
        success = wasSuccessful;
        action = queryAction;
        data = queryData;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "successful=" + success +
                ", action=" + action +
                ", data='" + data + '\'' +
                '}';
    }
}
