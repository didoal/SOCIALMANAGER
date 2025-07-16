package com.gestionclub.padres.model;

import java.util.Date;
import java.util.UUID;

public class Evento {
    private String id;
    private String titulo;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private String ubicacion;
    private String tipo; // "ENTRENAMIENTO", "PARTIDO", "EVENTO", "REUNION"
    private String creadorId;
    private String creadorNombre;
    private boolean esAdmin;
    private boolean activo;
    private String equipoId;
    
    // Nuevos campos para eventos recurrentes
    private boolean esRecurrente;
    private String frecuencia; // "DIARIA", "SEMANAL", "MENSUAL"
    private Date fechaFinRecurrencia;
    private String colorMarcador; // Color para marcador visual en calendario
    private String estado;
    private String prioridad;

    public Evento() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
        this.esRecurrente = false;
        this.colorMarcador = "#FFD700"; // Dorado por defecto
    }

    public Evento(String titulo, String descripcion, Date fechaInicio, Date fechaFin, 
                 String ubicacion, String tipo, String creadorId, String creadorNombre, boolean esAdmin) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.creadorId = creadorId;
        this.creadorNombre = creadorNombre;
        this.esAdmin = esAdmin;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(String creadorId) {
        this.creadorId = creadorId;
    }

    public String getCreadorNombre() {
        return creadorNombre;
    }

    public void setCreadorNombre(String creadorNombre) {
        this.creadorNombre = creadorNombre;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    // Métodos para compatibilidad con el código existente
    public String getEquipo() {
        return equipoId;
    }

    public void setEquipo(String equipo) {
        this.equipoId = equipo;
    }

    // Nuevos getters y setters para eventos recurrentes
    public boolean isEsRecurrente() {
        return esRecurrente;
    }

    public void setEsRecurrente(boolean esRecurrente) {
        this.esRecurrente = esRecurrente;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Date getFechaFinRecurrencia() {
        return fechaFinRecurrencia;
    }

    public void setFechaFinRecurrencia(Date fechaFinRecurrencia) {
        this.fechaFinRecurrencia = fechaFinRecurrencia;
    }

    public String getColorMarcador() {
        return colorMarcador;
    }

    public void setColorMarcador(String colorMarcador) {
        this.colorMarcador = colorMarcador;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getPrioridad() {
        return prioridad;
    }
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
    public java.util.Date getFecha() {
        return fechaInicio;
    }
}