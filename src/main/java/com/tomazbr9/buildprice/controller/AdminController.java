package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.sinapi.BatchStatusDTO;
import com.tomazbr9.buildprice.dto.sinapi.ImportResponseDTO;
import com.tomazbr9.buildprice.dto.user.UserResponseDTO;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import com.tomazbr9.buildprice.service.AdminService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service){
        this.service = service;
    }

    @PostMapping("/sinapi/import")
    public ResponseEntity<ImportResponseDTO> importSinapi(@RequestPart("file") MultipartFile file, @RequestPart("tab") String tab) {
        ImportResponseDTO response = service.importSinapi(file, tab);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sinapi/status/{jobId}")
    public ResponseEntity<BatchStatusDTO> getImportProcessStatus(@PathVariable Long jobId){
        BatchStatusDTO response = service.getImportProcessStatus(jobId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sinapi/stop/job/{jobId}")
    public  ResponseEntity<Void> stopJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        service.stopJob(jobId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<UserResponseDTO> response = service.getUsers(userDetails.getId());
        return ResponseEntity.ok(response);
    }

}
