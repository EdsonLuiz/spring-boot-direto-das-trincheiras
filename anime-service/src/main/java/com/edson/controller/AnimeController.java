package com.edson.controller;

import com.edson.domain.Anime;
import com.edson.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/animes")
@Slf4j
@RequiredArgsConstructor
public class AnimeController {
    private final AnimeService service;

    @GetMapping
    public ResponseEntity<List<Anime>> list() {
        log.info(Thread.currentThread().getName());
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/filter")
    public ResponseEntity<Anime> filterByName(@RequestParam String name) {
        log.info("Filtering anime by name {}", name);
        return service.list().stream()
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Anime> findById(@PathVariable Long id) {
        log.info("Find anime by id {}", id);
        return service.list().stream()
                .filter(a -> a.id().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody Anime anime) {
        return ResponseEntity.ok(service.save(anime));
    }
}
