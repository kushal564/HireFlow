package com.kushal.hireflow.common.response;

/**
 * Represents the structured JSON error body returned by the global exception handler.
 */

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
