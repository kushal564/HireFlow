package com.kushal.hireflow.company.repository;

/**
 * Provides JPA-based data access methods for company records used by the service layer.
 */


import com.kushal.hireflow.company.entity.Company;
import com.kushal.hireflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByRecruiter(User recruiter);
}