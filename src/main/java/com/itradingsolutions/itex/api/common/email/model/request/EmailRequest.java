package com.itradingsolutions.itex.api.common.email.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EmailRequest(

        @NotEmpty(message = "Subject is required")
        String subject,

        @NotEmpty(message = "Subject is required")
        String body,

        @NotEmpty(message = "To is required")
        @Size(min = 1, message = "A minimum of one recipient is required")
        List<String> to,

        List<String> cc,

        List<String> cco
) {

}
