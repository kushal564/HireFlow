package com.kushal.hireflow.company.service;

import com.kushal.hireflow.common.exception.ForbiddenException;
import com.kushal.hireflow.common.exception.ResourceNotFoundException;
import com.kushal.hireflow.company.dto.CompanyResponse;
import com.kushal.hireflow.company.dto.CreateCompanyRequest;
import com.kushal.hireflow.company.dto.UpdateCompanyRequest;
import com.kushal.hireflow.company.entity.Company;
import com.kushal.hireflow.company.repository.CompanyRepository;
import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.Role;
import com.kushal.hireflow.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    private User recruiter;
    private User anotherRecruiter;

    @BeforeEach
    void setUp() {

        recruiter = mock(User.class);
        anotherRecruiter = mock(User.class);

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
    void shouldCreateCompanyForRecruiter() {

        Role recruiterRole = mock(Role.class);

        when(recruiter.getId()).thenReturn(1L);
        when(recruiter.getRole()).thenReturn(recruiterRole);
        when(recruiterRole.getName()).thenReturn(RoleName.RECRUITER);

        CreateCompanyRequest request =
                mock(CreateCompanyRequest.class);

        Company savedCompany =
                mock(Company.class);

        when(request.getName())
                .thenReturn("Tech Solutions");

        when(request.getDescription())
                .thenReturn("Software company");

        when(request.getLocation())
                .thenReturn("Noida");

        when(savedCompany.getId())
                .thenReturn(1L);

        when(savedCompany.getName())
                .thenReturn("Tech Solutions");

        when(savedCompany.getDescription())
                .thenReturn("Software company");

        when(savedCompany.getLocation())
                .thenReturn("Noida");

        when(savedCompany.getRecruiter())
                .thenReturn(recruiter);

        when(companyRepository.save(any(Company.class)))
                .thenReturn(savedCompany);

        CompanyResponse response =
                companyService.createCompany(request);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("Tech Solutions", response.getName());
        assertEquals("Software company", response.getDescription());
        assertEquals("Noida", response.getLocation());
        assertEquals(1L, response.getRecruiterId());

        verify(companyRepository)
                .save(any(Company.class));
    }


    @Test
    void shouldRejectCompanyCreationForNonRecruiter() {

        User candidate = mock(User.class);
        Role candidateRole = mock(Role.class);

        when(candidate.getRole())
                .thenReturn(candidateRole);

        when(candidateRole.getName())
                .thenReturn(RoleName.CANDIDATE);

        authenticateAs(candidate);

        CreateCompanyRequest request =
                mock(CreateCompanyRequest.class);

        assertThrows(
                ForbiddenException.class,
                () -> companyService.createCompany(request)
        );

        verify(companyRepository, never())
                .save(any(Company.class));
    }


    @Test
    void shouldUpdateOwnCompany() {

        when(recruiter.getId())
                .thenReturn(1L);

        Company company =
                mock(Company.class);

        UpdateCompanyRequest request =
                mock(UpdateCompanyRequest.class);

        when(company.getRecruiter())
                .thenReturn(recruiter);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(request.getName())
                .thenReturn("Updated Company");

        when(request.getDescription())
                .thenReturn("Updated description");

        when(request.getLocation())
                .thenReturn("Delhi");

        when(companyRepository.save(company))
                .thenReturn(company);

        CompanyResponse response =
                companyService.updateCompany(1L, request);

        assertNotNull(response);

        verify(company)
                .setName("Updated Company");

        verify(company)
                .setDescription("Updated description");

        verify(company)
                .setLocation("Delhi");

        verify(companyRepository)
                .save(company);
    }


    @Test
    void shouldRejectUpdatingSomeoneElsesCompany() {

        when(recruiter.getId())
                .thenReturn(1L);

        when(anotherRecruiter.getId())
                .thenReturn(2L);

        Company company =
                mock(Company.class);

        when(company.getRecruiter())
                .thenReturn(anotherRecruiter);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        UpdateCompanyRequest request =
                mock(UpdateCompanyRequest.class);

        assertThrows(
                ForbiddenException.class,
                () -> companyService.updateCompany(1L, request)
        );

        verify(companyRepository, never())
                .save(any(Company.class));
    }


    @Test
    void shouldDeleteOwnCompany() {

        when(recruiter.getId())
                .thenReturn(1L);

        Company company =
                mock(Company.class);

        when(company.getRecruiter())
                .thenReturn(recruiter);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        companyService.deleteCompany(1L);

        verify(companyRepository)
                .delete(company);
    }


    @Test
    void shouldRejectDeletingSomeoneElsesCompany() {

        when(recruiter.getId())
                .thenReturn(1L);

        when(anotherRecruiter.getId())
                .thenReturn(2L);

        Company company =
                mock(Company.class);

        when(company.getRecruiter())
                .thenReturn(anotherRecruiter);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        assertThrows(
                ForbiddenException.class,
                () -> companyService.deleteCompany(1L)
        );

        verify(companyRepository, never())
                .delete(any(Company.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCompany() {

        UpdateCompanyRequest request =
                mock(UpdateCompanyRequest.class);

        when(companyRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> companyService.updateCompany(99L, request)
        );

        verify(companyRepository, never())
                .save(any(Company.class));
    }
}