package com.uberpets.model;

public class Person {

    private int id;
    private String name;
    private String lastName;

    public Person(int id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }
}

