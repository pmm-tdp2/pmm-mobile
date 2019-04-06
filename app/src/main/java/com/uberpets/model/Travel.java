package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;

public class Travel {

    private LatLng currentLocation;
    private LatLng destinyLocation;

    public Travel(LatLng currentLocation, LatLng destinyLocation) {
        this.currentLocation = currentLocation;
        this.destinyLocation = destinyLocation;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LatLng getDestinyLocation() {
        return destinyLocation;
    }

    public void setDestinyLocation(LatLng destinyLocation) {
        this.destinyLocation = destinyLocation;
    }

    @Override
    public String toString() {
        return "Travel{" +
                "currentLatitude:" + currentLocation.latitude +
                ", currentLongitude:" + currentLocation.longitude +
                ", destinyLatitude:" + destinyLocation.latitude +
                ", destinyLongitude:" + destinyLocation.longitude +
                '}';
    }
}
