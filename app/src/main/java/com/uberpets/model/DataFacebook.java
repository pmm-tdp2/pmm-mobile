package com.uberpets.model;

import java.io.Serializable;

public class DataFacebook implements Serializable {

    private final String pictureUrl;
    private final String name;
    private final int amountFriends;
    private final int timeActive;
    private final String idFacebook;

    public DataFacebook(DataFacebookBuilder builder) {
        this.pictureUrl = builder.pictureUrl;
        this.name = builder.name;
        this.amountFriends = builder.amountFriends;
        this.timeActive = builder.timeActive;
        this.idFacebook = builder.idFacebook;
    }

    public DataFacebook(String pictureUrl, String name, int amountFriends, int timeActive,String idFacebook) {
        this.pictureUrl = pictureUrl;
        this.name = name;
        this.amountFriends = amountFriends;
        this.timeActive = timeActive;
        this.idFacebook = idFacebook;
    }

    public static class DataFacebookBuilder {
        private String pictureUrl="";
        private String name="";
        private int amountFriends=0;
        private int timeActive=0;
        private final String idFacebook;

        public DataFacebookBuilder(String idFacebook) {
            this.idFacebook = idFacebook;
        }

        public DataFacebookBuilder setPictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
            return this;
        }

        public DataFacebookBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public DataFacebookBuilder setAmountFriends(int amountFriends) {
            this.amountFriends = amountFriends;
            return this;
        }

        public DataFacebookBuilder setTimeActive(int timeActive) {
            this.timeActive = timeActive;
            return this;
        }

        public DataFacebook build(){ return new DataFacebook(this);}
    }




    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getName() {
        return name;
    }

    public int getAmountFriends() {
        return amountFriends;
    }

    public int getTimeActive() {
        return timeActive;
    }

    public String getIdFacebook() {
        return idFacebook;
    }
}
