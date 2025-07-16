package com.gestionclub.padres.model;

import java.util.Date;

public class Asistencia {
    private String id;
    private String eventoId;
    private String jugadorId;
    private String jugadorNombre;
    private Date fecha;
    private boolean asistio;
    private String observaciones;
    private String equipoId;
    private String estado;

    public Asistencia() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.fecha = new Date();
    }

    public Asistencia(String eventoId, String jugadorId, String jugadorNombre, boolean asistio) {
        this();
        this.eventoId = eventoId;
        this.jugadorId = jugadorId;
        this.jugadorNombre = jugadorNombre;
        this.asistio = asistio;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }

    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getJugadorNombre() {
        return jugadorNombre;
    }

    public void setJugadorNombre(String jugadorNombre) {
        this.jugadorNombre = jugadorNombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isAsistio() {
        return asistio;
    }

    public void setAsistio(boolean asistio) {
        this.asistio = asistio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
} 