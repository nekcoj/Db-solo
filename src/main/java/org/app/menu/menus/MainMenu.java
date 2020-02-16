package org.app.menu.menus;

import org.app.App;
import org.app.menu.AppMenu;
import org.app.menu.Menu;

public class MainMenu extends AppMenu {
    private final SearchMenu searchMenu;
    private final AddMenu addMenu;
    private final EditMenu editMenu;
    private final RemoveMenu removeMenu;

    public MainMenu(App app) {
        super(app);

        searchMenu = new SearchMenu(app);
        addMenu = new AddMenu(app);
        editMenu = new EditMenu(app);
        removeMenu = new RemoveMenu(app);
    }

    @Override
    public void handle() throws IllegalAccessException {
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
            case "remove":
                removeMenu.handle();
                break;
            case "edit":
                editMenu.handle();
                break;
            case "quit":
                System.out.println("Goodbye :(");
                break;
        }
        if (!choice.key.equals("quit")) handle();
    }
}
