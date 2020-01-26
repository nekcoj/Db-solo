package org.app.menu;

import org.app.App;
import org.fsdb.Input;
import org.fsdb.Util;

public abstract class AppMenu {
    protected App app;

    public AppMenu(App app) {
        this.app = app;
    }

    public abstract void handle();

    protected Menu getClassMenu(String title) {
        var classFolders = app.getClassFolders();

        var menu = new Menu().setMenuTitle(title);
        for (String classFolder : classFolders)
            menu.addMenuItem(Util.capitalize(classFolder), classFolder);

        return menu;
    }

    protected static boolean promptYesNo(String msg) {
        System.out.print(msg);
        System.out.print(" [y/n]: ");
        var input = Input.getLine();

        if (input.equalsIgnoreCase("y")) return true;
        else if (input.equalsIgnoreCase("n")) return false;
        else return promptYesNo(msg);
    }
}