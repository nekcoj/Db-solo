package org.example;

import org.fsdb.database.Database;

public class Main {
    public static void main(String[] args) {
        String dbName = "example-db";

        Database db = new Database();
        db.create(dbName);
    }
}
