package com.uberpets.model;

public class RatingDTO {
    private final String id;
    private final String fromId;
    private final String toId;
    private final int travelId;
    private final float value;
    private final String comments;
    
    public static class RatingDTOBuilder {
        private String id;
        private String fromId;
        private String toId;
        private int travelId;
        private float value;
        private String comments;

        public RatingDTOBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public RatingDTOBuilder setFromId(String fromId) {
            this.fromId = fromId;
            return this;
        }

        public RatingDTOBuilder setToId(String toId) {
            this.toId = toId;
            return this;
        }

        public RatingDTOBuilder setTravelId(int travelId) {
            this.travelId = travelId;
            return this;
        }

        public RatingDTOBuilder setValue(Float value) {
            this.value = value;
            return this;
        }

        public RatingDTOBuilder setComments(String comments) {
            this.comments = comments;
            return this;
        }

        public RatingDTO build(){
            return new RatingDTO(this);
        }
    }


    public RatingDTO(RatingDTOBuilder builder) {
        this.id = builder.id;
        this.fromId = builder.fromId;
        this.toId = builder.toId;
        this.travelId = builder.travelId;
        this.value = builder.value;
        this.comments = builder.comments;
    }
}
