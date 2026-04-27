package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.project_item.ItemRequestDTO;
import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;
import com.tomazbr9.buildprice.dto.project_item.ItemUpdateDTO;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.ProjectItem;
import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.exception.InvalidTerritorialScopeException;
import com.tomazbr9.buildprice.exception.ItemNotFoundException;
import com.tomazbr9.buildprice.exception.ProjectNotFoundException;
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

    public ProjectItemService(ProjectRepository projectRepository, SinapiItemRepository sinapiItemRepository, ProjectItemRepository projectItemRepository) {
        this.projectRepository = projectRepository;
        this.sinapiItemRepository = sinapiItemRepository;
        this.projectItemRepository = projectItemRepository;
    }

    public ItemResponseDTO addItem(UUID sinapiItemId, ItemRequestDTO request, UUID userId) {

        Project project = projectRepository.findByIdAndUser_id(request.projectId(), userId).orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado"));

        SinapiItem sinapiItem = sinapiItemRepository.findById(sinapiItemId).orElseThrow(() -> new ItemNotFoundException("Item Sinapi não encontrado"));

        ProjectItem projectItem = new ProjectItem(null, request.quantity(), sinapiItem.getPrice(), project, sinapiItem);

        if (!sinapiItem.getUf().equals(project.getUf())) {
            throw new InvalidTerritorialScopeException("Uf do projeto e do insumo não coincidem");
        }

        ProjectItem savedItem = projectItemRepository.save(projectItem);

        BigDecimal subtotal = calculateSubTotal(savedItem);

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

    public ItemResponseDTO updateItem(UUID itemId, ItemUpdateDTO request) {

        ProjectItem projectItem = projectItemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item não encontrado"));
        SinapiItem sinapiItem = sinapiItemRepository.findById(projectItem.getSinapiItem().getId()).orElseThrow(() -> new ItemNotFoundException("Item Sinapi não encontrado"));

        if (request.quantity() != null) {
            projectItem.setQuantity(request.quantity());
        }

        ProjectItem savedItem = projectItemRepository.save(projectItem);
        BigDecimal subtotal = calculateSubTotal(savedItem);

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

    private BigDecimal calculateSubTotal(ProjectItem item) {
        return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

//    private boolean checkIfThenItemIsInTheProject(Project project, SinapiItem sinapiItem){
//
//    }

}

