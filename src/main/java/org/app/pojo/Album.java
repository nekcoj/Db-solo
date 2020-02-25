package org.app.pojo;

import org.app.Color;

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

    @ConvertFromId
    public int getId() {
        return id;
    }

    @Override
    public String getResolvedName() {
        return getName();
    }

    @Override
    public String getNameColored() {
        return  Color.setAlbumColor(name);
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
