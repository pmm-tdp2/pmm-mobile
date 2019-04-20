package com.uberpets.model;


/**
 * DTO Confirmation of travel used by user and driver
 * user when request a travel
 * driver when accept a travel
 */

public class TravelConfirmationDTO {
    private String travelID;
    private String rol;

    public TravelConfirmationDTO(String travelID, String rol) {
        this.travelID = travelID;
        this.rol = rol;
    }

    public String getTravelID() {
        return travelID;
    }

    public String getRol() {
        return rol;
    }
}
