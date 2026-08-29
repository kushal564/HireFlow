package com.kushal.hireflow.vacancy.service;

import com.kushal.hireflow.common.exception.BadRequestException;
import com.kushal.hireflow.common.exception.ForbiddenException;
import com.kushal.hireflow.company.entity.Company;
import com.kushal.hireflow.company.repository.CompanyRepository;
import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.Role;
import com.kushal.hireflow.user.entity.User;
import com.kushal.hireflow.vacancy.dto.CreateVacancyRequest;
import com.kushal.hireflow.vacancy.dto.UpdateVacancyRequest;
import com.kushal.hireflow.vacancy.dto.VacancyFilterRequest;
import com.kushal.hireflow.vacancy.entity.Vacancy;
import com.kushal.hireflow.vacancy.repository.VacancyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private VacancyService vacancyService;

    private User recruiter;
    private User anotherRecruiter;
    private Company company;
    private Company anotherCompany;

    @BeforeEach
    void setUp() {

        recruiter = mock(User.class);
        anotherRecruiter = mock(User.class);

        company = mock(Company.class);
        anotherCompany = mock(Company.class);

        authenticateAs(recruiter);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(User user) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        null
                )
        );
    }


    @Test
    void shouldCreateVacancyForRecruitersOwnCompany() {

        Role recruiterRole = mock(Role.class);

        when(recruiter.getId())
                .thenReturn(1L);

        when(recruiter.getRole())
                .thenReturn(recruiterRole);

        when(recruiterRole.getName())
                .thenReturn(RoleName.RECRUITER);

        when(company.getRecruiter())
                .thenReturn(recruiter);

        CreateVacancyRequest request =
                mock(CreateVacancyRequest.class);

        Vacancy savedVacancy =
                mock(Vacancy.class);

        when(request.getCompanyId())
                .thenReturn(1L);

        when(request.getTitle())
                .thenReturn("Java Backend Developer");

        when(request.getDescription())
                .thenReturn("Spring Boot backend developer");

        when(request.getLocation())
                .thenReturn("Noida");

        when(request.getSalaryFrom())
                .thenReturn(600000.0);

        when(request.getSalaryTo())
                .thenReturn(1000000.0);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(company.getId())
                .thenReturn(1L);

        when(savedVacancy.getId())
                .thenReturn(1L);

        when(savedVacancy.getTitle())
                .thenReturn("Java Backend Developer");

        when(savedVacancy.getDescription())
                .thenReturn("Spring Boot backend developer");

        when(savedVacancy.getLocation())
                .thenReturn("Noida");

        when(savedVacancy.getSalaryFrom())
                .thenReturn(600000.0);

        when(savedVacancy.getSalaryTo())
                .thenReturn(1000000.0);

        when(savedVacancy.getCompany())
                .thenReturn(company);

        when(vacancyRepository.save(any(Vacancy.class)))
                .thenReturn(savedVacancy);

        var response =
                vacancyService.createVacancy(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                "Java Backend Developer",
                response.getTitle()
        );

        verify(companyRepository)
                .findById(1L);

        verify(vacancyRepository)
                .save(any(Vacancy.class));
    }


    @Test
    void shouldRejectVacancyCreationForCandidate() {

        User candidate = mock(User.class);
        Role candidateRole = mock(Role.class);

        when(candidate.getRole())
                .thenReturn(candidateRole);

        when(candidateRole.getName())
                .thenReturn(RoleName.CANDIDATE);

        authenticateAs(candidate);

        CreateVacancyRequest request =
                mock(CreateVacancyRequest.class);

        assertThrows(
                ForbiddenException.class,
                () -> vacancyService.createVacancy(request)
        );

        verify(companyRepository, never())
                .findById(anyLong());

        verify(vacancyRepository, never())
                .save(any(Vacancy.class));
    }


    @Test
    void shouldRejectVacancyCreationForAnotherRecruitersCompany() {

        when(recruiter.getId())
                .thenReturn(1L);

        Role recruiterRole = mock(Role.class);

        when(recruiter.getRole())
                .thenReturn(recruiterRole);

        when(recruiterRole.getName())
                .thenReturn(RoleName.RECRUITER);

        when(anotherRecruiter.getId())
                .thenReturn(2L);

        when(company.getRecruiter())
                .thenReturn(anotherRecruiter);

        CreateVacancyRequest request =
                mock(CreateVacancyRequest.class);

        when(request.getCompanyId())
                .thenReturn(1L);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        assertThrows(
                ForbiddenException.class,
                () -> vacancyService.createVacancy(request)
        );

        verify(vacancyRepository, never())
                .save(any(Vacancy.class));
    }


    @Test
    void shouldUpdateOwnVacancy() {

        when(recruiter.getId())
                .thenReturn(1L);

        Vacancy vacancy =
                mock(Vacancy.class);

        UpdateVacancyRequest request =
                mock(UpdateVacancyRequest.class);

        when(vacancy.getCompany())
                .thenReturn(company);

        when(company.getRecruiter())
                .thenReturn(recruiter);

        when(vacancyRepository.findById(1L))
                .thenReturn(Optional.of(vacancy));

        when(request.getTitle())
                .thenReturn("Senior Java Developer");

        when(request.getDescription())
                .thenReturn("Updated description");

        when(request.getLocation())
                .thenReturn("Delhi");

        when(request.getSalaryFrom())
                .thenReturn(800000.0);

        when(request.getSalaryTo())
                .thenReturn(1200000.0);

        when(vacancyRepository.save(vacancy))
                .thenReturn(vacancy);

        var response =
                vacancyService.updateVacancy(1L, request);

        assertNotNull(response);

        verify(vacancy)
                .setTitle("Senior Java Developer");

        verify(vacancy)
                .setDescription("Updated description");

        verify(vacancy)
                .setLocation("Delhi");

        verify(vacancy)
                .setSalaryFrom(800000.0);

        verify(vacancy)
                .setSalaryTo(1200000.0);

        verify(vacancyRepository)
                .save(vacancy);
    }


    @Test
    void shouldRejectUpdatingSomeoneElsesVacancy() {

        when(recruiter.getId())
                .thenReturn(1L);

        when(anotherRecruiter.getId())
                .thenReturn(2L);

        Vacancy vacancy =
                mock(Vacancy.class);

        UpdateVacancyRequest request =
                mock(UpdateVacancyRequest.class);

        when(vacancy.getCompany())
                .thenReturn(anotherCompany);

        when(anotherCompany.getRecruiter())
                .thenReturn(anotherRecruiter);

        when(vacancyRepository.findById(1L))
                .thenReturn(Optional.of(vacancy));

        assertThrows(
                ForbiddenException.class,
                () -> vacancyService.updateVacancy(1L, request)
        );

        verify(vacancyRepository, never())
                .save(any(Vacancy.class));
    }


    @Test
    void shouldDeleteOwnVacancy() {

        when(recruiter.getId())
                .thenReturn(1L);

        Vacancy vacancy =
                mock(Vacancy.class);

        when(vacancy.getCompany())
                .thenReturn(company);

        when(company.getRecruiter())
                .thenReturn(recruiter);

        when(vacancyRepository.findById(1L))
                .thenReturn(Optional.of(vacancy));

        vacancyService.deleteVacancy(1L);

        verify(vacancyRepository)
                .delete(vacancy);
    }


    @Test
    void shouldRejectDeletingSomeoneElsesVacancy() {

        when(recruiter.getId())
                .thenReturn(1L);

        when(anotherRecruiter.getId())
                .thenReturn(2L);

        Vacancy vacancy =
                mock(Vacancy.class);

        when(vacancy.getCompany())
                .thenReturn(anotherCompany);

        when(anotherCompany.getRecruiter())
                .thenReturn(anotherRecruiter);

        when(vacancyRepository.findById(1L))
                .thenReturn(Optional.of(vacancy));

        assertThrows(
                ForbiddenException.class,
                () -> vacancyService.deleteVacancy(1L)
        );

        verify(vacancyRepository, never())
                .delete(any(Vacancy.class));
    }


    @Test
    void shouldRejectInvalidSalaryRange() {

        VacancyFilterRequest request =
                mock(VacancyFilterRequest.class);

        when(request.getMinSalary())
                .thenReturn(1000000.0);

        when(request.getMaxSalary())
                .thenReturn(500000.0);

        assertThrows(
                BadRequestException.class,
                () -> vacancyService.getAllVacancies(request)
        );

        verify(vacancyRepository, never())
                .findAll(
                        any(org.springframework.data.jpa.domain.Specification.class),
                        any(Pageable.class)
                );
    }
}