package com.ecoalerta.backend.dto;

import java.time.LocalDate;

public record ReporteResponse(
        Long id,
        String descripcion,
        LocalDate fecha,
        boolean cerrado,
        LocalDate fechaCierre,
        boolean vencido,
        Long puntoId,
        String puntoNombre,
        Long ciudadanoId,
        String ciudadanoNombre) { }
