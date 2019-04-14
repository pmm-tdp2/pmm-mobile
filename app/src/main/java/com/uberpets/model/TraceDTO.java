package com.uberpets.model;

public class TraceDTO {
    private String userId;
    private Sring driverId;
    private GeograficCoordenate geograficCoordenate;

    public TraceDTO(String userId, Sring driverId, GeograficCoordenate geograficCoordenate) {
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

    public Sring getDriverId() {
        return driverId;
    }

    public void setDriverId(Sring driverId) {
        this.driverId = driverId;
    }

    public GeograficCoordenate getGeograficCoordenate() {
        return geograficCoordenate;
    }

    public void setGeograficCoordenate(GeograficCoordenate geograficCoordenate) {
        this.geograficCoordenate = geograficCoordenate;
    }
}
