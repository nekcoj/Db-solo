package main.org.fsdb;

import java.util.HashMap;

public class Query {
    public String rootName;
    public String predicateField;
    public String predicateValue;
    public QueryAction action;

    public HashMap<String, String> values;

    private void initMap() {
        values = new HashMap<>();
    }

    public Query from(String name) {
        rootName = name;
        return this;
    }

    public Query where(String field, String value) {
        predicateField = field;
        predicateValue = value;
        return this;
    }

    public Query create() {
        action = QueryAction.CREATE;
        return this;
    }

    public Query fetch() {
        action = QueryAction.FETCH;
        return this;
    }

    public Query delete() {
        action = QueryAction.DELETE;
        return this;
    }

    public Query update(String field, String value) {
        if (values == null) initMap();
        action = QueryAction.UPDATE;
        values.put(field, value);
        return this;
    }

    public Query update(HashMap<String, String> newValues) {
        if (values == null) initMap();
        action = QueryAction.UPDATE;
        values.putAll(newValues);
        return this;
    }

    @Override
    public String toString() {
        return "Query{" +
                "rootName='" + rootName + '\'' +
                ", predicateField='" + predicateField + '\'' +
                ", predicateValue='" + predicateValue + '\'' +
                ", action=" + action +
                ", values=" + values +
                '}';
    }
}
