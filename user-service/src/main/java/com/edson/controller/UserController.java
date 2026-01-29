package com.edson.controller;

import com.edson.domain.User;
import com.edson.mapper.UserMapper;
import com.edson.request.UserPostRequest;
import com.edson.request.UserPutRequest;
import com.edson.response.UserGetResponse;
import com.edson.response.UserPostResponse;
import com.edson.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserGetResponse>> findAll(@RequestParam (required = false) String firstName) {
        var serviceResponse = service.findAll(firstName);
        var response = mapper.fromUserToUserGetResponse(serviceResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetResponse> findById(@PathVariable Long id) {
        var serviceResponse = service.findById(id);
        var responseDTO = mapper.fromUserToUserGetResponse(serviceResponse);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<UserPostResponse> create(@RequestBody @Valid UserPostRequest request) {
        var user = mapper.fromUserPostRequestToUser(request);
        var serviceResponse = service.create(user);
        var userPostResponse = mapper.fromUserToUserPostResponse(serviceResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(userPostResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UserPutRequest request) {
        User user = mapper.fromUserPutRequestToUser(request);
        service.update(user);
        return ResponseEntity.noContent().build();
    }
}
