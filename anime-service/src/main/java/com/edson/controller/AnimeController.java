package com.edson.controller;

import com.edson.domain.Anime;
import com.edson.mapper.AnimeMapper;
import com.edson.request.AnimePostRequest;
import com.edson.request.AnimePutRequest;
import com.edson.response.AnimeGetResponse;
import com.edson.response.AnimePostResponse;
import com.edson.response.AnimePutResponse;
import com.edson.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/animes")
@Slf4j
@RequiredArgsConstructor
public class AnimeController {
    private static final AnimeMapper ANIME_MAPPER = AnimeMapper.INSTANCE;
    private final AnimeService service;

    @GetMapping
    public ResponseEntity<List<AnimeGetResponse>> list() {
        log.info(Thread.currentThread().getName());
        List<AnimeGetResponse> response = ANIME_MAPPER.toGetResponse(service.list());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<AnimeGetResponse> filterByName(@RequestParam String name) {
        log.info("Filtering anime by name {}", name);
        return service.list().stream()
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findFirst()
                .map(ANIME_MAPPER::toGetResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimeGetResponse> findById(@PathVariable Long id) {
        log.info("Find anime by id {}", id);
        return service.findById(id)
                .map(ANIME_MAPPER::toGetResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AnimePostResponse> save(@RequestBody AnimePostRequest request) {
        var animeEntity = ANIME_MAPPER.fromAnimePostRequestToEntity(request);
        AnimePostResponse response = ANIME_MAPPER.toPostResponse(service.save(animeEntity));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete anime by id {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<AnimePutResponse> update(@RequestBody AnimePutRequest request) {
        var animeEntity = ANIME_MAPPER.fromAnimePutRequestToEntity(request);
        Optional<Anime> response = service.update(animeEntity);

        return response.map(anime -> ResponseEntity.ok(ANIME_MAPPER.toPutResponse(anime)))
                .orElse(ResponseEntity.notFound().build());
    }
}
