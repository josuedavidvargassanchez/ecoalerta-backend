package com.ecoalerta.backend.repositories;

import com.ecoalerta.backend.models.PuntoCritico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuntoCriticoRepository extends JpaRepository<PuntoCritico, Long> {
}