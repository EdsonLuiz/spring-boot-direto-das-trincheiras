package com.edson.response;

import lombok.Builder;

@Builder
public record UserPostResponse(Long id, String firstName, String lastName, String email) {
}
