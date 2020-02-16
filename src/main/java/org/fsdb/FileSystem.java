package org.fsdb;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileSystem {
    /**
     * Reads data from a file into a string and returns it.
     *
     * @param fileName Path of the file to read from.
     * @return Returns string with the data read, or null if something went wrong.
     */
    private static String readFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes given data to a file.
     *
     * @param fileName Path of the file to write to.
     * @param data     The data to be written into the file.
     */
    private static void writeFile(String fileName, String data) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests a paths existence.
     *
     * @param filePath Path to check.
     * @return Returns true if the path exists, else returns false.
     */
    private static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Removes a file or directory with the given path.
     *
     * @param filePath Path to remove.
     * @return Returns true if the file/directory was removed, else returns false.
     */
    private static boolean delete(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a directory at the given path.
     *
     * @param dirName Path of the directory to create.
     * @return Returns true if the directory was created.
     */
    public static boolean createDir(String dirName) {
        return new File(dirName).mkdir();
    }

    /**
     * Creates a directory at the given path.
     *
     * @param dirName      Path of the directory to create.
     * @param createNested If all missing directories should be created as well.
     * @return Returns true if the directory was created.
     */
    public static boolean createDir(String dirName, boolean createNested) {
        return new File(dirName).mkdirs();
    }

    /**
     * Gets all file paths in a directory and turns them into a java File array.
     *
     * @param dirName Path of the directory to get files from.
     * @return Returns array of files found or null if something went wrong.
     */
    public static File[] getDirFiles(String dirName) {
        try (Stream<Path> paths = Files.walk(Paths.get(dirName))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toArray(File[]::new);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static File[] getSubFolders(String databasePath){
        return new File(databasePath).listFiles(File::isDirectory);
    }
}
