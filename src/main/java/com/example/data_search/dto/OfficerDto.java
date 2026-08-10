package com.example.data_search.dto;

import java.time.LocalDate;

public class OfficerDto {
    private String name;
    private String role;
    private LocalDate appointedOn;
    private LocalDate resignedOn;
    private String nationality;
    private String occupation;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getAppointedOn() { return appointedOn; }
    public void setAppointedOn(LocalDate appointedOn) { this.appointedOn = appointedOn; }

    public LocalDate getResignedOn() { return resignedOn; }
    public void setResignedOn(LocalDate resignedOn) { this.resignedOn = resignedOn; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
}