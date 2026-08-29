package com.kushal.hireflow.auth.dto;

/** Holds the request payload used to resend an email verification link. */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendVerificationEmailRequest {
    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;
}
