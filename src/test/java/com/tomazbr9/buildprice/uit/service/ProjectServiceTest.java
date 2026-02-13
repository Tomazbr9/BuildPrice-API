package com.tomazbr9.buildprice.uit.service;

import com.tomazbr9.buildprice.dto.project.ProjectRequestDTO;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.ProjectItem;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.exception.UserNotFoundException;
import com.tomazbr9.buildprice.repository.ProjectItemRepository;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProjectItemRepository projectItemRepository;

    @InjectMocks
    ProjectService projectService;

    @Test
    void shouldCreateProjectWhenUserExists() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        ProjectRequestDTO request = new ProjectRequestDTO(
                "Obra X",
                "Cliente Y",
                "Descrição",
                "SP",
                new BigDecimal("10")
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> {
                    Project p = invocation.getArgument(0);
                    p.setId(UUID.randomUUID());
                    return p;
                });

        var response = projectService.createProject(request, userId);

        assertEquals("Obra X", response.nameWork());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> projectService.createProject(null, userId));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void shouldReturnProjectList() {

        UUID userId = UUID.randomUUID();

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setNameWork("Obra Teste");

        when(projectRepository.findByUser_id(userId))
                .thenReturn(Optional.of(List.of(project)));

        var response = projectService.getProjects(userId);

        assertEquals(1, response.size());
        assertEquals("Obra Teste", response.get(0).nameWork());
    }

    @Test
    void shouldCalculateTotalsCorrectly() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = new Project();
        project.setId(projectId);
        project.setBdi(new BigDecimal("10")); // 10%
        project.setNameWork("Obra Teste");

        when(projectRepository.findByIdAndUser_id(projectId, userId))
                .thenReturn(Optional.of(project));

        // Criando item fake
        ProjectItem item = mock(ProjectItem.class);
        var sinapi = mock(com.tomazbr9.buildprice.entity.SinapiItem.class);

        when(item.getId()).thenReturn(UUID.randomUUID());
        when(item.getQuantity()).thenReturn(2);
        when(item.getPrice()).thenReturn(new BigDecimal("100"));

        when(item.getSinapiItem()).thenReturn(sinapi);
        when(sinapi.getCodSinapi()).thenReturn("001");
        when(sinapi.getDescription()).thenReturn("Item Teste");
        when(sinapi.getClassification()).thenReturn("Classe");
        when(sinapi.getUnit()).thenReturn("UN");
        when(sinapi.getUf()).thenReturn("SP");

        when(projectItemRepository.findByProject_id(projectId))
                .thenReturn(List.of(item));

        var response = projectService.getProject(projectId, userId);

        // subtotal = 100 * 2 = 200
        assertEquals(new BigDecimal("200"), response.totalWithOutBDI());

        // 200 + 10% = 220
        assertEquals(new BigDecimal("220.00"), response.totalWithBDI());

        // margem = 20
        assertEquals(new BigDecimal("20.00"), response.grossMargin());
    }

    @Test
    void shouldDeleteProjectWhenExists() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = new Project();

        when(projectRepository.findByIdAndUser_id(projectId, userId))
                .thenReturn(Optional.of(project));

        projectService.deleteProject(projectId, userId);

        verify(projectRepository).delete(project);
    }

}
