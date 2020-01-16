package org.example;

import org.fsdb.FileSystem;
import org.fsdb.database.Database;
import org.example.pojo.Album;
import org.example.pojo.Artist;
import org.example.pojo.MusicObject;
import org.example.pojo.Song;

import java.io.File;
import java.util.*;

public class InputManager {
    private static final int GLOBAL_SEARCH = 3;
    private static final int EXIT = 4;

    private static final int ARTIST = 0;
    private static final int ALBUM = 1;
    private static final int SONG = 2;



    private Scanner userInput;
    private Database database;
    private ArrayList<MusicObject> searchResult;


    public InputManager(Database database) {
        this.database = database;
        this.userInput = new Scanner(System.in);

        init();
    }

    private void init(){
        int menuSelection;
        do {
            welcomeScreen();
            menuSelection = userChoice();
            userChosenAction(menuSelection);
        } while (menuSelection != 4);
    }



    public  ArrayList<MusicObject> getDataList( String subPath, String search, Database db ){
        String path = db.getDbName() + "/" + subPath;
        File[] fileArr = FileSystem.getDirFiles(path);
        int searchHits = 0;
        for (File f: fileArr) {
            String url = f.toString();
            String data = FileSystem.readFile(url);
            HashMap<String,String> dataMap = db.deserializeData(data);
            MusicObject result = getNameOfData(subPath,dataMap);
            String string = "";
            if(getClass(result) == ARTIST){
                Artist a = (Artist) result;
                string = a.getName();
            }else if(getClass(result) == ALBUM){
                Album a = (Album) result;
                string = a.getName();
            }else if(getClass(result) == SONG){
                Song a = (Song) result;
                string = a.getTitle();
            }else System.out.println("Error no class defined");

            if(string.toLowerCase().contains(search.toLowerCase())) {
                System.out.println(string);
                searchResult.add(result);
                searchHits++;
            }

        }
        System.out.printf("Found %d %s \n", searchHits, searchHits > 1 ? subPath : subPath.substring(0,subPath.length()-1));

        return searchResult;
    }

    private  int getClass(MusicObject musicObject){
        if(musicObject.getClass().equals(Artist.class)) return ARTIST;
        else if (musicObject.getClass().equals(Album.class)) return ALBUM;
        else if (musicObject.getClass().equals(Song.class)) return SONG;
        else return -1;
    }

    private  MusicObject getNameOfData(String subPath, HashMap<String, String> dataMap) {
        if(subPath.equals("artists")){
            return   new Artist(dataMap);
        }else if(subPath.equals("albums")){
            return    new Album(dataMap);
        }else {
            return   new Song(dataMap);
        }

    }





    private void welcomeScreen() {
        System.out.println("Vad vill du göra? \n1. Sök\n2. Lägg till låt\n3. Ta bort låt\n4. Avsluta");
    }

    private int userChoice(){
        try {
            return Integer.parseInt(userInput.next());
        } catch (Exception e){
            System.out.println("Felaktig inmatning, försök igen");
            return userChoice();
        }
    }
    private void userChosenAction(int userChoice){
        switch (userChoice){
            case 1: System.out.println("Sök");
              searchMenu();
                break;
            case 2: System.out.println("Lägga till låt");
                break;
            case 3: System.out.println("Ta bort");
                break;
            case 4: System.out.println("Hej då!");
                break;
            default: System.out.println("Felaktig inmatning, försök igen!");
                userChosenAction(userChoice());
        }
    }


    private void searchMenu(){
        searchResult= new ArrayList<>();
        List<File> subFolder = Arrays.asList(FileSystem.getSubFolders(database.getDbName()));
        ArrayList<String> menuChoice = new ArrayList<>();
        for (File f: subFolder) {
            menuChoice.add(f.toString().split("\\\\")[1]);
        }

        for(int i = 0; i < menuChoice.size(); i++){
            System.out.println((i + 1) + ". " + menuChoice.get(i));
        }
        System.out.println("4. search all.");
        System.out.println("5. exit to main menu.");

        int choice = userChoice() -1;
        if(choice == EXIT)return;
        System.out.println("Write search term");
        String search = userInput.next();

        if(choice == GLOBAL_SEARCH)globalSearch(menuChoice,search,database);
        else getDataList(menuChoice.get(choice),search,database);



    }

    private void globalSearch(ArrayList<String> menuChoice, String search, Database database) {
        for (int i = 0 ; i < menuChoice.size(); i++){
            getDataList(menuChoice.get(i),search,database);
        }

    }

}
