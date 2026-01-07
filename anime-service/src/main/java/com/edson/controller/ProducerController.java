package com.edson.controller;

import com.edson.domain.Producer;
import com.edson.mapper.ProducerMapper;
import com.edson.request.ProducerPostRequest;
import com.edson.response.ProducerGetResponse;
import com.edson.response.ProducerPostResponse;
import com.edson.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/producers")
@Slf4j
@RequiredArgsConstructor
public class ProducerController {
    private static final ProducerMapper PRODUCER_MAPPER = ProducerMapper.INSTANCE;
    private final ProducerService service;


    @GetMapping
    public ResponseEntity<List<ProducerGetResponse>> list() {
        log.info(Thread.currentThread().getName());
        var response = PRODUCER_MAPPER.toGetResponse(service.list());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<ProducerGetResponse> filterByName(@RequestParam String name) {
        log.info("Filtering Producer by name {}", name);
        return service.list().stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst()
                .map(PRODUCER_MAPPER::toGetResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProducerGetResponse> findById(@PathVariable Long id) {
        log.info("Find Producer by id {}", id);
        return service.findById(id)
                .map(PRODUCER_MAPPER::toGetResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "x-api-key=123")
    public ResponseEntity<ProducerPostResponse> save(@RequestBody ProducerPostRequest request) {
        Producer producerEntity = PRODUCER_MAPPER.toEntity(request);
        ProducerPostResponse response = PRODUCER_MAPPER.toPostResponse(service.save(producerEntity));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete Producer by id {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
