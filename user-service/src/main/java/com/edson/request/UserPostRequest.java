package com.edson.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UserPostRequest(
        @NotBlank(message = "The field 'firstName' is required")
        String firstName,
        @NotBlank(message = "The field 'lastName' is required")
        String lastName,
        @Email
        @NotBlank(message = "The field 'email' is required")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email is invalid"
        )
        String email) {
}
