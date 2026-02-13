package com.tomazbr9.buildprice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomazbr9.buildprice.dto.auth.LoginDTO;
import com.tomazbr9.buildprice.dto.user.UserRequestDTO;
import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // garante que o papel ROLE_USER existe
        roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(RoleName.ROLE_USER);
                    return roleRepository.save(role);
                });
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUser() throws Exception {

        UserRequestDTO request = new UserRequestDTO("newuser@email.com", "123456");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return error when email already exists")
    void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {

        // cria usuário existente
        userRepository.save(new User("dup@email.com", "123456",
                Set.of(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow())));

        UserRequestDTO request = new UserRequestDTO("dup@email.com", "123456");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should authenticate user and return token")
    void shouldLoginSuccessfully() throws Exception {
        // cria usuário codificado
        var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        var encoded = encoder.encode("123456");

        userRepository.save(new com.tomazbr9.buildprice.entity.User(
                "login@email.com",
                encoded,
                Set.of(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow())
        ));

        LoginDTO login = new LoginDTO("login@email.com", "123456");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}

