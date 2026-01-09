package com.edson.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Anime(Long id, String name, LocalDateTime createdAt) {
    public Anime withCreatedAt(LocalDateTime createdAt) {
        return new Anime(id, name, createdAt);
    }
}
