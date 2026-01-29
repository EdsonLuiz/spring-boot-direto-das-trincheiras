package com.edson.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProducerPutRequest(
        @NotNull(message = "Id cannot be null")
        @Positive(message = "Id must be positive")
        Long id,
        @NotNull(message = "Name cannot be null")
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Created at cannot be null")
        @Past(message = "Created at must be in the past")
        LocalDateTime createdAt) {
}
