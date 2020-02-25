package org.app.menu.menus;

import org.app.App;
import org.app.menu.AppMenu;
import org.app.pojo.Album;
import org.app.pojo.Artist;
import org.app.pojo.MusicObject;
import org.app.pojo.Song;
import org.fsdb.Database;
import org.fsdb.Input;
import org.fsdb.Printer;

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
        results = Database.getInstance().search(choice.key, searchTerm);

        Printer.printResults(Printer.sortResults(results), true);
        if (results.size() == 0) return;

        System.out.print("Enter [index] to remove or press [0] to return to main menu> ");

        int removeIndex;

        do removeIndex = Input.getIntInput();
        while (removeIndex < 0 || removeIndex > results.size());
        if(removeIndex == 0) return;

        var typeName = Database.getInstance().getType(results.get(removeIndex - 1)).second;
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