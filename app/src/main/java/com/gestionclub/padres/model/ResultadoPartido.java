package com.gestionclub.padres.model;

import java.util.Date;
import java.util.UUID;

public class ResultadoPartido {
    private String id;
    private String eventoId;
    private String equipoLocal;
    private String equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private int golesLocalPrimera;
    private int golesVisitantePrimera;
    private Date fechaPartido;
    private String ubicacion;
    private String estado; // "PENDIENTE", "EN_CURSO", "FINALIZADO"
    private String equipoId; // ID del equipo del club
    private Date fechaCreacion;
    private String creadorId;
    private String creadorNombre;

    public ResultadoPartido() {
        this.id = UUID.randomUUID().toString();
        this.fechaCreacion = new Date();
        this.estado = "PENDIENTE";
    }

    public ResultadoPartido(String eventoId, String equipoLocal, String equipoVisitante, 
                           String equipoId, Date fechaPartido, String ubicacion) {
        this();
        this.eventoId = eventoId;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.equipoId = equipoId;
        this.fechaPartido = fechaPartido;
        this.ubicacion = ubicacion;
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

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(String equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(String equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public int getGolesLocalPrimera() {
        return golesLocalPrimera;
    }

    public void setGolesLocalPrimera(int golesLocalPrimera) {
        this.golesLocalPrimera = golesLocalPrimera;
    }

    public int getGolesVisitantePrimera() {
        return golesVisitantePrimera;
    }

    public void setGolesVisitantePrimera(int golesVisitantePrimera) {
        this.golesVisitantePrimera = golesVisitantePrimera;
    }

    public Date getFechaPartido() {
        return fechaPartido;
    }

    public void setFechaPartido(Date fechaPartido) {
        this.fechaPartido = fechaPartido;
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

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    // Métodos de utilidad
    public String getResultadoCompleto() {
        return golesLocal + " - " + golesVisitante;
    }

    public String getResultadoPrimera() {
        return golesLocalPrimera + " - " + golesVisitantePrimera;
    }

    public boolean isVictoria() {
        return golesLocal > golesVisitante;
    }

    public boolean isEmpate() {
        return golesLocal == golesVisitante;
    }

    public boolean isDerrota() {
        return golesLocal < golesVisitante;
    }

    public boolean isFinalizado() {
        return "FINALIZADO".equals(estado);
    }

    public boolean isEnCurso() {
        return "EN_CURSO".equals(estado);
    }

    public boolean isPendiente() {
        return "PENDIENTE".equals(estado);
    }
} 