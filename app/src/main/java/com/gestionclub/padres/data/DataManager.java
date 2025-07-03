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
    private static final String KEY_EQUIPO_SELECCIONADO = "equipo_seleccionado";

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

        if (getEquipos().isEmpty()) {
            List<Equipo> equiposEjemplo = new ArrayList<>();
            equiposEjemplo.add(new Equipo("Leones Sub-10", "Sub-10", "entrenador1", "Carlos López", "Equipo de fútbol para niños de 8-10 años"));
            equiposEjemplo.add(new Equipo("Tigres Sub-12", "Sub-12", "entrenador2", "Ana Martínez", "Equipo de fútbol para niños de 10-12 años"));
            equiposEjemplo.add(new Equipo("Águilas Sub-14", "Sub-14", "entrenador3", "Roberto Sánchez", "Equipo de fútbol para adolescentes de 12-14 años"));
            guardarEquipos(equiposEjemplo);
        }

        if (getUsuarios().isEmpty()) {
            List<Usuario> usuariosEjemplo = new ArrayList<>();
            
            // Administrador
            Usuario admin = new Usuario("Administrador", "admin@club.com", "admin123", "administrador");
            usuariosEjemplo.add(admin);
            
            // Entrenadores
            Usuario entrenador1 = new Usuario("Carlos López", "carlos@club.com", "entrenador123", "entrenador");
            entrenador1.setEquipoId("equipo1");
            entrenador1.setEquipoNombre("Leones Sub-10");
            usuariosEjemplo.add(entrenador1);
            
            Usuario entrenador2 = new Usuario("Ana Martínez", "ana@club.com", "entrenador123", "entrenador");
            entrenador2.setEquipoId("equipo2");
            entrenador2.setEquipoNombre("Tigres Sub-12");
            usuariosEjemplo.add(entrenador2);
            
            Usuario entrenador3 = new Usuario("Roberto Sánchez", "roberto@club.com", "entrenador123", "entrenador");
            entrenador3.setEquipoId("equipo3");
            entrenador3.setEquipoNombre("Águilas Sub-14");
            usuariosEjemplo.add(entrenador3);
            
            // Jugadores
            Usuario jugador1 = new Usuario("Juan Pérez", "juan@club.com", "jugador123", "jugador");
            jugador1.setEquipoId("equipo1");
            jugador1.setEquipoNombre("Leones Sub-10");
            usuariosEjemplo.add(jugador1);
            
            Usuario jugador2 = new Usuario("María García", "maria@club.com", "jugador123", "jugador");
            jugador2.setEquipoId("equipo2");
            jugador2.setEquipoNombre("Tigres Sub-12");
            usuariosEjemplo.add(jugador2);
            
            Usuario jugador3 = new Usuario("Pedro López", "pedro@club.com", "jugador123", "jugador");
            jugador3.setEquipoId("equipo3");
            jugador3.setEquipoNombre("Águilas Sub-14");
            usuariosEjemplo.add(jugador3);
            
            guardarUsuarios(usuariosEjemplo);
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
        List<Equipo> equipos = getEquipos();
        for (Equipo equipo : equipos) {
            if (equipo.getId().equals(equipoId)) {
                return equipo;
            }
        }
        return null;
    }

    public String getEquipoSeleccionado() {
        return sharedPreferences.getString(KEY_EQUIPO_SELECCIONADO, null);
    }

    public void setEquipoSeleccionado(String equipoId) {
        sharedPreferences.edit().putString(KEY_EQUIPO_SELECCIONADO, equipoId).apply();
    }

    // Métodos para filtrar datos por equipo según rol
    public List<Usuario> getUsuariosPorEquipo(String equipoId) {
        List<Usuario> todos = getUsuarios();
        List<Usuario> delEquipo = new ArrayList<>();
        for (Usuario usuario : todos) {
            if (equipoId.equals(usuario.getEquipoId())) {
                delEquipo.add(usuario);
            }
        }
        return delEquipo;
    }

    public List<Evento> getEventosPorEquipo(String equipoId) {
        List<Evento> todos = getEventos();
        List<Evento> delEquipo = new ArrayList<>();
        for (Evento evento : todos) {
            if (equipoId.equals(evento.getEquipoId())) {
                delEquipo.add(evento);
            }
        }
        return delEquipo;
    }

    public List<Asistencia> getAsistenciasPorEquipo(String equipoId) {
        List<Asistencia> todas = getAsistencias();
        List<Asistencia> delEquipo = new ArrayList<>();
        for (Asistencia asistencia : todas) {
            if (equipoId.equals(asistencia.getEquipoId())) {
                delEquipo.add(asistencia);
            }
        }
        return delEquipo;
    }

    public List<Mensaje> getMensajesPorEquipo(String equipoId) {
        List<Mensaje> todos = getMensajes();
        List<Mensaje> delEquipo = new ArrayList<>();
        for (Mensaje mensaje : todos) {
            if (equipoId.equals(mensaje.getEquipoId())) {
                delEquipo.add(mensaje);
            }
        }
        return delEquipo;
    }

    public List<ObjetoPerdido> getObjetosPerdidosPorEquipo(String equipoId) {
        List<ObjetoPerdido> todos = getObjetosPerdidos();
        List<ObjetoPerdido> delEquipo = new ArrayList<>();
        for (ObjetoPerdido objeto : todos) {
            if (equipoId.equals(objeto.getEquipoId())) {
                delEquipo.add(objeto);
            }
        }
        return delEquipo;
    }

    // Método para obtener datos según el rol del usuario
    public List<Usuario> getUsuariosSegunRol(Usuario usuario) {
        if (usuario.isEsAdmin()) {
            String equipoSeleccionado = getEquipoSeleccionado();
            if (equipoSeleccionado != null) {
                return getUsuariosPorEquipo(equipoSeleccionado);
            }
            return getUsuarios(); // Todos los usuarios
        } else {
            return getUsuariosPorEquipo(usuario.getEquipoId());
        }
    }

    public List<Evento> getEventosSegunRol(Usuario usuario) {
        if (usuario.isEsAdmin()) {
            String equipoSeleccionado = getEquipoSeleccionado();
            if (equipoSeleccionado != null) {
                return getEventosPorEquipo(equipoSeleccionado);
            }
            return getEventos(); // Todos los eventos
        } else {
            return getEventosPorEquipo(usuario.getEquipoId());
        }
    }

    public List<Asistencia> getAsistenciasSegunRol(Usuario usuario) {
        if (usuario.isEsAdmin()) {
            String equipoSeleccionado = getEquipoSeleccionado();
            if (equipoSeleccionado != null) {
                return getAsistenciasPorEquipo(equipoSeleccionado);
            }
            return getAsistencias(); // Todas las asistencias
        } else {
            return getAsistenciasPorEquipo(usuario.getEquipoId());
        }
    }

    public List<Mensaje> getMensajesSegunRol(Usuario usuario) {
        if (usuario.isEsAdmin()) {
            String equipoSeleccionado = getEquipoSeleccionado();
            if (equipoSeleccionado != null) {
                return getMensajesPorEquipo(equipoSeleccionado);
            }
            return getMensajes(); // Todos los mensajes
        } else {
            return getMensajesPorEquipo(usuario.getEquipoId());
        }
    }

    public List<ObjetoPerdido> getObjetosPerdidosSegunRol(Usuario usuario) {
        if (usuario.isEsAdmin()) {
            String equipoSeleccionado = getEquipoSeleccionado();
            if (equipoSeleccionado != null) {
                return getObjetosPerdidosPorEquipo(equipoSeleccionado);
            }
            return getObjetosPerdidos(); // Todos los objetos
        } else {
            return getObjetosPerdidosPorEquipo(usuario.getEquipoId());
        }
    }
} 