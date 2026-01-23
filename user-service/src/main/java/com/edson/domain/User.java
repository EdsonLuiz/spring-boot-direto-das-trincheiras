package com.edson.domain;

import lombok.Builder;

@Builder
public record User(Long id, String firstName, String lastName, String email) {
}
