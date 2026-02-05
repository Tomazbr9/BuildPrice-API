package com.tomazbr9.buildprice.repository;

import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleName name);
}
