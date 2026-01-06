package com.edson.service;

import com.edson.domain.Anime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AnimeService {
    private static final List<Anime> ANIMES = new ArrayList<>(List.of(
            new Anime(1L, "Anime 01"),
            new Anime(2L, "Anime 02"),
            new Anime(3L, "Anime 03"),
            new Anime(4L, "Anime 04")
    ));

    public List<Anime> list(){
        return  ANIMES;
    }

    public Anime save(Anime anime){
        long id = ThreadLocalRandom.current().nextLong(0, 100_000);
        var newAnime = anime.withId(id);
        ANIMES.add(newAnime);
        return newAnime;
    }
}
