package org.app.menu;

public class MenuChoice {
    public final String text;
    public final String key;
    public final int index;

    public MenuChoice(String text, int idx, String key) {
        this.text = text;
        this.key = key;
        index = idx;
    }
}
