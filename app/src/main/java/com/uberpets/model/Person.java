package com.uberpets.model;

import java.io.Serializable;

public class Person implements Serializable {

    private String id;
    private String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "{" +
                "id: "+id +","
                + "name: "+name +","
                + "}";
    }
}

