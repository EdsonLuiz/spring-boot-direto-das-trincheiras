package com.edson.service;

import com.edson.domain.Anime;
import com.edson.exception.NotFoundException;
import com.edson.repository.AnimeHardCodedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeHardCodedRepository repository;

    public List<Anime> list(String name){
        return  StringUtils.hasText(name) ? repository.findByName(name) : repository.findAll() ;
    }

    public Anime findByIdOrThrowNotFound(Long id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Anime not found"));
    }

    public Anime save(Anime anime){
        return repository.save(anime);
    }

    public void delete(Long id) {
        var animeFound = findByIdOrThrowNotFound(id);
        repository.delete(animeFound);
    }

    public void update(Anime anime) {
        var oldAnime = findByIdOrThrowNotFound(anime.id());
        var newAnime = anime.withCreatedAt(oldAnime.createdAt());
        repository.update(newAnime);
    }
}
