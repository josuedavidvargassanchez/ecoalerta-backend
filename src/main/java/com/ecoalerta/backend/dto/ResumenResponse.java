package com.ecoalerta.backend.dto;

public record ResumenResponse(
        long puntosPendientes,
        long puntosEnAtencion,
        long puntosResueltos,
        long reportesAbiertos,
        long reportesVencidos) { }
