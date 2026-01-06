package com.edson.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/heroes")
public class HeroController {
    private static final List<String> HEROES = List.of("Hero 01", "Hero 02", "Hero 03");

    @GetMapping
    public ResponseEntity<List<String>> listAll(){
        return ResponseEntity.ok(HEROES);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<String>> listByName(@RequestParam(required = false) String name){
        return ResponseEntity.ok(HEROES.stream().filter(h -> h.equalsIgnoreCase(name)).toList());
    }

    @GetMapping("/{name}")
    public ResponseEntity<String> findByName(@PathVariable String name){
        return HEROES.stream()
                .filter(h -> h.equalsIgnoreCase(name))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
