package com.edson.request;

import lombok.Builder;

@Builder
public record UserPutRequest(Long id, String firstName, String lastName, String email) {
}
