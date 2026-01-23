package com.edson.repository;

import com.edson.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserHardCodedRepositoryTest {
    @InjectMocks
    private UserHardCodedRepository repository;

    @Mock
    private UserData userData;

    private User testUser01, testUser02;
    private List<User> users;
    private List<User> expectedUsers;

    @BeforeEach
    void setUp() {
        testUser01 = User.builder().id(1L).firstName("testUser01").lastName("lastName01").email("testUser01@lastName01.com").build();
        testUser02 = User.builder().id(2L).firstName("testUser02").lastName("lastName02").email("testUser02@lastName02.com").build();

        users = new ArrayList<>(List.of(testUser01, testUser02));
        expectedUsers = new ArrayList<>(List.of(testUser01, testUser02));
    }

    @Test
    @DisplayName("FindAll returns all users")
    void findAll_returnsAllUsers() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);

        // When
        var actualResult = repository.findAll();

        // Then
        Assertions.assertThat(actualResult.size()).isEqualTo(2);
        Assertions.assertThat(actualResult).isEqualTo(expectedUsers);
    }

    @Test
    @DisplayName("findAllByName returns all filtered name when name exists")
    void findByAllByName_returnsAllFilteredNames_whenNameExists() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);
        var expectedUsers = List.of(testUser01);
        var firstName = "testUser01";

        // When
        var actualResult = repository.findAllByName(firstName);

        // Then
        Assertions.assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedUsers);
    }

    @Test
    @DisplayName("findAllByName returns empty list when name does not exists")
    void findAllByName_returnsEmptyList_whenNameDoesNotExists() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);
        var firstName = "NonExistentName";

        // When
        var actualResult = repository.findAllByName(firstName);

        // Then
        Assertions.assertThat(actualResult).isEmpty();
    }

    @Test
    @DisplayName("findAllByName returns empty list when name does is null")
    void findAllByName_returnsEmptyList_whenNameDoesIsNull() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);
        String firstName = null;

        // When
        var actualResult = repository.findAllByName(firstName);

        // Then
        Assertions.assertThat(actualResult).isEmpty();
    }

    @Test
    @DisplayName("findById returns user when id exists")
    void findById_returnsUser_whenIdExists() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);

        // When
        var actualResult = repository.findById(testUser01.id());

        // Then
        Assertions.assertThat(actualResult)
                .contains(testUser01);
    }

    @Test
    @DisplayName("findById returns empty when id does not exists")
    void findById_returnsEmpty_whenIdDoesNotExists() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);
        var nonExistentId = 99L;

        // When
        var actualResult = repository.findById(nonExistentId);

        // Then
        Assertions.assertThat(actualResult).isEmpty();
    }

    @Test
    @DisplayName("create persists user")
    void create_persists_user() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);
        var userToCreate = User.builder()
                .id(99L)
                .firstName("SomeUser")
                .lastName("SomeLastName")
                .email("some@email.com")
                .build();

        // When
        User actualResult = repository.create(userToCreate);

        // Then
        Assertions.assertThat(actualResult).isNotNull();
    }

    @Test
    @DisplayName("deleteById removes user when user exists")
    void deleteById_removesUser_whenUserExists() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);

        // When
        repository.deleteById(testUser01.id());

        // Then
        Assertions.assertThat(users).doesNotContain(testUser01);
    }

    @Test
    @DisplayName("update updates user when user exists")
    void update_updatesUser_whenUserExists() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);
        var updatedUser = User.builder()
                .id(1L)
                .firstName("New Name")
                .lastName("New Last Name")
                .build();

        // When
        repository.update(updatedUser);

        // Then
        Assertions.assertThat(users).contains(updatedUser);
        Assertions.assertThat(users).doesNotContain(testUser01);
    }

    @Test
    @DisplayName("update do nothing when user does not exists")
    void update_doNothing_whenUserDoesNotExists() {
        // Given
        BDDMockito.given(userData.getUsers()).willReturn(users);
        var userToUpdate = User.builder()
                .id(99L)
                .firstName("Any")
                .lastName("Any")
                .email("any@valid.email")
                .build();

        // When
        repository.update(userToUpdate);

        // Then
        Assertions.assertThat(users).hasSize(2).doesNotContain(userToUpdate);
    }
}