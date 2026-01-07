package com.edson.domain;

import java.time.LocalDateTime;

public record Anime(Long id, String name, LocalDateTime createdAt) {
}
