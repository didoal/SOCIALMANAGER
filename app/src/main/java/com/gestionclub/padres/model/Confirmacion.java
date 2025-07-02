package com.gestionclub.padres.model;

public class Confirmacion {
    private String usuarioId;
    private String eventoId;
    private boolean presente;

    public Confirmacion(String usuarioId, String eventoId, boolean presente) {
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
        this.presente = presente;
    }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }
    public boolean isPresente() { return presente; }
    public void setPresente(boolean presente) { this.presente = presente; }
}