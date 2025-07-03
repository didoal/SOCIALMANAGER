package com.gestionclub.padres.model;

import java.util.Date;

public class Notificacion {
    private String id;
    private String titulo;
    private String mensaje;
    private String tipo; // "EVENTO", "MENSAJE", "OBJETO", "GENERAL"
    private Date fechaCreacion;
    private boolean leida;
    private String destinatarioId; // null para notificaciones globales
    private String remitenteId;
    private String remitenteNombre;
    private boolean esAdmin;

    public Notificacion() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.fechaCreacion = new Date();
        this.leida = false;
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
} 