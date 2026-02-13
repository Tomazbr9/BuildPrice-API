package com.tomazbr9.buildprice.integration.service;

import com.tomazbr9.buildprice.dto.project.ProjectRequestDTO;
import com.tomazbr9.buildprice.dto.project.ProjectResponseDTO;
import com.tomazbr9.buildprice.dto.project.ProjectWithItemsResponseDTO;
import com.tomazbr9.buildprice.entity.*;
import com.tomazbr9.buildprice.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.exception.UserNotFoundException;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.*;

import com.tomazbr9.buildprice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProjectServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectItemRepository projectItemRepository;

    @Autowired
    private SinapiItemRepository sinapiItemRepository;

    private User createUser() {
        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("123");
        return userRepository.save(user);
    }

    private Project createProject(User user) {
        Project project = new Project(
                null,
                "Obra Teste",
                Instant.now(),
                "Cliente X",
                "Descrição",
                "SP",
                new BigDecimal("10"), // 10% BDI
                user
        );
        return projectRepository.save(project);
    }

    private SinapiItem createSinapiItem() {
        SinapiItem item = new SinapiItem(
                null,
                "123",
                "Item teste",
                "Material",
                "UN",
                "SP",
                new BigDecimal("100.00"),
                "N",
                java.time.LocalDate.now()
        );
        return sinapiItemRepository.save(item);
    }

    private ProjectItem createProjectItem(Project project, SinapiItem sinapiItem) {
        ProjectItem item = new ProjectItem();
        item.setProject(project);
        item.setSinapiItem(sinapiItem);
        item.setQuantity(2);
        item.setPrice(new BigDecimal("100.00"));
        return projectItemRepository.save(item);
    }

    @Test
    void shouldCreateProject() {

        User user = createUser();

        ProjectRequestDTO request =
                new ProjectRequestDTO("Obra A", "Cliente A", "Desc", "SP", new BigDecimal("15"));

        ProjectResponseDTO response =
                projectService.createProject(request, user.getId());

        assertNotNull(response.id());
        assertEquals("Obra A", response.nameWork());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound_createProject() {

        ProjectRequestDTO request =
                new ProjectRequestDTO("Obra A", "Cliente A", "Desc", "SP", new BigDecimal("15"));

        assertThrows(UserNotFoundException.class,
                () -> projectService.createProject(request, UUID.randomUUID()));
    }

    @Test
    void shouldReturnProjectWithCalculatedTotals() {

        User user = createUser();
        Project project = createProject(user);
        SinapiItem sinapiItem = createSinapiItem();
        createProjectItem(project, sinapiItem);

        ProjectWithItemsResponseDTO response =
                projectService.getProject(project.getId(), user.getId());

        assertEquals(1, response.items().size());

        // subtotal esperado = 100 * 2 = 200
        assertEquals(0,
                response.totalWithOutBDI().compareTo(new BigDecimal("200.00")));

        // BDI 10% → 200 * 1.10 = 220
        assertEquals(0,
                response.totalWithBDI().compareTo(new BigDecimal("220.00")));

        // margem = 220 - 200 = 20
        assertEquals(0,
                response.grossMargin().compareTo(new BigDecimal("20.00")));
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFound_getProject() {

        assertThrows(ProjectNotFoundException.class,
                () -> projectService.getProject(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void shouldDeleteProject() {

        User user = createUser();
        Project project = createProject(user);

        projectService.deleteProject(project.getId(), user.getId());

        assertTrue(projectRepository.findById(project.getId()).isEmpty());
    }

}

