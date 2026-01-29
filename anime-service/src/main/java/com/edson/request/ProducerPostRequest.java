package com.edson.request;

import jakarta.validation.constraints.NotBlank;

public record ProducerPostRequest(
        @NotBlank(message = "The field 'name' is required")
        String name) {
}
