package com.uberpets.model;

public class LoginDTO {
    private String facebookId;
    private String role;

    public LoginDTO(String id, String role) {
        this.facebookId = id;
        this.role = role;
    }

    public String getId() {
        return facebookId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString(){
        return "{ facebookId: "+facebookId +", "+" role: "+role+ "}";
    }
}
