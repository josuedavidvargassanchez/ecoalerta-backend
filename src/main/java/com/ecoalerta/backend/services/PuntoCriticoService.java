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

    public List<PuntoCritico> listarTodos() {
        return repository.findAll();
    }

    public PuntoCritico guardar(PuntoCritico punto) {
        return repository.save(punto);
    }

    public PuntoCritico actualizarEstado(Long id, String nuevoEstado) {
        PuntoCritico punto = repository.findById(id).orElseThrow();
        punto.setEstado(nuevoEstado);
        return repository.save(punto);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}