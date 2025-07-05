package com.gestionclub.padres.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Equipo {
    private String id;
    private String nombre;
    private String categoria;
    private String entrenador;
    private List<String> jugadoresIds;
    private boolean activo;

    public Equipo(String nombre, String categoria, String entrenador) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.categoria = categoria;
        this.entrenador = entrenador;
        this.jugadoresIds = new ArrayList<>();
        this.activo = true;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEntrenador() { return entrenador; }
    public void setEntrenador(String entrenador) { this.entrenador = entrenador; }

    public List<String> getJugadoresIds() { return jugadoresIds; }
    public void setJugadoresIds(List<String> jugadoresIds) { this.jugadoresIds = jugadoresIds; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public void agregarJugador(String jugadorId) {
        if (!jugadoresIds.contains(jugadorId)) {
            jugadoresIds.add(jugadorId);
        }
    }

    public void removerJugador(String jugadorId) {
        jugadoresIds.remove(jugadorId);
    }

    public String getNombreCompleto() {
        return nombre + " (" + categoria + ")";
    }
} 