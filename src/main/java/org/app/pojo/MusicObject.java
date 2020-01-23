package org.app.pojo;

public interface MusicObject extends Comparable<MusicObject> {
    int id = 0;
    int getId();
    String getResolvedName();
    String getNameColored();
    int getArtistId();

    @Override
    int compareTo(MusicObject o);
}
