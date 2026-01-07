package com.edson.mapper;

import com.edson.domain.Anime;
import com.edson.request.AnimePostRequest;
import com.edson.request.AnimePutRequest;
import com.edson.response.AnimeGetResponse;
import com.edson.response.AnimePostResponse;
import com.edson.response.AnimePutResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AnimeMapper {
    AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", expression = "java(java.util.concurrent.ThreadLocalRandom.current().nextLong(0, 100_000))")
    Anime fromAnimePostRequestToEntity(AnimePostRequest animePostRequest);

    AnimePostResponse toPostResponse(Anime anime);

    AnimeGetResponse toGetResponse(Anime anime);

    List<AnimeGetResponse> toGetResponse(List<Anime> animes);

    Anime fromAnimePutRequestToEntity(AnimePutRequest animePutRequest);

    AnimePutResponse toPutResponse(Anime anime);
}
