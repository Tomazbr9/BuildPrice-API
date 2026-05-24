package com.tomazbr9.buildprice.repository;

import com.tomazbr9.buildprice.entity.SinapiItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SinapiItemRepository extends JpaRepository<SinapiItem, UUID> {

    Optional<SinapiItem> findByCodSinapi(String codSinapi);

    Page<SinapiItem> findByDescriptionContainingIgnoreCaseAndUf(
            String description,
            String uf,
            Pageable pageable
    );

    Page<SinapiItem> findByDescriptionContainingIgnoreCase(
            String description,
            Pageable pageable
    );

    Page<SinapiItem> findByDescriptionContainingIgnoreCaseAndUfAndTaxRelief(
        String description,
        String uf,
        String taxRelief,
        Pageable pageable
    );

    Page<SinapiItem> findByDescriptionContainingIgnoreCaseAndTaxRelief(
        String description,
        String taxRelief,
        Pageable pageable
    );
}