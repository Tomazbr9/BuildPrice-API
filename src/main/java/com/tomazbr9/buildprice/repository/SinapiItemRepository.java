package com.tomazbr9.buildprice.repository;

import com.tomazbr9.buildprice.entity.SinapiItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SinapiItemRepository extends JpaRepository<SinapiItem, UUID> {

    Optional<SinapiItem> findByCodSinapi(String codSinapi);



}
