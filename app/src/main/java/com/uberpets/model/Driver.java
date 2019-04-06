package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;

public class Driver {

    private LatLng currentLocation;
    private String photo;
    private String name;

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Driver(LatLng currentLocation, String photo, String name) {
        this.currentLocation = currentLocation;
        this.photo = photo;
        this.name = name;
    }
}

