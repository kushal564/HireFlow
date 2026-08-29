package com.kushal.hireflow.vacancy.service;

import com.kushal.hireflow.common.exception.BadRequestException;
import com.kushal.hireflow.common.exception.ForbiddenException;
import com.kushal.hireflow.common.exception.ResourceNotFoundException;
import com.kushal.hireflow.common.response.PageResponse;
import com.kushal.hireflow.company.entity.Company;
import com.kushal.hireflow.company.repository.CompanyRepository;
import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.User;
import com.kushal.hireflow.vacancy.dto.CreateVacancyRequest;
import com.kushal.hireflow.vacancy.dto.UpdateVacancyRequest;
import com.kushal.hireflow.vacancy.dto.VacancyFilterRequest;
import com.kushal.hireflow.vacancy.dto.VacancyResponse;
import com.kushal.hireflow.vacancy.entity.Vacancy;
import com.kushal.hireflow.vacancy.repository.VacancyRepository;
import com.kushal.hireflow.vacancy.specification.VacancySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("createdAt", "title", "location", "salaryFrom", "salaryTo");

    private final VacancyRepository vacancyRepository;
    private final CompanyRepository companyRepository;


    public VacancyResponse createVacancy(CreateVacancyRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        if (currentUser.getRole().getName() != RoleName.RECRUITER) {
            throw new ForbiddenException("Only recruiters can create vacancies");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can create vacancy only for your own company");
        }

        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(request.getTitle());
        vacancy.setDescription(request.getDescription());
        vacancy.setLocation(request.getLocation());
        vacancy.setSalaryFrom(request.getSalaryFrom());
        vacancy.setSalaryTo(request.getSalaryTo());
        vacancy.setCompany(company);

        Vacancy savedVacancy = vacancyRepository.save(vacancy);

        return mapToResponse(savedVacancy);
    }

    public PageResponse<VacancyResponse> getAllVacancies(VacancyFilterRequest request) {
        validateFilterRequest(request);

        Pageable pageable = createPageable(request);
        Page<Vacancy> vacancies = vacancyRepository.findAll(VacancySpecification.withFilters(request), pageable);

        return new PageResponse<>(
                vacancies.getContent().stream().map(this::mapToResponse).toList(),
                vacancies.getNumber(),
                vacancies.getSize(),
                vacancies.getTotalElements(),
                vacancies.getTotalPages(),
                vacancies.isLast()
        );
    }

    public VacancyResponse getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found"));

        return mapToResponse(vacancy);
    }

    public VacancyResponse updateVacancy(Long id, UpdateVacancyRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found"));

        if (!vacancy.getCompany().getRecruiter().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can update only your own vacancy");
        }

        vacancy.setTitle(request.getTitle());
        vacancy.setDescription(request.getDescription());
        vacancy.setLocation(request.getLocation());
        vacancy.setSalaryFrom(request.getSalaryFrom());
        vacancy.setSalaryTo(request.getSalaryTo());

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);

        return mapToResponse(updatedVacancy);
    }

    public void deleteVacancy(Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = (User) principal;

        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found"));

        if (!vacancy.getCompany().getRecruiter().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can delete only your own vacancy");
        }

        vacancyRepository.delete(vacancy);
    }

    private void validateFilterRequest(VacancyFilterRequest request) {
        Integer page = request.getPage();
        Integer size = request.getSize();
        Double minSalary = request.getMinSalary();
        Double maxSalary = request.getMaxSalary();

        if (page != null && page < 0) {
            throw new BadRequestException("Page must be greater than or equal to 0");
        }

        if (size != null && (size < 1 || size > 100)) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        if (minSalary != null && minSalary <= 0) {
            throw new BadRequestException("Min salary must be greater than 0");
        }

        if (maxSalary != null && maxSalary <= 0) {
            throw new BadRequestException("Max salary must be greater than 0");
        }

        if (minSalary != null && maxSalary != null && minSalary > maxSalary) {
            throw new BadRequestException("Min salary cannot be greater than max salary");
        }

        String sortBy = resolveSortBy(request);
        String sortDir = resolveSortDir(request);

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException("Invalid sortBy value");
        }

        if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
            throw new BadRequestException("Invalid sortDir value");
        }
    }

    private Pageable createPageable(VacancyFilterRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        String sortBy = resolveSortBy(request);
        String sortDir = resolveSortDir(request);

        Sort.Direction direction = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private String resolveSortBy(VacancyFilterRequest request) {
        return request.getSortBy() != null && !request.getSortBy().isBlank()
                ? request.getSortBy().trim()
                : "createdAt";
    }

    private String resolveSortDir(VacancyFilterRequest request) {
        return request.getSortDir() != null && !request.getSortDir().isBlank()
                ? request.getSortDir().trim().toLowerCase(Locale.ROOT)
                : "desc";
    }

    private VacancyResponse mapToResponse(Vacancy vacancy) {
        return new VacancyResponse(
                vacancy.getId(),
                vacancy.getTitle(),
                vacancy.getDescription(),
                vacancy.getLocation(),
                vacancy.getSalaryFrom(),
                vacancy.getSalaryTo(),
                vacancy.getCompany().getId(),
                vacancy.getCreatedAt()
        );
    }
}
