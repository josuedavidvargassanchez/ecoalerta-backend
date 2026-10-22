package com.ecoalerta.backend.controllers.web;

import com.ecoalerta.backend.dto.CiudadanoForm;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.services.CiudadanoService;
import com.ecoalerta.backend.services.ReporteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ciudadanos")
public class CiudadanoWebController {

    private final CiudadanoService ciudadanoService;
    private final ReporteService reporteService;

    public CiudadanoWebController(CiudadanoService ciudadanoService, ReporteService reporteService) {
        this.ciudadanoService = ciudadanoService;
        this.reporteService = reporteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ciudadanos", ciudadanoService.listarResumen());
        return "ciudadanos/lista";
    }

    @GetMapping("/{id}")
    public String ficha(@PathVariable Long id, Model model) {
        model.addAttribute("ciudadano", ciudadanoService.obtener(id));
        model.addAttribute("reportes", reporteService.deCiudadano(id));
        return "ciudadanos/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new CiudadanoForm());
        model.addAttribute("ciudadanoId", null);
        model.addAttribute("titulo", "Registrar ciudadano");
        return "ciudadanos/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") CiudadanoForm form, BindingResult errores,
                        Model model, RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                Long id = ciudadanoService.crear(form);
                flash.addFlashAttribute("mensaje", "Ciudadano registrado");
                return "redirect:/ciudadanos/" + id;
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        model.addAttribute("ciudadanoId", null);
        model.addAttribute("titulo", "Registrar ciudadano");
        return "ciudadanos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", CiudadanoForm.desde(ciudadanoService.obtener(id)));
        model.addAttribute("ciudadanoId", id);
        model.addAttribute("titulo", "Editar ciudadano");
        return "ciudadanos/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") CiudadanoForm form,
                             BindingResult errores, Model model, RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                ciudadanoService.actualizar(id, form);
                flash.addFlashAttribute("mensaje", "Ciudadano actualizado");
                return "redirect:/ciudadanos/" + id;
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        model.addAttribute("ciudadanoId", id);
        model.addAttribute("titulo", "Editar ciudadano");
        return "ciudadanos/formulario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        ciudadanoService.eliminar(id);
        flash.addFlashAttribute("mensaje", "Ciudadano eliminado");
        return "redirect:/ciudadanos";
    }
}
