package com.kushal.hireflow.user.service;

import com.kushal.hireflow.common.exception.ForbiddenException;
import com.kushal.hireflow.common.exception.ResourceNotFoundException;
import com.kushal.hireflow.enums.RoleName;
import com.kushal.hireflow.user.dto.UpdateUserRoleRequest;
import com.kushal.hireflow.user.entity.Role;
import com.kushal.hireflow.user.entity.User;
import com.kushal.hireflow.user.repository.RoleRepository;
import com.kushal.hireflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public User updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole().getName() != RoleName.ADMIN) {
            throw new ForbiddenException("Only admins can change user roles");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRole(role);
        return userRepository.save(user);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
