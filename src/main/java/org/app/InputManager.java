package org.app;

import org.fsdb.FileSystem;
import org.fsdb.Database;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.query.Query;

import java.io.File;
import java.util.*;

class InputManager {
    private static final int GLOBAL_SEARCH = 3;
    private static final int EXIT = 4;

    private static final int ARTIST = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 2;

    private Scanner userInput;
    private Database database;
    private ArrayList<MusicObject> searchResult;

    InputManager(Database database) {
        this.database = database;
        this.userInput = new Scanner(System.in);
    }

    void showMenu() {
        int menuSelection;
        do {
            System.out.println("Vad vill du göra? \n1. Sök\n2. Lägg till låt\n3. Ta bort låt\n4. Avsluta");
            menuSelection = userChoice();
            userChosenAction(menuSelection);
        } while (menuSelection != 4);
    }

    private ArrayList<MusicObject> getDataList(String subPath, String search, Database db, boolean printResult) {
        String path = db.getDbName() + "/" + subPath;
        File[] fileArr = FileSystem.getDirFiles(path);

        int searchHits = 0;
        for (File f : Objects.requireNonNull(fileArr)) {
            String url = f.toString();
            String data = FileSystem.readFile(url);
            HashMap<String, String> dataMap = db.deserializeData(data);
            MusicObject result = getNameOfData(subPath, dataMap);
            String string = "";

            if (getClass(result) == ARTIST) {
                Artist a = (Artist) result;
                string = a.getName();
            } else if (getClass(result) == ALBUM) {
                Album a = (Album) result;
                string = a.getName();
            } else if (getClass(result) == SONG) {
                Song a = (Song) result;
                string = a.getTitle();
            } else System.out.println("Error no class defined");

            if (string.toLowerCase().contains(search.toLowerCase())) {
                searchResult.add(result);
                searchHits++;
                if (printResult) System.out.println(string);
            }
        }
        if (printResult)
            System.out.printf("Found %d %s \n", searchHits, searchHits > 1 ? subPath : subPath.substring(0, subPath.length() - 1));
        return searchResult;
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
        try {
            return Integer.parseInt(userInput.next());
        } catch (Exception e) {
            System.out.println("Felaktig inmatning, försök igen");
            return userChoice();
        }
    }

    private void userChosenAction(int userChoice) {
        switch (userChoice) {
            case 1:
                System.out.println("Sök");
                searchMenu();
                break;
            case 2:
                System.out.println("Lägga till låt");
                addSong();
                break;
            case 3:
                System.out.println("Ta bort");
                break;
            case 4:
                System.out.println("Hej då!");
                break;
            default:
                System.out.println("Felaktig inmatning, försök igen!");
                userChosenAction(userChoice());
        }
    }

    private void addSong() {
        String path = "songs";
        searchResult = new ArrayList<>();
        System.out.println("Write song name to add");
        String songName = userInput.next();
        getDataList(path, songName, database, true);
        if (searchResult.size() > 0) {
            System.out.println("Continue to create song anyway? Y/N");
            String input = userInput.next();
            if (!input.toLowerCase().equals("y")) return;
        }
        Song song = new Song(generateID(path), 9000, songName, 9000, "Metal");
        HashMap<String, String> mapSong = song.mapObject();
        database.executeQuery(new Query().from(path).create(mapSong));
        System.out.printf("%s %s has been created!\n", path.substring(0, path.length() - 1), songName);
        //TODO
        // Search/Create Artist (Create Artist method) (int id, int album, String title, int track, String genre)
        // Search/Create Album  (Create Album method)

    }

    private int generateID(String type) {
        int newId = -1;
        ArrayList<MusicObject> list = getDataList(type, "", database, false);
        for (MusicObject musicObject : list) {
            if (musicObject.getId() > newId) newId = musicObject.getId();
        }
        return newId + 1;
    }

    private void searchMenu() {
        searchResult = new ArrayList<>();
        File[] subFolder = FileSystem.getSubFolders(database.getDbName());
        ArrayList<String> menuChoice = new ArrayList<>();
        for (File f : subFolder) {
            menuChoice.add(f.toString().split("\\\\")[1]);
        }

        for (int i = 0; i < menuChoice.size(); i++) {
            System.out.println((i + 1) + ". " + menuChoice.get(i));
        }
        System.out.println("4. search all.");
        System.out.println("5. exit to main menu.");

        int choice = userChoice() - 1;
        if (choice == EXIT) return;
        System.out.println("Write search term");
        String search = userInput.next();

        if (choice == GLOBAL_SEARCH) globalSearch(menuChoice, search, database);
        else getDataList(menuChoice.get(choice), search, database, true);
    }

    private void globalSearch(ArrayList<String> menuChoice, String search, Database database) {
        for (String choice : menuChoice) {
            getDataList(choice, search, database, true);
        }
    }
}
