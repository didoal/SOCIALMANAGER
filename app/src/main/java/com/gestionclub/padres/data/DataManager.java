package com.gestionclub.padres.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.ObjetoPerdido;
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Usuario;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String PREF_NAME = "SocialManagerPrefs";
    private static final String KEY_MENSAJES = "mensajes";
    private static final String KEY_OBJETOS_PERDIDOS = "objetos_perdidos";
    private static final String KEY_NOTIFICACIONES = "notificaciones";
    private static final String KEY_USUARIOS = "usuarios";
    private static final String KEY_USUARIO_ACTUAL = "usuario_actual";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public DataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        inicializarDatosEjemplo();
    }

    private void inicializarDatosEjemplo() {
        // Solo inicializar si no hay datos existentes
        if (getMensajes().isEmpty()) {
            List<Mensaje> mensajesEjemplo = new ArrayList<>();
            mensajesEjemplo.add(new Mensaje("admin1", "Administrador", 
                "¡Bienvenidos al club! Este es el chat general para todos los miembros.", true));
            mensajesEjemplo.add(new Mensaje("user1", "Juan Pérez", 
                "Hola a todos, ¿alguien sabe cuándo es el próximo entrenamiento?", false));
            guardarMensajes(mensajesEjemplo);
        }

        if (getObjetosPerdidos().isEmpty()) {
            List<ObjetoPerdido> objetosEjemplo = new ArrayList<>();
            objetosEjemplo.add(new ObjetoPerdido("Botella de agua", 
                "Botella azul de 500ml con el logo del club", "Gimnasio", 
                "user1", "Juan Pérez", false));
            objetosEjemplo.add(new ObjetoPerdido("Mochila deportiva", 
                "Mochila negra con rayas rojas, contiene ropa deportiva", "Vestuarios", 
                "user2", "María García", false));
            guardarObjetosPerdidos(objetosEjemplo);
        }

        if (getNotificaciones().isEmpty()) {
            List<Notificacion> notificacionesEjemplo = new ArrayList<>();
            notificacionesEjemplo.add(new Notificacion("Nuevo evento", 
                "Se ha programado un nuevo entrenamiento para el próximo sábado", "EVENTO", 
                null, "admin1", "Administrador", true));
            notificacionesEjemplo.add(new Notificacion("Objeto encontrado", 
                "Se encontró una botella de agua en el gimnasio", "OBJETO", 
                null, "admin1", "Administrador", true));
            guardarNotificaciones(notificacionesEjemplo);
        }
    }

    // Gestión de Mensajes
    public List<Mensaje> getMensajes() {
        String json = sharedPreferences.getString(KEY_MENSAJES, "[]");
        Type type = new TypeToken<ArrayList<Mensaje>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void guardarMensajes(List<Mensaje> mensajes) {
        String json = gson.toJson(mensajes);
        sharedPreferences.edit().putString(KEY_MENSAJES, json).apply();
    }

    public void agregarMensaje(Mensaje mensaje) {
        List<Mensaje> mensajes = getMensajes();
        mensajes.add(mensaje);
        guardarMensajes(mensajes);
    }

    // Gestión de Objetos Perdidos
    public List<ObjetoPerdido> getObjetosPerdidos() {
        String json = sharedPreferences.getString(KEY_OBJETOS_PERDIDOS, "[]");
        Type type = new TypeToken<ArrayList<ObjetoPerdido>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void guardarObjetosPerdidos(List<ObjetoPerdido> objetos) {
        String json = gson.toJson(objetos);
        sharedPreferences.edit().putString(KEY_OBJETOS_PERDIDOS, json).apply();
    }

    public void agregarObjetoPerdido(ObjetoPerdido objeto) {
        List<ObjetoPerdido> objetos = getObjetosPerdidos();
        objetos.add(objeto);
        guardarObjetosPerdidos(objetos);
    }

    public void actualizarObjetoPerdido(ObjetoPerdido objetoActualizado) {
        List<ObjetoPerdido> objetos = getObjetosPerdidos();
        for (int i = 0; i < objetos.size(); i++) {
            if (objetos.get(i).getId().equals(objetoActualizado.getId())) {
                objetos.set(i, objetoActualizado);
                break;
            }
        }
        guardarObjetosPerdidos(objetos);
    }

    // Gestión de Notificaciones
    public List<Notificacion> getNotificaciones() {
        String json = sharedPreferences.getString(KEY_NOTIFICACIONES, "[]");
        Type type = new TypeToken<ArrayList<Notificacion>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void guardarNotificaciones(List<Notificacion> notificaciones) {
        String json = gson.toJson(notificaciones);
        sharedPreferences.edit().putString(KEY_NOTIFICACIONES, json).apply();
    }

    public void agregarNotificacion(Notificacion notificacion) {
        List<Notificacion> notificaciones = getNotificaciones();
        notificaciones.add(notificacion);
        guardarNotificaciones(notificaciones);
    }

    public void marcarNotificacionComoLeida(String notificacionId) {
        List<Notificacion> notificaciones = getNotificaciones();
        for (Notificacion notificacion : notificaciones) {
            if (notificacion.getId().equals(notificacionId)) {
                notificacion.setLeida(true);
                break;
            }
        }
        guardarNotificaciones(notificaciones);
    }

    public int getNotificacionesNoLeidas() {
        List<Notificacion> notificaciones = getNotificaciones();
        int count = 0;
        for (Notificacion notificacion : notificaciones) {
            if (!notificacion.isLeida()) {
                count++;
            }
        }
        return count;
    }

    // Gestión de Usuarios
    public List<Usuario> getUsuarios() {
        String json = sharedPreferences.getString(KEY_USUARIOS, "[]");
        Type type = new TypeToken<ArrayList<Usuario>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void guardarUsuarios(List<Usuario> usuarios) {
        String json = gson.toJson(usuarios);
        sharedPreferences.edit().putString(KEY_USUARIOS, json).apply();
    }

    public Usuario getUsuarioActual() {
        String json = sharedPreferences.getString(KEY_USUARIO_ACTUAL, null);
        return json != null ? gson.fromJson(json, Usuario.class) : null;
    }

    public void guardarUsuarioActual(Usuario usuario) {
        String json = gson.toJson(usuario);
        sharedPreferences.edit().putString(KEY_USUARIO_ACTUAL, json).apply();
    }

    public void cerrarSesion() {
        sharedPreferences.edit().remove(KEY_USUARIO_ACTUAL).apply();
    }
} 