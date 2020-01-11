package main.org.fsdb;

public class QueryResult {
    public boolean successful;
    public QueryAction action;
    public String value;

    @Override
    public String toString() {
        return "QueryResult{" +
                "successful=" + successful +
                ", action=" + action +
                ", value='" + value + '\'' +
                '}';
    }
}
