package org.app;

import org.app.menu.menus.MainMenu;

public class Main {
    public static void main(String[] args) {
        String dbName = System.getenv("APP_DB");
        var app = new App(dbName);

        var menu = new MainMenu(app);
        menu.handle();
    }
}
