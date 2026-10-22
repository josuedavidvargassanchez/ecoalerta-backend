package com.ecoalerta.backend.repositories;

import com.ecoalerta.backend.dto.CategoriaConConteo;
import com.ecoalerta.backend.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);



    @Query("""
            SELECT new com.ecoalerta.backend.dto.CategoriaConConteo(c.id, c.nombre, c.descripcion, COUNT(p))
            FROM Categoria c LEFT JOIN c.puntos p
            GROUP BY c.id, c.nombre, c.descripcion
            ORDER BY c.nombre
            """)
    List<CategoriaConConteo> listarConConteo();
}
