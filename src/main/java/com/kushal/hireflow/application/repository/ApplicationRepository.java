package com.kushal.hireflow.application.repository;

/**
 * Provides JPA-based data access methods for application records used by the service layer.
 */

import com.kushal.hireflow.application.entity.Application;
import com.kushal.hireflow.user.entity.User;
import com.kushal.hireflow.vacancy.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidate(User candidate);
    boolean existsByCandidateAndVacancy(User candidate, Vacancy vacancy);
    List<Application> findByVacancy_Company_Recruiter(User recruiter);
}