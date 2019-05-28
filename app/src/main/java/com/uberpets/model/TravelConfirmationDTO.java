package com.uberpets.model;


/**
 * DTO Confirmation of travel used by user and driver
 * user when request a travel
 * driver when accept a travel
 */

public class TravelConfirmationDTO {
    private int travelId;
    private String role;
    private String id;
    private boolean accept;

    public TravelConfirmationDTO(int travelId, String role, String id, boolean accept) {
        this.travelId = travelId;
        this.role = role;
        this.id = id;
        this.accept = accept;
    }

    public String getId() {
        return id;
    }

    public int getTravelId() {
        return travelId;
    }

    public String getRole() {
        return role;
    }
}
