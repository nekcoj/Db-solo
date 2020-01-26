package org.fsdb.query;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryResult {
    public final boolean success;
    public final QueryAction action;
    public HashMap<String, String> data;
    public ArrayList<HashMap<String, String>> dataArray;

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
