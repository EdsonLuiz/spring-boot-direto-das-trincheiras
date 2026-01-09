package com.edson.repository;

import com.edson.domain.Anime;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class AnimeData {
    private final List<Anime> animes = new ArrayList<>();

    {
        animes.addAll(List.of(
                new Anime(1L, "Anime 01", LocalDateTime.now()),
                new Anime(2L, "Anime 02", LocalDateTime.now()),
                new Anime(3L, "Anime 03", LocalDateTime.now()),
                new Anime(4L, "Anime 04", LocalDateTime.now())
        ));
    }
}
