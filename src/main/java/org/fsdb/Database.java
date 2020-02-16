package org.fsdb;

import com.github.cliftonlabs.json_simple.*;
import org.fsdb.classes.Tuple;
import org.fsdb.query.Query;
import org.fsdb.query.QueryResult;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import java.io.File;

public class Database {
    private String dbName;
    private static final Database INSTANCE = new Database();

    public static Database getInstance() {
        return Database.INSTANCE;
    }

    public void create(String dbName) throws IllegalAccessException {
        this.dbName = dbName;
        createDir(dbName);
    }

    public void loadJsonFile(String filePath) throws IllegalAccessException {
        createSubdirsFromJSON(List.of(filePath), dbName);
    }

    public void loadJsonFiles(List<String> filePaths) throws IllegalAccessException {
        createSubdirsFromJSON(filePaths, dbName);
    }


    public QueryResult executeQuery(Query query) throws IllegalAccessException {
        QueryResult result;
        String rootDir = dbName + "/" + query.rootName;

        switch (query.action) {
            case FETCH: {
                var found = findWith(rootDir, query.predicateField, query.predicateValue);
                result = new QueryResult(true, query.action, found.second);
                break;
            }
            case FETCH_ALL: {
                var found = findAllWith(rootDir, query.predicateField, query.predicateValue);
                result = new QueryResult(true, query.action);
                result.dataArray = new ArrayList<>();
                result.dataArray.addAll(found.second);
                break;
            }
            case DELETE: {
                var found = findWith(rootDir, query.predicateField, query.predicateValue);
                boolean wasDeleted = delete(found.first);
                result = new QueryResult(wasDeleted, query.action, found.second);
                break;
            }
            case CREATE: {
                String fileName = rootDir + "/" + query.values.get("id");
                String data = serializeData(query.values);
                if (exists(fileName)) {
                    createDir(rootDir);
                }
                createFile(fileName, data);
                result = new QueryResult(true, query.action, query.values);
                break;
            }
            case UPDATE: {
                var found = findWith(rootDir, query.predicateField, query.predicateValue);

                // loop over and update given values
                for (Map.Entry<String, String> entry : query.values.entrySet()) {
                    if (found.second.containsKey(entry.getKey())) {
                        found.second.put(entry.getKey(), entry.getValue());
                    }
                }
                createFile(found.first, serializeData(found.second));
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

    private String serializeData(HashMap<String, String> data) {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            str.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
        }
        return str.toString().strip();
    }

    private Tuple<String, HashMap<String, String>> findWith(String root, String field, String value) {
        File[] dirFiles = getDirFiles(root);

        String fileName = null;
        HashMap<String, String> result = null;

        for (File file : Objects.requireNonNull(dirFiles)) {
            String content = readFile(file.getPath());
            fileName = file.getPath();
            var data = deserializeData(content);
            if (data.get(field).equals(value)) {
                result = data;
                break;
            }
        }
        return new Tuple<>(fileName, result);
    }

    private Tuple<String, ArrayList<HashMap<String, String>>> findAllWith(String root, String field, String value) {
        File[] dirFiles = getDirFiles(root);

        String fileName = null;
        var resultArray = new ArrayList<HashMap<String, String>>();

        for (File file : Objects.requireNonNull(dirFiles)) {
            String content = readFile(file.getPath());
            fileName = file.getPath();
            var data = deserializeData(content);
            if (data.get(field).equals(value)) {
                resultArray.add(data);
            }
        }
        return new Tuple<>(fileName, resultArray);
    }

    private void createSubdirsFromJSON(List<String> pathNames, String dbDir) throws IllegalAccessException {
        for (String path : pathNames) {
            if (!exists(path)) continue;
            String[] split = path.split("\\.");

            List<String> pathList;
            pathList = Arrays.asList(split);

            String[] dbPathSplit = pathList.get(0).split("/");

            List<String> getFilePath = Arrays.asList(dbPathSplit);
            String filePath = dbDir + "/" + getFilePath.get(getFilePath.size() - 1);

            if (!exists(filePath)) {
                createDir(filePath);
                createFileFromJSON(path, filePath);
                System.out.printf("Loaded data from JSON file '%s'\n", path);
            }
        }
    }

    private void createFileFromJSON(String filePath, String dirPath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            JsonArray ja = (JsonArray) Jsoner.deserialize(fileReader);

            for (Object ob : ja) {
                JsonObject temp = (JsonObject) ob;
                String fileName = dirPath + "/" + temp.get("id").toString();

                StringBuilder sb = new StringBuilder();
                for (var entry : temp.entrySet())
                    sb.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));

                createFile(fileName, sb.toString());
            }
        } catch (IOException | JsonException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void createFile(String dirPath, String data) throws IllegalAccessException{
        try{
            Method method = FileSystem.class.getDeclaredMethod("writeFile", String.class, String.class);
            method.setAccessible(true);
            method.invoke(null, dirPath, data);
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void createDir(String dirPath) throws IllegalAccessException{
        try{
            Method method = FileSystem.class.getDeclaredMethod("createDir", String.class);
            method.setAccessible(true);
            method.invoke(null, dirPath);
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String filePath){
        try{
            Method method = FileSystem.class.getDeclaredMethod("exists", String.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(null, filePath);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete (String filePath){
        try{
            Method method = FileSystem.class.getDeclaredMethod("delete", String.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(null, filePath);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String readFile(String filePath){
        try{
            Method method = FileSystem.class.getDeclaredMethod("readFile", String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, filePath);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File[] getDirFiles(String dirName){
        try{
            Method method = FileSystem.class.getDeclaredMethod("getDirFiles", String.class);
            method.setAccessible(true);
            return (File[]) method.invoke(null, dirName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File[] getSubFolders(String databasePath){
        try{
            Method method = FileSystem.class.getDeclaredMethod("getSubFolders", String.class);
            method.setAccessible(true);
            return (File[]) method.invoke(null, databasePath);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDbName() {
        return dbName;
    }
}