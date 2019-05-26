package com.uberpets.model;

public class TraceDTO {
    private String userId;
    private String driverId;
    private GeograficCoordenate geograficCoordenate;

    public TraceDTO(String userId, String driverId, GeograficCoordenate geograficCoordenate) {
        this.userId = userId;
        this.driverId = driverId;
        this.geograficCoordenate = geograficCoordenate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public GeograficCoordenate getGeograficCoordenate() {
        return geograficCoordenate;
    }

    public void setGeograficCoordenate(GeograficCoordenate geograficCoordenate) {
        this.geograficCoordenate = geograficCoordenate;
    }
}
