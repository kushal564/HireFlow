package com.kushal.hireflow.common.response;

/**
 * Represents a simple success response containing a single message.
 */

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
