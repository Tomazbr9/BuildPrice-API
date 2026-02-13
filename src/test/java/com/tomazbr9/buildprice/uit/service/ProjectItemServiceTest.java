package com.tomazbr9.buildprice.uit.service;

import com.tomazbr9.buildprice.dto.project_item.ItemRequestDTO;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.ProjectItem;
import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.exception.ItemNotFoundException;
import com.tomazbr9.buildprice.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.repository.ProjectItemRepository;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import com.tomazbr9.buildprice.repository.SinapiItemRepository;
import com.tomazbr9.buildprice.service.ProjectItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectItemServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SinapiItemRepository sinapiItemRepository;

    @Mock
    private ProjectItemRepository projectItemRepository;

    @InjectMocks
    private ProjectItemService projectItemService;

    @Test
    void shouldAddItemSuccessfully() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sinapiId = UUID.randomUUID();

        Project project = new Project();
        project.setId(projectId);

        SinapiItem sinapiItem = new SinapiItem();
        sinapiItem.setId(sinapiId);
        sinapiItem.setCodSinapi("001");
        sinapiItem.setDescription("Cimento");
        sinapiItem.setClassification("Material");
        sinapiItem.setUnit("SC");
        sinapiItem.setUf("SP");
        sinapiItem.setPrice(new BigDecimal("50"));

        ItemRequestDTO request = new ItemRequestDTO(projectId, 2);

        when(projectRepository.findByIdAndUser_id(projectId, userId))
                .thenReturn(Optional.of(project));

        when(sinapiItemRepository.findById(sinapiId))
                .thenReturn(Optional.of(sinapiItem));

        when(projectItemRepository.save(any(ProjectItem.class)))
                .thenAnswer(invocation -> {
                    ProjectItem item = invocation.getArgument(0);
                    item.setId(UUID.randomUUID());
                    return item;
                });

        var response = projectItemService.addItem(sinapiId, request, userId);

        // subtotal = 50 * 2 = 100
        assertEquals(new BigDecimal("100"), response.subTotal());
        assertEquals(2, response.quantity());
        assertEquals("Cimento", response.description());

        verify(projectItemRepository).save(any(ProjectItem.class));
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFound() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sinapiId = UUID.randomUUID();

        ItemRequestDTO request = new ItemRequestDTO(projectId, 2);

        when(projectRepository.findByIdAndUser_id(projectId, userId))
                .thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class,
                () -> projectItemService.addItem(sinapiId, request, userId));

        verify(projectItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSinapiItemNotFound() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID sinapiId = UUID.randomUUID();

        Project project = new Project();

        ItemRequestDTO request = new ItemRequestDTO(projectId, 2);

        when(projectRepository.findByIdAndUser_id(projectId, userId))
                .thenReturn(Optional.of(project));

        when(sinapiItemRepository.findById(sinapiId))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> projectItemService.addItem(sinapiId, request, userId));

        verify(projectItemRepository, never()).save(any());
    }




}
