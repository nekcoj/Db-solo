package org.app;

public class Main {
    public static void main(String[] args) {
        String dbName = System.getenv("APP_DB");
        var app = new App(dbName);
        var appMenu = new AppMenus(app);
        appMenu.handle();
    }
}
