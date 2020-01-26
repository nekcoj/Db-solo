package org.app.menu.menus;

import org.app.App;
import org.app.menu.AppMenu;
import org.app.menu.Menu;

public class MainMenu extends AppMenu {
    private SearchMenu searchMenu;
    private AddMenu addMenu;

    public MainMenu(App app) {
        super(app);

        searchMenu = new SearchMenu(app);
        addMenu = new AddMenu(app);
    }

    @Override
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
            case "search":
                searchMenu.handle();
                break;
            case "add":
                addMenu.handle();
                break;
            case "quit":
                System.out.println("Goodbye :(");
                break;
        }
        if (!choice.key.equals("quit")) handle();
    }
}
