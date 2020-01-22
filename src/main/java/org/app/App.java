package org.app;

import org.fsdb.FileSystem;
import org.fsdb.Database;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.Input;
import org.fsdb.Util;
import org.fsdb.classes.Tuple;
import org.fsdb.query.Query;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.List.*;

class App {
    private static final int GLOBAL_SEARCH = 3;
    private static final int EXIT = 4;

    private static final int ARTIST = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 2;
    private static final int INVALID_CHOICE = -1;

    private static final Comparator<Artist> ARTIST_COMPARATOR = ((a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
    private static final Comparator<Album> ALBUM_COMPARATOR = ((a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
    private static final Comparator<Song> SONG_COMPARATOR = ((s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));


    private Database database;

    App(String dbName) {
        database = new Database();
        database.create(dbName);
    }

    void init() {
        List<String> jsonFiles = of("assets/artists.json", "assets/albums.json", "assets/songs.json");
        database.loadJsonFiles(jsonFiles);
    }

    void show() {
        int menuSelection;
        do {
            System.out.print("Menu\n----------\n" +
                    "1) Search\n" +
                    "2) Add\n" +
                    "3) Remove\n" +
                    "4) Quit\n" +
                    "> "
            );
            menuSelection = userChoice();
            userChosenAction(menuSelection);
        } while (menuSelection != 4);
    }

    private ArrayList<MusicObject> getDataList(String subPath, String search) {
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

    private String getClassName(MusicObject musicObject) {
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

    private int userChoice() {
        // prompt again on if Integer.parseInt throws NumberFormatException inside getInt()
        try {
            return Input.getInt();
        } catch (Exception e) {
            System.out.println("Invalid menu choice, try again!");
            return userChoice();
        }
    }

    private void userChosenAction(int userChoice) {
        switch (userChoice) {
            case 1:
                System.out.println("Search\n----------");
                var choice = handleSubMenu();
                var results = getSearchResults(choice);
                printResults(results, false);
                break;
            case 2:
                System.out.println("Add\n----------");
                addSong();
                break;
            case 3:
                System.out.println("Remove\n----------");
                removeSong();
                break;
            case 4:
                System.out.println("Goodbye :(");
                break;
            default:
                userChosenAction(userChoice());
                break;
        }
    }

    private Tuple<Integer, List<String>> handleSubMenu() {

        var typeFolders = getTypeFolders();

        for (int i = 0; i < typeFolders.size(); i++)
            System.out.println((i + 1) + ". " + Util.capitalize(typeFolders.get(i)));

        int menuSize = typeFolders.size();
        System.out.printf("%d. Search all\n%d. Exit to main menu\n> ", menuSize + 1, menuSize + 2);

        int choice = userChoice() - 1;
        if (choice >= menuSize + 2 || choice < 0) return new Tuple<>(-1, typeFolders);

        return new Tuple<>(choice, typeFolders);
    }

    private List<String> getTypeFolders() {
        File[] subFolders = FileSystem.getSubFolders(database.getDbName());
        return Arrays.stream(subFolders).map(File::getName).collect(Collectors.toList());
    }

    private void removeSong() {
        Tuple<Integer, List<String>> choice = handleSubMenu();
        var results = getSearchResults(choice);

        printResults(results, true);
        System.out.print("Enter index to remove> ");
        int index = Input.getInt();

        var typeName = getClassName(results.get(index - 1));
        if (typeName == null) {
            System.out.println("Could not remove.");
            return;
        }

        String searchId = String.valueOf((results.get(index - 1)).getId());
        var deleteResult = database.executeQuery(new Query().from(typeName).where("id", searchId).delete());

        if (deleteResult.success) {
            if (typeName.equals("songs"))
                System.out.printf("Successfully removed %s\n", deleteResult.data.get("title"));
            else System.out.printf("Successfully removed %s\n", deleteResult.data.get("name"));
        }
    }

    private void addSong() {
        System.out.print("Write song name to add> ");
        String songName = Input.getLine();

        String path = "songs";
        getDataList(path, songName);

        Song song = new Song(generateID(path), 9000, songName, 9000, "Metal");
        HashMap<String, String> mapSong = song.mapObject();
        database.executeQuery(new Query().from(path).create(mapSong));

        System.out.printf("%s %s has been created!\n", Util.capitalize(path.substring(0, path.length() - 1)), songName);

        //TODO
        // Search/Create Artist (Create Artist method) (int id, int album, String title, int track, String genre)
        // Search/Create Album  (Create Album method)

    }

    private int generateID(String type) {
        int newId = -1;
        ArrayList<MusicObject> list = getDataList(type, "");
        for (MusicObject musicObject : list) {
            if (musicObject.getId() > newId) newId = musicObject.getId();
        }
        return newId + 1;
    }

    private ArrayList<MusicObject> getSearchResults(Tuple<Integer, List<String>> input) {
        if (input.first == EXIT || input.first == INVALID_CHOICE) return new ArrayList<>();

        String typeStr = input.first == 3 ? "all" : input.second.get(input.first);
        System.out.printf("Search %s> ", typeStr);
        var search = Input.getLine();

        ArrayList<MusicObject> results;
        if (input.first == GLOBAL_SEARCH) results = globalSearch((ArrayList<String>) input.second, search);
        else results = getDataList(input.second.get(input.first), search);

        return results;
    }

    private void printResults(ArrayList<MusicObject> results, boolean printIndexed) {
        int index = 0;
        var artists = (Artist[])results.stream().filter(x -> getClass(x) == ARTIST).toArray(Artist[]::new);
        Arrays.sort(artists, ARTIST_COMPARATOR);
        if (artists.length > 0) {
            System.out.printf("-- Artists (%d) --\n", artists.length);
            for (int i = 0; i < artists.length; i++, index++) {
                if (printIndexed) System.out.printf("[%d] %s\n", i + 1, artists[i].getName());
                else System.out.println(artists[i].getName());
            }
        }
        var albums = (Album[]) results.stream().filter(x -> getClass(x) == ALBUM).toArray(Album[]::new);
        Arrays.sort(albums, ALBUM_COMPARATOR);
        if (albums.length > 0) {
            System.out.printf("-- Albums (%d) --\n", albums.length);
            for (int i = 0; i < albums.length; i++) {
                if (printIndexed) System.out.printf("[%d] %s\n", i + 1, albums[i].getName());
                else System.out.println(albums[i].getName());
            }
        }
        var songs = (Song[]) results.stream().filter(x -> getClass(x) == SONG).toArray(Song[]::new);
        Arrays.sort(songs, SONG_COMPARATOR);
        if (songs.length > 0) {
            System.out.printf("-- Songs (%d) --\n", songs.length);
            for (int i = 0; i < songs.length; i++) {
                if (printIndexed) System.out.printf("[%d] %s\n", i + 1, songs[i].getTitle());
                else System.out.println(songs[i].getTitle());
            }
        }
    }

    private ArrayList<MusicObject> globalSearch(ArrayList<String> menuChoice, String search) {
        var totalResults = new ArrayList<MusicObject>();
        for (String choice : menuChoice) {
            totalResults.addAll(getDataList(choice, search));
        }
        return totalResults;
    }
}
