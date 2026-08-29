package com.kushal.hireflow.application.controller;

import com.kushal.hireflow.application.dto.ApplicationResponse;
import com.kushal.hireflow.application.dto.CreateApplicationRequest;
import com.kushal.hireflow.application.dto.UpdateApplicationStatusRequest;
import com.kushal.hireflow.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse applyToVacancy(@Valid @RequestBody CreateApplicationRequest request) {
        return applicationService.applyToVacancy(request);
    }

    @GetMapping("/my")
    public List<ApplicationResponse> getMyApplications() {
        return applicationService.getMyApplications();
    }

    @GetMapping("/recruiter")
    public List<ApplicationResponse> getRecruiterApplications() {
        return applicationService.getRecruiterApplications();
    }

    @PatchMapping("/{id}/status")
    public ApplicationResponse updateApplicationStatus(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateApplicationStatusRequest request) {
        return applicationService.updateApplicationStatus(id, request);
    }
}