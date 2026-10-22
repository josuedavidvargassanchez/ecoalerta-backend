package com.ecoalerta.backend.controllers.api;

import com.ecoalerta.backend.dto.ReporteResponse;
import com.ecoalerta.backend.services.ReporteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteRestController {

    private final ReporteService reporteService;

    public ReporteRestController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public List<ReporteResponse> listar(@RequestParam(defaultValue = "abiertos") String filtro) {
        return reporteService.listar(filtro);
    }

    @PostMapping("/{id}/cerrar")
    public void cerrar(@PathVariable Long id) {
        reporteService.cerrar(id);
    }
}
