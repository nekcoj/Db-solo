package org.app;

import org.fsdb.Database;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String dbName = "example-db";

        var db = new Database();
        db.create(dbName);

        // create files
        List<String> jsonFiles = List.of("assets/artists.json", "assets/albums.json", "assets/songs.json");
        db.loadJsonFiles(jsonFiles);

        var input = new InputManager(db);
        input.showMenu();
    }


}
