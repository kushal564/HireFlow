package com.kushal.hireflow.user.repository;


import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
