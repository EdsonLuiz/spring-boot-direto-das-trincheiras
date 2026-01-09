package com.edson.repository;

import com.edson.domain.Anime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnimeHardCodedRepository {
    private final AnimeData animesData;

    public List<Anime> findAll(){
        return animesData.getAnimes();
    }

    public Optional<Anime> findById(Long id){
        return animesData.getAnimes().stream()
                .filter(anime -> anime.id().equals(id))
                .findFirst();
    }

    public List<Anime> findByName(String name){
        return animesData.getAnimes().stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .toList();
    }

    public Anime save(Anime anime){
        animesData.getAnimes().add(anime);
        return anime;
    }

    public void delete(Anime anime) {
        animesData.getAnimes().removeIf(p -> p.id().equals(anime.id()));
    }

    public void update(Anime entity) {
        delete(entity);
        save(entity);
    }
}
