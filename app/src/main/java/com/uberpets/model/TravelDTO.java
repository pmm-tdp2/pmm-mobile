package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;


/**
 * This class is used when
 * - user send quotation to server of travel
 * - server send notification of travel to driver
 */

public class TravelDTO {
    private final String userId;
    private final String driverId;
    private final String travelID;
    private final LatLng from;
    private final LatLng to;
    private final int petSmallAmount;
    private final int petMediumAmount;
    private final int petLargeAmount;
    private final boolean hasACompanion;


    public static class TravelDTOBuilder {
        private final LatLng from;
        private final LatLng to;
        private String travelID = "";
        private String userId = "";
        private String driverId = "";
        private int petSmallAmount = 0;
        private int petMediumAmount = 0;
        private int petLargeAmount = 0;
        private boolean hasACompanion = false;

        public TravelDTOBuilder(LatLng from, LatLng to) {
            this.from = from;
            this.to = to;
        }

        public TravelDTOBuilder setTravelID(String travelID) {
            this.travelID = travelID;
            return this;
        }

        public TravelDTOBuilder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public TravelDTOBuilder setDriverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public TravelDTOBuilder setPetSmallAmount(int petSmallAmount) {
            this.petSmallAmount = petSmallAmount;
            return this;
        }

        public TravelDTOBuilder setPetMediumAmount(int petMediumAmount) {
            this.petMediumAmount = petMediumAmount;
            return this;
        }

        public TravelDTOBuilder setPetLargeAmount(int petLargeAmount) {
            this.petLargeAmount = petLargeAmount;
            return this;
        }

        public TravelDTOBuilder setHasACompanion(boolean hasACompanion) {
            this.hasACompanion = hasACompanion;
            return this;
        }

        public TravelDTO build() {
            return new TravelDTO(this);
        }

    }


    public TravelDTO(TravelDTOBuilder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.hasACompanion = builder.hasACompanion;
        this.petSmallAmount = builder.petLargeAmount;
        this.petMediumAmount = builder.petMediumAmount;
        this.petLargeAmount = builder.petLargeAmount;
        this.travelID = builder.travelID;
        this.userId = builder.userId;
        this.driverId = builder.driverId;
    }

    public String getUserId() {
        return userId;
    }

    public LatLng getFrom() {
        return from;
    }

    public LatLng getTo() {
        return to;
    }

    public int getPetSmallAmount() {
        return petSmallAmount;
    }

    public int getPetMediumAmount() {
        return petMediumAmount;
    }

    public int getPetLargeAmount() {
        return petLargeAmount;
    }

    public boolean isHasACompanion() {
        return hasACompanion;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getTravelID() {
        return travelID;
    }
}
