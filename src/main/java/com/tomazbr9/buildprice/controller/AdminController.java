package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.sinapi.ImportResponseDTO;
import com.tomazbr9.buildprice.service.AdminService;
import org.springframework.batch.core.JobExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;


    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PostMapping("/import")
    public ResponseEntity<?> importSinapi(@RequestParam("file") MultipartFile file) {

        ImportResponseDTO response = adminService.importSinapi(file);

        return ResponseEntity.ok(response);


    }

}
