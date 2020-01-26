package org.app.menu.menus;

import org.app.App;
import org.app.Color;
import org.app.menu.AppMenu;
import org.app.pojo.MusicObject;
import org.fsdb.Input;
import org.fsdb.classes.Tuple;
import org.fsdb.query.Query;

import java.util.ArrayList;
import java.util.List;

public class RemoveMenu extends AppMenu {
    public RemoveMenu(App app) {
        super(app);
    }

    @Override
    public void handle() {
        var choice = getClassMenu("Remove Menu")
                .addMenuItem("Return to main menu", "return")
                .show()
                .prompt("Enter option> ");

        if (choice.key.equals("return")) return;

        System.out.printf("Search %s> ", choice.key);

        var searchTerm = Input.getLine();

        ArrayList<MusicObject> results;
        results = app.getDataList(choice.key, searchTerm);

        app.printResults(app.sortResults(results), true);
        if (results.size() == 0) return;

        System.out.print("Enter [index] to remove or press [0] to return to main menu> ");

        var userInput = Input.getInt();
        if(userInput == 0) return;
        else if(userInput > results.size()) return;
        else {
            var typeName = app.getClassName(results.get(userInput - 1));
            if (typeName == null) {
                System.out.println("Could not remove.");
                return;
            }
            if (typeName.equals("albums")) {
                System.out.println("Deep removing album");
                app.deepRemoveAlbum(results.get(userInput - 1).getId());
            } else if (typeName.equals("artists")) {
                System.out.println("Deep removing artist");
                app.deepRemoveArtist(results.get(userInput - 1).getId());
            } else {
                // FIX: Has an index bug
                String searchId = String.valueOf((results.get(userInput - 1)).getId());
                var deleteResult = app.database.executeQuery(new Query().from(typeName).where("id", searchId).delete());

                if (deleteResult.success) {
                    if (typeName.equals("songs"))
                        System.out.printf("Successfully removed %s\n", Color.printSongColor(deleteResult.data.get("title")));
                    else
                        System.out.printf("Successfully removed %s\n", Color.printArtistColor(deleteResult.data.get("name")));
                }
            }
        }
    }
}