package com.edson.controller;

import com.edson.domain.Anime;
import com.edson.mapper.AnimeMapper;
import com.edson.request.AnimePostRequest;
import com.edson.request.AnimePutRequest;
import com.edson.response.AnimeGetResponse;
import com.edson.response.AnimePostResponse;
import com.edson.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/animes")
@Slf4j
@RequiredArgsConstructor
public class AnimeController {
    private final AnimeMapper mapper;
    private final AnimeService service;

    @GetMapping
    public ResponseEntity<List<AnimeGetResponse>> findAll(@RequestParam(required = false) String name) {
        log.info("find all anime by name: {}", name);
        List<Anime> response = service.list(name);
        return ResponseEntity.ok(mapper.toGetResponse(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimeGetResponse> findById(@PathVariable Long id) {
        log.info("Find anime by id {}", id);
        Anime anime = service.findByIdOrThrowNotFound(id);
        return ResponseEntity.ok(mapper.toGetResponse(anime));
    }

    @PostMapping
    public ResponseEntity<AnimePostResponse> save(@RequestBody AnimePostRequest requestBody) {
        var animeEntity = mapper.fromAnimePostRequestToEntity(requestBody);
        AnimePostResponse response = mapper.toPostResponse(service.save(animeEntity));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete anime by id {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody AnimePutRequest requestBody) {
        log.info("Update anime with id {}", requestBody.id());
        service.update(mapper.fromAnimePutRequestToEntity(requestBody));
        return ResponseEntity.noContent().build();
    }
}
