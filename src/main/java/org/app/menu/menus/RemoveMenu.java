package org.app.menu.menus;

import org.app.App;
import org.app.menu.AppMenu;

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

        System.out.println("Not yet implemented.");
    }
}
