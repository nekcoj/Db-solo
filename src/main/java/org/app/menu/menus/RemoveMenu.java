package org.app.menu.menus;

import org.app.App;
import org.app.menu.AppMenu;
import org.app.pojo.MusicObject;
import org.fsdb.Input;
import java.util.ArrayList;

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

        var removeIndex = app.getIntInput();

        var typeName = app.getClassName(results.get(removeIndex - 1));
        var removeId = results.get(removeIndex - 1).getId();

        if (typeName == null) {
            System.out.println("Could not remove.");
            return;
        }

        // maybe add non deep removal of artist and album?
        if (typeName.equals("artists")) {
            var removedArtist = app.deepRemoveArtist(removeId);
            System.out.printf("Removed artist %s and all associated albums/songs.\n", removedArtist.getNameColored());
        } else if (typeName.equals("albums")) {
            var removedAlbum = app.deepRemoveAlbum(removeId);
            System.out.printf("Removed album %s and all associated songs.\n", removedAlbum.getNameColored());
        } else {
            var removedSong = app.removeSong(removeId);
            System.out.printf("Removed song %s.\n", removedSong.getTitle());
        }
    }
}