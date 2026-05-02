package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.budget.BudgetRequestDTO;
import com.tomazbr9.buildprice.dto.budget.BudgetResponseDTO;
import com.tomazbr9.buildprice.entity.Budget;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import com.tomazbr9.buildprice.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    @Autowired
    private BudgetService service;

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> createBudget(@RequestBody BudgetRequestDTO request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        BudgetResponseDTO response = service.createBudget(request, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
