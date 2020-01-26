package org.app.menu.menus;

import org.app.App;
import org.app.Color;
import org.app.menu.AppMenu;
import org.fsdb.Input;

public class EditMenu extends AppMenu {
    public EditMenu(App app) {
        super(app);
    }

    @Override
    public void handle() {
        var choice = getClassMenu("Edit Menu")
                .addMenuItem("Return to main menu", "return")
                .show()
                .prompt("Enter option> ");

        if (choice.key.equals("return")) return;
        editMenu(choice.key);
    }

    private void editMenu(String typeName) {
        var singularName = typeName.substring(0, typeName.length() - 1);

        System.out.printf("Search for %s to edit> ", singularName);
        var objectName = Input.getLine();

        var objects = app.sortResults(app.search(typeName, objectName));

        if (objects.size() == 0) {
            System.out.println("No results found.");
            return;
        }

        app.printResults(objects, true);
        System.out.print("Enter index to edit> ");
        var index = app.getIntInput();

        if(index < 0 || index > objects.size()){
            System.out.println("Invalid choice!");
            return;
        }

        System.out.printf("New %s name> ", singularName);
        var newName = Input.getLine();

        var object = objects.get(index - 1);
        var wasUpdated = app.editObjectProp(object, typeName.equals("songs") ? "title" : "name", newName);

        if (wasUpdated) {
            var coloredName = newName;

            if (typeName.equals("artists")) coloredName = Color.setArtistColor(newName);
            else if (typeName.equals("albums")) coloredName = Color.setAlbumColor(newName);
            else coloredName = Color.setSongColor(newName);

            System.out.printf("Update %s %s to %s\n", singularName, object.getNameColored(), coloredName);
        } else
            System.out.printf("Something went wrong when trying to update the %s!\n", singularName);
    }
}
