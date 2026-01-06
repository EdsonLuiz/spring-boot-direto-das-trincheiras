package com.edson.domain;

public record Anime(Long id, String name) {
    public Anime withId(Long id) {
        return new Anime(id, this.name());
    }
}
