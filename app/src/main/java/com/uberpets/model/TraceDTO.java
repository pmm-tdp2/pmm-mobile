package com.uberpets.model;

public class TraceDTO {
    private int userId;
    private int driverId;
    private GeograficCoordenate geograficCoordenate;

    public TraceDTO(int userId, int driverId, GeograficCoordenate geograficCoordenate) {
        this.userId = userId;
        this.driverId = driverId;
        this.geograficCoordenate = geograficCoordenate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public GeograficCoordenate getGeograficCoordenate() {
        return geograficCoordenate;
    }

    public void setGeograficCoordenate(GeograficCoordenate geograficCoordenate) {
        this.geograficCoordenate = geograficCoordenate;
    }
}
