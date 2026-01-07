package com.edson.service;

import com.edson.domain.Producer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProducerService {
    private static final List<Producer> producers = new ArrayList<>();
    static {
        producers.addAll(List.of(
                new Producer(1L, "Producer 01", LocalDateTime.now()),
                new Producer(2L, "Producer 02", LocalDateTime.now()),
                new Producer(3L, "Producer 03", LocalDateTime.now()),
                new Producer(4L, "Producer 04", LocalDateTime.now())
        ));
    }

    public List<Producer> list(){
        return  producers;
    }

    public Producer save(Producer producer){
        producers.add(producer);
        return producer;
    }
}
