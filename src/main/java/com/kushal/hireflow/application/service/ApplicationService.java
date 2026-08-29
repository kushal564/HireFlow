package com.kushal.hireflow.application.service;

/**
 * Contains the business logic for application features and coordinates validation, security
 * checks, and repository access.
 */

import com.kushal.hireflow.application.dto.ApplicationResponse;
import com.kushal.hireflow.application.dto.CreateApplicationRequest;
import com.kushal.hireflow.application.dto.UpdateApplicationStatusRequest;
import com.kushal.hireflow.application.entity.Application;
import com.kushal.hireflow.application.repository.ApplicationRepository;
import com.kushal.hireflow.common.exception.BadRequestException;
import com.kushal.hireflow.common.exception.ForbiddenException;
import com.kushal.hireflow.common.exception.ResourceNotFoundException;
import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.User;
import com.kushal.hireflow.vacancy.entity.Vacancy;
import com.kushal.hireflow.vacancy.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final VacancyRepository vacancyRepository;


    public ApplicationResponse applyToVacancy(CreateApplicationRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        if (currentUser.getRole().getName() != RoleName.CANDIDATE) {
            throw new ForbiddenException("Only candidates can apply to vacancies");
        }

        Vacancy vacancy = vacancyRepository.findById(request.getVacancyId())
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found"));

        if (applicationRepository.existsByCandidateAndVacancy(currentUser, vacancy)) {
            throw new BadRequestException("You have already applied to this vacancy");
        }

        Application application = new Application();
        application.setCandidate(currentUser);
        application.setVacancy(vacancy);

        Application savedApplication = applicationRepository.save(application);

        return new ApplicationResponse(
                savedApplication.getId(),
                savedApplication.getCandidate().getId(),
                savedApplication.getVacancy().getId(),
                savedApplication.getStatus(),
                savedApplication.getCreatedAt()
        );
    }

    public java.util.List<ApplicationResponse> getMyApplications() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        java.util.List<Application> applications = applicationRepository.findByCandidate(currentUser);

        return applications.stream()
                .map(application -> new ApplicationResponse(
                        application.getId(),
                        application.getCandidate().getId(),
                        application.getVacancy().getId(),
                        application.getStatus(),
                        application.getCreatedAt()
                ))
                .toList();
    }

    public java.util.List<ApplicationResponse> getRecruiterApplications() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        if (currentUser.getRole().getName() != RoleName.RECRUITER) {
            throw new ForbiddenException("Only recruiters can view these applications");
        }

        java.util.List<Application> applications =
                applicationRepository.findByVacancy_Company_Recruiter(currentUser);

        return applications.stream()
                .map(application -> new ApplicationResponse(
                        application.getId(),
                        application.getCandidate().getId(),
                        application.getVacancy().getId(),
                        application.getStatus(),
                        application.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long id, UpdateApplicationStatusRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        if (currentUser.getRole().getName() != RoleName.RECRUITER) {
            throw new ForbiddenException("Only recruiters can update application status");
        }

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getVacancy().getCompany().getRecruiter().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can update only applications for your own vacancies");
        }

        application.setStatus(request.getStatus());

        Application updatedApplication = applicationRepository.save(application);

        return new ApplicationResponse(
                updatedApplication.getId(),
                updatedApplication.getCandidate().getId(),
                updatedApplication.getVacancy().getId(),
                updatedApplication.getStatus(),
                updatedApplication.getCreatedAt()
        );
    }
}