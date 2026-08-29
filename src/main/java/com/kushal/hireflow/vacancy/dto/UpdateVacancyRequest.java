package com.kushal.hireflow.vacancy.dto;

/** Holds the request data needed to update a vacancy through the API. */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVacancyRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotBlank(message = "Location is required")
    private String location;
    @NotNull(message = "Salary from is required")
    @Positive(message = "Salary from must be positive")
    private Double salaryFrom;
    @NotNull(message = "Salary to is required")
    @Positive(message = "Salary to must be positive")
    private Double salaryTo;
}
