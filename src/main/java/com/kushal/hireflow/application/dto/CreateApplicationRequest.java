package com.kushal.hireflow.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {
    @NotNull(message = "Vacancy id is required")
    private Long vacancyId;
}
