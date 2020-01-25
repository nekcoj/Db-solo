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

    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }
}
