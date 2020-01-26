package org.app.menu.menus;

import org.app.App;
import org.app.menu.AppMenu;
import org.app.pojo.MusicObject;
import org.fsdb.Input;

import java.util.ArrayList;

public class SearchMenu extends AppMenu {
    public SearchMenu(App app) {
        super(app);
    }

    @Override
    public void handle() {
        var choice = getClassMenu("Search Menu")
                .addMenuItem("Search all", "all")
                .addMenuItem("Return to main menu", "return")
                .show()
                .prompt("Enter option> ");

        if (choice.key.equals("return")) return;

        // ask for search term
        System.out.printf("Search %s> ", choice.key);
        var searchTerm = Input.getLine();

        var classFolders = (ArrayList<String>) app.getClassFolders();
        ArrayList<MusicObject> results;

        // global search
        if (choice.key.equals("all"))
            results = app.globalSearch(classFolders, searchTerm);
        else
            results = app.search(choice.key, searchTerm);

        // print found results
        app.printResults(app.sortResults(results), true);

        // ask if they want to print songs for this artist
        if (results.size() > 0 && choice.key.equals("artists")) {
            System.out.println("Do you want to print songs made by an artist?");
            System.out.print("Enter 0 to return or an index to list songs> ");

            var index = app.getIntInput();
            if (index == 0 || index > results.size()) return;
            app.printArtistSongs((results.get(index - 1).getResolvedName()));
        }
    }
}
