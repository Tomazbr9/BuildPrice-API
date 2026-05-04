package com.tomazbr9.buildprice.controller;

import com.tomazbr9.buildprice.dto.client.ClientRequestDTO;
import com.tomazbr9.buildprice.dto.client.ClientResponseDTO;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import com.tomazbr9.buildprice.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    @Autowired
    private ClientService service;

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getClients(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

        List<ClientResponseDTO> response = service.getClients(userDetails.getId());

        return ResponseEntity.ok().body(response);

    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> addClient(
            @RequestBody ClientRequestDTO request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){

        ClientResponseDTO response = service.addClient(request, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

}
