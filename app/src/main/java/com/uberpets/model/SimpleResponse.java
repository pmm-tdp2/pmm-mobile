package com.uberpets.model;

public class SimpleResponse {
    private int status;
    private String message;

    public SimpleResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMeesage() {
        return message;
    }
}
