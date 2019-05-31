package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


/**
 * This class is used when
 * - user send quotation to server of travel
 * - server send notification of travel to driver
 */

public class TravelDTO {
    private String userId;
    private String driverId;
    private final LatLng from;
    private final LatLng to;
    private final int smallPetQuantity;
    private final int mediumPetQuantity;
    private final int bigPetQuantity;
    private final boolean hasCompanion;
    private final int travelId;


    public static class TravelDTOBuilder {
        private final LatLng from;
        private final LatLng to;
        private int travelId;
        private String userId;
        private String driverId;
        private int smallPetQuantity = 0;
        private int mediumPetQuantity = 0;
        private int bigPetQuantity = 0;
        private boolean hasCompanion = false;

        public TravelDTOBuilder(LatLng from, LatLng to) {
            this.from = from;
            this.to = to;
        }

        public TravelDTOBuilder setTravelId(int travelId) {
            this.travelId = travelId;
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

        public TravelDTOBuilder setSmallPetQuantity(int smallPetQuantity) {
            this.smallPetQuantity = smallPetQuantity;
            return this;
        }

        public TravelDTOBuilder setMediumPetQuantity(int mediumPetQuantity) {
            this.mediumPetQuantity = mediumPetQuantity;
            return this;
        }

        public TravelDTOBuilder setBigPetQuantity(int bigPetQuantity) {
            this.bigPetQuantity = bigPetQuantity;
            return this;
        }

        public TravelDTOBuilder setHasCompanion(boolean hasCompanion) {
            this.hasCompanion = hasCompanion;
            return this;
        }

        public TravelDTO build() {
            return new TravelDTO(this);
        }

    }


    public TravelDTO(TravelDTOBuilder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.hasCompanion = builder.hasCompanion;
        this.smallPetQuantity = builder.smallPetQuantity;
        this.mediumPetQuantity = builder.mediumPetQuantity;
        this.bigPetQuantity = builder.bigPetQuantity;
        this.travelId = builder.travelId;
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

    public int getSmallPetQuantity() {
        return smallPetQuantity;
    }

    public int getMediumPetQuantity() {
        return mediumPetQuantity;
    }

    public int getBigPetQuantity() {
        return bigPetQuantity;
    }

    public boolean isHasCompanion() {
        return hasCompanion;
    }

    public String getDriverId() {
        return driverId;
    }

    public int getTravelId() {
        return travelId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    @Override
    public String toString(){
        return "userId: "+userId
         +" driverId: "+driverId
         +" travelId: "+travelId
         +" from: "+from.toString()
         +" to: "+to.toString()
         +" smallPetQuantity: "+smallPetQuantity
         +" mediumPetQuantity: "+mediumPetQuantity
         +" bigPetQuantity: "+bigPetQuantity
         +" hasCompanion: "+hasCompanion;

    }
}
