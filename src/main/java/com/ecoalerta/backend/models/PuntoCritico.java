package com.ecoalerta.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "puntos_criticos")
public class PuntoCritico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del punto es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "Debes ingresar una ubicación")
    @Size(max = 150, message = "Máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String ubicacion;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "Máximo 500 caracteres")
    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPunto estado = EstadoPunto.PENDIENTE;

    @Column(length = 200)
    private String imagenRuta;

    private LocalDate fechaRegistro = LocalDate.now();

    // Lado dueño: aquí está la columna categoria_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Lado dueño del @ManyToMany: tabla intermedia punto_entidad
    @ManyToMany
    @JoinTable(
            name = "punto_entidad",
            joinColumns = @JoinColumn(name = "punto_id"),
            inverseJoinColumns = @JoinColumn(name = "entidad_id"))
    private Set<Entidad> entidades = new HashSet<>();

    @OneToMany(mappedBy = "punto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reporte> reportes = new HashSet<>();

    public PuntoCritico() { }

    public PuntoCritico(String nombre, String ubicacion, String descripcion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoPunto getEstado() { return estado; }
    public void setEstado(EstadoPunto estado) { this.estado = estado; }

    public String getImagenRuta() { return imagenRuta; }
    public void setImagenRuta(String imagenRuta) { this.imagenRuta = imagenRuta; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Set<Entidad> getEntidades() { return entidades; }
    public void setEntidades(Set<Entidad> entidades) { this.entidades = entidades; }

    public Set<Reporte> getReportes() { return reportes; }
    public void setReportes(Set<Reporte> reportes) { this.reportes = reportes; }
}
