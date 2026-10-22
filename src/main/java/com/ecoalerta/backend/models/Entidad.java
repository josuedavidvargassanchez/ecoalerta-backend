package com.ecoalerta.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "entidades")
public class Entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la entidad es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Size(max = 60, message = "Máximo 60 caracteres")
    @Column(length = 60)
    private String tipo;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(length = 100)
    private String correo;

    @ManyToMany(mappedBy = "entidades")
    private Set<PuntoCritico> puntos = new HashSet<>();

    public Entidad() { }

    public Entidad(String nombre, String tipo, String correo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.correo = correo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Set<PuntoCritico> getPuntos() { return puntos; }
    public void setPuntos(Set<PuntoCritico> puntos) { this.puntos = puntos; }
}
