package org.fsdb.database.query;

import java.util.HashMap;

public class QueryResult {
    public boolean successful;
    public QueryAction action;
    public HashMap<String, String> data;

    public QueryResult(boolean wasSuccessful, QueryAction queryAction) {
        successful = wasSuccessful;
        action = queryAction;
    }

    public QueryResult(boolean wasSuccessful, QueryAction queryAction, HashMap<String, String> queryData) {
        successful = wasSuccessful;
        action = queryAction;
        data = queryData;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "successful=" + successful +
                ", action=" + action +
                ", data='" + data + '\'' +
                '}';
    }
}
