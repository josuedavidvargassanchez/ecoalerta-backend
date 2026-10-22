package com.ecoalerta.backend.controllers.api;

import com.ecoalerta.backend.dto.PuntoCriticoRequest;
import com.ecoalerta.backend.dto.PuntoCriticoResponse;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.services.PuntoCriticoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/puntos-criticos")
public class PuntoCriticoRestController {

    private final PuntoCriticoService puntoService;

    public PuntoCriticoRestController(PuntoCriticoService puntoService) {
        this.puntoService = puntoService;
    }

    @GetMapping
    public List<PuntoCriticoResponse> listar(@RequestParam(required = false) EstadoPunto estado) {
        return puntoService.listarRespuestas(estado);
    }

    @GetMapping("/{id}")
    public PuntoCriticoResponse obtener(@PathVariable Long id) {
        return puntoService.obtenerRespuesta(id);
    }

    @PostMapping
    public ResponseEntity<PuntoCriticoResponse> crear(@Valid @RequestBody PuntoCriticoRequest request) {
        PuntoCriticoResponse creado = puntoService.crearRespuesta(request);
        return ResponseEntity.created(URI.create("/api/puntos-criticos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public PuntoCriticoResponse actualizar(@PathVariable Long id, @Valid @RequestBody PuntoCriticoRequest request) {
        return puntoService.actualizarRespuesta(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        puntoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
