package com.edson.service;

import com.edson.domain.User;
import com.edson.repository.UserHardCodedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService service;

    @Mock
    private UserHardCodedRepository repository;

    private User testUser01, testUser02;
    private List<User> users;

    @BeforeEach
    void setUp() {
        testUser01 = User.builder()
                .id(1L)
                .firstName("testUser01")
                .lastName("lastName")
                .email("testuser01@user.com")
                .build();

        testUser02 = User.builder()
                .id(2L)
                .firstName("testUser02")
                .lastName("lastName")
                .email("testuser01@user.com")
                .build();

        users = new ArrayList<>(List.of(testUser01, testUser02));
    }

    @Test
    @DisplayName("findAll returns all users when firstName is null")
    void findAll_returnsAllUsers_WhenFirstNameIsNull() {
        // Given
        BDDMockito.given(repository.findAll()).willReturn(users);

        // When
        var actualResult = service.findAll(null);

        // Then

        Assertions.assertThat(actualResult)
                .as("Check that all users are returned")
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(testUser01, testUser02));

        // Auditing interactions
        BDDMockito.then(repository).should().findAll();
        BDDMockito.then(repository).should(BDDMockito.never()).findAllByName(BDDMockito.anyString());
    }

    @Test
    @DisplayName("findAll returns filtered users when firstName exists")
    void findAll_ReturnsFilteredUsers_WhenFirstNameExists() {
        // Given
        var existentFirstName = testUser01.firstName();
        BDDMockito.given(repository.findAllByName(existentFirstName)).willReturn(List.of(testUser01));

        // When
        var actualResult = service.findAll(existentFirstName);

        // Then

        Assertions.assertThat(actualResult)
                .as("Check that only filtered users are returned")
                .hasSize(1)
                .containsExactlyInAnyOrderElementsOf(List.of(testUser01));

        // Auditing interactions
        BDDMockito.then(repository).should().findAllByName(existentFirstName);
        BDDMockito.then(repository).should(BDDMockito.never()).findAll();
    }

    @Test
    @DisplayName("findById returns user when user exists")
    void findById_ReturnsUser_WhenUserExists() {
        // Given
        var existentId = 1L;
        BDDMockito.given(repository.findById(existentId)).willReturn(Optional.of(testUser01));

        // When
        User actualResult = service.findById(existentId);

        // Then
        Assertions.assertThat(actualResult)
                .as("Check that user with existent id is returned")
                .isEqualTo(testUser01);

        // Auditing interactions
        BDDMockito.then(repository).should().findById(existentId);
    }

    @Test
    @DisplayName("findById throws ResponseStatusException when user does not exists")
    void findById_ThrowsResponseStatusException_WhenUserDoesNotExists() {
        // Given
        var nonExistentId = 99L;

        BDDMockito.given(repository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.findById(nonExistentId))
                .withMessageContaining("User not found")
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);

        // Auditing interactions
        BDDMockito.then(repository).should().findById(nonExistentId);
    }

    @Test
    @DisplayName("create returns persisted user when successful")
    void create_ReturnsPersistedUser_WhenSuccessful() {
        // Given
        var newUser = User.builder()
                .id(99L)
                .firstName("Maria")
                .lastName("Doe")
                .email("maria@doe.com")
                .build();

        BDDMockito.given(repository.create(newUser)).willReturn(newUser);

        // When
        User actualResult = service.create(newUser);

        // Then
        Assertions.assertThat(actualResult)
                .isNotNull()
                .isEqualTo(newUser);

        // Auditing interactions
        BDDMockito.then(repository).should().create(newUser);
    }

    @Test
    @DisplayName("deleteById removes user when user exists")
    void deleteById_RemovesUserWhenUserExists() {
        // Given
        var existentUserId = 1L;
        BDDMockito.given(repository.findById(existentUserId)).willReturn(Optional.of(testUser01));

        // When
        service.deleteById(existentUserId);

        // Then

        // Auditing interactions
        BDDMockito.then(repository).should().deleteById(existentUserId);
        BDDMockito.then(repository).should().findById(existentUserId);
    }

    @Test
    @DisplayName("deleteById returns not found when user does not exists")
    void deleteById_ThrowsResponseStatusException_WhenUserDoesNotExists() {
        // Given
        var nonExistentId = 99L;
        BDDMockito.given(repository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.deleteById(nonExistentId))
                .withMessageContaining("User not found")
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);

        // Auditing interactions
        BDDMockito.then(repository).should().findById(nonExistentId);
        BDDMockito.then(repository).should(BDDMockito.never()).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("update returning nothing and updates user when user exists")
    void update_ReturningNothingAndUpdateUser_WhenUserExists() {
        // Given
        var userBeforeUpdate = testUser01;

        var newFirstName = "Juca";
        var newLastName = "Doe";
        var newEmail = "juca@doe.com";
        var userToBeUpdated = User.builder()
                .id(1L)
                .firstName(newFirstName)
                .lastName(newLastName)
                .email(newEmail)
                .build();

        BDDMockito.given(repository.findById(testUser01.id())).willReturn(Optional.of(testUser01));

        // When
        service.update(userToBeUpdated);

        // Then
        var userCaptor = ArgumentCaptor.forClass(User.class);
        BDDMockito.then(repository).should().update(userCaptor.capture());
        var capturedUser = userCaptor.getValue();

        Assertions.assertThat(capturedUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("firstName", newFirstName)
                .hasFieldOrPropertyWithValue("lastName", newLastName)
                .hasFieldOrPropertyWithValue("email", newEmail);

        // Aqui verificamos se o repositório foi chamado com o PRÓPRIO objeto userToBeUpdated.
        // O Mockito usa o método .equals() do objeto ou verifica a referencia de memória.
        // isto evita o uso do captor
        // BDDMockito.then(repository).should().update(userToBeUpdated);

        // Auditing interactions
        BDDMockito.then(repository).should().findById(testUser01.id());
    }

    @Test
    @DisplayName("update returns not found when user does not exists")
    void update_ReturnsNotFound_WhenUserDoesNotExists() {
        // Given
        var newFirstName = "Juca";
        var newLastName = "Doe";
        var newEmail = "juca@doe.com";
        var userToBeUpdated = User.builder()
                .id(99L)
                .firstName(newFirstName)
                .lastName(newLastName)
                .email(newEmail)
                .build();

        BDDMockito.given(repository.findById(userToBeUpdated.id())).willReturn(Optional.empty());

        // When
        // Then
        Assertions.assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.update(userToBeUpdated))
                .withMessageContaining("User not found")
                .extracting(ResponseStatusException::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);

        // Auditing interactions
        BDDMockito.then(repository).should(BDDMockito.never()).update(userToBeUpdated);
    }
}