package com.ecoalerta.backend.repositories;

import com.ecoalerta.backend.models.Reporte;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    long countByCiudadanoIdAndCerradoFalse(Long ciudadanoId);

    long countByCerradoFalse();

    long countByCerradoFalseAndFechaBefore(LocalDate limite);

    boolean existsByPuntoIdAndCerradoFalse(Long puntoId);

    boolean existsByCiudadanoId(Long ciudadanoId);

    @EntityGraph(attributePaths = {"punto", "ciudadano"})
    List<Reporte> findByCerradoFalseOrderByFechaAsc();

    @EntityGraph(attributePaths = {"punto", "ciudadano"})
    List<Reporte> findByCerradoTrueOrderByFechaCierreDesc();

    @EntityGraph(attributePaths = {"punto", "ciudadano"})
    List<Reporte> findByPuntoIdOrderByFechaDesc(Long puntoId);

    @EntityGraph(attributePaths = {"punto", "ciudadano"})
    List<Reporte> findByCiudadanoIdOrderByFechaDesc(Long ciudadanoId);


    @Query("""
            SELECT r FROM Reporte r
            JOIN FETCH r.punto
            JOIN FETCH r.ciudadano
            WHERE r.cerrado = false AND r.fecha < :limite
            ORDER BY r.fecha ASC
            """)
    List<Reporte> vencidos(@Param("limite") LocalDate limite);
}
