package main.org.example;

import main.org.fsdb.database.Database;

public class Main {
    public static void main(String[] args) {
        String dbName = System.getenv("EXAMPLE_DIR");

        Database db = new Database();
        db.create(dbName);
    }
}
