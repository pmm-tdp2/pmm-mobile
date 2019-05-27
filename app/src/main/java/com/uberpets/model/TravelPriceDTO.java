package com.uberpets.model;

public class TravelPriceDTO {
    private String price;
    private int id;

    public TravelPriceDTO(String price, int id) {
        this.price = price;
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public int getTravelId() {
        return id;
    }

    public String toString() {
        return "price: "+price+", travelId: "+id;
    }
}
