package com.example.data_search.dto;

import java.time.LocalDate;

public class PscDto {
    private String name;
    private String natureOfControl;
    private LocalDate notifiedOn;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNatureOfControl() { return natureOfControl; }
    public void setNatureOfControl(String natureOfControl) { this.natureOfControl = natureOfControl; }

    public LocalDate getNotifiedOn() { return notifiedOn; }
    public void setNotifiedOn(LocalDate notifiedOn) { this.notifiedOn = notifiedOn; }
}