package com.edson.response;

import java.time.LocalDateTime;

public record AnimePutResponse(Long id, String name, LocalDateTime createdAt) {
}
