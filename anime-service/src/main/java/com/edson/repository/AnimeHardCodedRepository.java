package com.edson.repository;

import com.edson.domain.Anime;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AnimeHardCodedRepository {
    private static final List<Anime> ANIMES = new ArrayList<>();

    static {
        ANIMES.addAll(List.of(
                new Anime(1L, "Anime 01", LocalDateTime.now()),
                new Anime(2L, "Anime 02", LocalDateTime.now()),
                new Anime(3L, "Anime 03", LocalDateTime.now()),
                new Anime(4L, "Anime 04", LocalDateTime.now())
        ));
    }

    public List<Anime> findAll(){
        return ANIMES;
    }

    public Optional<Anime> findById(Long id){
        return ANIMES.stream()
                .filter(anime -> anime.id().equals(id))
                .findFirst();
    }

    public List<Anime> findByName(String name){
        return ANIMES.stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .toList();
    }

    public Anime save(Anime anime){
        ANIMES.add(anime);
        return anime;
    }

    public void delete(Anime anime) {
        ANIMES.removeIf(p -> p.id().equals(anime.id()));
    }

    public void update(Anime entity) {
        delete(entity);
        save(entity);
    }
}
