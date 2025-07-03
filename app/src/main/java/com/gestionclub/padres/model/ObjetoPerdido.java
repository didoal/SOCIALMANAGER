package com.gestionclub.padres.model;

import java.util.Date;

public class ObjetoPerdido {
    private String id;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private String estado; // "PERDIDO", "ENCONTRADO", "RECLAMADO"
    private String reportadoPor;
    private String reportadoPorNombre;
    private Date fechaReporte;
    private String reclamadoPor;
    private String reclamadoPorNombre;
    private Date fechaReclamo;
    private boolean esAdmin;

    public ObjetoPerdido() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.fechaReporte = new Date();
        this.estado = "PERDIDO";
    }

    public ObjetoPerdido(String nombre, String descripcion, String ubicacion, 
                        String reportadoPor, String reportadoPorNombre, boolean esAdmin) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.reportadoPor = reportadoPor;
        this.reportadoPorNombre = reportadoPorNombre;
        this.esAdmin = esAdmin;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReportadoPor() {
        return reportadoPor;
    }

    public void setReportadoPor(String reportadoPor) {
        this.reportadoPor = reportadoPor;
    }

    public String getReportadoPorNombre() {
        return reportadoPorNombre;
    }

    public void setReportadoPorNombre(String reportadoPorNombre) {
        this.reportadoPorNombre = reportadoPorNombre;
    }

    public Date getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getReclamadoPor() {
        return reclamadoPor;
    }

    public void setReclamadoPor(String reclamadoPor) {
        this.reclamadoPor = reclamadoPor;
    }

    public String getReclamadoPorNombre() {
        return reclamadoPorNombre;
    }

    public void setReclamadoPorNombre(String reclamadoPorNombre) {
        this.reclamadoPorNombre = reclamadoPorNombre;
    }

    public Date getFechaReclamo() {
        return fechaReclamo;
    }

    public void setFechaReclamo(Date fechaReclamo) {
        this.fechaReclamo = fechaReclamo;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
} 