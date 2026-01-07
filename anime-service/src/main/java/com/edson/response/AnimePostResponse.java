package com.edson.response;

import java.time.LocalDateTime;

public record AnimePostResponse(Long id, String name, LocalDateTime createdAt) {
}
