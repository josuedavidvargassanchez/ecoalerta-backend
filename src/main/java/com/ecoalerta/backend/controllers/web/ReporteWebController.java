package com.ecoalerta.backend.controllers.web;

import com.ecoalerta.backend.dto.ReporteForm;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.services.CiudadanoService;
import com.ecoalerta.backend.services.PuntoCriticoService;
import com.ecoalerta.backend.services.ReporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reportes")
public class ReporteWebController {

    private final ReporteService reporteService;
    private final PuntoCriticoService puntoService;
    private final CiudadanoService ciudadanoService;

    public ReporteWebController(ReporteService reporteService, PuntoCriticoService puntoService,
                                CiudadanoService ciudadanoService) {
        this.reporteService = reporteService;
        this.puntoService = puntoService;
        this.ciudadanoService = ciudadanoService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "abiertos") String filtro, Model model) {
        model.addAttribute("reportes", reporteService.listar(filtro));
        model.addAttribute("filtro", filtro);
        return "reportes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long punto, Model model) {
        ReporteForm form = new ReporteForm();
        form.setPuntoId(punto);
        model.addAttribute("form", form);
        cargarOpciones(model);
        return "reportes/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") ReporteForm form, BindingResult errores,
                        Model model, RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                reporteService.crear(form);
                flash.addFlashAttribute("mensaje", "Reporte registrado");
                return "redirect:/puntos/" + form.getPuntoId();
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        cargarOpciones(model);
        return "reportes/formulario";
    }

    @PostMapping("/{id}/cerrar")
    public String cerrar(@PathVariable Long id, RedirectAttributes flash) {
        reporteService.cerrar(id);
        flash.addFlashAttribute("mensaje", "Reporte cerrado");
        return "redirect:/reportes";
    }

    @GetMapping("/vencidos.csv")
    public ResponseEntity<String> exportarVencidos() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reportes_vencidos.csv\"")
                .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                .body(reporteService.exportarVencidosCsv());
    }

    private void cargarOpciones(Model model) {
        model.addAttribute("puntos", puntoService.listarParaReportar());
        model.addAttribute("ciudadanos", ciudadanoService.listar());
    }
}
