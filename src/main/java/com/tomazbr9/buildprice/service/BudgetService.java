package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.budget.BudgetFullResponseDTO;
import com.tomazbr9.buildprice.dto.budget.BudgetRequestDTO;
import com.tomazbr9.buildprice.dto.budget.BudgetResponseDTO;
import com.tomazbr9.buildprice.dto.project.ProjectResponseDTO;
import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;
import com.tomazbr9.buildprice.entity.Budget;
import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.ProjectItem;
import com.tomazbr9.buildprice.exception.ProjectNotFoundException;
import com.tomazbr9.buildprice.repository.BudgetRepository;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public BudgetFullResponseDTO getBudget(UUID budgetId){

        Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        List<ItemResponseDTO> items = budget.getProjectItems().stream()
                .map(item -> new ItemResponseDTO(
                        item.getId(),
                        item.getSinapiItem().getCodSinapi(),
                        item.getSinapiItem().getDescription(),
                        item.getSinapiItem().getClassification(),
                        item.getSinapiItem().getUnit(),
                        item.getSinapiItem().getUf(),
                        item.getQuantity(),
                        item.getPrice()
                )).toList();

        BigDecimal withOutBdi = calculateWithOutBDI(items);
        BigDecimal withBdi = calculateWithBDI(withOutBdi, budget.getBdi());
        BigDecimal grossMargin = calculeGrossMargin(withOutBdi, withBdi);

        return new BudgetFullResponseDTO(
                budgetId,
                budget.getName(),
                budget.getCreatedAt(),
                budget.getBdi(),
                items,
                withOutBdi,
                withBdi,
                grossMargin
        );

    }


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
                budgetSaved.getId(),
                budgetSaved.getName(),
                budgetSaved.getBdi()
        );

    }

    private BigDecimal calculateWithOutBDI(List<ItemResponseDTO> items){

        return items.stream()
                .map(item -> {
                    return item.price().multiply(BigDecimal.valueOf(item.quantity()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
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
