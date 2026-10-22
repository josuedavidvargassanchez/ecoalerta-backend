package com.ecoalerta.backend.dto;

import com.ecoalerta.backend.models.EstadoPunto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PuntoCriticoRequest(
        @NotBlank(message = "El nombre del punto es obligatorio")
        @Size(max = 100, message = "Máximo 100 caracteres")
        String nombre,

        @NotBlank(message = "Debes ingresar una ubicación")
        @Size(max = 150, message = "Máximo 150 caracteres")
        String ubicacion,

        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(max = 500, message = "Máximo 500 caracteres")
        String descripcion,

        @NotNull(message = "Selecciona una categoría")
        Long categoriaId,

        EstadoPunto estado,

        Set<Long> entidadIds) {

    public PuntoCriticoRequest {
        if (estado == null) {
            estado = EstadoPunto.PENDIENTE;
        }
        if (entidadIds == null) {
            entidadIds = Set.of();
        }
    }
}
