package com.edson.mapper;

import com.edson.domain.Producer;
import com.edson.request.ProducerPostRequest;
import com.edson.request.ProducerPutRequest;
import com.edson.response.ProducerGetResponse;
import com.edson.response.ProducerPostResponse;
import com.edson.response.ProducerPutResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProducerMapper {
    ProducerMapper INSTANCE = Mappers.getMapper(ProducerMapper.class);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", expression = "java(java.util.concurrent.ThreadLocalRandom.current().nextLong(0, 100_000))")
    Producer fromProducerPostRequestToEntity(ProducerPostRequest request);

    ProducerPostResponse toPostResponse(Producer producer);

    ProducerGetResponse toGetResponse(Producer producer);

    List<ProducerGetResponse> toGetResponse(List<Producer> producer);

    Producer fromProducerPutRequestToEntity(ProducerPutRequest request);

    ProducerPutResponse toPutResponse(Producer producer);
}
