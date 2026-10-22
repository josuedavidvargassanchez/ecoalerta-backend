package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.CiudadanoForm;
import com.ecoalerta.backend.dto.CiudadanoResumen;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.Ciudadano;
import com.ecoalerta.backend.models.Contacto;
import com.ecoalerta.backend.repositories.CiudadanoRepository;
import com.ecoalerta.backend.repositories.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;
    private final ReporteRepository reporteRepository;

    public CiudadanoService(CiudadanoRepository ciudadanoRepository, ReporteRepository reporteRepository) {
        this.ciudadanoRepository = ciudadanoRepository;
        this.reporteRepository = reporteRepository;
    }

    @Transactional(readOnly = true)
    public List<CiudadanoResumen> listarResumen() {
        return ciudadanoRepository.listarResumen();
    }

    @Transactional(readOnly = true)
    public List<Ciudadano> listar() {
        return ciudadanoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Ciudadano obtener(Long id) {
        return ciudadanoRepository.findConContactoById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el ciudadano " + id));
    }

    public Long crear(CiudadanoForm form) {
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (ciudadanoRepository.existsByEmailIgnoreCase(email)) {
            throw new ReglaNegocioException("Ya hay un ciudadano registrado con ese correo");
        }
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setNombre(form.getNombre().trim());
        ciudadano.setEmail(email);
        ciudadano.setContacto(new Contacto(limpiar(form.getTelefono()), limpiar(form.getDireccion())));
        return ciudadanoRepository.save(ciudadano).getId();
    }

    public void actualizar(Long id, CiudadanoForm form) {
        Ciudadano ciudadano = obtener(id);
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (ciudadanoRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ReglaNegocioException("Ya hay otro ciudadano registrado con ese correo");
        }
        ciudadano.setNombre(form.getNombre().trim());
        ciudadano.setEmail(email);
        if (ciudadano.getContacto() == null) {
            ciudadano.setContacto(new Contacto());
        }
        ciudadano.getContacto().setTelefono(limpiar(form.getTelefono()));
        ciudadano.getContacto().setDireccion(limpiar(form.getDireccion()));
    }

    public void eliminar(Long id) {
        Ciudadano ciudadano = obtener(id);
        if (reporteRepository.existsByCiudadanoId(id)) {
            throw new ReglaNegocioException("No se puede eliminar un ciudadano que tiene reportes registrados");
        }
        ciudadanoRepository.delete(ciudadano);
    }

    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
