package com.tomazbr9.buildprice.integration.service;

import com.tomazbr9.buildprice.dto.project_item.ItemRequestDTO;
import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;
import com.tomazbr9.buildprice.entity.*;
import com.tomazbr9.buildprice.exception.ItemNotFoundException;
import com.tomazbr9.buildprice.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.*;

import com.tomazbr9.buildprice.service.ProjectItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProjectItemServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectItemService projectItemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SinapiItemRepository sinapiItemRepository;

    @Autowired
    private ProjectItemRepository projectItemRepository;

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
                "Cliente",
                "Desc",
                "SP",
                new BigDecimal("10"),
                user
        );
        return projectRepository.save(project);
    }

    private SinapiItem createSinapiItem() {
        SinapiItem item = new SinapiItem(
                null,
                "123",
                "Item Teste",
                "Material",
                "UN",
                "SP",
                new BigDecimal("50.00"),
                "N",
                LocalDate.now()
        );
        return sinapiItemRepository.save(item);
    }

    @Test
    void shouldAddItemSuccessfully() {

        User user = createUser();
        Project project = createProject(user);
        SinapiItem sinapiItem = createSinapiItem();

        ItemRequestDTO request =
                new ItemRequestDTO(project.getId(), 3);

        ItemResponseDTO response =
                projectItemService.addItem(
                        sinapiItem.getId(),
                        request,
                        user.getId()
                );

        assertNotNull(response.id());
        assertEquals(3, response.quantity());

        // preço = 50.00, quantidade = 3 → subtotal = 150.00
        assertEquals(0,
                response.subTotal().compareTo(new BigDecimal("150.00")));

        // garante que persistiu no banco
        assertEquals(1, projectItemRepository.count());
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFound() {

        SinapiItem sinapiItem = createSinapiItem();

        ItemRequestDTO request =
                new ItemRequestDTO(UUID.randomUUID(), 2);

        assertThrows(ProjectNotFoundException.class,
                () -> projectItemService.addItem(
                        sinapiItem.getId(),
                        request,
                        UUID.randomUUID()
                ));
    }

    @Test
    void shouldThrowExceptionWhenSinapiItemNotFound() {

        User user = createUser();
        Project project = createProject(user);

        ItemRequestDTO request =
                new ItemRequestDTO(project.getId(), 2);

        assertThrows(ItemNotFoundException.class,
                () -> projectItemService.addItem(
                        UUID.randomUUID(),
                        request,
                        user.getId()
                ));
    }

}

