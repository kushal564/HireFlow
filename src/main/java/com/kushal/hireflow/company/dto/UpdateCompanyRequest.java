package com.kushal.hireflow.company.dto;

/** Holds the request data needed to update a company through the API. */

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {
    @NotBlank(message = "Company name is required")
    private String name;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;
}
