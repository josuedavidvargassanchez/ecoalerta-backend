package com.ecoalerta.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "contactos")
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    @Size(max = 150)
    @Column(length = 150)
    private String direccion;

    public Contacto() { }

    public Contacto(String telefono, String direccion) {
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
