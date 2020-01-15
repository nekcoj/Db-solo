package org.example;

import org.fsdb.FileSystem;
import org.fsdb.database.Database;
import org.fsdb.database.query.Query;
import org.pojo.Album;
import org.pojo.Artist;
import org.pojo.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String dbName = "example-db";
        Database db = new Database();
        db.create(dbName);

        List<String> jsonFiles = List.of("assets/artists.json", "assets/albums.json", "assets/songs.json");
//        db.loadJsonFiles(jsonFiles);

    getDataList(dbName,"albums","acE",db).forEach(s -> System.out.println(s));

    }

    public static ArrayList<String> getDataList(String database, String subPath, String search, Database db ){
        String path = database + "/" + subPath;
        File[] fileArr = FileSystem.getDirFiles(path);
        ArrayList<String> searchResult = new ArrayList<>();
        for (File f: fileArr) {
            String url = f.toString();
            String data = FileSystem.readFile(url);
            HashMap<String,String> dataMap = db.deserializeData(data);
            String result = getNameOfData(subPath,dataMap);
           if(result.toLowerCase().contains(search.toLowerCase())) {
               searchResult.add(result);
           }
        }
        searchResult.add("Found " + searchResult.size() +  " " + subPath.substring(0,subPath.length() - 1) +" results" );
        return searchResult;
    }

    private static String getNameOfData(String subPath, HashMap<String, String> dataMap) {
        if(subPath.equals("artists")){
          return   new Artist(dataMap).getName();
        }else if(subPath.equals("albums")){
         return    new Album(dataMap).getName();
        }else {
          return   new Song(dataMap).getTitle();
        }

    }

}
