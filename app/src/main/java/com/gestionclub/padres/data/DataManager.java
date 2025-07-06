package com.gestionclub.padres.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.ObjetoPerdido;
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.model.Equipo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataManager {
    private static final String PREF_NAME = "SocialManagerPrefs";
    private static final String KEY_MENSAJES = "mensajes";
    private static final String KEY_OBJETOS_PERDIDOS = "objetos_perdidos";
    private static final String KEY_NOTIFICACIONES = "notificaciones";
    private static final String KEY_EVENTOS = "eventos";
    private static final String KEY_ASISTENCIAS = "asistencias";
    private static final String KEY_USUARIOS = "usuarios";
    private static final String KEY_USUARIO_ACTUAL = "usuario_actual";
    private static final String KEY_EQUIPOS = "equipos";

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

        if (getEventos().isEmpty()) {
            List<Evento> eventosEjemplo = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            
            // Evento para mañana
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 18);
            cal.set(Calendar.MINUTE, 0);
            Date fecha1 = cal.getTime();
            
            // Evento para el fin de semana
            cal.add(Calendar.DAY_OF_MONTH, 5);
            cal.set(Calendar.HOUR_OF_DAY, 10);
            Date fecha2 = cal.getTime();
            
            eventosEjemplo.add(new Evento("Entrenamiento Sub-12", 
                "Entrenamiento técnico y táctico para jugadores Sub-12", 
                fecha1, fecha1, "Cancha Principal", "ENTRENAMIENTO", 
                "admin1", "Administrador", true));
            
            eventosEjemplo.add(new Evento("Partido vs Club Deportivo", 
                "Partido amistoso contra Club Deportivo Local", 
                fecha2, fecha2, "Estadio Municipal", "PARTIDO", 
                "admin1", "Administrador", true));
            
            guardarEventos(eventosEjemplo);
        }

        if (getAsistencias().isEmpty()) {
            List<Asistencia> asistenciasEjemplo = new ArrayList<>();
            asistenciasEjemplo.add(new Asistencia("evento1", "user1", "Juan Pérez", true));
            asistenciasEjemplo.add(new Asistencia("evento1", "user2", "María García", false));
            asistenciasEjemplo.add(new Asistencia("evento2", "user1", "Juan Pérez", true));
            guardarAsistencias(asistenciasEjemplo);
        }

        if (getUsuarios().isEmpty()) {
            List<Usuario> usuariosEjemplo = new ArrayList<>();
            
            // Administrador principal
            Usuario admin = new Usuario("Administrador", "admin@club.com", "admin123", "administrador");
            usuariosEjemplo.add(admin);
            
            // Administrador alternativo para el usuario actual
            Usuario adminUsuario = new Usuario("Diego", "diego@club.com", "admin123", "administrador");
            usuariosEjemplo.add(adminUsuario);
            
            // Padres/Tutores
            Usuario padre1 = new Usuario("Juan Pérez", "juan@club.com", "padre123", "padre");
            padre1.setJugador("Titi");
            usuariosEjemplo.add(padre1);
            
            Usuario padre2 = new Usuario("María García", "maria@club.com", "padre123", "padre");
            padre2.setJugador("Carlos");
            usuariosEjemplo.add(padre2);
            
            guardarUsuarios(usuariosEjemplo);
        }

        if (getEquipos().isEmpty()) {
            List<Equipo> equiposEjemplo = new ArrayList<>();
            
            // Equipos de ejemplo
            Equipo equipo1 = new Equipo("Alevín A", "Alevín", "Carlos López");
            Equipo equipo2 = new Equipo("Infantil B", "Infantil", "María Rodríguez");
            Equipo equipo3 = new Equipo("Cadete A", "Cadete", "Juan García");
            
            equiposEjemplo.add(equipo1);
            equiposEjemplo.add(equipo2);
            equiposEjemplo.add(equipo3);
            
            guardarEquipos(equiposEjemplo);
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

    // Gestión de Eventos
    public List<Evento> getEventos() {
        String json = sharedPreferences.getString(KEY_EVENTOS, "[]");
        Type type = new TypeToken<ArrayList<Evento>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void guardarEventos(List<Evento> eventos) {
        String json = gson.toJson(eventos);
        sharedPreferences.edit().putString(KEY_EVENTOS, json).apply();
    }

    public void agregarEvento(Evento evento) {
        List<Evento> eventos = getEventos();
        eventos.add(evento);
        guardarEventos(eventos);
    }

    public void actualizarEvento(Evento eventoActualizado) {
        List<Evento> eventos = getEventos();
        for (int i = 0; i < eventos.size(); i++) {
            if (eventos.get(i).getId().equals(eventoActualizado.getId())) {
                eventos.set(i, eventoActualizado);
                break;
            }
        }
        guardarEventos(eventos);
    }

    public void eliminarEvento(String eventoId) {
        List<Evento> eventos = getEventos();
        List<Evento> eventosFiltrados = new ArrayList<>();
        for (Evento evento : eventos) {
            if (!evento.getId().equals(eventoId)) {
                eventosFiltrados.add(evento);
            }
        }
        guardarEventos(eventosFiltrados);
    }

    // Gestión de Asistencias
    public List<Asistencia> getAsistencias() {
        String json = sharedPreferences.getString(KEY_ASISTENCIAS, "[]");
        Type type = new TypeToken<ArrayList<Asistencia>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void guardarAsistencias(List<Asistencia> asistencias) {
        String json = gson.toJson(asistencias);
        sharedPreferences.edit().putString(KEY_ASISTENCIAS, json).apply();
    }

    public void agregarAsistencia(Asistencia asistencia) {
        List<Asistencia> asistencias = getAsistencias();
        asistencias.add(asistencia);
        guardarAsistencias(asistencias);
    }

    public void actualizarAsistencia(Asistencia asistenciaActualizada) {
        List<Asistencia> asistencias = getAsistencias();
        for (int i = 0; i < asistencias.size(); i++) {
            if (asistencias.get(i).getId().equals(asistenciaActualizada.getId())) {
                asistencias.set(i, asistenciaActualizada);
                break;
            }
        }
        guardarAsistencias(asistencias);
    }

    public List<Asistencia> getAsistenciasPorJugador(String jugadorId) {
        List<Asistencia> todas = getAsistencias();
        List<Asistencia> delJugador = new ArrayList<>();
        for (Asistencia asistencia : todas) {
            if (asistencia.getJugadorId().equals(jugadorId)) {
                delJugador.add(asistencia);
            }
        }
        return delJugador;
    }

    public double getPorcentajeAsistencia(String jugadorId) {
        List<Asistencia> asistencias = getAsistenciasPorJugador(jugadorId);
        if (asistencias.isEmpty()) return 0.0;
        
        int total = asistencias.size();
        int asistio = 0;
        for (Asistencia asistencia : asistencias) {
            if (asistencia.isAsistio()) {
                asistio++;
            }
        }
        return (double) asistio / total * 100;
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

    public void crearNotificacionEvento(Evento evento) {
        Notificacion notificacion = new Notificacion(
            "Nuevo evento: " + evento.getTitulo(),
            evento.getDescripcion() + "\nFecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(evento.getFechaInicio()) + "\nUbicación: " + evento.getUbicacion(),
            "EVENTO",
            null, // Notificación global
            evento.getCreadorId(),
            evento.getCreadorNombre(),
            evento.isEsAdmin()
        );
        agregarNotificacion(notificacion);
    }

    public void crearNotificacionMensaje(Mensaje mensaje) {
        Notificacion notificacion = new Notificacion(
            "Nuevo mensaje de " + mensaje.getAutorNombre(),
            mensaje.getContenido(),
            "MENSAJE",
            null, // Notificación global
            mensaje.getAutorId(),
            mensaje.getAutorNombre(),
            mensaje.isEsAdmin()
        );
        agregarNotificacion(notificacion);
    }

    public void crearNotificacionObjeto(ObjetoPerdido objeto) {
        Notificacion notificacion = new Notificacion(
            "Nuevo objeto perdido: " + objeto.getNombre(),
            "Se reportó: " + objeto.getDescripcion() + "\nUbicación: " + objeto.getUbicacion(),
            "OBJETO",
            null, // Notificación global
            objeto.getReportadoPor(),
            objeto.getReportadoPorNombre(),
            objeto.isEsAdmin()
        );
        agregarNotificacion(notificacion);
    }

    public void crearNotificacionSolicitud(String titulo, String mensaje, String tipo) {
        Notificacion notificacion = new Notificacion(
            titulo,
            mensaje,
            "SOLICITUD",
            null, // Notificación global
            "admin1",
            "Sistema",
            true
        );
        agregarNotificacion(notificacion);
    }

    public void crearDatosEjemploNotificaciones() {
        // Crear algunas notificaciones de ejemplo
        crearNotificacionSolicitud(
            "Nuevo jugador registrado",
            "Juan Pérez se ha registrado en el club. Revisa su perfil y confirma su membresía.",
            "REGISTRO"
        );
        
        crearNotificacionSolicitud(
            "Solicitud de cambio de horario",
            "María García solicita cambiar su horario de entrenamiento del martes al jueves.",
            "HORARIO"
        );
        
        crearNotificacionSolicitud(
            "Reporte de objeto encontrado",
            "Se encontró una mochila en el vestuario. Revisa la sección de objetos perdidos.",
            "OBJETO"
        );
    }

    // Gestión de Equipos
    public List<Equipo> getEquipos() {
        String json = sharedPreferences.getString(KEY_EQUIPOS, "[]");
        Type type = new TypeToken<ArrayList<Equipo>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void guardarEquipos(List<Equipo> equipos) {
        String json = gson.toJson(equipos);
        sharedPreferences.edit().putString(KEY_EQUIPOS, json).apply();
    }

    public void agregarEquipo(Equipo equipo) {
        List<Equipo> equipos = getEquipos();
        equipos.add(equipo);
        guardarEquipos(equipos);
    }

    public Equipo getEquipoPorId(String equipoId) {
        for (Equipo equipo : getEquipos()) {
            if (equipo.getId().equals(equipoId)) {
                return equipo;
            }
        }
        return null;
    }

    public List<Usuario> getJugadoresPorEquipo(String equipoId) {
        List<Usuario> jugadores = new ArrayList<>();
        Equipo equipo = getEquipoPorId(equipoId);
        if (equipo != null && equipo.getJugadoresIds() != null) {
            for (String jugadorId : equipo.getJugadoresIds()) {
                for (Usuario usuario : getUsuarios()) {
                    if (usuario.getId().equals(jugadorId) && usuario.isEsPadre()) {
                        jugadores.add(usuario);
                        break;
                    }
                }
            }
        }
        return jugadores;
    }

    // Buscar evento por ID
    public Evento getEventoById(String eventoId) {
        for (Evento evento : getEventos()) {
            if (evento.getId().equals(eventoId)) {
                return evento;
            }
        }
        return null;
    }

    // Verificar si existe un equipo por nombre
    public boolean existeEquipo(String nombre) {
        for (Equipo equipo : getEquipos()) {
            if (equipo.getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // Eliminar equipo por nombre
    public void eliminarEquipo(String nombre) {
        List<Equipo> equipos = getEquipos();
        List<Equipo> nuevos = new ArrayList<>();
        for (Equipo equipo : equipos) {
            if (!equipo.getNombre().equalsIgnoreCase(nombre)) {
                nuevos.add(equipo);
            }
        }
        guardarEquipos(nuevos);
    }

    // Obtener nombres de todos los equipos
    public List<String> getNombresEquipos() {
        List<String> nombres = new ArrayList<>();
        for (Equipo equipo : getEquipos()) {
            nombres.add(equipo.getNombre());
        }
        return nombres;
    }

    // Verificar si existe un usuario por email
    public boolean existeUsuario(String email) {
        for (Usuario usuario : getUsuarios()) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    // Agregar usuario
    public void agregarUsuario(Usuario usuario) {
        List<Usuario> usuarios = getUsuarios();
        usuarios.add(usuario);
        guardarUsuarios(usuarios);
    }

    // Eliminar usuario por email
    public void eliminarUsuario(String email) {
        List<Usuario> usuarios = getUsuarios();
        List<Usuario> nuevos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (!usuario.getEmail().equalsIgnoreCase(email)) {
                nuevos.add(usuario);
            }
        }
        guardarUsuarios(nuevos);
    }
} 