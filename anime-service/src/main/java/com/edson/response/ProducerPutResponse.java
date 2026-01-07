package com.edson.response;

import java.time.LocalDateTime;

public record ProducerPutResponse(Long id, String name, LocalDateTime createdAt) {
}
