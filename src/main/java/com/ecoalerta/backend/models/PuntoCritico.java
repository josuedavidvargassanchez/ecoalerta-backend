package com.ecoalerta.backend.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "puntos_criticos")
public class PuntoCritico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String barrio;
    private String direccionReferencia;
    private String descripcion;
    private String nivelSeveridad;
    private String estado;
    private LocalDate fechaReporte;

    public PuntoCritico() {
        this.fechaReporte = LocalDate.now();
        this.estado = "Pendiente";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getDireccionReferencia() { return direccionReferencia; }
    public void setDireccionReferencia(String direccionReferencia) { this.direccionReferencia = direccionReferencia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNivelSeveridad() { return nivelSeveridad; }
    public void setNivelSeveridad(String nivelSeveridad) { this.nivelSeveridad = nivelSeveridad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDate fechaReporte) { this.fechaReporte = fechaReporte; }
}