package org.app;

import org.app.menu.Menu;

public class Main {
    public static void main(String[] args) {
        var choice = new Menu()
                .setMenuTitle("Music Library")
                .addMenuItem("Search", "s")
                .addMenuItem("Add", "a")
                .addMenuItem("Remove", "r")
                .addMenuItem("Edit", "e")
                .addMenuItem("Quit", "q")
                .show()
                .prompt("Enter option> ");

        switch (choice.key) {
            case "s":
                System.out.println("Selected search");
                break;
            case "a":
                System.out.println("Selected add");
                break;
            case "r":
                System.out.println("Selected remove");
                break;
            case "e":
                System.out.println("Selected edit");
                break;
            case "q":
                System.out.println("Goodbye :)");
                break;
            default:
                System.out.println("Unknown menu choice!");
                break;
        }

//        String dbName = System.getenv("APP_DB");
//        var app = new App(dbName);
//        app.init();
//        app.show();
    }
}
