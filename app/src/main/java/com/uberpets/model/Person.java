package com.uberpets.model;

public class Person {

    private String id;
    private String name;
    private String lastName;

    public Person(String id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }
}

