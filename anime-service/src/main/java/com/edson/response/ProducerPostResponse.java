package com.edson.response;

import java.time.LocalDateTime;

public record ProducerPostResponse(Long id, String name, LocalDateTime createdAt) {
}
