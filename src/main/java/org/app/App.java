package org.app;

import org.app.menu.Menu;
import org.fsdb.FileSystem;
import org.fsdb.Database;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.Input;
import org.fsdb.classes.Tuple;
import org.fsdb.query.Query;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    private static final int INVALID_CHOICE = -1;
    private static final int ARTIST = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 2;
    private static final int GLOBAL_SEARCH = 3;
    private static final int EXIT = 4;

    public Database database;

    App(String dbName) {
        database = new Database();
        database.create(dbName);
    }

    void init() {
        List<String> jsonFiles = List.of("assets/artists.json", "assets/albums.json", "assets/songs.json");
        database.loadJsonFiles(jsonFiles);
    }

    public ArrayList<MusicObject> getDataList(String subPath, String search) {
        ArrayList<MusicObject> results = new ArrayList<>();

        String path = database.getDbName() + "/" + subPath;
        File[] fileArr = FileSystem.getDirFiles(path);

        for (File file : Objects.requireNonNull(fileArr)) {
            String url = file.toString();
            String data = FileSystem.readFile(url);

            HashMap<String, String> dataMap = database.deserializeData(data);
            MusicObject result = getNameOfData(subPath, dataMap);

            String searchString = "";
            if (getClass(result) == ARTIST) {
                Artist obj = (Artist) result;
                searchString = obj.getName();
            } else if (getClass(result) == ALBUM) {
                Album obj = (Album) result;
                searchString = obj.getName();
            } else if (getClass(result) == SONG) {
                Song obj = (Song) result;
                searchString = obj.getTitle();
            }

            if (searchString.toLowerCase().contains(search.toLowerCase()))
                results.add(result);
        }
        return results;
    }

    private int getClass(MusicObject musicObject) {
        if (musicObject.getClass().equals(Artist.class)) return ARTIST;
        else if (musicObject.getClass().equals(Album.class)) return ALBUM;
        else if (musicObject.getClass().equals(Song.class)) return SONG;
        else return -1;
    }

    public String getClassName(MusicObject musicObject) {
        if (musicObject.getClass().equals(Artist.class)) return "artists";
        else if (musicObject.getClass().equals(Album.class)) return "albums";
        else if (musicObject.getClass().equals(Song.class)) return "songs";
        else return null;
    }

    private MusicObject getNameOfData(String subPath, HashMap<String, String> dataMap) {
        if (subPath.equals("artists")) {
            return new Artist(dataMap);
        } else if (subPath.equals("albums")) {
            return new Album(dataMap);
        } else {
            return new Song(dataMap);
        }
    }

    public int getIntInput() {
        // prompt again on if Integer.parseInt throws NumberFormatException inside getInt()
        try {
            return Input.getInt();
        } catch (Exception e) {
            System.out.println("Invalid menu choice, try again!");
            return getIntInput();
        }
    }

    public List<String> getClassFolders() {
        File[] subFolders = FileSystem.getSubFolders(database.getDbName());
        return Arrays.stream(subFolders).map(File::getName).collect(Collectors.toList());
    }

    public Artist deepRemoveArtist(Integer artistId) {
        String albumPath = database.getDbName() + "/albums";
        File[] albums = FileSystem.getDirFiles(albumPath);

        for (var albumFile : Objects.requireNonNull(albums)) {
            var data = database.deserializeData(FileSystem.readFile(albumFile.getPath()));
            if (data.get("artist").equals(artistId.toString())) {
                FileSystem.delete(albumPath + "/" + data.get("id"));
            }
        }

        var deleteArtist = database.executeQuery(new Query()
                .from("artists").where("id", artistId.toString()).delete());

        var artist = new Artist(deleteArtist.data);
        for (var songId : artist.getRefSongIds()) {
            FileSystem.delete(database.getDbName() + "/songs/" + songId);
        }

        return new Artist(deleteArtist.data);
    }

    public Album deepRemoveAlbum(Integer albumId) {
        String songsPath = database.getDbName() + "/songs";
        File[] songs = FileSystem.getDirFiles(songsPath);

        var deleteAlbum = database.executeQuery(new Query()
                .from("albums").where("id", albumId.toString()).delete());

        for (var songFile : Objects.requireNonNull(songs)) {
            var data = database.deserializeData(FileSystem.readFile(songFile.getPath()));
            if (data.get("album").equals(albumId.toString())) {
                FileSystem.delete(songsPath + "/" + data.get("id"));
            }
        }

        return new Album(deleteAlbum.data);
    }

    public Song removeSong(Integer songId) {
        var deleteSong = database.executeQuery(new Query().from("songs").where("id", songId.toString()).delete());
        return new Song(deleteSong.data);
    }

    private void editSong() {
        System.out.print("Search for song to edit>  ");

        var songs = sortResults(getDataList("songs", Input.getLine()));
        printResults(songs, true);

        if (songs.size() == 0) {
            System.out.println("No results found.");
            return;
        }
        System.out.print("Enter index to edit> ");
        int index = Input.getInt();

        String searchId = String.valueOf((songs.get(index - 1)).getId());
        System.out.print("Enter new song title> ");
        String newSongTitle = Input.getLine();
        var editResult = database.executeQuery(new Query().from("songs").where("id", searchId).update("title", newSongTitle));

        if (editResult.success)
            System.out.printf("Successfully edited song, new song title is: %s\n", newSongTitle);
        else System.out.println("Could not edit song.");
    }

    private void editGenre() {
        System.out.print("Search for song to edit genre of>  ");
        var songs = sortResults(getDataList("songs", Input.getLine()));
        printResults(songs, true);

        if (songs.size() == 0) {
            System.out.println("No results found.");
            return;
        }

        System.out.print("Enter index to edit> ");
        int index = Input.getInt();
        String searchId = String.valueOf((songs.get(index - 1)).getId());
        System.out.print("Enter new song genre> ");
        String newGenre = Input.getLine();
        var editResult = database.executeQuery(new Query().from("songs").where("id", searchId).update("genres", newGenre));
        var fetchResult = database.executeQuery((new Query().from("songs").where("id", searchId).fetch()));
        var songTitle = fetchResult.data.get("title");
        if (editResult.success)
            System.out.printf("Successfully edited song, new genre of %s is: %s\n", Color.printSongColor(songTitle), newGenre);
        else System.out.println("Could not edit song.");
    }

    private void editAlbum() {
        System.out.print("Search for album to edit>  ");
        var albums = sortResults(getDataList("albums", Input.getLine()));
        printResults(albums, true);

        if (albums.size() == 0) {
            System.out.println("No results found.");
            return;
        }

        System.out.print("Enter index to edit> ");
        int index = Input.getInt();
        String searchId = String.valueOf((albums.get(index - 1)).getId());
        System.out.print("Enter new album title> ");
        String newAlbumTitle = Input.getLine();
        var editResult = database.executeQuery(new Query().from("albums").where("id", searchId).update("name", newAlbumTitle));
        //editResult.data.get("title");
        if (editResult.success)
            System.out.printf("Successfully edited album, new album title is: %s\n", Color.printAlbumColor(newAlbumTitle));
        else System.out.println("Could not edit album.");
    }

    private void editArtist() {
        System.out.print("Search for artist to edit>  ");

        var artists = sortResults(getDataList("artists", Input.getLine()));
        printResults(artists, true);

        if (artists.size() == 0) {
            System.out.println("No results found.");
            return;
        }

        System.out.print("Enter index to edit> ");
        int index = Input.getInt();

        String searchId = String.valueOf((artists.get(index - 1)).getId());
        System.out.print("Enter new artist name> ");
        String newArtistName = Input.getLine();
        var editResult = database.executeQuery(new Query().from("artists").where("id", searchId).update("name", newArtistName));

        if (editResult.success)
            System.out.printf("Successfully edited artist, new artist name is: %s\n", Color.printArtistColor(newArtistName));
        else System.out.println("Could not edit artist.");
    }

    public void addSong(Song song) {
        var songMap = song.mapObject();
        database.executeQuery(new Query().from("songs").create(songMap));
    }

    public void addArtist(Artist artist) {
        var artistMap = artist.mapObject();
        database.executeQuery(new Query().from("artists").create(artistMap));
    }

    public void addAlbum(Album album) {
        var albumMap = album.mapObject();
        database.executeQuery(new Query().from("albums").create(albumMap));
    }

    public ArrayList<MusicObject> getAlbumList(int artistId) {
        ArrayList<MusicObject> results = new ArrayList<>();

        String albumUrl = "albums";
        String path = database.getDbName() + "/" + albumUrl;
        File[] fileArr = FileSystem.getDirFiles(path);

        for (File file : Objects.requireNonNull(fileArr)) {
            String url = file.toString();
            String data = FileSystem.readFile(url);

            HashMap<String, String> dataMap = database.deserializeData(data);
            MusicObject result = getNameOfData(albumUrl, dataMap);
            Album album = (Album) result;

            if (artistId == album.getArtist())
                results.add(result);
        }
        return results;
    }

    public int generateId(String type) {
        int newId = -1;
        ArrayList<MusicObject> list = getDataList(type, "");
        for (MusicObject musicObject : list) {
            if (musicObject.getId() > newId) newId = musicObject.getId();
        }
        return newId + 1;
    }

    public ArrayList<MusicObject> sortResults(ArrayList<MusicObject> results) {
        results.sort(Comparator.naturalOrder());
        return results;
    }

    public void printResults(ArrayList<MusicObject> results, boolean printIndexed) {
        int index = 0;
        String artistStr = "", albumStr = "", songStr = "";

        var artists = (Artist[]) results.stream().filter(a -> getClass(a) == ARTIST).toArray(Artist[]::new);
        if (artists.length > 0) {
            artistStr = artists.length + " Artist(s) ";
            System.out.printf("-- Artists (%d) --\n", artists.length);
            for (int i = 0; i < artists.length; i++, index++) {
                if (printIndexed) System.out.printf("[%d] %s\n", index + 1, artists[i].getNameColored());
                else System.out.println(artists[i].getNameColored());
            }
        }
        var allArtistsPaths = FileSystem.getDirFiles(database.getDbName() + "/artists");
        var artistArray = new ArrayList<Artist>();
        for (File file : Objects.requireNonNull(allArtistsPaths)) {
            artistArray.add(new Artist(database.deserializeData(FileSystem.readFile(file.getPath()))));
        }

        var albums = (Album[]) results.stream().filter(a -> getClass(a) == ALBUM).toArray(Album[]::new);
        if (albums.length > 0) {
            albumStr = albums.length + " Album(s) ";
            System.out.printf("-- Albums (%d) --\n", albums.length);
            for (int i = 0; i < albums.length; i++, index++) {
                int finalI = i;

                var artistName = "Unknown Artist";
                var artistObject = artistArray.stream().filter(a -> a.getId() == albums[finalI].getArtistId()).findFirst();
                if (artistObject.isPresent())
                    artistName = artistObject.get().getNameColored();

                if (printIndexed)
                    System.out.printf("[%d] %s - %s\n", index + 1, albums[i].getNameColored(), artistName);
                else System.out.println(albums[i].getNameColored());
            }
        }
        var songs = (Song[]) results.stream().filter(s -> getClass(s) == SONG).toArray(Song[]::new);
        if (songs.length > 0) {
            songStr = songs.length + " Song(s) ";
            System.out.printf("-- Songs (%d) --\n", songs.length);
            for (int i = 0; i < songs.length; i++, index++) {
                int finalI = i;

                var artistName = "Unknown Artist";
                var artistObject = artistArray.stream().filter(a -> a.getId() == songs[finalI].getArtistId()).findFirst();
                if (artistObject.isPresent())
                    artistName = artistObject.get().getNameColored();

                if (printIndexed) System.out.printf("[%d] %s - %s\n", index + 1, songs[i].getNameColored(), artistName);
                else System.out.println(songs[i].getNameColored());
            }
        }

        if (index > 0) {
            System.out.printf("\nFound %s%s%s\n"
                    , Color.printArtistColor(artistStr)
                    , Color.printAlbumColor(albumStr)
                    , Color.printSongColor(songStr));
        } else {
            System.out.println("No results found!");
        }
    }

    public void printArtistSongs(String artistName) {
        var fetchResult = database.executeQuery(new Query().from("artists").where("name", artistName).fetch());
        if (!fetchResult.success) System.out.println("Could not find artist!");

        var artist = new Artist(fetchResult.data);
        var songsResult = database.executeQuery(new Query()
                .from("songs").where("artistId", String.valueOf(artist.getId()))
                .fetchAll());

        var songs = songsResult.dataArray
                .stream()
                .map(s -> s.get("title"))
                .collect(Collectors.toList());

        System.out.printf("\n----- Song(s) by %s -----\n", artist.getName());
        songs.forEach(s -> System.out.printf("%s - %s\n",
                Color.printSongColor(s), Color.printArtistColor(artist.getName())
        ));
    }

    public ArrayList<MusicObject> globalSearch(ArrayList<String> menuChoice, String search) {
        var totalResults = new ArrayList<MusicObject>();
        for (String choice : menuChoice) {
            totalResults.addAll(getDataList(choice, search));
        }
        return totalResults;
    }
}
