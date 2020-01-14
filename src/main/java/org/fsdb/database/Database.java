package org.fsdb.database;

import com.github.cliftonlabs.json_simple.*;
import org.fsdb.Util;
import org.fsdb.database.query.Query;
import org.fsdb.database.query.QueryResult;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import static org.fsdb.FileSystem.createDir;
import static org.fsdb.FileSystem.writeFile;

import java.io.File;
import java.util.HashMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.List;


public class Database {
    private String dbName;
    public Database() {
        List<String> dbFiles = new ArrayList<>();
        Path currentDir = Paths.get(".");
        String absPath = currentDir.toAbsolutePath().toString();
        String path[] = absPath.split("\\.");
        List<String> pathList;
        pathList = Arrays.asList(path);
        pathList.get(0);
        dbFiles.add("" + pathList.get(0) + "/songs.json");
        dbFiles.add("" + pathList.get(0) +"/albums.json");
        dbFiles.add("" + pathList.get(0) +"/artists.json");
        String dbDir = "" + pathList.get(0) + "/MusicLib";
        createDatabaseDir(dbFiles, dbDir);
    }

    public boolean create(String name) {
        return createDir(name);
    }

    public QueryResult executeQuery(Query query) {
        QueryResult result;

        switch (query.action) {
            case FETCH:
            case DELETE:
            case CREATE: {
                result = new QueryResult(true, query.action);
                break;
            }
            case UPDATE: {
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

    public HashMap<String, String> parseData(String fileData) {
        List<String> lines = Util.stringToLines(fileData);
        if (lines == null) return null;

        HashMap<String, String> values = new HashMap<>();

        for (String line : lines) {
            String[] split = line.split("=");
            values.put(split[0], split[1]);
        }

        return values;
    }

    public void createDatabaseDir(List<String> pathNames, String dbDir){
        createDir(dbDir);
        for (String s : pathNames) {
            String a[] = s.split("\\.");

            List<String> pathList;
            pathList = Arrays.asList(a);

            String[] dbPathSplit = pathList.get(0).split("/");

            List<String> getFilePath = Arrays.asList(dbPathSplit);
            String filePath = dbDir + "/" + getFilePath.get(getFilePath.size()-1);

            createDir(filePath);
            createFileFromJSON(s,filePath);
        }
    }
    public void createFileFromJSON(String filePath, String dirPath){
        try(FileReader fileReader = new FileReader(filePath)){
            Object deserialize = Jsoner.deserialize(fileReader);
            JsonArray ja = (JsonArray) deserialize;

            for (Object ob: ja) {
                JsonObject temp = (JsonObject) ob;
                String fileName = temp.get("_id").toString();
                writeFile(dirPath+"/"+fileName, temp.toString());
            }

        }catch (IOException e) {
            e.printStackTrace();
        }catch (JsonException e) {
            e.printStackTrace();
        }

    }
}