package com.tomazbr9.buildprice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomazbr9.buildprice.dto.project.ProjectRequestDTO;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.util.TestAuthHelper;
import com.tomazbr9.buildprice.util.TestUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwt;

    @BeforeEach
    void setup() throws Exception {

        // criar usuário usando factory
        var user = TestUserFactory.createDefaultUser(
                userRepository,
                roleRepository,
                passwordEncoder,
                null
        );

        // 2 fazer login real e pegar token
        jwt = TestAuthHelper.getJwtToken(
                mockMvc,
                objectMapper,
                user.getEmail(),
                "123456"
        );
    }

    @Test
    void shouldCreateProject() throws Exception {

        ProjectRequestDTO request =
                new ProjectRequestDTO(
                        "Obra Teste",
                        "Cliente",
                        "Descrição",
                        "SP",
                        new BigDecimal("10")
                );

        mockMvc.perform(post("/api/v1/project")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nameWork").value("Obra Teste"));
    }

    @Test
    void shouldReturnProjects() throws Exception {

        mockMvc.perform(get("/api/v1/project")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

}
