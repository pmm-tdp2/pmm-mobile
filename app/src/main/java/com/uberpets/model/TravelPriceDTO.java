package com.uberpets.model;

public class TravelPriceDTO {
    private String price;
    private int travelID;

    public TravelPriceDTO(String price, int travelID) {
        this.price = price;
        this.travelID = travelID;
    }

    public String getPrice() {
        return price;
    }

    public int getTravelID() {
        return travelID;
    }
}
