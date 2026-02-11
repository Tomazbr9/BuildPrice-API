package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.project.ProjectRequestDTO;
import com.tomazbr9.buildprice.dto.project.ProjectResponseDTO;
import com.tomazbr9.buildprice.dto.project.ProjectWithItemsResponseDTO;
import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.ProjectItem;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.exception.UserNotFoundException;
import com.tomazbr9.buildprice.repository.ProjectItemRepository;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectItemRepository projectItemRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectItemRepository projectItemRepository){
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectItemRepository = projectItemRepository;
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO request, UUID userId){

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Project newProject = new Project(null, request.nameWork(), Instant.now(), request.clientName(), request.description(), request.uf(), request.bdi(), user);

        Project savedProject = projectRepository.save(newProject);

        return new ProjectResponseDTO(savedProject.getId(), savedProject.getNameWork(), savedProject.getClientName(), savedProject.getDescription(), savedProject.getUf(), savedProject.getBdi(), savedProject.getCreatedAt());

    }

    public List<ProjectResponseDTO> getProjects(UUID userId){

        List<Project> projects = projectRepository.findByUser_id(userId).orElseThrow(() -> new ProjectNotFoundException("Projetos não encontrados"));

        return projects.stream()
                .map(project -> new ProjectResponseDTO(
                        project.getId(),
                        project.getNameWork(),
                        project.getClientName(),
                        project.getDescription(),
                        project.getUf(),
                        project.getBdi(),
                        project.getCreatedAt()
                )).toList();
    }

    public ProjectWithItemsResponseDTO getProject(UUID projectId, UUID userId){

        Project project = projectRepository.findByIdAndUser_id(projectId, userId).orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado"));

        List<ProjectItem> items = projectItemRepository.findByProject_id(project.getId());

        List<ItemResponseDTO> itemsResponse = items.stream()
                .map((item) -> {

                    BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new ItemResponseDTO(
                        item.getId(),
                        item.getSinapiItem().getCodSinapi(),
                        item.getSinapiItem().getDescription(),
                        item.getSinapiItem().getClassification(),
                        item.getSinapiItem().getUnit(),
                        item.getSinapiItem().getUf(),
                        item.getQuantity(),
                        item.getPrice(),
                        subtotal
                    );
                }

                ).toList();

        BigDecimal totalWithOutBDI = calculateWithOutBDI(itemsResponse);
        BigDecimal totalWithBDI = calculateWithBDI(totalWithOutBDI, project.getBdi());
        BigDecimal grossMargin = calculeGrossMargin(totalWithOutBDI, totalWithBDI);

        return new ProjectWithItemsResponseDTO(
                project.getId(),
                project.getNameWork(),
                project.getClientName(),
                project.getDescription(),
                project.getUf(),
                project.getBdi(),
                itemsResponse,
                totalWithOutBDI,
                totalWithBDI,
                grossMargin,
                project.getCreatedAt());
    }

    public void deleteProject(UUID projectId, UUID userId){

        Project project = projectRepository.findByIdAndUser_id(projectId, userId).orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado"));

        projectRepository.delete(project);

    }

    private BigDecimal calculateWithOutBDI(List<ItemResponseDTO> items){
        return items.stream()
                .map(ItemResponseDTO::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateWithBDI(BigDecimal subtotal, BigDecimal bdi){

        BigDecimal fatorBdi = bdi.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal multiplier = BigDecimal.ONE.add(fatorBdi);

        return subtotal.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculeGrossMargin(BigDecimal totalWithOutBDI, BigDecimal totalWithBDI){
        return totalWithBDI.subtract(totalWithOutBDI).setScale(2, RoundingMode.HALF_UP);
    }

}
