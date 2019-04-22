package com.uberpets.model;


/**
 * DTO Confirmation of travel used by user and driver
 * user when request a travel
 * driver when accept a travel
 */

public class TravelConfirmationDTO {
    private int travelID;
    private String rol;

    public TravelConfirmationDTO(int travelID, String rol) {
        this.travelID = travelID;
        this.rol = rol;
    }

    public int getTravelID() {
        return travelID;
    }

    public String getRol() {
        return rol;
    }
}
