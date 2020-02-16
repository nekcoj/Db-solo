package org.app;

import org.app.menu.menus.MainMenu;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        App app = new App("MusicDB");
        var menu = new MainMenu(app);
        menu.handle();
    }
}
