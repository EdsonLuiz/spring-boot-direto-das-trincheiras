package com.edson.request;

import java.time.LocalDateTime;

public record AnimePutRequest(Long id, String name, LocalDateTime createdAt) {
}
