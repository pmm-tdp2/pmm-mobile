package com.uberpets.model;

public class Driver {

    private String license;
    private String firstName;
    private String lastName;

    public Driver(String license, String firstName, String lastName) {
        this.license = license;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

