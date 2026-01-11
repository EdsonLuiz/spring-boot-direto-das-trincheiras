package com.edson.service;

import com.edson.domain.Anime;
import com.edson.repository.AnimeHardCodedRepository;
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

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeServiceTest {
    @InjectMocks
    private AnimeService service;

    @Mock
    private AnimeHardCodedRepository repository;

    List<Anime> animes;

    @BeforeEach
    void setUp() {
        animes = new ArrayList<>(List.of(
                new Anime(1L, "Anime 01", LocalDateTime.now()),
                new Anime(2L, "Anime 02", LocalDateTime.now()),
                new Anime(3L, "Anime 03", LocalDateTime.now()),
                new Anime(4L, "Anime 04", LocalDateTime.now())
        ));
    }

    @Test
    @DisplayName("list returns all elements when name is null")
    @Order(1)
    void list_returnsAllEntities_whenNameIsNull() {
        // Given
        BDDMockito.given(repository.findAll()).willReturn(animes);

        // When
        var actualAnimes = service.list(null);

        // Then
        Assertions.assertThat(actualAnimes)
                .as("The returned list should contain all animes and not be empty")
                .isNotNull()
                .hasSize(animes.size())
                .containsExactlyInAnyOrderElementsOf(animes);

        // Auditing interactions
        BDDMockito.then(repository).should().findAll();
        BDDMockito.then(repository).should(BDDMockito.never()).findByName(BDDMockito.anyString());
    }

    @Test
    @DisplayName("list returns all elements where the name matches")
    @Order(2)
    void list_ReturnsAllEntities_WhereNameMatches() {
        // Given
        var expectedAnime = animes.getFirst();
        var expectedAnimesFound = List.of(expectedAnime);
        BDDMockito.given(repository.findByName(expectedAnime.name()))
                .willReturn(expectedAnimesFound);

        // When
        var actualAnimes = service.list(expectedAnime.name());

        // Then
        Assertions.assertThat(actualAnimes)
                .as("List by name - should return matching elements and maintain data integrity")
                .isNotNull()
                .hasSize(expectedAnimesFound.size())
                .containsExactlyInAnyOrderElementsOf(expectedAnimesFound);

        // Auditing interactions
        BDDMockito.then(repository).should().findByName(expectedAnime.name());
        BDDMockito.then(repository).should(BDDMockito.never()).findAll();
    }

    @Test
    @DisplayName("list returns an empty list when name no matches")
    @Order(3)
    void list_ReturnsEmptyList_WhenNameNoMatches() {
        // Given
        var nonExistentName = "NonExistentName";
        BDDMockito.given(repository.findByName(nonExistentName)).willReturn(List.of());

        // When
        var actualAnimes = service.list(nonExistentName);

        // Then
        Assertions.assertThat(actualAnimes)
                .as("list by name - should confirm no matches found and return an empty collection")
                .isNotNull()
                .isEmpty();

        // Auditing interactions
        BDDMockito.then(repository).should().findByName(nonExistentName);
    }

    @Test
    @DisplayName("findByIdOrThrowNotFound returns a entity when id exists")
    @Order(4)
    void findByIdOrThrowNotFound_ReturnsEntity_WhenIdExists() {
        // Given
        var expectedAnime = animes.getFirst();
        var existentId = expectedAnime.id();
        BDDMockito.given(repository.findById(existentId)).willReturn(Optional.of(expectedAnime));

        // When
        var actualAnime = service.findByIdOrThrowNotFound(existentId);

        // Then
        Assertions.assertThat(actualAnime)
                .as("List Anime by id - should return the matching entity and ensure field-level parity with the source record")
                .isNotNull()
                .isEqualTo(expectedAnime);

        // Auditing interactions
        BDDMockito.then(repository).should().findById(existentId);
    }

    @Test
    @DisplayName("findByIdOrThrowNotFound throws ResponseStatusException when id is not found")
    @Order(5)
    void findByIdOrThrowNotFound_ThrowNotFound_WhenIdIsNotFound() {
        // Given
        var nonExistentId = 99L;
        BDDMockito.given(repository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.findByIdOrThrowNotFound(nonExistentId))
                .as("Find anime by id - should trigger a not found response and adhere tho the API error contract 404")
                .satisfies(exception -> {
                    Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    Assertions.assertThat(exception.getReason()).isEqualTo("Anime not found");
                });

        // Auditing interactions
        BDDMockito.then(repository).should().findById(nonExistentId);
    }

    @Test
    @DisplayName("save returns a persisted element when successful")
    @Order(6)
    void save_ReturnsPersistedElement_WhenSuccessful() {
        // Given
        var validId = 99L;
        var validName = "validName";
        var validCreatedAt = LocalDateTime.now().minusDays(1);
        var animeToSave = Anime.builder()
                .name(validName)
                .build();

        var animeSaved = Anime.builder()
                .id(validId)
                .name(validName)
                .createdAt(validCreatedAt)
                .build();

        BDDMockito.given(repository.save(animeToSave)).willReturn(animeSaved);

        // When
        var actualAnime = service.save(animeToSave);

        // Then
        Assertions.assertThat(actualAnime)
                .as("Persistence - should return the saved entity and ensure complete data hydration")
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", validId)
                .hasFieldOrPropertyWithValue("name", validName)
                .hasFieldOrPropertyWithValue("createdAt", validCreatedAt);

        // Auditing interactions
        BDDMockito.then(repository).should().save(animeToSave);
    }

    @Test
    @DisplayName("delete removes an element when id exists")
    @Order(7)
    void delete_RemovesElement_WhenIdExists() {
        // Given
        var expectedAnime = animes.getFirst();
        var existentId = expectedAnime.id();
        BDDMockito.given(repository.findById(existentId)).willReturn(Optional.of(expectedAnime));

        // When & Then
        Assertions.assertThatCode(() -> service.delete(existentId))
                .as("when deleting anime with valid id %d, expect successful execution and repository removal trigger", existentId)
                .doesNotThrowAnyException();

        // Auditing interactions
        BDDMockito.then(repository).should().findById(existentId);
        BDDMockito.then(repository).should().delete(expectedAnime);
    }

    @Test
    @DisplayName("delete throws ResponseStatusException when id is not found")
    @Order(8)
    void delete_ThrowNotFound_WhenIdIsNotFound() {
        // Given
        var nonExistentId = 99L;
        BDDMockito.given(repository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .as("when deleting anime with non-existent id %d, expect a 404 Not Found error contract", nonExistentId)
                .isThrownBy(() -> service.delete(nonExistentId))
                .satisfies(exception -> {
                    Assertions.assertThat(exception.getStatusCode())
                            .as("when checking error status with id %d, expect HTTP 404", nonExistentId)
                            .isEqualTo(HttpStatus.NOT_FOUND);
                    Assertions.assertThat(exception.getReason())
                            .as("when checking error reason with id %d, expect 'Anime not found'", nonExistentId)
                            .isEqualTo("Anime not found");
                });

        // Auditing interactions
        BDDMockito.then(repository).should().findById(nonExistentId);
        BDDMockito.then(repository).should(BDDMockito.never()).delete(BDDMockito.any(Anime.class));
    }

    @Test
    @DisplayName("update modifies an element when id exist")
    @Order(9)
    void update_ModifiesElement_WhenIdExists() {
        // Given
        var newAnimeName = "newAnimeName";
        var existentAnime = animes.getFirst();
        var validId = existentAnime.id();
        var originalCreatedAt = existentAnime.createdAt();

        var updateRequest = Anime.builder()
                .id(validId)
                .name(newAnimeName)
                .createdAt(null)
                .build();

        BDDMockito.given(repository.findById(validId)).willReturn(Optional.of(existentAnime));

        // When
        service.update(updateRequest);

        // Then
        var animeCaptor = ArgumentCaptor.forClass(Anime.class);
        BDDMockito.then(repository).should().update(animeCaptor.capture());
        var savedAnime = animeCaptor.getValue();

        Assertions.assertThat(savedAnime)
                .as("when updating anime with id %d, expect successful field update and creation date preservation", validId)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", newAnimeName)
                .hasFieldOrPropertyWithValue("createdAt", originalCreatedAt);

        // Auditing interactions
        BDDMockito.then(repository).should().findById(validId);
    }

    @Test
    @DisplayName("update throws ResponseStatusException when id is not found")
    @Order(10)
    void update_ThrowNotFound_WhenIdIsNotFound() {
        // Given
        var newAnimeName = "newAnimeName";
        var existentAnime = animes.getFirst();
        var nonExistentId = existentAnime.id();

        var updateRequest = Anime.builder()
                .id(nonExistentId)
                .name(newAnimeName)
                .createdAt(null)
                .build();

        BDDMockito.given(repository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then

        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .as("when updating non-existent anime with id %d, expect a 404 Not Found error contract", nonExistentId)
                .isThrownBy(() -> service.update(updateRequest))
                .satisfies(exception -> {

                    Assertions.assertThat(exception.getStatusCode())
                            .as("when checking error status with id %d, expect HTTP 404", nonExistentId)
                            .isEqualTo(HttpStatus.NOT_FOUND);
                    Assertions.assertThat(exception.getReason())
                            .as("when checking error reason with id %d, expect 'Anime not found'", nonExistentId)
                            .isEqualTo("Anime not found");
                });

        // Auditing interactions
        BDDMockito.then(repository).should().findById(nonExistentId);
        BDDMockito.then(repository).should(BDDMockito.never()).update(BDDMockito.any(Anime.class));
    }
}