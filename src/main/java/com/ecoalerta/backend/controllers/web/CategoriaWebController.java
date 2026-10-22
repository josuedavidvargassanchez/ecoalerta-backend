package com.ecoalerta.backend.controllers.web;

import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.Categoria;
import com.ecoalerta.backend.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaWebController {

    private final CategoriaService categoriaService;

    public CategoriaWebController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("form", new Categoria());
        cargarLista(model, null);
        return "categorias/lista";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", categoriaService.obtener(id));
        cargarLista(model, id);
        return "categorias/lista";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") Categoria form, BindingResult errores,
                        Model model, RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                categoriaService.crear(form);
                flash.addFlashAttribute("mensaje", "Categoría creada");
                return "redirect:/categorias";
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        cargarLista(model, null);
        return "categorias/lista";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") Categoria form,
                             BindingResult errores, Model model, RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                categoriaService.actualizar(id, form);
                flash.addFlashAttribute("mensaje", "Categoría actualizada");
                return "redirect:/categorias";
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        cargarLista(model, id);
        return "categorias/lista";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        categoriaService.eliminar(id);
        flash.addFlashAttribute("mensaje", "Categoría eliminada");
        return "redirect:/categorias";
    }

    private void cargarLista(Model model, Long editandoId) {
        model.addAttribute("categorias", categoriaService.listarConConteo());
        model.addAttribute("editandoId", editandoId);
    }
}
