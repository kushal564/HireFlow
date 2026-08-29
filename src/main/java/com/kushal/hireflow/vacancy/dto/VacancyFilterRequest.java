package com.kushal.hireflow.vacancy.dto;

import lombok.*;

/**
 * Holds the filter, pagination, and sorting options used when listing vacancies.
 */

@Getter
@Setter
@NoArgsConstructor
public class VacancyFilterRequest {

    private String title;
    private String location;
    private Long companyId;
    private String companyName;
    private Double minSalary;
    private Double maxSalary;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDir;


}
