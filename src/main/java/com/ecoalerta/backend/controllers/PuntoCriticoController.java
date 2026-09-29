package com.ecoalerta.backend.controllers;

import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.services.PuntoCriticoService;
import com.ecoalerta.backend.services.StorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/puntos")
public class PuntoCriticoController {

    @Autowired
    private PuntoCriticoService service;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public String listarPuntos(Model model) {
        model.addAttribute("puntos", service.obtenerTodos());
        if (!model.containsAttribute("punto")) {
            model.addAttribute("punto", new PuntoCritico());
        }
        return "puntos/lista";
    }

    @PostMapping("/guardar")
    public String guardarPunto(
            @Valid @ModelAttribute("punto") PuntoCritico punto,
            BindingResult errores,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        if (errores.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.punto", errores);
            redirectAttributes.addFlashAttribute("punto", punto);
            return "redirect:/puntos";
        }

        if (!file.isEmpty()) {
            String rutaImagen = storageService.store(file);
            punto.setImagenRuta(rutaImagen);
        }

        service.guardar(punto);
        redirectAttributes.addFlashAttribute("mensajeExito", "Punto crítico registrado correctamente.");
        return "redirect:/puntos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPunto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Punto crítico eliminado exitosamente.");
        return "redirect:/puntos";
    }
}