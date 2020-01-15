package org.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Album {

     private int  id;
     private int  artistId;
     private String name;
     private int year;
     private ArrayList<Integer> songIds;

    public Album(int id, int artistId, String name, int year) {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
        this.year = year;
        this.songIds = new ArrayList<>();


    }

    public Album(HashMap<String,String> queryResult) {
        this.id = Integer.parseInt(queryResult.get("id")) ;
        this.artistId = Integer.parseInt(queryResult.get("artistId"));
        this.name = queryResult.get("name");
        this.year =  Integer.parseInt(queryResult.get("year"));
        this.songIds = parseToList(queryResult.get("&songIds"));
    }
    public HashMap mapObject(){
        HashMap<String, String> convertedAlbum = new HashMap<>();
        convertedAlbum.put("id", String.valueOf(this.id));
        convertedAlbum.put("artistId", String.valueOf(this.artistId));
        convertedAlbum.put("name", this.name);
        convertedAlbum.put("year", String.valueOf(this.year));
        convertedAlbum.put("&songIds",this.songIds.toString().replaceAll("\\s+",""));
        return convertedAlbum;
    }

    private static ArrayList<Integer> parseToList(String songIds){
        songIds = songIds.substring(1, songIds.length() -1);
        return (ArrayList<Integer>) Stream.of(songIds.split(","))
                .map (elem -> Integer.parseInt(new String(elem)) )
                .collect(Collectors.toList());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<Integer> getSongIds() {
        return songIds;
    }

    public void setSongIds(ArrayList<Integer> songIds) {
        this.songIds = songIds;
    }
}
