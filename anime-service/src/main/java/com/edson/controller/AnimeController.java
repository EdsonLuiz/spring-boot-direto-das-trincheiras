package com.edson.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/animes")
@Slf4j
public class AnimeController {

    @GetMapping
    public ResponseEntity<List<String>> list(){
        log.info(Thread.currentThread().getName());
        return ResponseEntity.ok(List.of("Anime01", "Anime02", "Anime03"));
    }
}
