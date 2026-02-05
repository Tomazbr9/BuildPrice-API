package com.tomazbr9.buildprice.repository;

import com.tomazbr9.buildprice.entity.ProjectItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectItemRepository extends JpaRepository<ProjectItem, UUID> {

}
