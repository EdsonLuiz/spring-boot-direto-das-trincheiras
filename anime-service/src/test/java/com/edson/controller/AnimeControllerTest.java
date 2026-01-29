package com.edson.controller;

import com.edson.domain.Anime;
import com.edson.mapper.AnimeMapper;
import com.edson.request.AnimePostRequest;
import com.edson.request.AnimePutRequest;
import com.edson.response.AnimeGetResponse;
import com.edson.response.AnimePostResponse;
import com.edson.service.AnimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;

@WebMvcTest(AnimeController.class)
class AnimeControllerTest {
    private static final String URI = "/api/v1/animes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnimeService service;

    @MockitoBean
    private AnimeMapper mapper;

    private Anime anime01, anime02;
    private AnimeGetResponse animeGetResponse01, animeGetResponse02;

    @BeforeEach

    void setUp() {
        anime01 = Anime.builder()
                .name("Anime01")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .build();

        anime02 = Anime.builder()
                .name("Anime02")
                .id(2L)
                .createdAt(LocalDateTime.now())
                .build();

        animeGetResponse01 = new AnimeGetResponse(1L, "Anime01");
        animeGetResponse02 = new AnimeGetResponse(2L, "Anime02");
    }

    @Test
    @DisplayName("GET /api/v1/animes returns a list with all producers when name is null")
    @Order(1)
    void findAll_returns200AndList() throws Exception {
        // Given
        var expectedAnimes = List.of(animeGetResponse01, animeGetResponse02);
        var expectedJson = objectMapper.writeValueAsString(expectedAnimes);

        given(service.list(null)).willReturn(List.of(anime01, anime02));
        given(mapper.toGetResponse(List.of(anime01, anime02))).willReturn(List.of(animeGetResponse01, animeGetResponse02));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(URI))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("HTTP GET /api/v1/animes?name=Animes01 → 200 when name matches")
    @Order(2)
    void findAll_return200_whenNameMatches() throws Exception {
        var nameToFind = anime01.name();
        var animes = List.of(anime01);
        var animesResponse = List.of(animeGetResponse01);
        var expectedJson = objectMapper.writeValueAsString(animesResponse);

        // Given
        given(service.list(nameToFind)).willReturn(animes);
        given(mapper.toGetResponse(animes)).willReturn(animesResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", nameToFind))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("HTTP GET /api/v1/animes?name=Animes99 → 200 when name does not exists")
    @Order(3)
    void findAll_return200_whenNameDoesNotExists() throws Exception {
        var nameToFind = "NonExistentName";
        var animes = List.of(anime01);
        var animesResponse = List.<AnimeGetResponse>of();
        var expectedJson = objectMapper.writeValueAsString(animesResponse);

        // Given
        given(service.list(nameToFind)).willReturn(animes);
        given(mapper.toGetResponse(animes)).willReturn(animesResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("HTTP GET /api/v1/animes/1 → 200 when anime exists")
    @Order(4)
    void findById_returns200_whenAnimeExists() throws Exception {
        // Given
        var requestedId = 1L;
        var expectedJson = objectMapper.writeValueAsString(animeGetResponse01);
        given(service.findByIdOrThrowNotFound(requestedId)).willReturn(anime01);
        given(mapper.toGetResponse(anime01)).willReturn(animeGetResponse01);


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", requestedId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @DisplayName("HTTP GET /api/v1/animes/99 → 404 when anime does not exists")
    @Order(5)
    void findById_returns404_whenAnimeDoesNotExists() throws Exception {
        // Given
        var requestedId = 99L;
        var errorMessage = "Anime not found";
        given(service.findByIdOrThrowNotFound(requestedId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", requestedId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("HTTP POST /api/v1/animes → 201 when request is valid")
    @Order(6)
    void save_returns201_whenSuccessful() throws Exception {
        // Given
        var animePostResponse = new AnimePostResponse(anime01.id(), anime01.name(), anime01.createdAt());
        AnimePostRequest animePostRequest = new AnimePostRequest("Anime01");
        var jsonRequest = objectMapper.writeValueAsString(animePostRequest);
        var jsonResponse = objectMapper.writeValueAsString(anime01);

        given(mapper.fromAnimePostRequestToEntity(animePostRequest)).willReturn(anime01);
        given(service.save(any())).willReturn(anime01);
        given(mapper.toPostResponse(anime01)).willReturn(animePostResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(jsonResponse));
    }

    @Test
    @DisplayName("HTTP DELETE /api/v1/animes/{id} → 204 No content when successful")
    @Order(7)
    void delete_Returns204_WhenSuccessful() throws Exception {
        var existentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/{id}", existentId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("HTTP DELETE /api/v1/animes/{id} → 404 Not found when resource does not exists")
    @Order(7)
    void delete_Returns404_WhenResourceDoesNotExists() throws Exception {
        // Given
        willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).given(service).delete(anyLong());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/{id}", 99L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("HTTP UPDATE /api/v1/animes → 204 No content when update is successful")
    @Order(8)
    void update_Returns204_WhenUpdateIsSuccessful() throws Exception {
        // Given
        var requestObject = new AnimePutRequest(99L, "Some New Name");

        given(mapper.fromAnimePutRequestToEntity(any())).willReturn(anime01);
        willDoNothing().given(service).update(any());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(URI)
                        .content(objectMapper.writeValueAsString(requestObject))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("HTTP UPDATE /api/v1/animes → 404 Not found when update element does not exists")
    @Order(9)
    void update_Returns404_WhenUpdateElementDoesNotExists() throws Exception {
        // Given
        var requestObject = new AnimePutRequest(99L, "InvalidTest");

        given(mapper.fromAnimePutRequestToEntity(any())).willReturn(anime01);
        willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).given(service).update(any());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(URI)
                        .content(objectMapper.writeValueAsString(requestObject))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("create_ReturnsBadRequest_WhenValidationFails_Scenarios")
    @DisplayName("POST /api/v1/animes returns 400 Bad Request when validation fails")
    void create_ReturnsBadRequest_WhenValidationFails(AnimePostRequest requestBodyObject, String expectedMessage) throws Exception {
        // Given
        var requestBodyJson = objectMapper.writeValueAsString(requestBodyObject);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Auditing interactions
        BDDMockito.then(mapper).should(BDDMockito.never()).fromAnimePostRequestToEntity(BDDMockito.any(AnimePostRequest.class));
        BDDMockito.then(service).should(BDDMockito.never()).save(BDDMockito.any(Anime.class));
    }

    private static Stream<Arguments> create_ReturnsBadRequest_WhenValidationFails_Scenarios() {
        var animePostRequestBlankName = new AnimePostRequest("");
        var nameNoBlankMessage = "The field 'name' is required";

        return Stream.of(
                Arguments.of(animePostRequestBlankName, nameNoBlankMessage)
        );
    }

    @ParameterizedTest
    @MethodSource("update_ReturnsBadRequest_WhenValidationFails_Scenarios")
    @DisplayName("PUT /api/v1/animes returns 400 Bad Request when validation fails")
    void update_ReturnsBadRequest_WhenValidationFails(AnimePutRequest requestBodyObject) throws Exception {
        // Given
        var requestBodyJson = objectMapper.writeValueAsString(requestBodyObject);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Auditing interactions
        BDDMockito.then(mapper).should(BDDMockito.never()).fromAnimePutRequestToEntity(BDDMockito.any(AnimePutRequest.class));
        BDDMockito.then(service).should(BDDMockito.never()).update(BDDMockito.any(Anime.class));
    }

    private static Stream<Arguments> update_ReturnsBadRequest_WhenValidationFails_Scenarios() {
        var requestAllBlak = AnimePutRequest.builder().build();
        var requestNullId = AnimePutRequest.builder().id(null).name("Juca").build();
        var requestBlankName = AnimePutRequest.builder().id(99L).name("").build();

        return Stream.of(
                Arguments.of(requestAllBlak),
                Arguments.of(requestNullId),
                Arguments.of(requestBlankName)
        );
    }

}