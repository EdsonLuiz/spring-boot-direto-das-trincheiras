package com.edson.service;

import com.edson.domain.Anime;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnimeService {
    private static final List<Anime> ANIMES = new ArrayList<>(List.of(
            new Anime(1L, "Anime 01", LocalDateTime.now()),
            new Anime(2L, "Anime 02", LocalDateTime.now()),
            new Anime(3L, "Anime 03",  LocalDateTime.now()),
            new Anime(4L, "Anime 04", LocalDateTime.now())
    ));

    public List<Anime> list(){
        return  ANIMES;
    }

    public Optional<Anime> findById(Long id){
        return ANIMES.stream()
                .filter(anime -> anime.id().equals(id))
                .findFirst();
    }

    public Anime save(Anime anime){
        ANIMES.add(anime);
        return anime;
    }

    public void delete(Long id) {
        ANIMES.stream()
                .filter(anime -> anime.id().equals(id))
                .findFirst()
                .ifPresent(ANIMES::remove);
    }


    public Optional<Anime> update(Anime animeEntity) {
        if(ANIMES.removeIf(anime -> anime.id().equals(animeEntity.id()))) {
            ANIMES.add(animeEntity);
            return Optional.of(animeEntity);
        }

        return Optional.empty();
    }
}
