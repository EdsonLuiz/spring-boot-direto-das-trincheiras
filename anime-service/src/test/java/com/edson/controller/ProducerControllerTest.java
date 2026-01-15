package com.edson.controller;

import com.edson.domain.Producer;
import com.edson.mapper.ProducerMapper;
import com.edson.request.ProducerPostRequest;
import com.edson.request.ProducerPutRequest;
import com.edson.response.ProducerGetResponse;
import com.edson.response.ProducerPostResponse;
import com.edson.service.ProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = ProducerController.class)
@ComponentScan(basePackages = "com.edson")
class ProducerControllerTest {
    public static final String PRODUCERS_URI = "/api/v1/producers";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProducerMapper mapper;

    @MockitoBean
    private ProducerService service;

    private Producer p1, p2;

    private ProducerGetResponse r1, r2;

    @BeforeEach
    void setUp() {
        p1 = Producer.builder().id(1L).name("p1").createdAt(LocalDateTime.now()).build();
        p2 = Producer.builder().id(2L).name("p2").createdAt(LocalDateTime.now()).build();

        r1 = new ProducerGetResponse(1L, "p1");
        r2 = new ProducerGetResponse(2L, "p2");
    }

    @Test
    @DisplayName("GET /api/v1/producers returns a list with all producers when argument is null")
    @Order(1)
    void findAll() throws Exception {
        // Given
        var producers = List.of(p1, p2);
        var responseList = List.of(r1, r2);
        var expectedJson = objectMapper.writeValueAsString(responseList);
        BDDMockito.given(service.findAll(null)).willReturn(producers);
        BDDMockito.given(mapper.toGetResponse(producers)).willReturn(responseList);

        // When
        var reponse = mockMvc.perform(MockMvcRequestBuilders.get(PRODUCERS_URI))
                .andDo(MockMvcResultHandlers.print());

        // Then
        reponse.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        Assertions.assertThat(reponse
                        .andReturn().getResponse().getContentAsString())
                .as("when list all producers with null name, expect HTTP 200 and and json parity with %s", expectedJson)
                .isNotBlank()
                .isEqualTo(expectedJson);

        // Auditing interactions
        BDDMockito.then(service).should().findAll(null);
        BDDMockito.then(mapper).should().toGetResponse(ArgumentMatchers.anyList());
    }

    @Test
    @DisplayName("GET /api/v1/producers?name=p1 returns a list of elements when name matches")
    @Order(2)
    void listALl_ReturnsAFindOfElements_WhenNameMatches() throws Exception {
        // Given
        var targetName = "p1";
        var producers = List.of(p1);
        var responseList = List.of(r1);
        var expectedJson = objectMapper.writeValueAsString(responseList);

        BDDMockito.given(service.findAll(producers.getFirst().name())).willReturn(producers);
        BDDMockito.given(mapper.toGetResponse(producers)).willReturn(responseList);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.get(PRODUCERS_URI)
                        .param("name", targetName))
                .andDo(MockMvcResultHandlers.print());

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        Assertions.assertThat(response.andReturn().getResponse().getContentAsString())
                .as("when listing producers with given name %s, expect HTTP 200 and JSON parity with %s", targetName, expectedJson)
                .isEqualTo(expectedJson);

        // Auditing interactions
        BDDMockito.then(service).should().findAll(targetName);
        BDDMockito.then(mapper).should().toGetResponse(producers);
    }

    @Test
    @DisplayName("GET /api/v1/producers?name=NonExistent returns an empty list when name does not exists")
    @Order(3)
    void listAll_ReturnsAnEmptyFind_WhenNameDoesNotExists() throws Exception {
        // Given
        var nonExistentName = "NonExistentName";
        var producers = List.<Producer>of();
        var producersGetResponse = List.<ProducerGetResponse>of();
        var expectedJson = objectMapper.writeValueAsString(producersGetResponse);

        BDDMockito.given(service.findAll(nonExistentName)).willReturn(producers);
        BDDMockito.given(mapper.toGetResponse(producers)).willReturn(producersGetResponse);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.get(PRODUCERS_URI)
                        .param("name", nonExistentName))
                .andDo(MockMvcResultHandlers.print());

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        Assertions.assertThat(response.andReturn().getResponse().getContentAsString())
                .as("when listing producers with non-existent name %s, expect HTTP 200 an empty JSON array '[]'", nonExistentName);

        // Auditing interactions
        BDDMockito.then(service).should().findAll(nonExistentName);
        BDDMockito.then(mapper).should().toGetResponse(producers);
    }

    @Test
    @DisplayName("GET /api/v1/producers/1 returns a producer when id exists")
    @Order(4)
    void findById_ReturnsProducer_WhenIdExists() throws Exception {
        // Given
        var validId = 1L;
        var producer = p1;
        var producerGetResponse = r1;
        var expectedJson = objectMapper.writeValueAsString(producerGetResponse);

        BDDMockito.given(service.findByIdOrThrowNotFound(validId)).willReturn(producer);
        BDDMockito.given(mapper.toGetResponse(producer)).willReturn(producerGetResponse);

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.get(PRODUCERS_URI + "/{id}", validId))
                .andDo(MockMvcResultHandlers.print());

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));

        Assertions.assertThat(response.andReturn().getResponse().getContentAsString())
                .as("when finding producer with id %d, expect HTTP 200 and JSON parity with %s", expectedJson)
                .isEqualTo(expectedJson);

        // Auditing interactions
        BDDMockito.then(service).should().findByIdOrThrowNotFound(validId);
        BDDMockito.then(mapper).should().toGetResponse(producer);
    }

    @Test
    @DisplayName("GET /api/v1/producers/99 returns 404 Not Found when producer not exist")
    @Order(5)
    void findById_Returns404_WhenProducerNotFound() throws Exception {
        // Given
        var nonExistentId = 99L;

        BDDMockito.given(service.findByIdOrThrowNotFound(nonExistentId))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producer not found"));

        // When
        var response = mockMvc.perform(MockMvcRequestBuilders.get(PRODUCERS_URI + "/{id}", nonExistentId))
                .andDo(MockMvcResultHandlers.print());

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Assertions.assertThat(response.andReturn().getResponse().getErrorMessage())
                .as("when passing an non-existent id %d, expected 404 and specific error reason", nonExistentId)
                .isEqualTo("Producer not found");

        // Auditing interactions
        BDDMockito.then(service).should().findByIdOrThrowNotFound(nonExistentId);
        BDDMockito.then(mapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("POST /api/v1/producers returns 201 Created and the created producer when successful")
    @Order(6)
    void save_ReturnsCreatedAndProducer_WhenSuccessful() throws Exception {
        // Given
        var request = new ProducerPostRequest(p1.name());
        var producerEntity = p1;
        var response = new ProducerPostResponse(p1.id(), p1.name(), p1.createdAt());
        var saved = p1;

        BDDMockito.given(mapper.fromProducerPostRequestToEntity(request)).willReturn(producerEntity);
        BDDMockito.given(mapper.toPostResponse(producerEntity)).willReturn(response);
        BDDMockito.given(service.save(producerEntity)).willReturn(saved);

        // When
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(PRODUCERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key", "123")
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var responseBody = mvcResult.getResponse().getContentAsString();

        // Then
        var actualResponse = objectMapper.readValue(responseBody, ProducerGetResponse.class);

        Assertions.assertThat(actualResponse.name())
                .as("when saving producer with name %s, expect the response to maintain name parity")
                .isEqualTo(request.name());

        Assertions.assertThat(actualResponse.id())
                .as("when saving producer, expect a valid id in the response")
                .isNotNull()
                .isPositive();

        // Auditing interactions
        BDDMockito.then(service).should().save(producerEntity);
        BDDMockito.then(mapper).should().fromProducerPostRequestToEntity(request);
        BDDMockito.then(mapper).should().toPostResponse(saved);
    }

    @Test
    @DisplayName("POST /api/v1/producers returns 400 Bad Request when x-api-key header is missing")
    @Order(7)
    void save_ReturnsBadRequest_WhenHeaderIsMissing() throws Exception {
        // Given
        var request = new ProducerPostRequest(p1.name());
        var jsonBody = objectMapper.writeValueAsString(request);

        // When
        var result = mockMvc.perform(MockMvcRequestBuilders.post(PRODUCERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        // Then
        result.andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // Auditing interactions
        BDDMockito.then(service).shouldHaveNoInteractions();
        BDDMockito.then(mapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("DELETE /api/v1/producers/{id} return 204 when successful")
    @Order(8)
    void delete_ReturnsNoContent_WhenSuccessful() throws Exception {
        // Given
        var existentId = 1L;

        BDDMockito.willDoNothing().given(service).delete(existentId);

        // When
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCERS_URI + "/{id}", existentId))
                .andDo(MockMvcResultHandlers.print());

        var responseBody = mvcResult.andReturn().getResponse().getContentAsString();

        // Then
        mvcResult.andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertThat(responseBody)
                .as("when deleting producer with id %s, expect HTTP 204 and empty BODY", existentId)
                .isEmpty();

        // Auditing interactions
        BDDMockito.then(service).should().delete(existentId);
    }

    @Test
    @DisplayName("DELETE /api/v1/producers/{id} returns 404 Not Found when producer does not exist")
    @Order(9)
    void delete_ReturnsNotFound_WhenIdDoesNotExists() throws Exception {
        // Given
        var nonExistentId = 99L;
        var errorMessage = "Producer not found";

        BDDMockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage))
                .when(service)
                .delete(nonExistentId);

        // When
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCERS_URI + "/{id}", nonExistentId))
                .andDo(MockMvcResultHandlers.print());

        var responseBody = mvcResult.andReturn().getResponse().getErrorMessage();

        // Then
        mvcResult.andExpect(MockMvcResultMatchers.status().isNotFound());

        Assertions.assertThat(responseBody)
                .as("when deleting a producer with non existent id %d, expect 404 NOT FOUND and message %s", errorMessage)
                .isEqualTo(errorMessage);


        // Auditing interactions
        BDDMockito.then(service).should().delete(nonExistentId);
        BDDMockito.then(mapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("UPDATE /api/v1/producers returns 204 no content when update successful")
    @Order(10)
    void update_ReturnsNoContent_WhenSuccessfulUpdate() throws Exception {
        // Given
        var updatedName = "New Name";
        var request = new ProducerPutRequest(p1.id(), updatedName, p1.createdAt());
        var producerEntity = Producer.builder().id(p1.id()).name(updatedName).createdAt(p1.createdAt()).build();
        var jsonRequest = objectMapper.writeValueAsString(request);

        BDDMockito.given(mapper.fromProducerPutRequestToEntity(request)).willReturn(producerEntity);

        // When

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PRODUCERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        var responseBody = mvcResult.andReturn().getResponse().getContentAsString();

        // Then
        mvcResult.andExpect(MockMvcResultMatchers.status().isNoContent());

        // Auditing interactions
        BDDMockito.then(service).should().update(producerEntity);
    }

    @Test
    @DisplayName("UPDATE /api/v1/producers return 404 not found when producer id does not exist")
    @Order(11)
    void update_ReturnsNotFound_WhenIdDoesNotExists() throws Exception {
        // Given
        var errorMessage = "Producer not found";
        var invalidId = 99L;
        var request = new ProducerPutRequest(invalidId, p1.name(), p1.createdAt());
        var producerEntity = p1;

        BDDMockito.given(mapper.fromProducerPutRequestToEntity(request)).willReturn(producerEntity);
        BDDMockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage))
                .when(service).update(producerEntity);

        // When
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PRODUCERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        mvcResult.andExpect(MockMvcResultMatchers.status().isNotFound());

        Assertions.assertThat(mvcResult.andReturn().getResponse().getErrorMessage())
                .as("when updating non-existent producer with id %d, expect HTTP 404 and error message")
                .isEqualTo(errorMessage);

        // Auditing interactions
        BDDMockito.then(service).should().update(producerEntity);
    }
}