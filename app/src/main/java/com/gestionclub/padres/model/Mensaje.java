package com.gestionclub.padres.model;

import java.util.Date;

public class Mensaje {
    private String id;
    private String autorId;
    private String autorNombre;
    private String contenido;
    private Date fechaCreacion;
    private boolean esAdmin;
    private String equipoId;
    private boolean destacado;

    public Mensaje() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.fechaCreacion = new Date();
        this.destacado = false;
    }

    public Mensaje(String autorId, String autorNombre, String contenido, boolean esAdmin) {
        this();
        this.autorId = autorId;
        this.autorNombre = autorNombre;
        this.contenido = contenido;
        this.esAdmin = esAdmin;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    public boolean isDestacado() {
        return destacado;
    }

    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }

    // Métodos para compatibilidad con el código existente
    public String getEquipo() {
        return equipoId;
    }

    public void setEquipo(String equipo) {
        this.equipoId = equipo;
    }
} 