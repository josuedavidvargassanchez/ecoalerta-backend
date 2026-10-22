package com.ecoalerta.backend.controllers.web;

import com.ecoalerta.backend.dto.PuntoForm;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.services.CategoriaService;
import com.ecoalerta.backend.services.EntidadService;
import com.ecoalerta.backend.services.PuntoCriticoService;
import com.ecoalerta.backend.services.ReporteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/puntos")
public class PuntoWebController {

    private final PuntoCriticoService puntoService;
    private final CategoriaService categoriaService;
    private final EntidadService entidadService;
    private final ReporteService reporteService;

    public PuntoWebController(PuntoCriticoService puntoService, CategoriaService categoriaService,
                              EntidadService entidadService, ReporteService reporteService) {
        this.puntoService = puntoService;
        this.categoriaService = categoriaService;
        this.entidadService = entidadService;
        this.reporteService = reporteService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) EstadoPunto estado,
                         @RequestParam(required = false) Long categoria,
                         @RequestParam(defaultValue = "0") int pagina,
                         Model model) {
        model.addAttribute("resultado", puntoService.buscar(q, estado, categoria, pagina));
        model.addAttribute("q", q);
        model.addAttribute("estadoSel", estado);
        model.addAttribute("categoriaSel", categoria);
        model.addAttribute("estados", EstadoPunto.values());
        model.addAttribute("categorias", categoriaService.listar());
        return "puntos/lista";
    }

    // ---- Ficha
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("punto", puntoService.obtenerDetalle(id));
        model.addAttribute("reportes", reporteService.dePunto(id));
        model.addAttribute("estados", EstadoPunto.values());
        return "puntos/detalle";
    }

    // ---- Alta
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new PuntoForm());
        prepararFormulario(model, null, null);
        return "puntos/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") PuntoForm form,
                        BindingResult errores,
                        @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                        Model model,
                        RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                Long id = puntoService.crear(form.toRequest(), imagen);
                flash.addFlashAttribute("mensaje", "Punto crítico registrado correctamente");
                return "redirect:/puntos/" + id;
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        prepararFormulario(model, null, null);
        return "puntos/formulario";
    }

    // ---- Edición
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        PuntoCritico punto = puntoService.obtenerDetalle(id);
        model.addAttribute("form", PuntoForm.desde(punto));
        prepararFormulario(model, id, punto.getImagenRuta());
        return "puntos/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("form") PuntoForm form,
                             BindingResult errores,
                             @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                             Model model,
                             RedirectAttributes flash) {
        if (!errores.hasErrors()) {
            try {
                puntoService.actualizar(id, form.toRequest(), imagen);
                flash.addFlashAttribute("mensaje", "Punto crítico actualizado");
                return "redirect:/puntos/" + id;
            } catch (ReglaNegocioException e) {
                errores.reject("regla", e.getMessage());
            }
        }
        prepararFormulario(model, id, puntoService.obtenerDetalle(id).getImagenRuta());
        return "puntos/formulario";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam EstadoPunto estado, RedirectAttributes flash) {
        puntoService.cambiarEstado(id, estado);
        flash.addFlashAttribute("mensaje", "Estado cambiado a " + estado.getEtiqueta());
        return "redirect:/puntos/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        puntoService.eliminar(id);
        flash.addFlashAttribute("mensaje", "Punto crítico eliminado");
        return "redirect:/puntos";
    }

    private void prepararFormulario(Model model, Long puntoId, String imagenActual) {
        model.addAttribute("titulo", puntoId == null ? "Registrar punto crítico" : "Editar punto crítico");
        model.addAttribute("puntoId", puntoId);
        model.addAttribute("imagenActual", imagenActual);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("entidades", entidadService.listar());
        model.addAttribute("estados", EstadoPunto.values());
    }
}
