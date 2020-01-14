package org.fsdb.database;

import org.fsdb.FileSystem;
import org.fsdb.Util;
import org.fsdb.database.query.Query;
import org.fsdb.database.query.QueryResult;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Database {
    private Scanner userInput = new Scanner(System.in);
    private String dbName;

    public boolean create(String name) {
        dbName = name;
        return FileSystem.createDir(name);
    }



    public void init(){
        int menuSelection;
        do {
            welcomeScreen();
            menuSelection = userChoice();
            userChosenAction(menuSelection);
        } while (menuSelection != 4);
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

    public QueryResult executeQuery(Query query) {
        QueryResult result;
        String rootDir = dbName + "/" + query.rootName;

        switch (query.action) {
            case FETCH: {
                var found = findWith(rootDir, query.predicateField, query.predicateValue);
                if (found == null) {
                    result = null;
                    break;
                }
                result = new QueryResult(true, query.action, found.second);
                break;
            }
            case DELETE: {
                var found = findWith(rootDir, query.predicateField, query.predicateValue);
                if (found == null) {
                    result = null;
                    break;
                }
                boolean wasDeleted = FileSystem.delete(found.first);
                result = new QueryResult(wasDeleted, query.action);
                break;
            }
            case CREATE: {
                String fileName = rootDir + "/" + query.values.get("id");
                String data = serializeData(query.values);
                if (!FileSystem.exists(fileName)) {
                    FileSystem.createDir(rootDir);
                }
                FileSystem.writeFile(fileName, data);
                result = new QueryResult(true, query.action, query.values);
                break;
            }
            case UPDATE: {
                var found = findWith(rootDir, query.predicateField, query.predicateValue);
                if (found == null) { // handle properly
                    result = null;
                    break;
                }
                // loop over and update given values
                for (Map.Entry<String, String> entry : query.values.entrySet()) {
                    if (found.second.containsKey(entry.getKey())) {
                        found.second.put(entry.getKey(), entry.getValue());
                    }
                }
                FileSystem.writeFile(found.first, serializeData(found.second));
                result = new QueryResult(true, query.action, query.values);
                break;
            }
            default: {
                result = null;
                break;
            }
        }

        return result;
    }

    public HashMap<String, String> deserializeData(String fileData) {
        List<String> lines = Util.stringToLines(fileData);
        if (lines == null) return null;

        HashMap<String, String> values = new HashMap<>();

        for (String line : lines) {
            String[] split = line.split("=");
            values.put(split[0], split[1]);
        }

        return values;
    }

    public String serializeData(HashMap<String, String> data) {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            str.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
        }
        return str.toString().strip();
    }

    private Tuple<String, HashMap<String, String>> findWith(String root, String field, String value) {
        File[] dirFiles = FileSystem.getDirFiles(root);
        if (dirFiles == null) return null;

        String fileName = null;
        HashMap<String, String> result = null;

        for (File file : dirFiles) {
            String content = FileSystem.readFile(file.getPath());
            fileName = file.getPath();
            var data = deserializeData(content);
            if (data.get(field).equals(value)) {
                result = data;
                break;
            }
        }
        return new Tuple<>(fileName, result);
    }
}
