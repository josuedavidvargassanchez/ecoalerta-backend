package com.ecoalerta.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReporteForm {

    @NotNull(message = "Selecciona un punto crítico")
    private Long puntoId;

    @NotNull(message = "Selecciona un ciudadano")
    private Long ciudadanoId;

    @NotBlank(message = "La descripción del reporte es obligatoria")
    @Size(max = 500, message = "Máximo 500 caracteres")
    private String descripcion;

    public ReporteForm() { }

    public Long getPuntoId() { return puntoId; }
    public void setPuntoId(Long puntoId) { this.puntoId = puntoId; }

    public Long getCiudadanoId() { return ciudadanoId; }
    public void setCiudadanoId(Long ciudadanoId) { this.ciudadanoId = ciudadanoId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
