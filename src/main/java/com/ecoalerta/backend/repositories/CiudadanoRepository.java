package com.ecoalerta.backend.repositories;

import com.ecoalerta.backend.dto.CiudadanoResumen;
import com.ecoalerta.backend.models.Ciudadano;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CiudadanoRepository extends JpaRepository<Ciudadano, Long> {

    List<Ciudadano> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "contacto")
    Optional<Ciudadano> findConContactoById(Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("""
            SELECT new com.ecoalerta.backend.dto.CiudadanoResumen(
                c.id, c.nombre, c.email, ct.telefono, ct.direccion, COUNT(r))
            FROM Ciudadano c
            LEFT JOIN c.contacto ct
            LEFT JOIN c.reportes r
            GROUP BY c.id, c.nombre, c.email, ct.telefono, ct.direccion
            ORDER BY c.nombre
            """)
    List<CiudadanoResumen> listarResumen();
}
