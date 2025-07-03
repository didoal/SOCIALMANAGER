package com.gestionclub.padres.model;

import java.util.Date;
import java.util.UUID;

public class Equipo {
    private String id;
    private String nombre;
    private String categoria;
    private String entrenadorId;
    private String entrenadorNombre;
    private String descripcion;
    private Date fechaCreacion;
    private boolean activo;

    public Equipo(String nombre, String categoria, String entrenadorId, String entrenadorNombre, String descripcion) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.categoria = categoria;
        this.entrenadorId = entrenadorId;
        this.entrenadorNombre = entrenadorNombre;
        this.descripcion = descripcion;
        this.fechaCreacion = new Date();
        this.activo = true;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEntrenadorId() { return entrenadorId; }
    public void setEntrenadorId(String entrenadorId) { this.entrenadorId = entrenadorId; }

    public String getEntrenadorNombre() { return entrenadorNombre; }
    public void setEntrenadorNombre(String entrenadorNombre) { this.entrenadorNombre = entrenadorNombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre + " (" + categoria + ")";
    }
} 