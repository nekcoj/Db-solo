package org.app;

import org.fsdb.FileSystem;
import org.fsdb.Database;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.Util;

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
            System.out.print(
                    "Menu\n----------\n" +
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

    private ArrayList<MusicObject> getDataList(String subPath, String search, Database db) {
        String path = db.getDbName() + "/" + subPath;
        File[] fileArr = FileSystem.getDirFiles(path);

        int searchHits = 0;
        for (File file : Objects.requireNonNull(fileArr)) {
            String url = file.toString();
            String data = FileSystem.readFile(url);
            HashMap<String, String> dataMap = db.deserializeData(data);
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
            } else System.out.println("Error no class defined");

            if (searchString.toLowerCase().contains(search.toLowerCase())) {
                System.out.println(searchString);
                searchResult.add(result);
                searchHits++;
            }
        }

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
                break;
            case 3:
                System.out.println("Remove\n----------");
                break;
            case 4:
                System.out.println("Goodbye :(");
                break;
            default:
                userChosenAction(userChoice());
                break;
        }
    }

    private void searchMenu() {
        searchResult = new ArrayList<>();

        File[] subFolder = FileSystem.getSubFolders(database.getDbName());
        ArrayList<String> menuChoice = new ArrayList<>();

        // get folder names
        for (File file : subFolder)
            menuChoice.add(file.toString().split("\\\\")[1]);

        // print menu search choices
        for (int i = 0; i < menuChoice.size(); i++)
            System.out.println((i + 1) + ". " + Util.capitalize(menuChoice.get(i)));

        System.out.println("4. Search all.\n5. Exit to main menu.");

        int choice = userChoice() - 1;
        if (choice == EXIT) return;

        System.out.print("Search for> ");
        String search = userInput.next();

        if (choice == GLOBAL_SEARCH) globalSearch(menuChoice, search, database);
        else getDataList(menuChoice.get(choice), search, database);
    }

    private void globalSearch(ArrayList<String> menuChoice, String search, Database database) {
        for (String choice : menuChoice) {
            getDataList(choice, search, database);
        }
    }
}
