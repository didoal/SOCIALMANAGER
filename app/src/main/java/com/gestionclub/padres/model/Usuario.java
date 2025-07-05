package com.gestionclub.padres.model;

import java.util.Date;
import java.util.UUID;

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String password;
    private String rol; // "padre", "tutor" o "administrador"
    private String jugador;
    private Date fechaRegistro;
    private boolean activo;

    public Usuario(String nombre, String jugador, String password, String rol) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.jugador = jugador;
        this.password = password;
        this.rol = rol;
        this.fechaRegistro = new Date();
        this.activo = true;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getJugador() { return jugador; }
    public void setJugador(String jugador) { this.jugador = jugador; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isEsAdmin() {
        return "administrador".equalsIgnoreCase(rol);
    }

    public boolean isEsPadre() {
        return "padre".equalsIgnoreCase(rol) || "tutor".equalsIgnoreCase(rol);
    }

    // Métodos adicionales para compatibilidad
    public String getUsername() {
        return email; // Usamos el email como username
    }

    public String getNombreReal() {
        return nombre;
    }
}