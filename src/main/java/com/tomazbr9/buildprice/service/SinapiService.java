package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.PageResponseDTO;
import com.tomazbr9.buildprice.dto.sinapi.SinapiItemResponseDTO;
import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.repository.SinapiItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

@Service
public class SinapiService {

    private final SinapiItemRepository sinapiItemRepository;

    public SinapiService(SinapiItemRepository sinapiItemRepository) {
        this.sinapiItemRepository = sinapiItemRepository;
    }

    public PageResponseDTO<SinapiItemResponseDTO> searchItems(String search, String uf, String taxRelief, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("description").ascending());

        Page<SinapiItemResponseDTO> result;
        boolean hasUf = uf != null && !uf.isBlank();
        boolean hasTaxRelief = taxRelief != null && !taxRelief.isBlank();

        if (hasUf && hasTaxRelief) {
            result = sinapiItemRepository
                    .findByDescriptionContainingIgnoreCaseAndUfAndTaxRelief(search, uf.toUpperCase(), taxRelief.toUpperCase(), pageable)
                    .map(this::toResponse);
        } else if (hasUf) {
            result = sinapiItemRepository
                    .findByDescriptionContainingIgnoreCaseAndUf(search, uf.toUpperCase(), pageable)
                    .map(this::toResponse);
        } else if (hasTaxRelief) {
            result = sinapiItemRepository
                    .findByDescriptionContainingIgnoreCaseAndTaxRelief(search, taxRelief.toUpperCase(), pageable)
                    .map(this::toResponse);
        } else {
            result = sinapiItemRepository
                    .findByDescriptionContainingIgnoreCase(search, pageable)
                    .map(this::toResponse);
        }

        return new PageResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private SinapiItemResponseDTO toResponse(SinapiItem item) {
        return new SinapiItemResponseDTO(
                item.getId(),
                item.getCodSinapi(),
                item.getDescription(),
                item.getClassification(),
                item.getUnit(),
                item.getUf(),
                item.getTaxRelief(),
                item.getPrice()
        );
    }
}
