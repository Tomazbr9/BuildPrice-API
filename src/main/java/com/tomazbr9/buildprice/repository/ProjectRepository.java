package com.tomazbr9.buildprice.repository;

import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<List<Project>> findByUser_id(UUID userId);

    Optional<Project> findByIdAndUser_id(UUID projectId, UUID userId);

}
