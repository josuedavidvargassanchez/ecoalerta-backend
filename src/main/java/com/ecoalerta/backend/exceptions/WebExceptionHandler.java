package com.ecoalerta.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class WebExceptionHandler {

    @ExceptionHandler(ReglaNegocioException.class)
    public String reglaNegocio(ReglaNegocioException ex, HttpServletRequest request, RedirectAttributes flash) {
        flash.addFlashAttribute("error", ex.getMessage());
        return volver(request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String imagenDemasiadoGrande(HttpServletRequest request, RedirectAttributes flash) {
        flash.addFlashAttribute("error", "La imagen es demasiado grande (máximo 5 MB)");
        return volver(request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ModelAndView noEncontrado(NoSuchElementException ex) {
        ModelAndView mv = new ModelAndView("error/404");
        mv.addObject("mensaje", ex.getMessage());
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }

    private String volver(HttpServletRequest request) {
        String anterior = request.getHeader("Referer");
        return "redirect:" + ((anterior == null || anterior.isBlank()) ? "/" : anterior);
    }
}
