package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;

public class Travel {

    private String userId;
    private LatLng from;
    private LatLng to;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LatLng getFrom() {
        return from;
    }

    public void setFrom(LatLng from) {
        this.from = from;
    }

    public LatLng getTo() {
        return to;
    }

    public void setTo(LatLng to) {
        this.to = to;
    }

    public Travel(String userId, LatLng currentLocation, LatLng destinyLocation) {
        this.userId = userId;
        this.from = currentLocation;
        this.to = destinyLocation;
    }

}
