package com.kushal.hireflow.company.controller;

/**
 * Exposes REST endpoints for company operations and delegates incoming requests to the
 * corresponding service layer.
 */


import com.kushal.hireflow.company.dto.CompanyResponse;
import com.kushal.hireflow.company.dto.CreateCompanyRequest;
import com.kushal.hireflow.company.dto.UpdateCompanyRequest;
import com.kushal.hireflow.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        return companyService.createCompany(request);
    }

    @GetMapping
    public List<CompanyResponse> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    public CompanyResponse getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    @PutMapping("/{id}")
    public CompanyResponse updateCompany(@PathVariable Long id,
                                         @Valid @RequestBody UpdateCompanyRequest request) {
        return companyService.updateCompany(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }
}