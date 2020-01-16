package org.app.pojo;

import java.util.HashMap;

public class Song implements MusicObject {
    private int id;
    private int album;
    private String title;
    private int track;
    private String genre;

    public Song(int id, int album, String title, int track, String genre) {
        this.id = id;
        this.album = album;
        this.title = title;
        this.track = track;
        this.genre = genre;
    }

    public Song(HashMap<String, String> queryResult) {
        this.id = Integer.parseInt(queryResult.get("id"));
        this.album = Integer.parseInt(queryResult.get("album"));
        this.title = queryResult.get("title");
        this.track = Integer.parseInt(queryResult.get("track"));
        this.genre = queryResult.get("genre");

    }

    public HashMap<String, String> mapObject() {
        var convertedSong = new HashMap<String, String>();
        convertedSong.put("id", String.valueOf(this.id));
        convertedSong.put("album", String.valueOf(this.album));
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

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
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
