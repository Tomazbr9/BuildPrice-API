package com.tomazbr9.buildprice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomazbr9.buildprice.dto.project_item.ItemRequestDTO;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.*;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class ProjectItemControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private SinapiItemRepository sinapiItemRepository;

    private String jwt;
    private UUID projectId;
    private UUID sinapiItemId;

    @BeforeEach
    void setup() throws Exception {

        var user = TestUserFactory.createDefaultUser(
                userRepository,
                roleRepository,
                passwordEncoder
        );

        jwt = TestAuthHelper.getJwtToken(
                mockMvc,
                objectMapper,
                user.getEmail(),
                "123456"
        );

        Project project = new Project(
                null,
                "Obra Teste",
                Instant.now(),
                "Cliente",
                "Descrição",
                "SP",
                new BigDecimal("10"),
                user
        );

        project = projectRepository.save(project);
        projectId = project.getId();

        SinapiItem sinapiItem = new SinapiItem(
                null,
                "001",
                "Item Teste",
                "Material",
                "UN",
                "SP",
                new BigDecimal("50.00"),
                "ISD",
                LocalDate.now()
        );

        sinapiItem = sinapiItemRepository.save(sinapiItem);
        sinapiItemId = sinapiItem.getId();
    }

    @Test
    void shouldAddItemToProject() throws Exception {

        ItemRequestDTO request = new ItemRequestDTO(projectId, 2);

        mockMvc.perform(post("/api/v1/projects/{sinapiItemId}/items", sinapiItemId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Item Teste"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.subtotal").value(100.00));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoToken() throws Exception {

        ItemRequestDTO request = new ItemRequestDTO(projectId, 2);

        mockMvc.perform(post("/api/v1/projects/{sinapiItemId}/items", sinapiItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenProjectDoesNotBelongToUser() throws Exception {

        // cria outro usuário
        var otherUser = TestUserFactory.createUser(
                "otherUser@email.com",
                "123456",
                userRepository,
                roleRepository,
                RoleName.ROLE_USER,
                passwordEncoder
        );

        // cria projeto para outro usuário
        Project otherProject = new Project(
                null,
                "Obra Outro",
                Instant.now(),
                "Cliente",
                "Descrição",
                "SP",
                new BigDecimal("5"),
                otherUser
        );

        otherProject = projectRepository.save(otherProject);

        ItemRequestDTO request = new ItemRequestDTO(otherProject.getId(), 2);

        mockMvc.perform(post("/api/v1/projects/{sinapiItemId}/items", sinapiItemId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenSinapiItemDoesNotExist() throws Exception {

        ItemRequestDTO request = new ItemRequestDTO(projectId, 2);

        mockMvc.perform(post("/api/v1/projects/{sinapiItemId}/items", UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}

