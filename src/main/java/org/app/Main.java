package org.app;

import org.app.menu.Menu;
import org.fsdb.Util;

public class Main {
    public static void main(String[] args) {
        Menu testMenu = new Menu();

        testMenu.setMenuTitle("Menu title");
        testMenu.addMenuItem("Option sssssssssssssssssssssssssssssssss 1");
        testMenu.addMenuItem("Option asda asdsa 2");
        testMenu.addMenuItem("Option  asdasdadadadad 3");

        testMenu.show();
        var choice = testMenu.prompt("Enter option> ");

        Util.clearScreen();
        System.out.println("You chose option " + choice);

//        String dbName = System.getenv("APP_DB");
//        var app = new App(dbName);
//        app.init();
//        app.show();
    }
}
