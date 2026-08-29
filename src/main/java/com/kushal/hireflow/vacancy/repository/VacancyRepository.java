package com.kushal.hireflow.vacancy.repository;

/**
 * Provides JPA-based data access methods for vacancy records used by the service layer.
 */

import com.kushal.hireflow.company.entity.Company;
import com.kushal.hireflow.vacancy.entity.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long>, JpaSpecificationExecutor<Vacancy> {
    List<Vacancy> findByCompany(Company company);
    List<Vacancy> findByTitleContainingIgnoreCase(String title);
    List<Vacancy> findByLocationContainingIgnoreCase(String location);
}
