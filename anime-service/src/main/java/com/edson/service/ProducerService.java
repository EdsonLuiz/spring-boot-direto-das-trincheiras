package com.edson.service;

import com.edson.domain.Producer;
import com.edson.exception.NotFoundException;
import com.edson.repository.ProducerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final ProducerRepository repository;

    public List<Producer> findAll(String name){
        return name == null ? repository.findAll() : repository.findByName(name);
    }

    public Producer findByIdOrThrowNotFound(Long id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Producer not found"));
    }

    public Producer save(Producer producer){
        return repository.save(producer);
    }

    public void delete(Long id) {
        Producer producer = findByIdOrThrowNotFound(id);
        repository.delete(producer);
    }

    public void update(Producer entity) {
        this.assertProducerExists(entity.getId());
        repository.save(entity);
    }

    private void assertProducerExists(Long id){
        this.findByIdOrThrowNotFound(id);
    }
}
