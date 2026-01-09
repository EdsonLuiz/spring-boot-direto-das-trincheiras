package com.edson.repository;

import com.edson.domain.Producer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class ProducerData {
    private final List<Producer> producers = new ArrayList<>();

    {
        producers.addAll(List.of(
                new Producer(1L, "Producer 01", LocalDateTime.now()),
                new Producer(2L, "Producer 02", LocalDateTime.now()),
                new Producer(3L, "Producer 03", LocalDateTime.now()),
                new Producer(4L, "Producer 04", LocalDateTime.now())
        ));
    }

}
