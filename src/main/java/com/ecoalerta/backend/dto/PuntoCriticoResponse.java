package com.ecoalerta.backend.dto;

import com.ecoalerta.backend.models.Entidad;
import com.ecoalerta.backend.models.PuntoCritico;

import java.time.LocalDate;
import java.util.List;

public record PuntoCriticoResponse(
        Long id,
        String nombre,
        String ubicacion,
        String descripcion,
        String estado,
        Long categoriaId,
        String categoria,
        List<String> entidades,
        String imagenUrl,
        LocalDate fechaRegistro) {

    public static PuntoCriticoResponse from(PuntoCritico p) {
        List<String> nombresEntidades = p.getEntidades().stream()
                .map(Entidad::getNombre)
                .sorted()
                .toList();
        return new PuntoCriticoResponse(
                p.getId(),
                p.getNombre(),
                p.getUbicacion(),
                p.getDescripcion(),
                p.getEstado().name(),
                p.getCategoria() == null ? null : p.getCategoria().getId(),
                p.getCategoria() == null ? null : p.getCategoria().getNombre(),
                nombresEntidades,
                p.getImagenRuta() == null ? null : "/subidas/" + p.getImagenRuta(),
                p.getFechaRegistro());
    }
}
