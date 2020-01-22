package org.app.pojo;

import java.util.ArrayList;
import java.util.HashMap;
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

    public HashMap<String, String> mapObject() {
        var convertedArtist = new HashMap<String, String>();
        convertedArtist.put("id", String.valueOf(this.id));
        convertedArtist.put("name", this.name);
        convertedArtist.put("refSongIds", this.refSongIds.toString().replaceAll("\\s+", ""));
        return convertedArtist;
    }

    public Artist(HashMap<String, String> queryResult) {
        this.id = Integer.parseInt(queryResult.get("id"));
        this.name = queryResult.get("name");
        this.refSongIds = parseToList(queryResult.get("refSongIds"));
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
