package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.project.ProjectRequestDTO;
import com.tomazbr9.buildprice.dto.project.ProjectResponseDTO;
import com.tomazbr9.buildprice.dto.project.ProjectWithItemsResponseDTO;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import com.tomazbr9.buildprice.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @RequestBody @Valid ProjectRequestDTO request,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        ProjectResponseDTO response = service.createProject(request, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getProjects(@AuthenticationPrincipal UserDetailsImpl userDetails){

        List<ProjectResponseDTO> response = service.getProjects(userDetails.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectWithItemsResponseDTO> getProject(@PathVariable UUID projectId, @AuthenticationPrincipal UserDetailsImpl userDetails){

        ProjectWithItemsResponseDTO response = service.getProject(projectId, userDetails.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId, @AuthenticationPrincipal UserDetailsImpl userDetails){

        service.deleteProject(projectId, userDetails.getId());

        return ResponseEntity.noContent().build();
    }

}
