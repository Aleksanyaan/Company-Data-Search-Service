package com.example.data_search.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "persons_with_significant_control")
public class PersonWithSignificantControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_number", nullable = false)
    private Company company;

    private String name;

    @Column(name = "nature_of_control", length = 1000)
    private String natureOfControl; // comma-separated list, kept simple

    @Column(name = "notified_on")
    private LocalDate notifiedOn;

    private String kind;

    public PersonWithSignificantControl() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNatureOfControl() { return natureOfControl; }
    public void setNatureOfControl(String natureOfControl) { this.natureOfControl = natureOfControl; }

    public LocalDate getNotifiedOn() { return notifiedOn; }
    public void setNotifiedOn(LocalDate notifiedOn) { this.notifiedOn = notifiedOn; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
}