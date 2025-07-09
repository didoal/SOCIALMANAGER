package com.gestionclub.padres.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gestionclub.padres.model.Mensaje;
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
    private static final String KEY_NOTIFICACIONES = "notificaciones";
    private static final String KEY_EVENTOS = "eventos";
    private static final String KEY_ASISTENCIAS = "asistencias";
    private static final String KEY_USUARIOS = "usuarios";
    private static final String KEY_USUARIO_ACTUAL = "usuario_actual";
    private static final String KEY_EQUIPOS = "equipos";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public DataManager(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            gson = new Gson();
            inicializarDatosEjemplo();
        }
    }

    private void inicializarDatosEjemplo() {
        // Solo inicializar si no hay datos existentes
        if (getMensajes().isEmpty()) {
            List<Mensaje> mensajesEjemplo = new ArrayList<>();
            
            // Mensaje de bienvenida destacado
            Mensaje mensajeBienvenida = new Mensaje("admin1", "Administrador", 
                "¡Bienvenidos al club CD Santiaguiño de Guizán! Este es el chat general para todos los miembros.", true);
            mensajeBienvenida.setDestacado(true);
            mensajesEjemplo.add(mensajeBienvenida);
            
            // Mensaje sobre horarios destacado
            Mensaje mensajeHorarios = new Mensaje("admin1", "Administrador", 
                "📅 Horarios de entrenamiento: Lunes y Miércoles de 18:00 a 20:00. ¡No falten!", true);
            mensajeHorarios.setDestacado(true);
            mensajesEjemplo.add(mensajeHorarios);
            
            // Mensaje normal
            mensajesEjemplo.add(new Mensaje("user1", "Juan Pérez", 
                "Hola a todos, ¿alguien sabe cuándo es el próximo partido?", false));
            
            guardarMensajes(mensajesEjemplo);
        }

        if (getNotificaciones().isEmpty()) {
            List<Notificacion> notificacionesEjemplo = new ArrayList<>();
            notificacionesEjemplo.add(new Notificacion("Nuevo evento", 
                "Se ha programado un nuevo entrenamiento para el próximo sábado", "EVENTO", 
                null, "admin1", "Administrador", true));
            notificacionesEjemplo.add(new Notificacion("Bienvenida al club", 
                "¡Bienvenido al CD Santiaguiño de Guizán! Esperamos que disfrutes tu experiencia.", "SISTEMA", 
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
            Usuario admin = new Usuario("Administrador", null, "admin123", "administrador");
            admin.setId("admin_001");
            admin.setEmail("admin@club.com");
            usuariosEjemplo.add(admin);
            
            // Administrador alternativo
            Usuario adminAlt = new Usuario("Diego", null, "admin123", "administrador");
            adminAlt.setId("admin_002");
            adminAlt.setEmail("diego@club.com");
            usuariosEjemplo.add(adminAlt);
            
            // Padres/Tutores organizados
            Usuario padre1 = new Usuario("Juan Pérez", "Carlos Pérez", "padre123", "padre");
            padre1.setId("padre_001");
            padre1.setEmail("juan@club.com");
            padre1.setEquipoId("equipo_alevin_a");
            usuariosEjemplo.add(padre1);
            
            Usuario padre2 = new Usuario("María García", "Ana García", "padre123", "padre");
            padre2.setId("padre_002");
            padre2.setEmail("maria@club.com");
            padre2.setEquipoId("equipo_infantil_b");
            usuariosEjemplo.add(padre2);
            
            Usuario padre3 = new Usuario("Luis Martínez", "Pedro Martínez", "padre123", "padre");
            padre3.setId("padre_003");
            padre3.setEmail("luis@club.com");
            padre3.setEquipoId("equipo_cadete_a");
            usuariosEjemplo.add(padre3);
            
            Usuario padre4 = new Usuario("Carmen López", "Miguel López", "padre123", "padre");
            padre4.setId("padre_004");
            padre4.setEmail("carmen@club.com");
            padre4.setEquipoId("equipo_alevin_a");
            usuariosEjemplo.add(padre4);
            
            guardarUsuarios(usuariosEjemplo);
        }

        if (getEquipos().isEmpty()) {
            List<Equipo> equiposEjemplo = new ArrayList<>();
            
            // Equipos organizados con IDs únicos
            Equipo alevinA = new Equipo("Alevín A", "Alevín", "Carlos López");
            alevinA.setId("equipo_alevin_a");
            equiposEjemplo.add(alevinA);
            
            Equipo infantilB = new Equipo("Infantil B", "Infantil", "María Rodríguez");
            infantilB.setId("equipo_infantil_b");
            equiposEjemplo.add(infantilB);
            
            Equipo cadeteA = new Equipo("Cadete A", "Cadete", "Juan García");
            cadeteA.setId("equipo_cadete_a");
            equiposEjemplo.add(cadeteA);
            
            guardarEquipos(equiposEjemplo);
            
            // Asignar jugadores a equipos después de crear los equipos
            agregarJugadorAEquipo("equipo_alevin_a", "padre_001");
            agregarJugadorAEquipo("equipo_alevin_a", "padre_004");
            agregarJugadorAEquipo("equipo_infantil_b", "padre_002");
            agregarJugadorAEquipo("equipo_cadete_a", "padre_003");
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

    public void actualizarMensaje(Mensaje mensajeActualizado) {
        List<Mensaje> mensajes = getMensajes();
        for (int i = 0; i < mensajes.size(); i++) {
            if (mensajes.get(i).getId().equals(mensajeActualizado.getId())) {
                mensajes.set(i, mensajeActualizado);
                break;
            }
        }
        guardarMensajes(mensajes);
    }

    public void toggleDestacadoMensaje(String mensajeId) {
        List<Mensaje> mensajes = getMensajes();
        for (Mensaje mensaje : mensajes) {
            if (mensaje.getId().equals(mensajeId)) {
                mensaje.setDestacado(!mensaje.isDestacado());
                break;
            }
        }
        guardarMensajes(mensajes);
    }

    public List<Mensaje> getMensajesDestacados() {
        List<Mensaje> todosMensajes = getMensajes();
        List<Mensaje> destacados = new ArrayList<>();
        for (Mensaje mensaje : todosMensajes) {
            if (mensaje.isDestacado()) {
                destacados.add(mensaje);
            }
        }
        return destacados;
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
        // Si el evento tiene equipo/categoría, notificar solo a ese equipo
        if (evento.getEquipo() != null && !evento.getEquipo().isEmpty()) {
            crearNotificacionParaEquipo(evento, evento.getEquipo());
        } else {
            // Notificación global
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
        
        // Crear recordatorio automático para el día anterior
        crearRecordatorioEvento(evento);
        
        // Crear solicitudes de confirmación de asistencia
        crearSolicitudesConfirmacionAsistencia(evento);
    }
    
    /**
     * Crea un recordatorio automático para un evento (un día antes)
     */
    public void crearRecordatorioEvento(Evento evento) {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(evento.getFechaInicio());
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1); // Un día antes
            
            java.util.Date fechaRecordatorio = cal.getTime();
            java.util.Date ahora = new java.util.Date();
            
            // Solo crear recordatorio si el evento es en el futuro
            if (evento.getFechaInicio().after(ahora)) {
                String mensajeRecordatorio = "Recordatorio: El evento '" + evento.getTitulo() + 
                    "' será mañana a las " + new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(evento.getFechaInicio()) + 
                    " en " + evento.getUbicacion();
                
                // Si el evento tiene equipo/categoría, crear recordatorio específico para ese equipo
                if (evento.getEquipo() != null && !evento.getEquipo().isEmpty()) {
                    crearRecordatorioParaEquipo(evento, mensajeRecordatorio, fechaRecordatorio);
                } else {
                    // Recordatorio global
                    Notificacion recordatorio = new Notificacion(
                        "Recordatorio: " + evento.getTitulo(),
                        mensajeRecordatorio,
                        "RECORDATORIO",
                        null, // Notificación global
                        "sistema",
                        "Sistema Automático",
                        true
                    );
                    
                    // Programar el recordatorio para el día anterior
                    programarRecordatorio(recordatorio, fechaRecordatorio);
                }
            }
        } catch (Exception e) {
            // Log del error pero no fallar la aplicación
            android.util.Log.e("DataManager", "Error creando recordatorio: " + e.getMessage());
        }
    }
    
    /**
     * Crea recordatorio específico para los jugadores de un equipo
     */
    private void crearRecordatorioParaEquipo(Evento evento, String mensajeRecordatorio, java.util.Date fechaRecordatorio) {
        try {
            List<Usuario> jugadoresEquipo = getJugadoresPorEquipo(evento.getEquipo());
            
            for (Usuario jugador : jugadoresEquipo) {
                Notificacion recordatorio = new Notificacion(
                    "Recordatorio para tu equipo: " + evento.getTitulo(),
                    mensajeRecordatorio + "\n\nEste evento es específico para tu equipo.",
                    "RECORDATORIO",
                    jugador.getId(), // Recordatorio específico para este jugador
                    "sistema",
                    "Sistema Automático",
                    true
                );
                
                // Programar el recordatorio para el día anterior
                programarRecordatorio(recordatorio, fechaRecordatorio);
            }
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error creando recordatorio para equipo: " + e.getMessage());
        }
    }
    
    /**
     * Programa un recordatorio para una fecha específica
     */
    private void programarRecordatorio(Notificacion recordatorio, java.util.Date fechaProgramada) {
        // Por ahora, guardamos el recordatorio programado en las notificaciones
        // En una implementación real, usaríamos WorkManager o AlarmManager
        recordatorio.setFechaCreacion(fechaProgramada);
        agregarNotificacion(recordatorio);
    }
    
    /**
     * Verifica y crea recordatorios automáticos para eventos próximos
     */
    public void verificarRecordatoriosAutomaticos() {
        try {
            List<Evento> eventos = getEventos();
            List<Notificacion> notificaciones = getNotificaciones();
            java.util.Calendar ahora = java.util.Calendar.getInstance();
            java.util.Calendar proximas24h = java.util.Calendar.getInstance();
            proximas24h.add(java.util.Calendar.HOUR, 24);
            
            for (Evento evento : eventos) {
                // Verificar si el evento está en las próximas 24 horas
                if (evento.getFechaInicio().after(ahora.getTime()) && 
                    evento.getFechaInicio().before(proximas24h.getTime())) {
                    
                    // Verificar si ya existe una notificación de recordatorio para este evento
                    boolean existeRecordatorio = false;
                    for (Notificacion notificacion : notificaciones) {
                        if (notificacion.getTitulo().contains("Recordatorio: " + evento.getTitulo()) && 
                            "RECORDATORIO".equals(notificacion.getTipo())) {
                            existeRecordatorio = true;
                            break;
                        }
                    }
                    
                    if (!existeRecordatorio) {
                        crearRecordatorioEvento(evento);
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error verificando recordatorios: " + e.getMessage());
        }
    }
    
    /**
     * Crea notificaciones específicas para jugadores de un equipo/categoría
     */
    public void crearNotificacionParaEquipo(Evento evento, String equipoId) {
        try {
            List<Usuario> jugadoresEquipo = getJugadoresPorEquipo(equipoId);
            
            for (Usuario jugador : jugadoresEquipo) {
                Notificacion notificacion = new Notificacion(
                    "Evento para tu equipo: " + evento.getTitulo(),
                    "Tu equipo tiene un evento: " + evento.getDescripcion() + 
                    "\nFecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(evento.getFechaInicio()) + 
                    "\nUbicación: " + evento.getUbicacion(),
                    "EVENTO_EQUIPO",
                    jugador.getId(), // Notificación específica para este jugador
                    evento.getCreadorId(),
                    evento.getCreadorNombre(),
                    evento.isEsAdmin()
                );
                agregarNotificacion(notificacion);
            }
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error creando notificaciones para equipo: " + e.getMessage());
        }
    }

    public void crearNotificacionMensaje(Mensaje mensaje) {
        try {
            // Crear notificación para todos los usuarios del equipo del mensaje
            List<Usuario> usuariosNotificar = new ArrayList<>();
            
            if (mensaje.getEquipo() != null && !mensaje.getEquipo().isEmpty()) {
                // Mensaje específico para un equipo
                for (Usuario usuario : getUsuarios()) {
                    if (usuario.getEquipo() != null && usuario.getEquipo().equals(mensaje.getEquipo())) {
                        usuariosNotificar.add(usuario);
                    }
                }
            } else {
                // Mensaje global - notificar a todos los usuarios
                usuariosNotificar.addAll(getUsuarios());
            }
            
            // Crear notificación para cada usuario
            for (Usuario usuario : usuariosNotificar) {
                Notificacion notificacion = new Notificacion(
                    "Nuevo mensaje: " + mensaje.getAutorNombre(),
                    mensaje.getContenido(),
                    "MENSAJE",
                    usuario.getId(),
                    mensaje.getAutorId(),
                    mensaje.getAutorNombre(),
                    true
                );
                agregarNotificacion(notificacion);
            }
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error creando notificación de mensaje: " + e.getMessage());
        }
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
            "Nuevo evento programado",
            "Se ha programado un nuevo entrenamiento para el próximo fin de semana.",
            "EVENTO"
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

    // Actualizar equipo
    public void actualizarEquipo(Equipo equipoActualizado) {
        List<Equipo> equipos = getEquipos();
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getId().equals(equipoActualizado.getId())) {
                equipos.set(i, equipoActualizado);
                break;
            }
        }
        guardarEquipos(equipos);
    }

    // Agregar jugador a equipo
    public void agregarJugadorAEquipo(String equipoId, String jugadorId) {
        Equipo equipo = getEquipoPorId(equipoId);
        if (equipo != null) {
            equipo.agregarJugador(jugadorId);
            actualizarEquipo(equipo);
        }
    }

    // Remover jugador de equipo
    public void removerJugadorDeEquipo(String equipoId, String jugadorId) {
        Equipo equipo = getEquipoPorId(equipoId);
        if (equipo != null) {
            equipo.removerJugador(jugadorId);
            actualizarEquipo(equipo);
        }
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

    // Actualizar usuario
    public void actualizarUsuario(Usuario usuarioActualizado) {
        List<Usuario> usuarios = getUsuarios();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuarioActualizado.getId())) {
                usuarios.set(i, usuarioActualizado);
                break;
            }
        }
        guardarUsuarios(usuarios);
    }

    /**
     * Crea solicitudes de confirmación de asistencia para un evento
     */
    public void crearSolicitudesConfirmacionAsistencia(Evento evento) {
        try {
            List<Usuario> usuariosNotificar = new ArrayList<>();
            
            // Determinar qué usuarios deben recibir la solicitud
            if (evento.getEquipo() != null && !evento.getEquipo().isEmpty()) {
                // Evento específico para un equipo
                List<Usuario> jugadoresEquipo = getJugadoresPorEquipo(evento.getEquipo());
                usuariosNotificar.addAll(jugadoresEquipo);
            } else {
                // Evento global - notificar a todos los usuarios que no sean admin
                for (Usuario usuario : getUsuarios()) {
                    if (!usuario.isEsAdmin() && usuario.getJugador() != null && !usuario.getJugador().isEmpty()) {
                        usuariosNotificar.add(usuario);
                    }
                }
            }
            
            // Crear solicitud para cada usuario
            for (Usuario usuario : usuariosNotificar) {
                Notificacion solicitud = new Notificacion(
                    "Confirmar asistencia: " + evento.getTitulo(),
                    "Por favor confirma tu asistencia al evento:\n" + evento.getDescripcion() + 
                    "\nFecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(evento.getFechaInicio()) + 
                    "\nUbicación: " + evento.getUbicacion() +
                    "\n\nVe a la sección Asistencia para confirmar.",
                    "SOLICITUD_ASISTENCIA",
                    usuario.getId(), // Notificación específica para este usuario
                    "sistema",
                    "Sistema Automático",
                    true
                );
                
                agregarNotificacion(solicitud);
            }
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error creando solicitudes de confirmación: " + e.getMessage());
        }
    }
} 