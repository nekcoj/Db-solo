package org.app.pojo;

import org.app.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Album implements MusicObject {
    private int id;
    private int artist;
    private String name;
    private int year;

    public Album(int id, int artist, String name, int year) {
        this.id = id;
        this.artist = artist;
        this.name = name;
        this.year = year;
    }

    public Album(HashMap<String, String> queryResult) {
        this.id = Integer.parseInt(queryResult.get("id"));
        this.artist = Integer.parseInt(queryResult.get("artist"));
        this.name = queryResult.get("name");
        this.year = Integer.parseInt(queryResult.get("year"));
    }

    public HashMap<String, String> mapObject() {
        var convertedAlbum = new HashMap<String, String>();
        convertedAlbum.put("id", String.valueOf(this.id));
        convertedAlbum.put("artist", String.valueOf(this.artist));
        convertedAlbum.put("name", this.name);
        convertedAlbum.put("year", String.valueOf(this.year));
        return convertedAlbum;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getResolvedName() {
        return getName();
    }

    @Override
    public String getNameColored() {
        return  Color.printAlbumColor(name);
    }

    @Override
    public int getArtistId() {
        return artist;
    }

    @Override
    public int compareTo(MusicObject o) {
        return getName().compareTo(o.getResolvedName());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArtist() {
        return artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
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

}
