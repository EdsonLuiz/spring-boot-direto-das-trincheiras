package com.edson.repository;

import com.edson.domain.Producer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProducerHardCodedRepository {
    private final ProducerData producerData;

    public List<Producer> findAll(){
        return producerData.getProducers();
    }

    public Optional<Producer> findById(Long id){
        return producerData.getProducers().stream()
                .filter(producer -> producer.id().equals(id))
                .findFirst();
    }

    public List<Producer> findByName(String name){
        return producerData.getProducers().stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .toList();
    }

    public Producer save(Producer producer){
        producerData.getProducers().add(producer);
        return producer;
    }

    public void delete(Producer producer) {
        producerData.getProducers().removeIf(p -> p.id().equals(producer.id()));
    }

    public void update(Producer entity) {
        delete(entity);
        save(entity);
    }
}
