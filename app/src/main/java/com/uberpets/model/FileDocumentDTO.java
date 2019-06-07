package com.uberpets.model;

public class FileDocumentDTO {
    private int id;
    private String name;
    private String extension;
    private String data;

    public FileDocumentDTO(int id, String name, String extension, String data) {
        this.id = id;
        this.name = name;
        this.extension = extension;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getData() {
        return data;
    }
}
