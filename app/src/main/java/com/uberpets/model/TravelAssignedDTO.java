package com.uberpets.model;

/**
 * This is user by both the user and the driver
 * is the response to confirmation of travel
 */
public class TravelAssignedDTO {
    private int travelId;
    private String time;
    private Person user;
    private Person driver;

    public TravelAssignedDTO(int travelId, String time, Person user, Person driver) {
        this.travelId = travelId;
        this.time = time;
        this.user = user;
        this.driver = driver;
    }

    public int getTravelId() {
        return travelId;
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
