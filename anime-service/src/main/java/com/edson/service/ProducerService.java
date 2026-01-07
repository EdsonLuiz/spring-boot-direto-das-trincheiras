package com.edson.service;

import com.edson.domain.Producer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProducerService {
    private static final List<Producer> PRODUCERS = new ArrayList<>();
    static {
        PRODUCERS.addAll(List.of(
                new Producer(1L, "Producer 01", LocalDateTime.now()),
                new Producer(2L, "Producer 02", LocalDateTime.now()),
                new Producer(3L, "Producer 03", LocalDateTime.now()),
                new Producer(4L, "Producer 04", LocalDateTime.now())
        ));
    }

    public List<Producer> list(){
        return PRODUCERS;
    }

    public Optional<Producer> findById(Long id){
        return PRODUCERS.stream()
                .filter(producer -> producer.id().equals(id))
                .findFirst();
    }

    public Producer save(Producer producer){
        PRODUCERS.add(producer);
        return producer;
    }

    public void delete(Long id) {
        PRODUCERS.stream()
                .filter(producer -> producer.id().equals(id))
                .findFirst()
                .ifPresent(PRODUCERS::remove);
    }

    public Optional<Producer> update(Producer entity) {
        if(PRODUCERS.removeIf(producer -> producer.id().equals(entity.id()))) {
            PRODUCERS.add(entity);
            return Optional.of(entity);
        }
        return Optional.empty();
    }
}
