package main.org.fsdb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSystem {
    public static String readFile(String filePath) {
        String returnStr = "";
        try {
            returnStr = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return returnStr;
    }

    public static void writeFile(String filePath, String data) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }


    public static boolean delete(String filePath) {
        boolean isDeleted = false;
        try {
            isDeleted = Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }
}
