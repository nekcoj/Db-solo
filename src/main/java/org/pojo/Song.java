package org.pojo;

import java.util.HashMap;

public class Song {
     private int id;
     private int albumId;
     private String title;
     private int track;
     private String genre;


    public Song(int id, int albumId, String title, int track, String genre) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.track = track;
        this.genre = genre;
    }

    public Song(HashMap<String,String> queryResult) {
        this.id = Integer.parseInt(queryResult.get("id"));
        this.albumId = Integer.parseInt(queryResult.get("albumId"));
        this.title = queryResult.get("title");
        this.track = Integer.parseInt(queryResult.get("track"));
        this.genre = queryResult.get("genre");
    }
    public HashMap mapObject(){
        HashMap<String, String> convertedSong = new HashMap<>();
        convertedSong.put("id", String.valueOf(this.id));
        convertedSong.put("albumId", String.valueOf(this.albumId));
        convertedSong.put("title", this.title);
        convertedSong.put("track", String.valueOf(this.track));
        convertedSong.put("genre", this.genre);
        return convertedSong;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
