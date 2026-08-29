package com.kushal.hireflow.user.controller;

import com.kushal.hireflow.common.response.MessageResponse;
import com.kushal.hireflow.user.dto.UpdateUserRoleRequest;
import com.kushal.hireflow.user.entity.User;
import com.kushal.hireflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/role")
    public ResponseEntity<MessageResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        userService.updateUserRole(id, request);

        return ResponseEntity.ok(
                new MessageResponse("User role updated successfully")
        );
    }
}
