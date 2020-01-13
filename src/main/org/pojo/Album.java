package main.org.pojo;

import java.util.ArrayList;

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
