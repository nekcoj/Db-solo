package org.app.pojo;

import org.app.Color;

public class Song implements MusicObject {
    private int id;
//    private int album;
    @ConvertFromId
    private Album album;
    private String title;
    private int track;
    private String genre;
    private int artistId;

    public Song(int id, Album album, String title, int track, String genre, int artistId) {
        this.id = id;
        this.album = album;
        this.title = title;
        this.track = track;
        this.genre = genre;
        this.artistId = artistId;
    }


    public int getId() {
        return id;
    }

    @Override
    public String getResolvedName() {
        return getTitle();
    }

    @Override
    public String getNameColored() {
      return  Color.setSongColor(title);
    }

    @Override
    public int compareTo(MusicObject o) {
        return getTitle().compareTo(o.getResolvedName());
    }

    public void setId(int id) {
        this.id = id;
    }

    @ConvertFromId
    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
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

    @Override
    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }
}
