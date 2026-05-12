package com.tomazbr9.buildprice.controller;
 
import com.tomazbr9.buildprice.dto.PageResponseDTO;
import com.tomazbr9.buildprice.dto.sinapi.SinapiItemResponseDTO;
import com.tomazbr9.buildprice.service.SinapiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/api/v1/sinapi")
public class SinapiController {
 
    private final SinapiService sinapiService;
 
    public SinapiController(SinapiService sinapiService) {
        this.sinapiService = sinapiService;
    }
 
    @GetMapping("/items")
    public ResponseEntity<PageResponseDTO<SinapiItemResponseDTO>> searchItems(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String uf,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponseDTO<SinapiItemResponseDTO> response = sinapiService.searchItems(search, uf, page, size);
        return ResponseEntity.ok(response);
    }
}