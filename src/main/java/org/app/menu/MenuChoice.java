package org.app.menu;

public class MenuChoice {
    public String text;
    public String key;
    public int index;

    public MenuChoice(String text, int idx, String key) {
        this.text = text;
        this.key = key;
        index = idx;
    }
}
