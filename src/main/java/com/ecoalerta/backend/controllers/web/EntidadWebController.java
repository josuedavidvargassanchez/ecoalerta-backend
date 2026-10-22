package com.ecoalerta.backend.controllers.web;

import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.Entidad;
import com.ecoalerta.backend.services.EntidadService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/entidades")
public class EntidadWebController {

    private final EntidadService entidadService;

    public EntidadWebController(EntidadService entidadService) {
        this.entidadService = entidadService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("form", new Entidad());
        cargarLista(model, null);
        return "entidades/lista";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", entidadService.obtener(id));
        cargarLista(model, id);
        return "entidades/lista";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") Entidad form, BindingResult errores,
                        Model model, RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                entidadService.crear(form);
                flash.addFlashAttribute("mensaje", "Entidad creada");
                return "redirect:/entidades";
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        cargarLista(model, null);
        return "entidades/lista";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") Entidad form,
                             BindingResult errores, Model model, RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                entidadService.actualizar(id, form);
                flash.addFlashAttribute("mensaje", "Entidad actualizada");
                return "redirect:/entidades";
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        cargarLista(model, id);
        return "entidades/lista";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        entidadService.eliminar(id);
        flash.addFlashAttribute("mensaje", "Entidad eliminada");
        return "redirect:/entidades";
    }

    private void cargarLista(Model model, Long editandoId) {
        model.addAttribute("entidades", entidadService.listarConConteo());
        model.addAttribute("editandoId", editandoId);
    }
}
