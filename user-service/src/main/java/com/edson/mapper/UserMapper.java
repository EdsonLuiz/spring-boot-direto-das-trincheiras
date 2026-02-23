package com.edson.mapper;

import com.edson.domain.User;
import com.edson.request.UserPostRequest;
import com.edson.request.UserPutRequest;
import com.edson.response.UserGetResponse;
import com.edson.response.UserPostResponse;
import lombok.Generated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Generated
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    List<UserGetResponse> fromUserToUserGetResponse(List<User> user);
    UserGetResponse fromUserToUserGetResponse(User user);

    User fromUserPostRequestToUser(UserPostRequest request);

    UserPostResponse fromUserToUserPostResponse(User entity);

    User fromUserPutRequestToUser(UserPutRequest request);
}
