package com.gestionclub.padres.model;

import java.util.Date;

public class Notificacion {
    private String id;
    private String titulo;
    private String mensaje;
    private String tipo; // "EVENTO", "MENSAJE", "OBJETO", "RECORDATORIO", "SOLICITUD", "URGENTE"
    private Date fechaCreacion;
    private boolean leida;
    private String destinatarioId; // null para notificaciones globales
    private String remitenteId;
    private String remitenteNombre;
    private boolean esAdmin;
    private String prioridad; // "ALTA", "MEDIA", "BAJA"
    private String equipoDestinatario; // Equipo específico para la notificación
    private String estado; // "PENDIENTE", "APROBADA", "RECHAZADA"
    private String creador; // Nombre del creador de la notificación

    public Notificacion() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.fechaCreacion = new Date();
        this.leida = false;
        this.prioridad = "MEDIA";
        this.estado = "PENDIENTE";
    }

    public Notificacion(String titulo, String mensaje, String tipo, 
                       String destinatarioId, String remitenteId, 
                       String remitenteNombre, boolean esAdmin) {
        this();
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.destinatarioId = destinatarioId;
        this.remitenteId = remitenteId;
        this.remitenteNombre = remitenteNombre;
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(String destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(String remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEquipoDestinatario() {
        return equipoDestinatario;
    }

    public void setEquipoDestinatario(String equipoDestinatario) {
        this.equipoDestinatario = equipoDestinatario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }
} 