package com.edson.service;

import com.edson.domain.Anime;
import com.edson.exception.NotFoundException;
import com.edson.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository repository;

    @Transactional(readOnly = true)
    public List<Anime> list(String name){
        return  StringUtils.hasText(name) ? repository.findByName(name) : repository.findAll() ;
    }

    @Transactional(readOnly = true)
    public Anime findByIdOrThrowNotFound(Long id){
        return findOrThrow(id);
    }

    @Transactional
    public Anime save(Anime anime){
        return repository.save(anime);
    }

    @Transactional
    public void delete(Long id) {
        var animeFound = findByIdOrThrowNotFound(id);
        repository.delete(animeFound);
    }

    @Transactional
    public void update(Anime anime) {
        var oldAnime = findByIdOrThrowNotFound(anime.getId());
        BeanUtils.copyProperties(anime, oldAnime, "id", "createdAt");
        repository.save(oldAnime);
    }

    private Anime findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Anime not found"));
    }
}
