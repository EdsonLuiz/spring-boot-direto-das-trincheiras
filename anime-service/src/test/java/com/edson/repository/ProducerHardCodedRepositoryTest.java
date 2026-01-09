package com.edson.repository;

import com.edson.domain.Producer;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerHardCodedRepositoryTest {
    @InjectMocks
    private ProducerHardCodedRepository repository;

    @Mock
    private ProducerData producerData;
    private final List<Producer> producerList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        producerList.addAll(List.of(
                new Producer(1L, "Producer 01", LocalDateTime.now()),
                new Producer(2L, "Producer 02", LocalDateTime.now()),
                new Producer(3L, "Producer 03", LocalDateTime.now()),
                new Producer(4L, "Producer 04", LocalDateTime.now())
        ));

    }

    @Test
    @DisplayName("findAll returns a list with all producers")
    @Order(1)
    void findAll_ReturnsAllProducers_WhenSuccess() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var sut = repository.findAll();
        Assertions.assertThat(sut)
                .isNotNull()
                .hasSize(producerList.size())
                .hasSameElementsAs(producerList);
    }

    @Test
    @DisplayName("findAll returns a producer with given id")
    @Order(2)
    void findById_ReturnsAProducer_WhenSuccess() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var expectedProducer = producerList.getFirst();

        var sut = repository.findById(expectedProducer.id());
        Assertions.assertThat(sut)
                .isPresent()
                .contains(expectedProducer);
    }

    @Test
    @DisplayName("findByName returns an empty list when name is null")
    @Order(3)
    void findByName_ReturnsAnEmptyList_WhenNameIsNull() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var sut = repository.findByName(null);
        Assertions.assertThat(sut)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findByName returns a list with found objects when name match")
    @Order(4)
    void findByName_ReturnsAProducerList_WhenNameMatch() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var expectedProducer = producerList.getFirst();

        var sut = repository.findByName(expectedProducer.name());
        Assertions.assertThat(sut)
                .contains(expectedProducer);
    }

    @Test
    @DisplayName("save creates a producer")
    @Order(5)
    void save_CreatesAProducer_WhenSuccess() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var newProducer = Producer.builder()
                .id(99L)
                .name("Producer 99")
                .createdAt(LocalDateTime.now())
                .build();


        var sut = repository.save(newProducer);
        Assertions.assertThat(sut).isEqualTo(newProducer).hasNoNullFieldsOrProperties();
        Optional<Producer> producerFound = repository.findById(newProducer.id());
        Assertions.assertThat(producerFound).isPresent().contains(newProducer);
    }

    @Test
    @DisplayName("delete removes a producer")
    @Order(6)
    void delete_RemoveAProducer_WhenSuccess() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var producerToDelete = producerList.getFirst();


        repository.delete(producerToDelete);

        Assertions.assertThat(this.producerList).doesNotContain(producerToDelete);
    }

    @Test
    @DisplayName("update updates a producer")
    @Order(7)
    void update_UpdateAProducer_WhenSuccess() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var producerToUpdate = producerList.getFirst();
        var producerChanged = Producer.builder()
                .id(producerToUpdate.id())
                .name("UPDATED PRODUCER")
                .createdAt(producerToUpdate.createdAt())
                .build();

        repository.update(producerChanged);

        Assertions.assertThat(this.producerList).contains(producerChanged);

        var producerFound = repository.findById(producerChanged.id());
        Assertions.assertThat(producerFound).isPresent().contains(producerChanged);
        Assertions.assertThat(producerFound.get().name()).isEqualTo(producerChanged.name());
    }
}