package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.CategoriaConConteo;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.Categoria;
import com.ecoalerta.backend.repositories.CategoriaRepository;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final PuntoCriticoRepository puntoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, PuntoCriticoRepository puntoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.puntoRepository = puntoRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<CategoriaConConteo> listarConConteo() {
        return categoriaRepository.listarConConteo();
    }

    @Transactional(readOnly = true)
    public Categoria obtener(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la categoría " + id));
    }

    public void crear(Categoria datos) {
        String nombre = datos.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }
        categoriaRepository.save(new Categoria(nombre, limpiar(datos.getDescripcion())));
    }

    public void actualizar(Long id, Categoria datos) {
        Categoria categoria = obtener(id);
        String nombre = datos.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra categoría con ese nombre");
        }
        categoria.setNombre(nombre);
        categoria.setDescripcion(limpiar(datos.getDescripcion()));
    }

    public void eliminar(Long id) {
        Categoria categoria = obtener(id);
        if (puntoRepository.countByCategoriaId(id) > 0) {
            throw new ReglaNegocioException("No se puede eliminar una categoría que tiene puntos críticos");
        }
        categoriaRepository.delete(categoria);
    }

    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
