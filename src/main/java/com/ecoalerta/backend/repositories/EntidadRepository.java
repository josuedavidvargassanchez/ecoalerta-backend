package com.ecoalerta.backend.repositories;

import com.ecoalerta.backend.dto.EntidadConConteo;
import com.ecoalerta.backend.models.Entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EntidadRepository extends JpaRepository<Entidad, Long> {

    List<Entidad> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    @Query("""
            SELECT new com.ecoalerta.backend.dto.EntidadConConteo(e.id, e.nombre, e.tipo, e.correo, COUNT(p))
            FROM Entidad e LEFT JOIN e.puntos p
            GROUP BY e.id, e.nombre, e.tipo, e.correo
            ORDER BY e.nombre
            """)
    List<EntidadConConteo> listarConConteo();
}
