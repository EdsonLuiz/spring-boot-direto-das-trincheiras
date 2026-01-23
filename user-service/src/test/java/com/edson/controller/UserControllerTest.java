package com.edson.controller;

import com.edson.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static final String URI = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    @DisplayName("GET /api/v1/users returns all user")
    void findAll_returnsAllUsers() throws Exception {
        // Given
        var user01 = User.builder().id(1L).firstName("name01").lastName("lastName01").email("name01@lastName01").build();
        var user02 = User.builder().id(2L).firstName("name02").lastName("lastName02").email("name01@lastName02").build();
        var users = List.of(user01, user02);

        var expectedJson = objectMapper.writeValueAsString(users);

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URI));

        // Then
        response
                .andExpect(MockMvcResultMatchers.status().isOk());


    }
}
