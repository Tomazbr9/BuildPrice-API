package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.PageResponseDTO;
import com.tomazbr9.buildprice.dto.sinapi.SinapiItemResponseDTO;
import com.tomazbr9.buildprice.repository.SinapiItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SinapiService {

    private final SinapiItemRepository sinapiItemRepository;

    public SinapiService(SinapiItemRepository sinapiItemRepository) {
        this.sinapiItemRepository = sinapiItemRepository;
    }

    public PageResponseDTO<SinapiItemResponseDTO> searchItems(String search, String uf, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<SinapiItemResponseDTO> result;

        if (uf != null && !uf.isBlank()) {
            result = sinapiItemRepository
                    .findByDescriptionContainingIgnoreCaseAndUf(search, uf.toUpperCase(), pageable)
                    .map(item -> new SinapiItemResponseDTO(
                            item.getId(),
                            item.getCodSinapi(),
                            item.getDescription(),
                            item.getClassification(),
                            item.getUnit(),
                            item.getUf(),
                            item.getPrice()
                    ));
        } else {
            result = sinapiItemRepository
                    .findByDescriptionContainingIgnoreCase(search, pageable)
                    .map(item -> new SinapiItemResponseDTO(
                            item.getId(),
                            item.getCodSinapi(),
                            item.getDescription(),
                            item.getClassification(),
                            item.getUnit(),
                            item.getUf(),
                            item.getPrice()
                    ));
        }

        return new PageResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}