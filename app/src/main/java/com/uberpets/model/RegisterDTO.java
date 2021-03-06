package com.uberpets.model;

import java.util.ArrayList;
import java.util.List;

public class RegisterDTO {
    private final String facebookId;
    private final String name;
    private final String dni;
    private final String phone;
    private List<FileDocumentDTO> files;
    private final String role;

    public static class RegisterDTOBuilder {
        private final String facebookId;
        private final String name;
        private String dni;
        private String phone;
        private List<FileDocumentDTO> files = new ArrayList<>();
        private String role;
        public RegisterDTOBuilder(String id, String name, String photoProfile) {
            this.facebookId = id;
            this.name = name;
            FileDocumentDTO file = new FileDocumentDTO();
            file.setData(photoProfile);
            file.setName("profile");
            files.add(file);
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
            FileDocumentDTO file = new FileDocumentDTO();
            file.setData(photoCar);
            file.setName("car");
            files.add(file);
            return this;
        }

        public RegisterDTOBuilder setPhotoInsurance(String photoInsurance) {
            FileDocumentDTO file = new FileDocumentDTO();
            file.setData(photoInsurance);
            file.setName("insurance");
            files.add(file);
            return this;
        }

        public RegisterDTOBuilder setPhotoLicense(String photoLicense) {
            FileDocumentDTO file = new FileDocumentDTO();
            file.setData(photoLicense);
            file.setName("license");
            files.add(file);
            return this;
        }

        public RegisterDTOBuilder setRole(String role) {
            this.role = role;
            return this;
        }

        public RegisterDTO build() {
            return new RegisterDTO(this);
        }
    }

    public RegisterDTO(RegisterDTOBuilder builder) {
        this.facebookId = builder.facebookId;
        this.name = builder.name;
        this.dni = builder.dni;
        this.phone = builder.phone;
        this.files = builder.files;
        this.role = builder.role;
    }

    public String getName() {
        return name;
    }

    public String getDni() {
        return dni;
    }

    public String getPhone() {
        return phone;
    }

    public List<FileDocumentDTO> getFiles() {
        return files;
    }
}
