package com.edson.repository;

import com.edson.domain.Anime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeHardCodedRepositoryTest {
    @InjectMocks
    private AnimeHardCodedRepository repository;

    @Mock
    private AnimeData animeData;

    private List<Anime> animeList;

    @BeforeEach
    void setUp() {
        animeList = new ArrayList<>(List.of(
                new Anime(1L, "Anime 01", LocalDateTime.now()),
                new Anime(2L, "Anime 02", LocalDateTime.now()),
                new Anime(3L, "Anime 03", LocalDateTime.now()),
                new Anime(4L, "Anime 04", LocalDateTime.now())
        ));
    }

    @Test
    @DisplayName("findAll returns a list with all elements when successful")
    @Order(1)
    void findAll_ReturnsAllEntities_WhenSuccessful() {
        BDDMockito.given(repository.findAll()).willReturn(animeList);
        var sut = repository.findAll();
        Assertions.assertThat(sut).isNotNull().isNotEmpty().hasSize(animeList.size()).hasSameElementsAs(animeList);
    }

    @Test
    @DisplayName("findById returns an optional with entity when id exists")
    @Order(2)
    void findById_ReturnsOptionalEntity_WhenIdExists() {
        BDDMockito.given(repository.findAll()).willReturn(animeList);
        var animeExpected = animeList.getFirst();
        var sut = repository.findById(animeExpected.id());
        Assertions.assertThat(sut).isNotNull().contains(animeExpected);
    }

    @Test
    @DisplayName("findByName returns an empty list when name is null or empty")
    @Order(3)
    void findByName_ReturnsEmptyList_WhenNameIsNullOrEmpty() {
        BDDMockito.given(repository.findAll()).willReturn(animeList);
        var sut = repository.findByName(null);
        Assertions.assertThat(sut).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findByName returns a list of entities when name matches")
    @Order(4)
    void findByName_ReturnsEntityList_WhenNameMatches() {
        BDDMockito.given(repository.findAll()).willReturn(animeList);
        var expectedAnime = animeList.getFirst();
        var sut = repository.findByName(expectedAnime.name());
        Assertions.assertThat(sut).isNotNull().contains(expectedAnime);
    }

    @Test
    @DisplayName("save persists and returns the entity when successful")
    @Order(5)
    void save_PersistsEntity_WhenSuccessful() {
        BDDMockito.given(repository.findAll()).willReturn(animeList);

        var animeToSave = Anime.builder()
                .id(99L)
                .name("Anime 99")
                .createdAt(LocalDateTime.now())
                .build();

        var sut = repository.save(animeToSave);
        Assertions.assertThat(sut).isEqualTo(animeToSave);
        var animeFound = repository.findById(sut.id());
        Assertions.assertThat(animeFound).isNotNull().contains(sut);
    }

    @Test
    @DisplayName("delete removes the entity when successful")
    @Order(6)
    void delete_RemovesEntity_WhenSuccessful() {
        BDDMockito.given(repository.findAll()).willReturn(animeList);
        var animeToDelete = animeList.getFirst();
        repository.delete(animeToDelete);
        Assertions.assertThat(animeList).doesNotContain(animeToDelete);
    }

    @Test
    @DisplayName("update modifies and persists the entity when successful")
    @Order(7)
    void update_UpdatesEntity_WhenSuccessful() {
        BDDMockito.given(repository.findAll()).willReturn(animeList);

        var animeToUpdate = animeList.getFirst();
        animeToUpdate = Anime.builder()
                .id(animeToUpdate.id())
                .name("UPDATED").build();

        repository.update(animeToUpdate);
        Assertions.assertThat(animeList).contains(animeToUpdate);
        var animeFound = repository.findById(animeToUpdate.id());
        Assertions.assertThat(animeFound).isPresent().contains(animeToUpdate);
        Assertions.assertThat(animeFound.get().name()).isEqualTo(animeToUpdate.name());
    }
}