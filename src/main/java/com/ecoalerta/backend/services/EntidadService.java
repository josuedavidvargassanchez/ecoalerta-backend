package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.EntidadConConteo;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.Entidad;
import com.ecoalerta.backend.repositories.EntidadRepository;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EntidadService {

    private final EntidadRepository entidadRepository;
    private final PuntoCriticoRepository puntoRepository;

    public EntidadService(EntidadRepository entidadRepository, PuntoCriticoRepository puntoRepository) {
        this.entidadRepository = entidadRepository;
        this.puntoRepository = puntoRepository;
    }

    @Transactional(readOnly = true)
    public List<Entidad> listar() {
        return entidadRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<EntidadConConteo> listarConConteo() {
        return entidadRepository.listarConConteo();
    }

    @Transactional(readOnly = true)
    public Entidad obtener(Long id) {
        return entidadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la entidad " + id));
    }

    public void crear(Entidad datos) {
        String nombre = datos.getNombre().trim();
        if (entidadRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una entidad con ese nombre");
        }
        entidadRepository.save(new Entidad(nombre, limpiar(datos.getTipo()), limpiar(datos.getCorreo())));
    }

    public void actualizar(Long id, Entidad datos) {
        Entidad entidad = obtener(id);
        String nombre = datos.getNombre().trim();
        if (entidadRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra entidad con ese nombre");
        }
        entidad.setNombre(nombre);
        entidad.setTipo(limpiar(datos.getTipo()));
        entidad.setCorreo(limpiar(datos.getCorreo()));
    }

    public void eliminar(Long id) {
        Entidad entidad = obtener(id);
        if (puntoRepository.existsByEntidadesId(id)) {
            throw new ReglaNegocioException("No se puede eliminar una entidad asignada a puntos críticos");
        }
        entidadRepository.delete(entidad);
    }

    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
