package com.ecoalerta.backend.repositories;

import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.models.PuntoCritico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PuntoCriticoRepository extends JpaRepository<PuntoCritico, Long> {

    @EntityGraph(attributePaths = "categoria")
    @Query("""
            SELECT p FROM PuntoCritico p
            WHERE (:texto IS NULL
                   OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(p.ubicacion) LIKE LOWER(CONCAT('%', :texto, '%')))
              AND (:estado IS NULL OR p.estado = :estado)
              AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
            ORDER BY p.id DESC
            """)
    Page<PuntoCritico> buscar(@Param("texto") String texto,
                              @Param("estado") EstadoPunto estado,
                              @Param("categoriaId") Long categoriaId,
                              Pageable pageable);

    @EntityGraph(attributePaths = {"categoria", "entidades"})
    Optional<PuntoCritico> findConRelacionesById(Long id);

    @Query("""
            SELECT DISTINCT p FROM PuntoCritico p
            LEFT JOIN FETCH p.categoria
            LEFT JOIN FETCH p.entidades
            WHERE (:estado IS NULL OR p.estado = :estado)
            ORDER BY p.id
            """)
    List<PuntoCritico> listarConRelaciones(@Param("estado") EstadoPunto estado);

    @EntityGraph(attributePaths = "categoria")
    List<PuntoCritico> findTop5ByOrderByIdDesc();

    // Puntos que todavía admiten reportes
    List<PuntoCritico> findByEstadoNotOrderByNombreAsc(EstadoPunto estado);

    long countByEstado(EstadoPunto estado);

    long countByCategoriaId(Long categoriaId);

    boolean existsByEntidadesId(Long entidadId);

    boolean existsByNombreIgnoreCaseAndUbicacionIgnoreCase(String nombre, String ubicacion);

    boolean existsByNombreIgnoreCaseAndUbicacionIgnoreCaseAndIdNot(String nombre, String ubicacion, Long id);
}
