package com.edson.controller;

import com.edson.domain.Producer;
import com.edson.mapper.ProducerMapper;
import com.edson.request.ProducerPostRequest;
import com.edson.request.ProducerPutRequest;
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
    private final ProducerMapper mapper;
    private final ProducerService service;

    @GetMapping
    public ResponseEntity<List<ProducerGetResponse>> listAll(@RequestParam(required = false) String name) {
        log.info("list all producers: {}", name);
        List<Producer> response = service.findAll(name);
        List<ProducerGetResponse> producerGetResponses = mapper.toGetResponse(response);
        return ResponseEntity.ok(producerGetResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProducerGetResponse> findById(@PathVariable Long id) {
        log.info("Find Producer by id {}", id);
        Producer producer = service.findByIdOrThrowNotFound(id);
        ProducerGetResponse producerGetResponse = mapper.toGetResponse(producer);
        return ResponseEntity.ok(producerGetResponse);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "x-api-key=123")
    public ResponseEntity<ProducerPostResponse> save(@RequestBody ProducerPostRequest request) {
        log.info("save producer request: {}", request);
        Producer producerEntity = mapper.fromProducerPostRequestToEntity(request);
        ProducerPostResponse response = mapper.toPostResponse(service.save(producerEntity));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete Producer by id {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody ProducerPutRequest request) {
        log.info("update producer request: {}", request);
        var producerEntity = mapper.fromProducerPutRequestToEntity(request);
        service.update(producerEntity);
        return ResponseEntity.notFound().build();
    }

}
