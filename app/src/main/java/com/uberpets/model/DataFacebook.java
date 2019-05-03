package com.uberpets.model;

import java.io.Serializable;

public class DataFacebook implements Serializable {

    private final String pictureUrl;
    private final String name;
    private final int amountFriends;
    private final int timeActive;

    public DataFacebook(DataFacebookBuilder builder) {
        this.pictureUrl = builder.pictureUrl;
        this.name = builder.name;
        this.amountFriends = builder.amountFriends;
        this.timeActive = builder.timeActive;
    }

    public DataFacebook(String pictureUrl, String name, int amountFriends, int timeActive) {
        this.pictureUrl = pictureUrl;
        this.name = name;
        this.amountFriends = amountFriends;
        this.timeActive = timeActive;
    }

    public static class DataFacebookBuilder {
        private String pictureUrl="";
        private String name="";
        private int amountFriends=0;
        private int timeActive=0;

        public DataFacebookBuilder() {
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
}
