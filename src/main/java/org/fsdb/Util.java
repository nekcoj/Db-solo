package org.fsdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Util {
    /**
     * Takes a string and converts it to a List of strings, separated by newline.
     * @param input String input.
     * @return List with lines.
     */
    public static List<String> stringToLines(String input) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(input.strip()))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            return lines;
        } catch (IOException e) {
            return null;
        }
    }
}
