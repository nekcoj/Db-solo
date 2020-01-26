package org.fsdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Input {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String getLine(boolean allowBlank) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error with Input.getLine");
        }
    }

    public static String getLine() {
        try {
            String in = "";
            while (in.isBlank()) in = reader.readLine();
            return in.strip();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error with Input.getLine");
        }
    }

    public static int getInt() {
        return Integer.parseInt(getLine().split("\\s+")[0]);
    }
}
