package com.ecoalerta.backend.dto;

import com.ecoalerta.backend.models.Entidad;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.models.PuntoCritico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class PuntoForm {

    @NotBlank(message = "El nombre del punto es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @NotBlank(message = "Debes ingresar una ubicación")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String ubicacion;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "Máximo 500 caracteres")
    private String descripcion;

    @NotNull(message = "Selecciona una categoría")
    private Long categoriaId;

    private EstadoPunto estado = EstadoPunto.PENDIENTE;

    private Set<Long> entidadIds = new HashSet<>();

    public PuntoForm() { }

    public static PuntoForm desde(PuntoCritico p) {
        PuntoForm form = new PuntoForm();
        form.nombre = p.getNombre();
        form.ubicacion = p.getUbicacion();
        form.descripcion = p.getDescripcion();
        form.categoriaId = p.getCategoria() == null ? null : p.getCategoria().getId();
        form.estado = p.getEstado();
        for (Entidad e : p.getEntidades()) {
            form.entidadIds.add(e.getId());
        }
        return form;
    }

    public PuntoCriticoRequest toRequest() {
        return new PuntoCriticoRequest(nombre, ubicacion, descripcion, categoriaId, estado, entidadIds);
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public EstadoPunto getEstado() { return estado; }
    public void setEstado(EstadoPunto estado) { this.estado = estado; }

    public Set<Long> getEntidadIds() { return entidadIds; }
    public void setEntidadIds(Set<Long> entidadIds) { this.entidadIds = entidadIds; }
}
