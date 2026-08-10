package com.example.data_search.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "officers")
public class Officer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_number", nullable = false)
    private Company company;

    private String name;
    private String role;

    @Column(name = "appointed_on")
    private LocalDate appointedOn;

    @Column(name = "resigned_on")
    private LocalDate resignedOn;

    private String nationality;
    private String occupation;

    @Column(length = 1000)
    private String address;

    public Officer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}