package com.tomazbr9.buildprice.repository;

import com.tomazbr9.buildprice.entity.SinapiItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SinapiItemRepository extends JpaRepository<SinapiItem, UUID> {
}
