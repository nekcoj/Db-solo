package org.fsdb.database;

import com.github.cliftonlabs.json_simple.*;
import org.fsdb.FileSystem;
import org.fsdb.Util;
import org.fsdb.database.query.Query;
import org.fsdb.database.query.QueryResult;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static org.fsdb.FileSystem.createDir;
import static org.fsdb.FileSystem.writeFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private String dbName;

    public boolean create(String name) {
        dbName = name;
        return createDir(name);
    }

    public void loadJsonFile(String filePath) {
        createSubdirsFromJSON(List.of(filePath), dbName);
    }

    public void loadJsonFiles(List<String> filePaths) {
        createSubdirsFromJSON(filePaths, dbName);
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

    private void createSubdirsFromJSON(List<String> pathNames, String dbDir) {
        for (String s : pathNames) {
            String[] a = s.split("\\.");

            List<String> pathList;
            pathList = Arrays.asList(a);

            String[] dbPathSplit = pathList.get(0).split("/");

            List<String> getFilePath = Arrays.asList(dbPathSplit);
            String filePath = dbDir + "/" + getFilePath.get(getFilePath.size() - 1);

            createDir(filePath);
            createFileFromJSON(s, filePath);
        }
    }

    private void createFileFromJSON(String filePath, String dirPath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            JsonArray ja = (JsonArray)Jsoner.deserialize(fileReader);

            for (Object ob : ja) {
                JsonObject temp = (JsonObject) ob;
                String fileName = temp.get("id").toString();

                StringBuilder sb = new StringBuilder();
                for (var entry : temp.entrySet())
                    sb.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));

                writeFile(dirPath + "/" + fileName, sb.toString());
            }

        } catch (IOException | JsonException e) {
            e.printStackTrace();
        }
    }
}