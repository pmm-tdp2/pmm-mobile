package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


/**
 * This class is used when
 * - user send quotation to server of travel
 * - server send notification of travel to driver
 */

public class CopyTravelDTO implements Serializable {
    private final String userId;
    private final int smallPetQuantity;
    private final int mediumPetQuantity;
    private final int bigPetQuantity;
    private final boolean hasCompanion;
    private final String driverId;
    private final int travelId;


    public static class CopyTravelDTOBuilder {
        private int travelId;
        private String userId;
        private String driverId;
        private int smallPetQuantity = 0;
        private int mediumPetQuantity = 0;
        private int bigPetQuantity = 0;
        private boolean hasCompanion = false;

        public CopyTravelDTOBuilder() {
        }

        public CopyTravelDTOBuilder setTravelId(int travelId) {
            this.travelId = travelId;
            return this;
        }

        public CopyTravelDTOBuilder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public CopyTravelDTOBuilder setDriverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public CopyTravelDTOBuilder setSmallPetQuantity(int smallPetQuantity) {
            this.smallPetQuantity = smallPetQuantity;
            return this;
        }

        public CopyTravelDTOBuilder setMediumPetQuantity(int mediumPetQuantity) {
            this.mediumPetQuantity = mediumPetQuantity;
            return this;
        }

        public CopyTravelDTOBuilder setBigPetQuantity(int bigPetQuantity) {
            this.bigPetQuantity = bigPetQuantity;
            return this;
        }

        public CopyTravelDTOBuilder setHasCompanion(boolean hasCompanion) {
            this.hasCompanion = hasCompanion;
            return this;
        }

        public CopyTravelDTO build() {
            return new CopyTravelDTO(this);
        }

    }


    public CopyTravelDTO(CopyTravelDTOBuilder builder) {
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

    @Override
    public String toString(){
        return "userId: "+userId
         +" driverId: "+driverId
         +" travelId: "+travelId
         +" smallPetQuantity: "+smallPetQuantity
         +" mediumPetQuantity: "+mediumPetQuantity
         +" bigPetQuantity: "+bigPetQuantity
         +" hasCompanion: "+hasCompanion;

    }
}
