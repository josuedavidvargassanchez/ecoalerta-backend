package com.ecoalerta.backend.services;

import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PuntoCriticoService {

    @Autowired
    private PuntoCriticoRepository repository;

    public List<PuntoCritico> obtenerTodos() {
        return repository.findAll();
    }

    public PuntoCritico guardar(PuntoCritico punto) {
        if (punto.getEstado() == null || punto.getEstado().isEmpty()) {
            punto.setEstado("PENDIENTE");
        }
        return repository.save(punto);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}