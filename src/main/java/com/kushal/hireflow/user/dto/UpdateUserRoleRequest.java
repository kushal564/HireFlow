package com.kushal.hireflow.user.dto;

import com.kushal.hireflow.enums.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {

    @NotNull(message = "Role is required")
    private RoleName role;
}
