package com.kushal.hireflow.company.dto;

/** Represents the API response payload returned for company data. */

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Long recruiterId;
}
