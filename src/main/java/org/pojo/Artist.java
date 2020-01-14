package org.pojo;

import java.util.HashMap;

public class Artist {

    private int id;
    private String name;

    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public Artist(HashMap<String,String> queryResult) {
        this.id = Integer.parseInt(queryResult.get("id"));
        this.name = queryResult.get("name");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
