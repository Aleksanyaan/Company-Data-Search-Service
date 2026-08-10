package com.example.data_search.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class CompanyDto {
    private String companyNumber;
    private String companyName;
    private String status;
    private String companyType;
    private LocalDate incorporationDate;
    private LocalDate dissolutionDate;
    private String registeredOfficeAddress;
    private String sicCodes;
    private Instant lastScrapedAt;
    private List<OfficerDto> officers;
    private List<PscDto> personsWithSignificantControl;

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

    public Instant getLastScrapedAt() { return lastScrapedAt; }
    public void setLastScrapedAt(Instant lastScrapedAt) { this.lastScrapedAt = lastScrapedAt; }

    public List<OfficerDto> getOfficers() { return officers; }
    public void setOfficers(List<OfficerDto> officers) { this.officers = officers; }

    public List<PscDto> getPersonsWithSignificantControl() { return personsWithSignificantControl; }
    public void setPersonsWithSignificantControl(List<PscDto> personsWithSignificantControl) { this.personsWithSignificantControl = personsWithSignificantControl; }
}