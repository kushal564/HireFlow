package com.kushal.hireflow.company.service;


import com.kushal.hireflow.common.exception.ForbiddenException;
import com.kushal.hireflow.common.exception.ResourceNotFoundException;
import com.kushal.hireflow.company.dto.CompanyResponse;
import com.kushal.hireflow.company.dto.CreateCompanyRequest;
import com.kushal.hireflow.company.dto.UpdateCompanyRequest;
import com.kushal.hireflow.company.entity.Company;
import com.kushal.hireflow.company.repository.CompanyRepository;
import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponse createCompany(CreateCompanyRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        if (currentUser.getRole().getName() != RoleName.RECRUITER) {
            throw new ForbiddenException("Only recruiters can create companies");
        }

        Company company = new Company();
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setLocation(request.getLocation());
        company.setRecruiter(currentUser);

        Company savedCompany = companyRepository.save(company);

        return new CompanyResponse(
                savedCompany.getId(),
                savedCompany.getName(),
                savedCompany.getDescription(),
                savedCompany.getLocation(),
                savedCompany.getRecruiter().getId()
        );
    }

    public List<CompanyResponse> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();

        return companies.stream()
                .map(company -> new CompanyResponse(
                        company.getId(),
                        company.getName(),
                        company.getDescription(),
                        company.getLocation(),
                        company.getRecruiter().getId()
                ))
                .toList();
    }

    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getLocation(),
                company.getRecruiter().getId()
        );
    }

    public CompanyResponse updateCompany(Long id, UpdateCompanyRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can update only your own company");
        }

        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setLocation(request.getLocation());

        Company updatedCompany = companyRepository.save(company);

        return new CompanyResponse(
                updatedCompany.getId(),
                updatedCompany.getName(),
                updatedCompany.getDescription(),
                updatedCompany.getLocation(),
                updatedCompany.getRecruiter().getId()
        );
    }

    public void deleteCompany(Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can delete only your own company");
        }

        companyRepository.delete(company);
    }
}