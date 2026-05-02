package com.tomazbr9.buildprice.repository;

import com.tomazbr9.buildprice.entity.Budget;
import com.tomazbr9.buildprice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findByUserId(UUID userId);

}
