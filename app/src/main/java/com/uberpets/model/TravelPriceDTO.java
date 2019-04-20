package com.uberpets.model;

public class TravelPriceDTO {
    private String price;
    private String travelID;

    public TravelPriceDTO(String price, String travelID) {
        this.price = price;
        this.travelID = travelID;
    }

    public String getPrice() {
        return price;
    }

    public String getTravelID() {
        return travelID;
    }
}
