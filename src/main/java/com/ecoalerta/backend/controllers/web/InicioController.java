package com.ecoalerta.backend.controllers.web;

import com.ecoalerta.backend.services.PuntoCriticoService;
import com.ecoalerta.backend.services.ResumenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final ResumenService resumenService;
    private final PuntoCriticoService puntoService;

    public InicioController(ResumenService resumenService, PuntoCriticoService puntoService) {
        this.resumenService = resumenService;
        this.puntoService = puntoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("resumen", resumenService.obtener());
        model.addAttribute("ultimos", puntoService.ultimos());
        return "index";
    }
}
