package org.fsdb;

import org.app.pojo.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Converter {

    public static MusicObject getObjectFromHashMap(String subPath, HashMap<String, String> objectMap) {
        MusicObject returnObject = null;
        try {
            switch (subPath) {
                case "artists":
                    returnObject = getArtistObject(objectMap);
                    break;
                case "albums":
                    returnObject = getAlbumObject(objectMap);
                    break;
                case "songs":
                    returnObject = getSongObject(objectMap);
                    break;
            }
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    private static Artist getArtistObject(HashMap<String, String> objectMap) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        int id = Integer.parseInt(objectMap.get("id"));
        String artistName = objectMap.get("name");
        String songIds = objectMap.get("refSongIds");
        Constructor<Artist> constructor = Artist.class.getConstructor(int.class, String.class, String.class);
        return constructor.newInstance(id, artistName, songIds);
    }

    private static Album getAlbumObject(HashMap<String, String> objectMap) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int id = Integer.parseInt(objectMap.get("id"));
        int artistId = Integer.parseInt(objectMap.get("artist"));
        int year = Integer.parseInt(objectMap.get("year"));
        String albumTitle = objectMap.get("name");
        Constructor<Album> constructor = Album.class.getConstructor(int.class, int.class, String.class, int.class);
        return constructor.newInstance(id, artistId, albumTitle, year);
    }

    private static Song getSongObject(HashMap<String, String> objectMap) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int id = Integer.parseInt(objectMap.get("id"));
        int albumId = Integer.parseInt(objectMap.get("album"));
        Album album = (Album) getObjectFromId("albums", albumId);
        String songTitle = objectMap.get("title");
        int trackOnAlbum = Integer.parseInt(objectMap.get("track"));
        String genre = objectMap.get("genres");
        int artistId = Integer.parseInt(objectMap.get("artistId"));
        Constructor<Song> constructor = Song.class.getConstructor(int.class, Album.class, String.class, int.class, String.class, int.class);
        return constructor.newInstance(id, album, songTitle, trackOnAlbum, genre, artistId);
    }

    public static HashMap<String, String> getHashMapFromObject(MusicObject musicObject) {
        var convertedObject = new HashMap<String, String>();
        switch (musicObject.getClass().getSimpleName()) {
            case "Artist":
                convertedObject = getArtistMap(musicObject);
                break;
            case "Album":
                convertedObject = getAlbumMap(musicObject);
                break;
            case "Song":
                convertedObject = getSongMap(musicObject);
                break;
        }
        return convertedObject;
    }

    private static HashMap<String, String> getArtistMap(MusicObject musicObject) {
        HashMap<String, String> artistMap = new HashMap<>();
        Artist artist = (Artist) musicObject;
        artistMap.put("id", String.valueOf(artist.getId()));
        artistMap.put("name", artist.getResolvedName());
        artistMap.put("refSongIds", artist.getRefSongIds().toString().replaceAll("\\s+", ""));
        return artistMap;
    }

    private static HashMap<String, String> getAlbumMap(MusicObject musicObject) {
        HashMap<String, String> albumMap = new HashMap<>();
        Album album = (Album) musicObject;
        albumMap.put("id", String.valueOf(album.getId()));
        albumMap.put("artist", String.valueOf(album.getArtistId()));
        albumMap.put("name", album.getResolvedName());
        albumMap.put("year", String.valueOf(album.getYear()));
        return albumMap;
    }

    private static HashMap<String, String> getSongMap(MusicObject musicObject) {
        HashMap<String, String> songMap = new HashMap<>();
        Song song = (Song) musicObject;
        songMap.put("id", String.valueOf(song.getId()));
            Class<?> obj = musicObject.getClass();
            for (Field field : obj.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConvertFromId.class)) {
                    songMap.put("album", String.valueOf(getIdFromObject(((Song) musicObject).getAlbum())));
                }
            }
        songMap.put("title", song.getResolvedName());
        songMap.put("track", String.valueOf(song.getTrack()));
        songMap.put("genre", song.getGenre());
        songMap.put("artistId", String.valueOf(song.getArtistId()));
        return songMap;
    }

    private static int getIdFromObject(MusicObject musicObject){
        Class<?> object = musicObject.getClass();
        int id = 1;/*
        for(Field field : object.getDeclaredFields()){
            if(field.isAnnotationPresent(ConvertFromId.class)){
                field.setAccessible(true);
                Class<?> mo = field.getType();*/
                for(Method method : object.getMethods()){
                    if(method.isAnnotationPresent(ConvertFromId.class)){
                        try {
                            method.setAccessible(true);
                            id = (Integer) method.invoke(musicObject);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
        return id;
    }

    public static MusicObject getObjectFromId(String type, int objectId) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        MusicObject mo = null;
        if(type.equals("albums")){
            HashMap<String, String> objectData =  Database.getInstance().deserializeData(
                    FileSystem.readFile(Database.getInstance().getDbName() + "/" + type + "/" + objectId));
            int id = Integer.parseInt(objectData.get("id"));
            int artistId = Integer.parseInt(objectData.get("artist"));
            int year = Integer.parseInt(objectData.get("year"));
            String albumTitle = objectData.get("name");
            Constructor<Album> constructor = Album.class.getConstructor(int.class, int.class, String.class, int.class);
            mo = constructor.newInstance(id, artistId, albumTitle, year);
        }
        return mo;
    }

}
