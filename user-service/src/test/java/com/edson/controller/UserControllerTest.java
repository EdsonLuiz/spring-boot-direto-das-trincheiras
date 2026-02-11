package com.edson.controller;

import com.edson.domain.User;
import com.edson.exception.DefaultErrorMessage;
import com.edson.exception.NotFoundException;
import com.edson.mapper.UserMapper;
import com.edson.request.UserPostRequest;
import com.edson.request.UserPutRequest;
import com.edson.response.UserGetResponse;
import com.edson.response.UserPostResponse;
import com.edson.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static final String URI = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService service;

    @MockitoBean
    private UserMapper mapper;

    @Test
    @DisplayName("GET /api/v1/users returns 200 with all user")
    void findAll_returnsAllUsers() throws Exception {
        // Given
        List<User> users;
        User user01, user02;

        user01 = User.builder().id(1L).firstName("name01").lastName("lastName01").email("name01@lastName01").build();
        user02 = User.builder().id(2L).firstName("name02").lastName("lastName02").email("name01@lastName02").build();

        users = List.of(user01, user02);

        var userGetResponse01 = UserGetResponse.builder().id(1L).firstName("name01").lastName("lastName01").email("name01@lastName01").build();
        var userGetResponse02 = UserGetResponse.builder().id(2L).firstName("name02").lastName("lastName02").email("name01@lastName02").build();
        var userResponse = List.of(userGetResponse01, userGetResponse02);

        var expectedJson = objectMapper.writeValueAsString(userResponse);

        BDDMockito.given(service.findAll(null)).willReturn(users);
        BDDMockito.given(mapper.fromUserToUserGetResponse(users)).willReturn(userResponse);

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URI));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("GET /api/v1/users?name= returns 200 with a list of filtered users when name matches")
    void findAll_ReturnsFilteredList_WhenNameMatches() throws Exception {
        // Given
        String firstNameToFilter = "name01";
        User user01 = User.builder().id(1L).firstName("name01").lastName("lastName01").email("name01@lastName01").build();
        var users = List.of(user01);

        UserGetResponse responseDTO = UserGetResponse.builder()
                .id(user01.id())
                .firstName(user01.firstName())
                .lastName(user01.lastName())
                .email(user01.email())
                .build();

        List<UserGetResponse> responseList = List.of(responseDTO);
        var expectedJson = objectMapper.writeValueAsString(List.of(responseDTO));

        BDDMockito.given(service.findAll(firstNameToFilter)).willReturn(users);
        BDDMockito.given(mapper.fromUserToUserGetResponse(users)).willReturn(responseList);

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URI)
                .param("firstName", firstNameToFilter));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("GET /api/v1/users returns HTTP STATUS 200 with empty list when filtered first name does not match")
    void findAll_ReturnsEmptyList_WhenFilteredFirstNameDoesNotMatch() throws Exception {
        // Given
        var firstName = "NonExistentFirstName";
        var noUsers = List.<User>of();
        var noResponseDTO = List.<UserGetResponse>of();
        var expectedJson = objectMapper.writeValueAsString(List.of());

        BDDMockito.given(service.findAll(firstName)).willReturn(noUsers);
        BDDMockito.given(mapper.fromUserToUserGetResponse(noUsers)).willReturn(noResponseDTO);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.get(URI)
                .param("firstName", firstName));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns HTTP STATUS 200 with a user when id exists")
    void findById_ReturnsAUser_WhenIdExists() throws Exception {
        // Given
        var userId = 1L;
        User user = User.builder().id(1L).firstName("name01").lastName("lastName01").email("name01@lastName01").build();
        UserGetResponse responseDTO = UserGetResponse.builder()
                .id(user.id())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .email(user.email())
                .build();

        var expectedJson = objectMapper.writeValueAsString(responseDTO);

        BDDMockito.given(service.findById(userId)).willReturn(user);
        BDDMockito.given(mapper.fromUserToUserGetResponse(user)).willReturn(responseDTO);

        // When
        var responseOfRequest = mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", userId));

        // Then
        responseOfRequest
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns HTTP STATUS 404 when id does not exists")
    void findById_ReturnsNotFound_WhenIdDoesNotExists() throws Exception {
        // Given
        var nonExistentId = 99L;

        String errorMessage = "User not found";
        var expectedJson = objectMapper.writeValueAsString(new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(), errorMessage));
        BDDMockito.given(service.findById(nonExistentId)).willThrow(new NotFoundException(errorMessage));

        // When
        var responseOfRequest = mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", nonExistentId));

        // Then
        responseOfRequest
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("POST /api/v1/users return HTTP STATUS 201 when create a user")
    void create_ReturnsCreated_WhenSuccessful() throws Exception {
        // Given
        var userPostRequest = UserPostRequest.builder()
                .firstName("Juca")
                .lastName("Doe")
                .email("juca@doe.com")
                .build();

        var requestBody = objectMapper.writeValueAsString(userPostRequest);
        var newUser = User.builder()
                .id(99L)
                .firstName("Juca")
                .lastName("Doe")
                .email("juca@doe.com")
                .build();

        var userPostResponse = UserPostResponse.builder()
                .id(99L)
                .firstName("Juca")
                .lastName("Doe")
                .email("juca@doe.com")
                .build();

        var expectedJson = objectMapper.writeValueAsString(userPostResponse);

        BDDMockito.given(service.create(newUser)).willReturn(newUser);
        BDDMockito.given(mapper.fromUserToUserPostResponse(newUser)).willReturn(userPostResponse);
        BDDMockito.given(mapper.fromUserPostRequestToUser(BDDMockito.any(UserPostRequest.class))).willReturn(newUser);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} returns HTTP STATUS 204 with empty JSON when success")
    void deleteById_ReturnsNoContent_WhenSuccess() throws Exception {
        // Given

        long validId = 99L;
        BDDMockito.willDoNothing().given(service).deleteById(validId);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/{id}", validId));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Auditing interactions
        BDDMockito.then(service).should().deleteById(validId);
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} returns HTTP STATUS 404 when id does not exists")
    void deleteById_ReturnsNotFound_WhenIdNotExists() throws Exception {
        // Given
        String errorMessage = "User not found";
        var expectedJson = objectMapper.writeValueAsString(new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(), errorMessage));
        long nonExistentId = 99L;
        BDDMockito.willThrow(new NotFoundException(errorMessage)).given(service).deleteById(nonExistentId);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/{id}", nonExistentId));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        // Auditing interactions
        BDDMockito.then(service).should().deleteById(nonExistentId);
    }

    @Test
    @DisplayName("PUT /api/v1/users returns HTTP STATUS 204 when update is successful")
    void update_ReturnsNoContent_WhenSuccessful() throws Exception {
        // Given
        var requestBody = UserPutRequest.builder()
                .id(1L)
                .firstName("juca")
                .lastName("Doe")
                .email("juca@doe.com")
                .build();

        var requestBodyJson = objectMapper.writeValueAsString(requestBody);

        var user = User.builder()
                .id(1L)
                .firstName("juca")
                .lastName("Doe")
                .email("juca@doe.com")
                .build();

        BDDMockito.given(mapper.fromUserPutRequestToUser(BDDMockito.any(UserPutRequest.class))).willReturn(user);
        BDDMockito.willDoNothing().given(service).update(user);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Auditing interactions
        BDDMockito.then(service).should().update(user);
    }

    @Test
    @DisplayName("PUT /api/v1/users returns HTTP STATUS 404 when identity no match")
    void update_ReturnsNotFound_WhenIdentityNotMatch() throws Exception {
        // Given
        String errorMessage = "User not found";
        var expectedJson = objectMapper.writeValueAsString(new DefaultErrorMessage(HttpStatus.NOT_FOUND.value(), errorMessage));
        var requestBody = UserPutRequest.builder()
                .id(99L)
                .firstName("juca")
                .lastName("Doe")
                .email("juca@doe.com")
                .build();

        var requestBodyJson = objectMapper.writeValueAsString(requestBody);

        var user = User.builder()
                .id(99L)
                .firstName("juca")
                .lastName("Doe")
                .email("juca@doe.com")
                .build();

        BDDMockito.given(mapper.fromUserPutRequestToUser(BDDMockito.any(UserPutRequest.class))).willReturn(user);
        BDDMockito.willThrow(new NotFoundException(errorMessage)).given(service).update(user);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        // Auditing interactions
        BDDMockito.then(service).should().update(user);
    }

    @ParameterizedTest
    @DisplayName("POST /api/v1/users returns 400 Bad request when validation fails")
    @MethodSource("create_returnsBadRequest_WhenValidationFails_Scenarios")
    void create_returnsBadRequest_WhenValidationFails(UserPostRequest requestBody, List<String> expectedErrorMessage) throws Exception {
        // Given
        var invalidRequestBodyJson = objectMapper.writeValueAsString(requestBody);
        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBodyJson));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();

        Exception resolvedException = response.andReturn().getResolvedException();
        Assertions.assertThat(resolvedException).isNotNull();
        Assertions.assertThat(resolvedException.getMessage()).contains(expectedErrorMessage);

        // Auditing interactions
        BDDMockito.then(mapper).should(BDDMockito.never()).fromUserPostRequestToUser(BDDMockito.any(UserPostRequest.class));
        BDDMockito.then(service).should(BDDMockito.never()).create(BDDMockito.any(User.class));
        BDDMockito.then(mapper).should(BDDMockito.never()).fromUserToUserPostResponse(BDDMockito.any(User.class));
    }

    private static Stream<Arguments> create_returnsBadRequest_WhenValidationFails_Scenarios() {
        var firstNameNoBlankMessage = "The field 'firstName' is required";
        var lastNameNoBlankMessage = "The field 'lastName' is required";
        var emailNoBlankMessage = "The field 'email' is required";
        var emailPatternMessage = "Email is invalid";
        var allNoBlankMessages = List.of(firstNameNoBlankMessage, lastNameNoBlankMessage, emailNoBlankMessage);

        var allFieldsBlank = UserPostRequest.builder()
                .firstName("")
                .lastName("")
                .email("")
                .build();

        var firstNameBlank = UserPostRequest.builder()
                .firstName("")
                .lastName("Doe")
                .email("valid@email.com")
                .build();

        var lastNameBlank = UserPostRequest.builder()
                .firstName("Juca")
                .lastName("")
                .email("valid@email.com")
                .build();

        var emailNameBlank = UserPostRequest.builder()
                .firstName("Juca")
                .lastName("Doe")
                .email("")
                .build();

        var emailPatternMatch = UserPostRequest.builder()
                .firstName("Juca")
                .lastName("Doe")
                .email("email@@doe.com")
                .build();


        return Stream.of(
                Arguments.of(allFieldsBlank, allNoBlankMessages),
                Arguments.of(firstNameBlank, List.of(firstNameNoBlankMessage)),
                Arguments.of(lastNameBlank, List.of(lastNameNoBlankMessage)),
                Arguments.of(emailNameBlank, List.of(emailNoBlankMessage)),
                Arguments.of(emailPatternMatch, List.of(emailPatternMessage))
        );
    }

    @ParameterizedTest
    @DisplayName("PUT /api/v1/users returns 400 Bad request when validation fails")
    @MethodSource("update_returnsBadRequest_WhenValidationFails_Scenarios")
    void update_returnsBadRequest_WhenValidationFails(UserPutRequest requestBody, List<String> expectedErrorMessage) throws Exception {
        // Given
        var invalidRequestBodyJson = objectMapper.writeValueAsString(requestBody);
        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBodyJson));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Exception resolvedException = response.andReturn().getResolvedException();
        Assertions.assertThat(resolvedException).isNotNull();
        Assertions.assertThat(resolvedException.getMessage()).contains(expectedErrorMessage);

        // Auditing interactions
        BDDMockito.then(mapper).should(BDDMockito.never()).fromUserPutRequestToUser(BDDMockito.any(UserPutRequest.class));
        BDDMockito.then(service).should(BDDMockito.never()).update(BDDMockito.any(User.class));
    }

    private static Stream<Arguments> update_returnsBadRequest_WhenValidationFails_Scenarios() {
        var idNotNullMessage = "The field 'id' is required";
        var firstNameNoBlankMessage = "The field 'firstName' is required";
        var lastNameNoBlankMessage = "The field 'lastName' is required";
        var emailNoBlankMessage = "The field 'email' is required";
        var emailPatternMessage = "Email is invalid";
        var allNoBlankMessages = List.of(idNotNullMessage, firstNameNoBlankMessage, lastNameNoBlankMessage, emailNoBlankMessage);

        var allFieldsBlank = UserPutRequest.builder().build();

        var idNull = UserPutRequest.builder()
                .id(null)
                .firstName("Juca")
                .lastName("Doe")
                .email("valid@email.com")
                .build();

        var firstNameBlank = UserPutRequest.builder()
                .id(99L)
                .firstName("")
                .lastName("Doe")
                .email("valid@email.com")
                .build();

        var lastNameBlank = UserPutRequest.builder()
                .id(99L)
                .firstName("Juca")
                .lastName("")
                .email("valid@email.com")
                .build();

        var emailNameBlank = UserPutRequest.builder()
                .id(99L)
                .firstName("Juca")
                .lastName("Doe")
                .email("")
                .build();

        var emailPatternMatch = UserPutRequest.builder()
                .id(99L)
                .firstName("Juca")
                .lastName("Doe")
                .email("email@@doe.com")
                .build();


        return Stream.of(
                Arguments.of(allFieldsBlank, allNoBlankMessages),
                Arguments.of(idNull, List.of(idNotNullMessage)),
                Arguments.of(firstNameBlank, List.of(firstNameNoBlankMessage)),
                Arguments.of(lastNameBlank, List.of(lastNameNoBlankMessage)),
                Arguments.of(emailNameBlank, List.of(emailNoBlankMessage)),
                Arguments.of(emailPatternMatch, List.of(emailPatternMessage))
        );
    }
}
