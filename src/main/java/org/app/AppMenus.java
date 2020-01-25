package org.app;

import org.app.menu.Menu;
import org.app.pojo.MusicObject;
import org.fsdb.Input;
import org.fsdb.Util;

import java.util.ArrayList;

public class AppMenus {
    private App app;

    public AppMenus(App app) {
        this.app = app;
    }

    public void handle() {
        var choice = new Menu()
                .setMenuTitle("Music Library")
                .addMenuItem("Search", "search")
                .addMenuItem("Add", "add")
                .addMenuItem("Remove", "remove")
                .addMenuItem("Edit", "edit")
                .addMenuItem("Quit", "quit")
                .show()
                .prompt("Enter option> ");

        switch (choice.key) {
            case "search":  searchMenu();   break;
            case "add":     addMenu();      break;
            case "remove":  removeMenu();   break;
            case "edit":    editMenu();     break;
            case "quit":
                System.out.println("Goodbye :(");
                break;
        }
        if (!choice.key.equals("quit")) handle();
    }

    private void addMenu() {
        System.out.println("Not yet implemented");
    }

    private void removeMenu() {
        System.out.println("Not yet implemented");
    }

    public void editMenu() {
        System.out.println("Not yet implemented");
    }

    public void searchMenu() {
        var menu = getClassMenu("Search Menu");

        var choice = menu
                .addMenuItem("Search all", "all")
                .addMenuItem("Return to main menu", "return")
                .show()
                .prompt("Enter option> ");


        if (choice.key.equals("return")) return;

        // ask for search term
        System.out.printf("Search %s> ", choice.key);
        var searchTerm = Input.getLine();

        var classFolders = (ArrayList<String>)app.getClassFolders();
        ArrayList<MusicObject> results;
        // global search
        if (choice.key.equals("all"))
            results = app.globalSearch(classFolders, searchTerm);
        else
            results = app.getDataList(choice.key, searchTerm);

        // print found results
        app.printResults(app.sortResults(results), true, true);
    }

    private Menu getClassMenu(String title) {
        var classFolders = app.getClassFolders();

        var menu = new Menu().setMenuTitle(title);
        for (String classFolder : classFolders)
            menu.addMenuItem(Util.capitalize(classFolder), classFolder);

        return menu;
    }
}
