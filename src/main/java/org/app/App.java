package org.app;

import org.fsdb.FileSystem;
import org.fsdb.Database;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.Input;
import org.fsdb.Util;
import org.fsdb.query.Query;

import java.io.File;
import java.util.*;

class App {
    private static final int GLOBAL_SEARCH = 3;
    private static final int EXIT = 4;

    private static final int ARTIST = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 2;

    private Database database;

    App(String dbName) {
        database = new Database();
        database.create(dbName);
    }

    void init() {
        List<String> jsonFiles = List.of("assets/artists.json", "assets/albums.json", "assets/songs.json");
        database.loadJsonFiles(jsonFiles);
    }

    void show() {
        int menuSelection;
        do {
            System.out.print("Menu\n----------\n" +
                    "1) Search\n" +
                    "2) Add\n" +
                    "3) Remove\n" +
                    "4) Edit\n" +
                    "5) Quit\n" +
                    "> "
            );
            menuSelection = userChoice();
            userChosenAction(menuSelection);
        } while (menuSelection != 5);
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
                searchMenu();
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
                System.out.println("Edit\n----------");
                printEditMenu();
                editMenuChoice(userChoice());
                break;
            case 5:
                System.out.println("Goodbye :(");
                break;
            default:
                userChosenAction(userChoice());
                break;
        }
    }

    private void printEditMenu(){
        System.out.print("Menu\n----------\n" +
                "1) Edit song\n" +
                "2) Edit album\n" +
                "3) Edit artist\n" +
                "4) Edit genre\n" +
                "5) Back to main menu\n" +
                "> ");
    }
    private void editMenuChoice(int userChoice) {
        switch (userChoice) {
            case 1:
                System.out.println("Edit song\n----------");
                editSong();
                break;
            case 2:
                System.out.println("Edit album\n----------");
                editAlbum();
                break;
            case 3:
                System.out.println("Edit artist\n----------");
                editArtist();
                break;
            case 4:
                System.out.println("Edit genre\n----------");
                editGenre();
                break;
            case 5:
                System.out.println("Returning to main menu\n----------");
                break;
        }
    }
    private void removeSong() {
        System.out.print("Search for song to remove>  ");

        var songs = getDataList("songs", Input.getLine());
        printResults(songs, true);

        System.out.print("Enter index to remove> ");
        int index = Input.getInt();

        String searchId = String.valueOf((songs.get(index - 1)).getId());
        var deleteResult = database.executeQuery(new Query().from("songs").where("id", searchId).delete());

        if (deleteResult.success) System.out.println("Successfully removed song.");
        else System.out.println("Could not remove song.");
    }
    private void editSong() {
        System.out.print("Search for song to edit>  ");

        var songs = getDataList("songs", Input.getLine());
        printResults(songs, true);

        System.out.print("Enter index to edit> ");
        int index = Input.getInt();

        String searchId = String.valueOf((songs.get(index - 1)).getId());
        System.out.print("Enter new song title> ");
        String newSongTitle = Input.getLine();
        var editResult = database.executeQuery(new Query().from("songs").where("id", searchId).update("title", newSongTitle));
        //editResult.data.get("title");
        if (editResult.success) System.out.printf("Successfully edited song, new song title is: %s\n", newSongTitle);
        else System.out.println("Could not edit song.");
    }
    private void editGenre() {
        System.out.print("Search for song to edit genre of>  ");
        var songs = getDataList("songs", Input.getLine());
        printResults(songs, true);
        System.out.print("Enter index to edit> ");
        int index = Input.getInt();
        String searchId = String.valueOf((songs.get(index - 1)).getId());
        System.out.print("Enter new song genre> ");
        String newGenre = Input.getLine();
        var editResult = database.executeQuery(new Query().from("songs").where("id", searchId).update("genres", newGenre));
        var fetchResult = database.executeQuery((new Query().from("songs").where("id", searchId).fetch()));
        var songTitle = fetchResult.data.get("title");
        if (editResult.success) System.out.printf("Successfully edited song, new genre of %s is: %s\n", songTitle, newGenre);
        else System.out.println("Could not edit song.");
    }
    private void editAlbum() {
        System.out.print("Search for album to edit>  ");
        var albums = getDataList("albums", Input.getLine());
        printResults(albums, true);
        System.out.print("Enter index to edit> ");
        int index = Input.getInt();
        String searchId = String.valueOf((albums.get(index - 1)).getId());
        System.out.print("Enter new album title> ");
        String newAlbumTitle = Input.getLine();
        var editResult = database.executeQuery(new Query().from("albums").where("id", searchId).update("name", newAlbumTitle));
        //editResult.data.get("title");
        if (editResult.success) System.out.printf("Successfully edited album, new album title is: %s\n", newAlbumTitle);
        else System.out.println("Could not edit album.");
    }
    private void editArtist() {
        System.out.print("Search for artist to edit>  ");

        var artists = getDataList("artists", Input.getLine());
        printResults(artists, true);

        System.out.print("Enter index to edit> ");
        int index = Input.getInt();

        String searchId = String.valueOf((artists.get(index - 1)).getId());
        System.out.print("Enter new artist name> ");
        String newArtistName = Input.getLine();
        var editResult = database.executeQuery(new Query().from("artists").where("id", searchId).update("name", newArtistName));
        //editResult.data.get("title");
        if (editResult.success) System.out.printf("Successfully edited artist, new artist name is: %s\n", newArtistName);
        else System.out.println("Could not edit artist.");
    }
    private void addSong() {
        System.out.print("Write song name to add> ");

        String songName = Input.getLine();
        System.out.println(songName);

        String path = "songs";
        getDataList(path, songName);

        Song song = new Song(generateID(path), 9000, songName, 9000, "Metal",9000);
        HashMap<String, String> mapSong = song.mapObject();
        database.executeQuery(new Query().from(path).create(mapSong));

        System.out.printf("%s %s has been created!\n", path.substring(0, path.length() - 1), songName);

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

    private void searchMenu() {
        File[] subFolder = FileSystem.getSubFolders(database.getDbName());
        ArrayList<String> menuChoice = new ArrayList<>();

        // get folder names
        for (File file : subFolder)
            menuChoice.add(file.getName());

        // print menu search choices
        for (int i = 0; i < menuChoice.size(); i++)
            System.out.println((i + 1) + ". " + Util.capitalize(menuChoice.get(i)));

        System.out.println("4. Search all.\n5. Exit to main menu.");

        int choice = userChoice() - 1;
        if (choice == EXIT) return;

        System.out.print("Search for> ");
        String search = Input.getLine();

        ArrayList<MusicObject> results;
        if (choice == GLOBAL_SEARCH) results = globalSearch(menuChoice, search);
        else results = getDataList(menuChoice.get(choice), search);

        printResults(results, false);
    }

    private void printResults(ArrayList<MusicObject> results, boolean printIndexed) {
        var artists = (Artist[]) results.stream().filter(x -> getClass(x) == ARTIST).toArray(Artist[]::new);
        if (artists.length > 0) {
            System.out.printf("-- Artists (%d) --\n", artists.length);
            for (int i = 0; i < artists.length; i++) {
                if (printIndexed) System.out.printf("[%d] %s\n", i + 1, artists[i].getName());
                else System.out.println(artists[i].getName());
            }
        } else {
            System.out.printf("No artists (%d) found.\n", artists.length);
        }
        var albums = (Album[]) results.stream().filter(x -> getClass(x) == ALBUM).toArray(Album[]::new);
        if (albums.length > 0) {
            System.out.printf("-- Albums (%d) --\n", albums.length);
            for (int i = 0; i < albums.length; i++) {
                if (printIndexed) System.out.printf("[%d] %s\n", i + 1, albums[i].getName());
                else System.out.println(albums[i].getName());
            }
        } else {
            System.out.printf("No albums (%d) found.\n", albums.length);
        }
        var songs = (Song[]) results.stream().filter(x -> getClass(x) == SONG).toArray(Song[]::new);
        if (songs.length > 0) {
            System.out.printf("-- Songs (%d) --\n", songs.length);
            for (int i = 0; i < songs.length; i++) {
                if (printIndexed) System.out.printf("[%d] %s\n", i + 1, songs[i].getTitle());
                else System.out.println(songs[i].getTitle());
            }
        } else {
            System.out.printf("No songs (%d) found.\n", songs.length);
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
