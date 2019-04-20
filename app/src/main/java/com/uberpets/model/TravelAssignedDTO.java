package com.uberpets.model;

/**
 * This is user by both the user and the driver
 * is the response to confirmation of travel
 */
public class TravelAssignedDTO {
    private String travelID;
    private String time;
    private Person user;
    private Person driver;

    public TravelAssignedDTO(String travelID, String time, Person user, Person driver) {
        this.travelID = travelID;
        this.time = time;
        this.user = user;
        this.driver = driver;
    }

    public String getTravelID() {
        return travelID;
    }

    public String getTime() {
        return time;
    }

    public Person getUser() {
        return user;
    }

    public Person getDriver() {
        return driver;
    }
}
