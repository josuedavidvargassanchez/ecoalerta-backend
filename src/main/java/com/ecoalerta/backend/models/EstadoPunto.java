package com.ecoalerta.backend.models;

public enum EstadoPunto {
    PENDIENTE("Pendiente"),
    EN_ATENCION("En atención"),
    RESUELTO("Resuelto");

    private final String etiqueta;

    EstadoPunto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
