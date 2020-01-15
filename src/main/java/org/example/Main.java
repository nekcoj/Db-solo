package org.example;
import org.fsdb.database.Database;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String dbName = "example-db";
        Database db = new Database();
        db.create(dbName);
        new InputManager(db);

        List<String> jsonFiles = List.of("assets/artists.json", "assets/albums.json", "assets/songs.json");
//        db.loadJsonFiles(jsonFiles);



    }



}
