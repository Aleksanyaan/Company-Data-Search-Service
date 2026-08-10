package com.example.data_search.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @Column(name = "company_number", length = 16)
    private String companyNumber;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    private String status;

    @Column(name = "company_type")
    private String companyType;

    @Column(name = "incorporation_date")
    private LocalDate incorporationDate;

    @Column(name = "dissolution_date")
    private LocalDate dissolutionDate;

    @Column(name = "registered_office_address", length = 1000)
    private String registeredOfficeAddress;

    @Column(name = "sic_codes", length = 500)
    private String sicCodes;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "last_scraped_at")
    private Instant lastScrapedAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Officer> officers = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PersonWithSignificantControl> personsWithSignificantControl = new ArrayList<>();

    public Company() {}

    public String getCompanyNumber() { return companyNumber; }
    public void setCompanyNumber(String companyNumber) { this.companyNumber = companyNumber; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }

    public LocalDate getIncorporationDate() { return incorporationDate; }
    public void setIncorporationDate(LocalDate incorporationDate) { this.incorporationDate = incorporationDate; }

    public LocalDate getDissolutionDate() { return dissolutionDate; }
    public void setDissolutionDate(LocalDate dissolutionDate) { this.dissolutionDate = dissolutionDate; }

    public String getRegisteredOfficeAddress() { return registeredOfficeAddress; }
    public void setRegisteredOfficeAddress(String registeredOfficeAddress) { this.registeredOfficeAddress = registeredOfficeAddress; }

    public String getSicCodes() { return sicCodes; }
    public void setSicCodes(String sicCodes) { this.sicCodes = sicCodes; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public Instant getLastScrapedAt() { return lastScrapedAt; }
    public void setLastScrapedAt(Instant lastScrapedAt) { this.lastScrapedAt = lastScrapedAt; }

    public List<Officer> getOfficers() { return officers; }
    public void setOfficers(List<Officer> officers) { this.officers = officers; }

    public List<PersonWithSignificantControl> getPersonsWithSignificantControl() { return personsWithSignificantControl; }
    public void setPersonsWithSignificantControl(List<PersonWithSignificantControl> pscs) { this.personsWithSignificantControl = pscs; }
}