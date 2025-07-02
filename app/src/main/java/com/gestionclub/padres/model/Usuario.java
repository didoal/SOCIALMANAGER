package com.gestionclub.padres.model;

public class Usuario {
    private String username;
    private String nombreReal;
    private String rol; // "jugador" o "administrador"
    private String categoria; // Ejemplo: "Sub-10", "Sub-12"

    public Usuario(String username, String nombreReal, String rol, String categoria) {
        this.username = username;
        this.nombreReal = nombreReal;
        this.rol = rol;
        this.categoria = categoria;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNombreReal() { return nombreReal; }
    public void setNombreReal(String nombreReal) { this.nombreReal = nombreReal; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}