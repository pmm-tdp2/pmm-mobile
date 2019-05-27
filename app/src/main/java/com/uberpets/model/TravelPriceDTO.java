package com.uberpets.model;

public class TravelPriceDTO {
    private String price;
    private int travelId;

    public TravelPriceDTO(String price, int travelId) {
        this.price = price;
        this.travelId = travelId;
    }

    public String getPrice() {
        return price;
    }

    public int getTravelId() {
        return travelId;
    }
}
