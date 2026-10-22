package com.ecoalerta.backend.dto;

import com.ecoalerta.backend.models.Ciudadano;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CiudadanoForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String email;

    @Size(max = 20, message = "Máximo 20 caracteres")
    private String telefono;

    @Size(max = 150, message = "Máximo 150 caracteres")
    private String direccion;

    public CiudadanoForm() { }

    public static CiudadanoForm desde(Ciudadano c) {
        CiudadanoForm form = new CiudadanoForm();
        form.nombre = c.getNombre();
        form.email = c.getEmail();
        if (c.getContacto() != null) {
            form.telefono = c.getContacto().getTelefono();
            form.direccion = c.getContacto().getDireccion();
        }
        return form;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
