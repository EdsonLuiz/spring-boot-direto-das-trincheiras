package com.edson.repository;

import com.edson.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserDataTest {

    @Test
    @DisplayName("getUsers returns a list containing two user with sequential ids")
    void getUsers_returnsAListOfUses() {
        // Given
        UserData userData = new UserData();

        // When
        List<User> actualResult = userData.getUsers();

        // Then
        Assertions.assertThat(actualResult).hasSize(2);
    }

}