package com.uberpets.model;

import com.google.android.gms.maps.model.LatLng;


/**
 * This class is used when
 * - user send quotation to server of travel
 * - server send notification of travel to driver
 */

public class Travel {
    private Person user;
    private Person driver;
    private final LatLng from;
    private final LatLng to;
    private final int smallPetQuantity;
    private final int mediumPetQuantity;
    private final int bigPetQuantity;
    private final boolean hasCompanion;
    private final int travelId;


    public static class TravelBuilder {
        private final LatLng from;
        private final LatLng to;
        private int travelId;
        private Person user;
        private Person driver;
        private int smallPetQuantity = 0;
        private int mediumPetQuantity = 0;
        private int bigPetQuantity = 0;
        private boolean hasCompanion = false;

        public TravelBuilder(LatLng from, LatLng to) {
            this.from = from;
            this.to = to;
        }

        public TravelBuilder setTravelId(int travelId) {
            this.travelId = travelId;
            return this;
        }

        public TravelBuilder setUser(Person user) {
            this.user = user;
            return this;
        }

        public TravelBuilder setDriver(Person driver) {
            this.driver = driver;
            return this;
        }

        public TravelBuilder setSmallPetQuantity(int smallPetQuantity) {
            this.smallPetQuantity = smallPetQuantity;
            return this;
        }

        public TravelBuilder setMediumPetQuantity(int mediumPetQuantity) {
            this.mediumPetQuantity = mediumPetQuantity;
            return this;
        }

        public TravelBuilder setBigPetQuantity(int bigPetQuantity) {
            this.bigPetQuantity = bigPetQuantity;
            return this;
        }

        public TravelBuilder setHasCompanion(boolean hasCompanion) {
            this.hasCompanion = hasCompanion;
            return this;
        }

        public Travel build() {
            return new Travel(this);
        }

    }


    public Travel(TravelBuilder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.hasCompanion = builder.hasCompanion;
        this.smallPetQuantity = builder.smallPetQuantity;
        this.mediumPetQuantity = builder.mediumPetQuantity;
        this.bigPetQuantity = builder.bigPetQuantity;
        this.travelId = builder.travelId;
        this.user = builder.user;
        this.driver = builder.driver;
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

    public int getTravelId() {
        return travelId;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Person getDriver() {
        return driver;
    }

    public void setDriver(Person driver) {
        this.driver = driver;
    }

    @Override
    public String toString(){
        return "user: "+user.toString()
         +" driver: "+driver.toString()
         +" travelId: "+travelId
         +" from: "+from.toString()
         +" to: "+to.toString()
         +" smallPetQuantity: "+smallPetQuantity
         +" mediumPetQuantity: "+mediumPetQuantity
         +" bigPetQuantity: "+bigPetQuantity
         +" hasCompanion: "+hasCompanion;

    }
}
