package com.ecoalerta.backend.controllers;

import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.services.PuntoCriticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PuntoCriticoController {

    @Autowired
    private PuntoCriticoService puntoCriticoService;

    @GetMapping({"/", "/puntos"})
    public String listarPuntos(Model model) {
        model.addAttribute("puntos", puntoCriticoService.obtenerTodos());
        model.addAttribute("puntoNuevo", new PuntoCritico());
        return "index";
    }

    @PostMapping("/puntos/guardar")
    public String guardarPunto(PuntoCritico puntoCritico) {
        puntoCriticoService.guardar(puntoCritico);
        return "redirect:/";
    }
}