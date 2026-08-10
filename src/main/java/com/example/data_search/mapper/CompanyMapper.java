package com.example.data_search.mapper;

import com.example.data_search.dto.CompanyDto;
import com.example.data_search.dto.OfficerDto;
import com.example.data_search.dto.PscDto;
import com.example.data_search.entity.Company;
import com.example.data_search.entity.Officer;
import com.example.data_search.entity.PersonWithSignificantControl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyMapper {

    public CompanyDto toDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setCompanyNumber(company.getCompanyNumber());
        dto.setCompanyName(company.getCompanyName());
        dto.setStatus(company.getStatus());
        dto.setCompanyType(company.getCompanyType());
        dto.setIncorporationDate(company.getIncorporationDate());
        dto.setDissolutionDate(company.getDissolutionDate());
        dto.setRegisteredOfficeAddress(company.getRegisteredOfficeAddress());
        dto.setSicCodes(company.getSicCodes());
        dto.setLastScrapedAt(company.getLastScrapedAt());
        dto.setOfficers(toOfficerDtos(company.getOfficers()));
        dto.setPersonsWithSignificantControl(toPscDtos(company.getPersonsWithSignificantControl()));
        return dto;
    }

    public List<CompanyDto> toDtos(List<Company> companies) {
        return companies.stream().map(this::toDto).collect(Collectors.toList());
    }

    private List<OfficerDto> toOfficerDtos(List<Officer> officers) {
        if (officers == null) return List.of();
        return officers.stream().map(o -> {
            OfficerDto dto = new OfficerDto();
            dto.setName(o.getName());
            dto.setRole(o.getRole());
            dto.setAppointedOn(o.getAppointedOn());
            dto.setResignedOn(o.getResignedOn());
            dto.setNationality(o.getNationality());
            dto.setOccupation(o.getOccupation());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<PscDto> toPscDtos(List<PersonWithSignificantControl> pscs) {
        if (pscs == null) return List.of();
        return pscs.stream().map(p -> {
            PscDto dto = new PscDto();
            dto.setName(p.getName());
            dto.setNatureOfControl(p.getNatureOfControl());
            dto.setNotifiedOn(p.getNotifiedOn());
            return dto;
        }).collect(Collectors.toList());
    }
}