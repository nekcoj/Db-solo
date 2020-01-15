package org.example;

import org.fsdb.FileSystem;
import org.fsdb.database.Database;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String dbName = "example-db";

        Database db = new Database();
        db.create(dbName);

        List<String> jsonFiles = List.of("assets/artists.json", "assets/albums.json", "assets/songs.json");
//        db.loadJsonFiles(jsonFiles);


        File[] fileArr = FileSystem.getDirFiles("example-db/albums");
        for (File f: fileArr) {
          //  System.out.println( FileSystem.readFile(new Album( db.deserializeData(f.toString()))));
        }
    }
}
