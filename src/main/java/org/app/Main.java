package org.app;

public class Main {
    public static void main(String[] args) {
        String dbName = "example-db";
        var app = new App(dbName);
        app.init();
        app.show();
    }
}
