package com.edson.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AnimePutRequest(
        @NotNull(message = "The field 'id' is required")
        Long id,
        @NotBlank(message = "The field 'name' is required")
        String name) {
}
