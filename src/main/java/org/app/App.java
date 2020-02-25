package org.app;

import org.app.menu.menus.MainMenu;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.Converter;
import org.fsdb.Database;
import org.fsdb.FileSystem;
import org.fsdb.query.Query;

import java.io.File;
import java.util.*;

public class App {

    App(String dbName) throws IllegalAccessException {
        Database.getInstance().create(dbName);
        List<String> jsonFiles = List.of("assets/artists.json", "assets/albums.json", "assets/songs.json");
        Database.getInstance().loadJsonFiles(jsonFiles);
        var menu = new MainMenu(this);
        menu.handle();
    }

    public Artist deepRemoveArtist(Integer artistId) {
        String albumPath = Database.getInstance().getDbName() + "/albums";
        File[] albums = FileSystem.getDirFiles(albumPath);

        for (var albumFile : Objects.requireNonNull(albums)) {
            var data = Database.getInstance().deserializeData(FileSystem.readFile(albumFile.getPath()));
            if (data.get("artist").equals(artistId.toString())) {
                FileSystem.delete(albumPath + "/" + data.get("id"));
            }
        }

        var deleteArtist = Database.getInstance().executeQuery(new Query()
                .from("artists").where("id", artistId.toString()).delete());

        var artist = (Artist) Converter.getObjectFromHashMap("artists", deleteArtist.data);
        for (var songId : artist.getRefSongIds()) {
            FileSystem.delete(Database.getInstance().getDbName() + "/songs/" + songId);
        }

        return (Artist) Converter.getObjectFromHashMap("artists", deleteArtist.data);
    }

    public Album deepRemoveAlbum(Integer albumId) {
        String songsPath = Database.getInstance().getDbName() + "/songs";
        File[] songs = FileSystem.getDirFiles(songsPath);

        var deleteAlbum = Database.getInstance().executeQuery(new Query()
                .from("albums").where("id", albumId.toString()).delete());

        for (var songFile : Objects.requireNonNull(songs)) {
            var data = Database.getInstance().deserializeData(FileSystem.readFile(songFile.getPath()));
            if (data.get("album").equals(albumId.toString())) {
                FileSystem.delete(songsPath + "/" + data.get("id"));
            }
        }
        return (Album) Converter.getObjectFromHashMap("albums", deleteAlbum.data);
    }

    public Song removeSong(Integer songId) {
        var deleteSong = Database.getInstance().executeQuery(new Query().from("songs").where("id", songId.toString()).delete());
        return (Song) Converter.getObjectFromHashMap("songs", deleteSong.data);
    }

    public int generateId(String type) {
        int newId = -1;
        ArrayList<MusicObject> list;
        list = Database.getInstance().search(type, "");
        for (MusicObject musicObject : list) {
            if (musicObject.getId() > newId) newId = musicObject.getId();
        }
        return newId + 1;
    }
}
