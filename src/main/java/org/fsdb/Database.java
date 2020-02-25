package org.fsdb;

import com.github.cliftonlabs.json_simple.*;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.classes.Tuple;
import org.fsdb.query.Query;
import org.fsdb.query.QueryResult;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.fsdb.MusicClassEnum.*;

public class Database {
    private String dbName;
    private static final Database INSTANCE = new Database();

    public static Database getInstance() {
        return Database.INSTANCE;
    }

    public void create(String dbName) {
        this.dbName = dbName;
        FileSystem.createDir(dbName);
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
                boolean wasDeleted = FileSystem.delete(found.first);
                result = new QueryResult(wasDeleted, query.action, found.second);
                break;
            }
            case CREATE: {
                String fileName = rootDir + "/" + query.values.get("id");
                String data = serializeData(query.values);
                if (FileSystem.exists(fileName)) {
                    FileSystem.createDir(rootDir);
                }
                FileSystem.writeFile(fileName, data);
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

    private String serializeData(HashMap<String, String> data) {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            str.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
        }
        return str.toString().strip();
    }

    private Tuple<String, HashMap<String, String>> findWith(String root, String field, String value) {
        File[] dirFiles = FileSystem.getDirFiles(root);

        String fileName = null;
        HashMap<String, String> result = null;

        for (File file : Objects.requireNonNull(dirFiles)) {
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

    private Tuple<String, ArrayList<HashMap<String, String>>> findAllWith(String root, String field, String value) {
        File[] dirFiles = FileSystem.getDirFiles(root);

        String fileName = null;
        var resultArray = new ArrayList<HashMap<String, String>>();

        for (File file : Objects.requireNonNull(dirFiles)) {
            String content = FileSystem.readFile(file.getPath());
            fileName = file.getPath();
            var data = deserializeData(content);
            if (data.get(field).equals(value)) {
                resultArray.add(data);
            }
        }
        return new Tuple<>(fileName, resultArray);
    }

    private void createSubdirsFromJSON(List<String> pathNames, String dbDir) {
        for (String path : pathNames) {
            if (!FileSystem.exists(path)) continue;
            String[] split = path.split("\\.");

            List<String> pathList;
            pathList = Arrays.asList(split);

            String[] dbPathSplit = pathList.get(0).split("/");

            List<String> getFilePath = Arrays.asList(dbPathSplit);
            String filePath = dbDir + "/" + getFilePath.get(getFilePath.size() - 1);

            if (!FileSystem.exists(filePath)) {
                FileSystem.createDir(filePath);
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

                FileSystem.writeFile(fileName, sb.toString());
            }
        } catch (IOException | JsonException e) {
            e.printStackTrace();
        }
    }

    public List<String> getClassFolders() {
        File[] subFolders = FileSystem.getSubFolders(Database.getInstance().getDbName());
        return Arrays.stream(subFolders).map(File::getName).collect(Collectors.toList());
    }

    public ArrayList<MusicObject> search(String subPath, String search) {
        ArrayList<MusicObject> results = new ArrayList<>();

        String path = getDbName() + "/" + subPath;
        File[] fileArr = FileSystem.getDirFiles(path);

        for (File file : Objects.requireNonNull(fileArr)) {
            String url = file.toString();
            String data = FileSystem.readFile(url);

            HashMap<String, String> dataMap = deserializeData(data);
            MusicObject result = Converter.getObjectFromHashMap(subPath, dataMap);

            String searchString = "";
            if (getType(result).first == ARTIST.ordinal()) {
                Artist obj = (Artist) result;
                searchString = obj.getName();
            } else if (getType(result).first == ALBUM.ordinal()) {
                Album obj = (Album) result;
                searchString = obj.getName();
            } else if (getType(result).first == SONG.ordinal()) {
                Song obj = (Song) result;
                searchString = obj.getTitle();
            }

            if (searchString.toLowerCase().contains(search.toLowerCase()))
                results.add(result);
        }
        return results;
    }

    public Tuple<Integer, String> getType(MusicObject musicObject) {
        if (musicObject.getClass().equals(Artist.class)) return new Tuple<>(ARTIST.ordinal(), "artists");
        else if (musicObject.getClass().equals(Album.class)) return new Tuple<>(ALBUM.ordinal(), "albums");
        else if (musicObject.getClass().equals(Song.class)) return new Tuple<>(SONG.ordinal(), "songs");
        else return new Tuple<>(-1, "");
    }


    public ArrayList<MusicObject> globalSearch(ArrayList<String> menuChoice, String search) {
        var totalResults = new ArrayList<MusicObject>();
        for (String choice : menuChoice) {
            totalResults.addAll(Database.getInstance().search(choice, search));
        }
        return totalResults;
    }

    public boolean editObjectProp(MusicObject object, String propName, String newValue) {
        var typeName = getType(object).second;
        var editResult = Database.getInstance().executeQuery(new Query()
                .from(typeName)
                .where("id", String.valueOf(object.getId()))
                .update(propName, newValue));

        return editResult.success;
    }

    public ArrayList<MusicObject> getAlbumList(int artistId) {
        ArrayList<MusicObject> results = new ArrayList<>();

        String albumUrl = "albums";
        String path = Database.getInstance().getDbName() + "/" + albumUrl;
        File[] fileArr = FileSystem.getDirFiles(path);

        for (File file : Objects.requireNonNull(fileArr)) {
            String url = file.toString();
            String data = FileSystem.readFile(url);

            HashMap<String, String> dataMap = Database.getInstance().deserializeData(data);
            MusicObject result;
            result = Converter.getObjectFromHashMap(albumUrl, dataMap);
            Album album = (Album) result;

            if (artistId == album.getArtist())
                results.add(result);
        }
        return results;
    }

    public void addObject(MusicObject object) {
        var typeName = getType(object).second;
        Database.getInstance().executeQuery(new Query().from(typeName).create(Converter.getHashMapFromObject(object)));
    }

    public String getDbName() {
        return dbName;
    }
}