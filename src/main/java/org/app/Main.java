package org.app;

public class Main {
    public static void main(String[] args) {
        String dbName = System.getenv("APP_DB");
        var app = new App(dbName);
        app.init();
        app.show();
    }
}
