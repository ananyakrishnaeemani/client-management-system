package com.advisor.portal.repository;

import com.advisor.portal.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByAgentEmail(String agentEmail);
    Optional<Client> findByIdAndAgentEmail(Long id, String agentEmail);
}
