package org.app.menu.menus;

import org.app.App;
import org.app.menu.AppMenu;

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

        // handle edits
        System.out.println("Not yet implemented.");
    }
}
