package com.kushal.hireflow.vacancy.dto;

/** Represents the API response payload returned for vacancy data. */

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacancyResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Double salaryFrom;
    private Double salaryTo;
    private Long companyId;
    private LocalDateTime createdAt;
}
