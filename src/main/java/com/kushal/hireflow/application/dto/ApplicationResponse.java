package com.kushal.hireflow.application.dto;

/** Represents the API response payload returned for application data. */

import com.kushal.hireflow.enums.ApplicationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long candidateId;
    private Long vacancyId;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
}
