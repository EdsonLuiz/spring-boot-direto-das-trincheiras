package com.edson.request;

import java.time.LocalDateTime;

public record ProducerPutRequest(Long id, String name, LocalDateTime createdAt) {
}
