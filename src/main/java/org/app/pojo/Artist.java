package org.app.pojo;

import org.app.Color;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Artist implements MusicObject {
    private int id;
    private String name;
    private ArrayList<Integer> refSongIds;

    public Artist(int id, String name, ArrayList<Integer> refSongIds) {
        this.id = id;
        this.name = name;
        this.refSongIds = refSongIds;
    }

    public Artist(int id, String name, String refSongIds) {
        this.id = id;
        this.name = name;
        this.refSongIds = parseToList(refSongIds);
    }

    private static ArrayList<Integer> parseToList(String songIds) {
        songIds = songIds.substring(1, songIds.length() - 1);
        return songIds.length() > 0 ? (ArrayList<Integer>) Stream.of(songIds.split(","))
                .map(var -> Integer.valueOf(var.replaceAll("\\s","")))
                .collect(Collectors.toList()) : new ArrayList<>();
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
        return  Color.setArtistColor(name);
    }

    @Override
    public int getArtistId() {
        return 0;
    }

    @Override
    public int compareTo(MusicObject o) {
        return getName().compareTo(o.getResolvedName());
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getRefSongIds() {
        return refSongIds;
    }

    public void setRefSongIds(ArrayList<Integer> refSongIds) {
        this.refSongIds = refSongIds;
    }
}
