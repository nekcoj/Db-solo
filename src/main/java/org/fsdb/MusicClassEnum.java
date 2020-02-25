package org.fsdb;

public enum MusicClassEnum {
    ARTIST(0),
    ALBUM(1),
    SONG(2);

    private final int value;
    MusicClassEnum(int value){
        this.value = value;
    }


    /*private static final int ARTIST = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 2;*/
}
