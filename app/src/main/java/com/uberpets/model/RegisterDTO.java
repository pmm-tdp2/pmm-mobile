package com.uberpets.model;

public class RegisterDTO {

    private final String name;
    private final String photoProfile;
    private final String dni;
    private final String phone;
    private final String photoCar;
    private final String photoInsurance;
    private final String photoLicense;


    public static class RegisterDTOBuilder {
        private final String name;
        private final String photoProfile;
        private String dni;
        private String phone;
        private String photoCar;
        private String photoInsurance;
        private String photoLicense;

        public RegisterDTOBuilder(String name, String photoProfile) {
            this.name = name;
            this.photoProfile = photoProfile;
        }

        public RegisterDTOBuilder setDni(String dni) {
            this.dni = dni;
            return this;
        }

        public RegisterDTOBuilder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public RegisterDTOBuilder setPhotoCar(String photoCar) {
            this.photoCar = photoCar;
            return this;
        }

        public RegisterDTOBuilder setPhotoInsurance(String photoInsurance) {
            this.photoInsurance = photoInsurance;
            return this;
        }

        public RegisterDTOBuilder setPhotoLicense(String photoLicense) {
            this.photoLicense = photoLicense;
            return this;
        }

        public RegisterDTO build() {
            return new RegisterDTO(this);
        }
    }

    public RegisterDTO(RegisterDTOBuilder builder) {
        this.name = builder.name;
        this.photoProfile = builder.photoProfile;
        this.dni = builder.dni;
        this.phone = builder.phone;
        this.photoCar = builder.photoCar;
        this.photoInsurance = builder.photoInsurance;
        this.photoLicense = builder.photoLicense;
    }

    public String getName() {
        return name;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public String getDni() {
        return dni;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhotoCar() {
        return photoCar;
    }

    public String getPhotoInsurance() {
        return photoInsurance;
    }

    public String getPhotoLicense() {
        return photoLicense;
    }
}
