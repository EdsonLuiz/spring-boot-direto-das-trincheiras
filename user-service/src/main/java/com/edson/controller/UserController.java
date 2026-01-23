package com.edson.controller;

import com.edson.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        var user01 = User.builder().id(1L).build();
        var response = List.of(user01);
        return ResponseEntity.ok(response);
    }
}
