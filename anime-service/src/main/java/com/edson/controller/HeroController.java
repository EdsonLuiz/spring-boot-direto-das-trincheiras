package com.edson.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/heroes")
public class HeroController {
    private static final List<String> HEROES = List.of("Hero 01", "Hero 02", "Hero 03");

    @GetMapping
    public ResponseEntity<List<String>> listAll(){
        return ResponseEntity.ok(HEROES);
    }

    @GetMapping("/filter/{name}")
    public ResponseEntity<List<String>> listByName(@PathVariable (required = false) String name){
        return ResponseEntity.ok(HEROES.stream().filter(h -> h.equalsIgnoreCase(name)).toList());

    }
}
