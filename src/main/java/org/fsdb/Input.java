package org.fsdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Input {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

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

    public static int getIntInput() {
        // prompt again on if Integer.parseInt throws NumberFormatException inside getInt()
        try {
            return Input.getInt();
        } catch (Exception e) {
            System.out.println("Invalid menu choice, try again!");
            return getIntInput();
        }
    }
}
