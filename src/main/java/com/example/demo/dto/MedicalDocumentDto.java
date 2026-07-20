package com.example.demo.dto;

import com.example.demo.model.MedicalDocument;

public class MedicalDocumentDto {
    private Long id;
    private String name;
    private String size;
    private String date;
    private String data;

    public MedicalDocumentDto() {}

    public MedicalDocumentDto(MedicalDocument doc) {
        this.id = doc.getId();
        this.name = doc.getName();
        this.size = doc.getSize();
        this.date = doc.getDate();
        this.data = doc.getData();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSize() { return size; }
    public String getDate() { return date; }
    public String getData() { return data; }
}
