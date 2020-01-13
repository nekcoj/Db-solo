package org.example;

import org.fsdb.database.Database;
import org.fsdb.database.query.Query;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String dbName = "example-db";

        Database db = new Database();
        db.create(dbName);

        // create data
        var createResult = db.executeQuery(new Query()
                .from("artists")
                .create(new HashMap<>(Map.of(
                        "id", "123",
                        "name", "Dragonforce")
                )));

        if (createResult.successful) {
            System.out.println("Created data");
            System.out.println(createResult.data);
        }

        // fetch data
        var fetchResult = db.executeQuery(new Query()
                .from("artists").where("id", "123").fetch());

        if (fetchResult.successful) {
            System.out.println("Fetched data");
            System.out.println(fetchResult.data);
        }

        // update data
        var updateResult = db.executeQuery(new Query()
                .from("artists")
                .where("id", "123")
                .update("name", "Metallica"));

        if (updateResult.successful) {
            System.out.println("Updated data");
            System.out.println(updateResult.data);
        }

        // fetch data
        fetchResult = db.executeQuery(new Query()
                .from("artists").where("id", "123").fetch());

        if (fetchResult.successful) {
            System.out.println("Fetched data");
            System.out.println(fetchResult.data);
        }

        // delete data
        var deleteResult = db.executeQuery(new Query()
                .from("artists").where("id", "123").delete());

        if (deleteResult.successful)
            System.out.println("Removed data");
    }
}
