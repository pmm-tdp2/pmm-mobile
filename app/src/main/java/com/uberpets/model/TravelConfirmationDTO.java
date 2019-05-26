package com.uberpets.model;


/**
 * DTO Confirmation of travel used by user and driver
 * user when request a travel
 * driver when accept a travel
 */

public class TravelConfirmationDTO {
    private int travelID;
    private String rol;
    private String id;
    private boolean accept;

    public TravelConfirmationDTO(int travelID, String rol, String id, boolean accept) {
        this.travelID = travelID;
        this.rol = rol;
        this.id = id;
        this.accept = accept;
    }

    public String getId() {
        return id;
    }

    public int getTravelID() {
        return travelID;
    }

    public String getRol() {
        return rol;
    }
}
