package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


/**
 * This class is used when
 * - user send quotation to server of travel
 * - server send notification of travel to driver
 */

public class TravelDTO implements Serializable {
    private final String userId;
    private final String driverId;
    private final int travelID;
    private final LatLng from;
    private final LatLng to;
    private final int petAmountSmall;
    private final int petAmountMedium;
    private final int petAmountLarge;
    private final boolean hasACompanion;


    public static class TravelDTOBuilder {
        private final LatLng from;
        private final LatLng to;
        private int travelID;
        private String userId;
        private String driverId;
        private int petAmountSmall = 0;
        private int petAmountMedium = 0;
        private int petAmountLarge = 0;
        private boolean hasACompanion = false;

        public TravelDTOBuilder(LatLng from, LatLng to) {
            this.from = from;
            this.to = to;
        }

        public TravelDTOBuilder setTravelID(int travelID) {
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

        public TravelDTOBuilder setpetAmountSmall(int petAmountSmall) {
            this.petAmountSmall = petAmountSmall;
            return this;
        }

        public TravelDTOBuilder setpetAmountMedium(int petAmountMedium) {
            this.petAmountMedium = petAmountMedium;
            return this;
        }

        public TravelDTOBuilder setpetAmountLarge(int petAmountLarge) {
            this.petAmountLarge = petAmountLarge;
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
        this.petAmountSmall = builder.petAmountSmall;
        this.petAmountMedium = builder.petAmountMedium;
        this.petAmountLarge = builder.petAmountLarge;
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

    public int getpetAmountSmall() {
        return petAmountSmall;
    }

    public int getpetAmountMedium() {
        return petAmountMedium;
    }

    public int getpetAmountLarge() {
        return petAmountLarge;
    }

    public boolean isHasACompanion() {
        return hasACompanion;
    }

    public String getDriverId() {
        return driverId;
    }

    public int getTravelID() {
        return travelID;
    }

    @Override
    public String toString(){
        return "userId: "+userId
         +" driverId: "+driverId
         +" travelID: "+travelID
         +" from: "+from
         +" to: "+to
         +" petAmountSmall: "+petAmountSmall
         +" petAmountMedium: "+petAmountMedium
         +" petAmountLarge: "+petAmountLarge
         +" hasACompanion: "+hasACompanion;

    }
}
