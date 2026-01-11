package com.edson.service;

import com.edson.domain.Producer;
import com.edson.repository.ProducerHardCodedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerServiceTest {
    @InjectMocks
    private ProducerService service;

    @Mock
    private ProducerHardCodedRepository repository;

    private List<Producer> producers;

    @BeforeEach
    void setUp() {
        producers = new ArrayList<>(List.of(
                new Producer(1L, "Producer 01", LocalDateTime.now()),
                new Producer(2L, "Producer 02", LocalDateTime.now()),
                new Producer(3L, "Producer 03", LocalDateTime.now()),
                new Producer(4L, "Producer 04", LocalDateTime.now())
        ));
    }

    @Test
    @DisplayName("findAll returns all elements when name is null")
    @Order(1)
    void findAll_ReturnsAllEntities_WhenNameIsNull() {
        BDDMockito.given(repository.findAll()).willReturn(producers);
        var sut = service.findAll(null);
        Assertions.assertThat(sut).isNotNull().hasSameElementsAs(producers);
    }

    @Test
    @DisplayName("findAll returns a list of elements when name matches")
    @Order(2)
    void findAll_ReturnsEntitiesByName_WhenNameMatches() {
        var producer = producers.getFirst();
        List<Producer> expectedProducersFound = singletonList(producer);
        BDDMockito.given(repository.findByName(producer.name())).willReturn(expectedProducersFound);
        var sut = service.findAll(producer.name());
        Assertions.assertThat(sut).isNotNull().containsAll(expectedProducersFound);
    }

    @Test
    @DisplayName("findAll returns an empty list of elements when name no matches")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameNoMatches() {
        var name = "no-match";
        BDDMockito.given(service.findAll(name)).willReturn(emptyList());

        var sut = service.findAll(name);
        Assertions.assertThat(sut).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findByIdOrThrowNotFound returns an element when id exists")
    @Order(4)
    void findByIdOrThrowNotFound_ReturnsEntity_WhenIdExists() {
        // Given
        var expectedProducer = producers.getFirst();
        var id = expectedProducer.id();
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(expectedProducer));
        // When
        var actualProducer = service.findByIdOrThrowNotFound(id);
        // Then
        Assertions.assertThat(actualProducer)
                .as("The returned Producer should not be null and must match the expected value.")
                .isNotNull()
                .isEqualTo(expectedProducer);
        BDDMockito.verify(repository, BDDMockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("findByIdOrThrowNotFound throws ResponseStatusException when id is not found")
    @Order(5)
    void findByIdOrThrowNotFound_ThrowsResponseStatusException_WhenIdNotFound() {
        // Given
        var idDoesNotExists = 99L;
        BDDMockito.given(repository.findById(idDoesNotExists))
                .willReturn(Optional.empty());
        // When
        // Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.findByIdOrThrowNotFound(idDoesNotExists))
                .as("It should throw a ResponseStatusException with status 404.")
                .satisfies(exception -> {
                    Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    Assertions.assertThat(exception.getReason()).isEqualTo("Producer not found");
                });
    }

    @Test
    @DisplayName("save returns a persisted element when successful")
    @Order(6)
    void save_ReturnsPersistedEntity_WhenSuccessful() {
        // Given
        var producerToSave = Producer.builder()
                .name("Producer 99")
                .build();
        var producerSaved = Producer.builder()
                .id(99L)
                .name("Producer 99")
                .createdAt(LocalDateTime.now())
                .build();
        BDDMockito.given(repository.save(producerToSave)).willReturn(producerSaved);

        // When
        var actualProducer = service.save(producerToSave);

        // Then
        Assertions.assertThat(actualProducer)
                .as("The returned producer should not be null and should have a generated ID")
                .isNotNull()
                .isEqualTo(producerSaved)
                .hasFieldOrPropertyWithValue("id", 99L);

        Assertions.assertThat(actualProducer.createdAt())
                .as("The persisted producer should have a creation timestamp")
                .isNotNull();
    }

    @Test
    @DisplayName("delete removes an element when id exists")
    @Order(7)
    void delete_RemovesEntity_WhenIdExists() {
        // Given
        var id = 99L;
        var producerToDelete = Producer.builder()
                .id(id)
                .name("Producer 99")
                .createdAt(LocalDateTime.now())
                .build();

        BDDMockito.given(repository.findById(id)).willReturn(Optional.of(producerToDelete));

        // When
        // Then
        Assertions.assertThatCode(() -> service.delete(id))
                .doesNotThrowAnyException();

        BDDMockito.then(repository).should().delete(producerToDelete);
        BDDMockito.then(repository).should().findById(id);
    }

    @Test
    @DisplayName("delete throws ResponseStatusException when id is not found")
    @Order(8)
    void delete_ThrowsResponseStatusException_WhenIdNotFound() {
        // Given
        var nonExistentId = 99L;

        BDDMockito.given(repository.findById(nonExistentId)).willReturn(Optional.empty());

        // When
        // Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.delete(nonExistentId));

        BDDMockito.then(repository).should(BDDMockito.never()).delete(BDDMockito.any(Producer.class));
    }

    @Test
    @DisplayName("update modifies an element when id exists")
    @Order(9)
    void update_UpdatesEntity_WhenIdExists() {
        // Given
        var validId = 99L;
        var originalCreatedAt = LocalDateTime.now().minusDays(1);

        var existingProducer = Producer.builder()
                .id(validId)
                .name("Producer 99")
                .createdAt(originalCreatedAt)
                .build();

        String updatedName = "UPDATED Producer 99";
        var updateRequest = Producer.builder()
                .id(validId)
                .name(updatedName)
                .createdAt(originalCreatedAt)
                .build();

        BDDMockito.given(repository.findById(validId))
                .willReturn(Optional.of(existingProducer));

        // When
        service.update(updateRequest);

        // Then
        ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
        BDDMockito.then(repository).should().findById(validId);
        BDDMockito.then(repository).should().update(producerCaptor.capture());

        Producer savedProducer = producerCaptor.getValue();
        Assertions.assertThat(savedProducer.name())
                .as("The name should be updated to the new value")
                .isEqualTo(updatedName);

        Assertions.assertThat(savedProducer.createdAt())
                .as("The original createdAt date must be preserved after update")
                .isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("update throws ResponseStatusException when id is not found")
    @Order(10)
    void update_ThrowsResponseStatusException_WhenIdNotFound() {
        // Given
        var nonExistentId = 99L;
        var producerToUpdate = Producer.builder()
                .id(nonExistentId)
                .name("Invalid Producer 99")
                .build();

        BDDMockito.given(repository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.update(producerToUpdate))
                .as("Should throw 404 not found when producer does not exist")
                .satisfies(exception -> {
                    Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    Assertions.assertThat(exception.getReason()).isEqualTo("Producer not found");
                });

        // Auditing interactions
        BDDMockito.then(repository).should().findById(nonExistentId);
        BDDMockito.then(repository).should(BDDMockito.never()).update(BDDMockito.any(Producer.class));
    }
}