package com.edson.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record Producer(Long id, @JsonProperty("name") String name, LocalDateTime createdAt) {
}
