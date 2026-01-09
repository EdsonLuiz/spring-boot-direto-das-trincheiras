package com.edson.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Producer(Long id, @JsonProperty("name") String name, LocalDateTime createdAt) {
    public Producer withCreatedAt(LocalDateTime createdAt) {
        return new Producer(id, name, createdAt);
    }
}
