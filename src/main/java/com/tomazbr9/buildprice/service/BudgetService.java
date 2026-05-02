package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.budget.BudgetRequestDTO;
import com.tomazbr9.buildprice.dto.budget.BudgetResponseDTO;
import com.tomazbr9.buildprice.dto.project.ProjectResponseDTO;
import com.tomazbr9.buildprice.entity.Budget;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.repository.BudgetRepository;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public BudgetResponseDTO createBudget(BudgetRequestDTO request, UUID userId){

        Project project = projectRepository.findByIdAndUser_id(request.projectId(), userId).orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado"));

        Budget budget = Budget.builder()
                .name(request.name())
                .bdi(request.bdi())
                .createdAt(Instant.now())
                .project(project)
                .build();

        Budget budgetSaved = budgetRepository.save(budget);

        return new BudgetResponseDTO(
                budgetSaved.getName(),
                budgetSaved.getBdi()
        );

    }


}
