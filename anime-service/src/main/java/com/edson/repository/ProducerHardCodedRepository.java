package com.edson.repository;

import com.edson.domain.Producer;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProducerHardCodedRepository {
    private static final List<Producer> PRODUCERS = new ArrayList<>();

    static {
        PRODUCERS.addAll(List.of(
                new Producer(1L, "Producer 01", LocalDateTime.now()),
                new Producer(2L, "Producer 02", LocalDateTime.now()),
                new Producer(3L, "Producer 03", LocalDateTime.now()),
                new Producer(4L, "Producer 04", LocalDateTime.now())
        ));
    }

    public List<Producer> findAll(){
        return PRODUCERS;
    }

    public Optional<Producer> findById(Long id){
        return PRODUCERS.stream()
                .filter(producer -> producer.id().equals(id))
                .findFirst();
    }

    public List<Producer> findByName(String name){
        return PRODUCERS.stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .toList();
    }

    public Producer save(Producer producer){
        PRODUCERS.add(producer);
        return producer;
    }

    public void delete(Producer producer) {
        PRODUCERS.removeIf(p -> p.id().equals(producer.id()));
    }

    public void update(Producer entity) {
        delete(entity);
        save(entity);
    }
}
