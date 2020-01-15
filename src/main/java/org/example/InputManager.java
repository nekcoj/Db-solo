package org.example;

import org.fsdb.FileSystem;
import org.fsdb.database.Database;
import org.pojo.Album;
import org.pojo.Artist;
import org.pojo.MusicObject;
import org.pojo.Song;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.*;

public class InputManager {
    private Scanner userInput;
    private Database database;


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



    public static ArrayList<MusicObject> getDataList(String database, String subPath, String search, Database db ){
        String path = database + "/" + subPath;
        File[] fileArr = FileSystem.getDirFiles(path);
        ArrayList<MusicObject> searchResult = new ArrayList<>();
        for (File f: fileArr) {
            String url = f.toString();
            String data = FileSystem.readFile(url);
            HashMap<String,String> dataMap = db.deserializeData(data);
            MusicObject result = getNameOfData(subPath,dataMap);
            String string = "";
            if(getClass(result) == 0){
                Artist a = (Artist) result;
                string = a.getName();
            }else if(getClass(result) == 1){
                Album a = (Album) result;
                string = a.getName();
            }else if(getClass(result) == 2){
                Song a = (Song) result;
                string = a.getTitle();
            }else System.out.println("Error no class defined");

            if(string.toLowerCase().contains(search.toLowerCase())) {
                System.out.println(string);
                searchResult.add(result);
            }
        }

        return searchResult;
    }

    private static int getClass(MusicObject musicObject){
        if(musicObject.getClass().equals(Artist.class)) return 0;
        else if (musicObject.getClass().equals(Album.class)) return 1;
        else if (musicObject.getClass().equals(Song.class)) return 2;
        else return -1;
    }

    private static MusicObject getNameOfData(String subPath, HashMap<String, String> dataMap) {
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
        List<File> subFolder = Arrays.asList(FileSystem.getSubFolders(database.getDbName()));
        ArrayList<String> menuChoice = new ArrayList<>();
        for (File f: subFolder) {
            menuChoice.add(f.toString().split("\\\\")[1]);
        }
        for(int i = 0; i < menuChoice.size(); i++){
            System.out.println((i + 1) + ". " + menuChoice.get(i));
        }

        int choice = userChoice() -1;

        System.out.println("Write search term");
        String search = userInput.next();

        getDataList(database.getDbName(),menuChoice.get(choice),search,database);

    }

}
