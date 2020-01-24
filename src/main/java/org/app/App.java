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

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

class App {
    private static final int INVALID_CHOICE = -1;
    private static final int ARTIST = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 2;
    private static final int GLOBAL_SEARCH = 3;
    private static final int EXIT = 4;

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
                var results = sortResults(getSearchResults(choice));
                printResults(results, true, true);

                // if the search was artists we ask if they want to print the songs for that artist
                if (choice.first > choice.second.size() - 1) break;
                else if (choice.second.get(choice.first).equals("artists")) {
                    System.out.println("Enter 0 to return to menu");
                    System.out.print("or enter index to list songs> ");
                    var index = Input.getInt();
                    if (index == 0) break;
                    printArtistSongs(results.get(index - 1).getResolvedName());
                }

                break;
            case 2:
                System.out.println("Add\n----------");
                addSong();
                break;
            case 3:
                System.out.println("Remove\n----------");
                removeObject();
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

    private void printEditMenu() {
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

    private void removeObject() {
        Tuple<Integer, List<String>> choice = handleSubMenu();
        var results = sortResults(getSearchResults(choice));

        printResults(results, true, true);

        if (results.size() == 0) {
            System.out.println("No results found.");
            return;
        }

        System.out.print("Enter index to remove> ");
        int index = Input.getInt();

        var typeName = getClassName(results.get(index - 1));
        if (typeName == null) {
            System.out.println("Could not remove.");
            return;
        }

        if (typeName.equals("albums")) {
            System.out.println("Deep removing album");
            deepRemoveAlbum(results.get(index - 1).getId());
        } else if (typeName.equals("artists")) {
            System.out.println("Deep removing artist");
            deepRemoveArtist(results.get(index - 1).getId());
        } else {
            String searchId = String.valueOf((results.get(index - 1)).getId());
            var deleteResult = database.executeQuery(new Query().from(typeName).where("id", searchId).delete());

            if (deleteResult.success) {
                if (typeName.equals("songs"))
                    System.out.printf("Successfully removed %s\n", deleteResult.data.get("title"));
                else System.out.printf("Successfully removed %s\n", deleteResult.data.get("name"));
            }
            if (deleteResult.success) {
                if (typeName.equals("songs"))
                    System.out.printf("Successfully removed %s\n", Color.printSongColor(deleteResult.data.get("title")));
                else if (typeName.equals("albums")) {
                    System.out.printf("Successfully removed %s\n", Color.printAlbumColor(deleteResult.data.get("name")));
                } else
                    System.out.printf("Successfully removed %s\n", Color.printArtistColor(deleteResult.data.get("name")));
            }
        }
    }

    private void deepRemoveArtist(Integer artistId) {
        String albumPath = database.getDbName() + "/albums";
        File[] albums = FileSystem.getDirFiles(albumPath);

        for (var albumFile : Objects.requireNonNull(albums)) {
            var data = database.deserializeData(FileSystem.readFile(albumFile.getPath()));
            if (data.get("artist").equals(artistId.toString())) {
                FileSystem.delete(albumPath + "/" + data.get("id"));
                System.out.printf("[ALBUM] removed album with name %s\n", data.get("name"));
            }
        }

        var deleteArtist = database.executeQuery(new Query()
                .from("artists").where("id", artistId.toString()).delete());

        var artist = new Artist(deleteArtist.data);
        for (var songId : artist.getRefSongIds()) {
            FileSystem.delete(database.getDbName() + "/songs/" + songId);
            System.out.printf("[SONG] removed song with id %d\n", songId);
        }
    }

    private void deepRemoveAlbum(Integer albumId) {
        FileSystem.delete(database.getDbName() + "/albums/" + albumId);
        String songsPath = database.getDbName() + "/songs";
        File[] songs = FileSystem.getDirFiles(songsPath);

        for (var songFile : Objects.requireNonNull(songs)) {
            var data = database.deserializeData(FileSystem.readFile(songFile.getPath()));
            if (data.get("album").equals(albumId.toString())) {
                FileSystem.delete(songsPath + "/" + data.get("id"));
                System.out.printf("Removed song %s\n", data.get("title"));
            }
        }
    }

    private void editSong() {
        System.out.print("Search for song to edit>  ");

        var songs = sortResults(getDataList("songs", Input.getLine()));
        printResults(songs, true, true);

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
        printResults(songs, true, true);

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
        printResults(albums, true, true);

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
        printResults(artists, true, true);

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

    private void addSong() {
        System.out.print("Write song name to add> ");
        String songName = Input.getLine();

        String path = "songs";
        getDataList(path, songName);
        int songId = generateID(path);
        Artist artist = addArtist(songId);
        Album album = addAlbum(artist.getId());
        Song song = new Song(songId, album.getId(), songName, -1, "Metal", artist.getId());
        HashMap<String, String> mapSong = song.mapObject();
        database.executeQuery(new Query().from(path).create(mapSong));

        System.out.printf("%s %s has been created, with the artist: %s associated with the album %s!\n", Util.capitalize(path.substring(0, path.length() - 1)),
                Color.printSongColor(songName),
                Color.printArtistColor(artist.getName()),
                Color.printAlbumColor(album.getName()));
    }

    private Artist addArtist(int songId) {
        var path = "artists";
        System.out.print("Who's the artist?>  ");
        String artistInput = Input.getLine();
        var artists = sortResults(getDataList(path, artistInput));

        if (artists.size() > 0) {
            printResults(artists, true, true);
            System.out.println("Are any of these the requested artist?> ");
            System.out.println("If yes, enter index to select> ");
            System.out.println("Else, press 0 to create new " + Color.printArtistColor("artist"));
        } else {
            System.out.println("No results found.");
            System.out.println("Press 0 to create new " + Color.printArtistColor("artist"));
        }

        int index = Input.getInt();
        Artist artist;
        if (index == 0) {
            System.out.print("Write new Artist >");
            artistInput = Input.getLine();
            ArrayList<Integer> newArrayList = new ArrayList<>();
            newArrayList.add(songId);
            artist = new Artist(generateID(path), artistInput, newArrayList);
        } else {
            artist = (Artist) artists.get(index - 1);
        }
        database.executeQuery(new Query().from(path).create(artist.mapObject()));
        return artist;
    }

    private Album addAlbum(int artistId) {
        Album album;
        var path = "albums";
        var albums = sortResults(getAlbumList(artistId));
        if (albums.size() > 0) {
            printResults(albums, true, true);
            System.out.println("Are any of these the requested albums?> ");
            System.out.println("If yes, enter index to select> ");
            System.out.println("Else, press 0 to create a new album.");
        } else {
            System.out.println("Press 0 to create a new album.");
        }
        int index = Input.getInt();

        if (index == 0) {
            System.out.println("What is the name of the album?");
            String albumName = Input.getLine();
            System.out.println("What year was the album released?");
            int year = Input.getInt();
            album = new Album(generateID(path), artistId, albumName, year);
        } else {
            album = (Album) albums.get(index - 1);
        }
        database.executeQuery(new Query().from(path).create(album.mapObject()));
        return album;
    }

    private ArrayList<MusicObject> getAlbumList(int artistId) {
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
            if (artistId == album.getArtist()) {
                results.add(result);
            }

        }
        return results;
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

    private ArrayList<MusicObject> sortResults(ArrayList<MusicObject> results) {
        results.sort(Comparator.naturalOrder());
        return results;
    }

    private void printResults(ArrayList<MusicObject> results, boolean printIndexed, boolean printResult) {
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
         var allArtistsPaths = FileSystem.getDirFiles(database.getDbName() +"/artists");
        var artistArray = new ArrayList<Artist>();
        for(File file : allArtistsPaths){
            artistArray.add(new Artist(database.deserializeData(FileSystem.readFile(file.getPath()))));
        }

        var albums = (Album[]) results.stream().filter(a -> getClass(a) == ALBUM).toArray(Album[]::new);
        if (albums.length > 0) {
            albumStr = albums.length + " Album(s) ";
            System.out.printf("-- Albums (%d) --\n", albums.length);
            for (int i = 0; i < albums.length; i++, index++) {

                int finalI = i;
                var artistFiltered =  artistArray.stream().filter(a -> a.getId() == albums[finalI].getArtistId()).findFirst().get();

                if (printIndexed) System.out.printf("[%d] %s - %s\n", index + 1, albums[i].getNameColored(), artistFiltered.getNameColored());
                else System.out.println(albums[i].getNameColored());
            }
        }
        var songs = (Song[]) results.stream().filter(s -> getClass(s) == SONG).toArray(Song[]::new);
        if (songs.length > 0) {
            songStr = songs.length + " Song(s) ";
            System.out.printf("-- Songs (%d) --\n", songs.length);
            for (int i = 0; i < songs.length; i++, index++) {
                int finalI = i;
                var artistFiltered =  artistArray.stream().filter(a -> a.getId() == songs[finalI].getArtistId()).findFirst().get();
                if (printIndexed) System.out.printf("[%d] %s - %s\n", index + 1, songs[i].getNameColored(),artistFiltered.getNameColored());
                else System.out.println(songs[i].getNameColored());
            }
        }

        System.out.printf("\nFound %s%s%s\n"
                , Color.printArtistColor(artistStr)
                , Color.printAlbumColor(albumStr)
                , Color.printSongColor(songStr));
    }

    private void printArtistSongs(String artistName) {
        var fetchResult = database.executeQuery(new Query().from("artists").where("name", artistName).fetch());
        if (!fetchResult.success) System.out.println("Could find artist!");

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

    private ArrayList<MusicObject> globalSearch(ArrayList<String> menuChoice, String search) {
        var totalResults = new ArrayList<MusicObject>();
        for (String choice : menuChoice) {
            totalResults.addAll(getDataList(choice, search));
        }
        return totalResults;
    }
}
