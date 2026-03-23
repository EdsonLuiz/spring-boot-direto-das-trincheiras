package com.edson.mapper;

import com.edson.domain.Producer;
import com.edson.request.ProducerPostRequest;
import com.edson.request.ProducerPutRequest;
import com.edson.response.ProducerGetResponse;
import com.edson.response.ProducerPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProducerMapper {

    Producer fromProducerPostRequestToEntity(ProducerPostRequest request);

    ProducerPostResponse toPostResponse(Producer producer);

    ProducerGetResponse toGetResponse(Producer producer);

    List<ProducerGetResponse> toGetResponse(List<Producer> producer);

    Producer fromProducerPutRequestToEntity(ProducerPutRequest request);
}
