package com.gestionclub.padres.model;

public class Evento {
    private String id;
    private String tipo; // "amistoso", "partido", "entrenamiento"
    private String titulo;

    public Evento(String id, String tipo, String titulo) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
}