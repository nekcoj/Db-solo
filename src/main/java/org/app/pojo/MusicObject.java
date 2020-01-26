package org.app.pojo;

import java.util.HashMap;

public interface MusicObject extends Comparable<MusicObject> {
    int id = 0;

    int getId();
    String getResolvedName();
    String getNameColored();
    int getArtistId();

    HashMap<String, String> mapObject();

    @Override
    int compareTo(MusicObject o);
}
