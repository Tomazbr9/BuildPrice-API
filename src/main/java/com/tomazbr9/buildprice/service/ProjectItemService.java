package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.project_item.ItemRequestDTO;
import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.ProjectItem;
import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.repository.ProjectItemRepository;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import com.tomazbr9.buildprice.repository.SinapiItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProjectItemService {

    private final ProjectRepository projectRepository;
    private final SinapiItemRepository sinapiItemRepository;
    private final ProjectItemRepository projectItemRepository;

    public ProjectItemService(ProjectRepository projectRepository, SinapiItemRepository sinapiItemRepository, ProjectItemRepository projectItemRepository){
        this.projectRepository = projectRepository;
        this.sinapiItemRepository = sinapiItemRepository;
        this.projectItemRepository = projectItemRepository;
    }

    public ItemResponseDTO addItem(UUID sinapiItemId, ItemRequestDTO request, UUID userId){

        Project project = projectRepository.findByIdAndUser_id(request.projectId(), userId).orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        SinapiItem sinapiItem = sinapiItemRepository.findById(sinapiItemId).orElseThrow(() -> new RuntimeException("Item não encontrado"));

        ProjectItem projectItem = new ProjectItem(null, request.quantity(), sinapiItem.getPrice(), project, sinapiItem);

        ProjectItem savedItem = projectItemRepository.save(projectItem);

        BigDecimal subtotal = savedItem.getPrice().multiply(BigDecimal.valueOf(savedItem.getQuantity()));

        return new ItemResponseDTO(
                savedItem.getId(),
                sinapiItem.getCodSinapi(),
                sinapiItem.getDescription(),
                sinapiItem.getClassification(),
                sinapiItem.getUnit(),
                sinapiItem.getUf(),
                savedItem.getQuantity(),
                savedItem.getPrice(),
                subtotal
        );
    }
}

