package com.ecoalerta.backend.controllers;

import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.services.PuntoCriticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puntos-criticos")
@CrossOrigin(origins = "*")
public class PuntoCriticoController {

    @Autowired
    private PuntoCriticoService service;

    @GetMapping
    public List<PuntoCritico> obtenerTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public PuntoCritico crearPunto(@RequestBody PuntoCritico punto) {
        return service.guardar(punto);
    }

    @PutMapping("/{id}/estado")
    public PuntoCritico actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return service.actualizarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public void eliminarPunto(@PathVariable Long id) {
        service.eliminar(id);
    }
}