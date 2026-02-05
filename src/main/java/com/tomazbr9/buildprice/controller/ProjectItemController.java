package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.project_item.ItemRequestDTO;
import com.tomazbr9.buildprice.dto.project_item.ItemResponseDTO;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import com.tomazbr9.buildprice.service.ProjectItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectItemController {

    private final ProjectItemService service;

    public ProjectItemController(ProjectItemService service){
        this.service = service;
    }

    @PostMapping("/{sinapiItemId}/items")
    public ResponseEntity<ItemResponseDTO> addItem(@PathVariable UUID sinapiItemId, @RequestBody ItemRequestDTO request, @AuthenticationPrincipal UserDetailsImpl userDetails){

        ItemResponseDTO response = service.addItem(sinapiItemId, request, userDetails.getId());

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
