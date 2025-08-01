package com.gestionclub.padres.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.ResultadoPartido;
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
    private static final String KEY_RESULTADOS_PARTIDOS = "resultados_partidos";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public DataManager(Context context) {
        if (context != null) {
            try {
                sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                gson = new Gson();
                inicializarDatosEjemplo();
            } catch (Exception e) {
                android.util.Log.e("DataManager", "Error al inicializar DataManager", e);
                // En caso de error, crear objetos básicos para evitar NullPointerException
                sharedPreferences = null;
                gson = new Gson();
            }
        } else {
            android.util.Log.e("DataManager", "Error: Context es null en DataManager");
            // Crear objetos básicos para evitar NullPointerException
            sharedPreferences = null;
            gson = new Gson();
        }
    }

    private void inicializarDatosEjemplo() {
        // Solo inicializar si no hay datos existentes
        if (getMensajes().isEmpty()) {
            List<Mensaje> mensajesEjemplo = new ArrayList<>();
            
            // Mensaje de bienvenida destacado
            Mensaje mensajeBienvenida = new Mensaje("admin1", "José Antonio Suarez González", 
                "¡Bienvenidos al club CD Santiaguiño de Guizán! Este es el chat general para todos los miembros.", true);
            mensajeBienvenida.setDestacado(true);
            mensajesEjemplo.add(mensajeBienvenida);
            
            // Mensaje sobre horarios destacado
            Mensaje mensajeHorarios = new Mensaje("admin1", "José Antonio Suarez González", 
                "📅 Horarios de entrenamiento: Lunes y Miércoles de 18:00 a 20:00. ¡No falten!", true);
            mensajeHorarios.setDestacado(true);
            mensajesEjemplo.add(mensajeHorarios);
            
            // Mensaje sobre inicio de temporada destacado
            Mensaje mensajeTemporada = new Mensaje("admin1", "José Antonio Suarez González", 
                "Inicio dos Adestramentos Tempada 2025/2026", true);
            mensajeTemporada.setDestacado(true);
            mensajesEjemplo.add(mensajeTemporada);
            
            // Mensaje sobre partido destacado
            Mensaje mensajePartido = new Mensaje("admin1", "José Antonio Suarez González", 
                "⚽ Próximo partido: Sábado 15 de febrero vs Club Deportivo Local. ¡Vamos equipo!", true);
            mensajePartido.setDestacado(true);
            mensajesEjemplo.add(mensajePartido);
            
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

    public void eliminarMensaje(String mensajeId) {
        List<Mensaje> mensajes = getMensajes();
        mensajes.removeIf(mensaje -> mensaje.getId().equals(mensajeId));
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
        // Ordenar de más nuevos a más antiguos
        destacados.sort((m1, m2) -> {
            if (m1.getFechaCreacion() == null && m2.getFechaCreacion() == null) return 0;
            if (m1.getFechaCreacion() == null) return 1;
            if (m2.getFechaCreacion() == null) return -1;
            return m2.getFechaCreacion().compareTo(m1.getFechaCreacion());
        });
        return destacados;
    }

    // Agregar mensaje destacado
    public void agregarMensajeDestacado(Mensaje mensaje) {
        mensaje.setDestacado(true);
        agregarMensaje(mensaje);
    }

    // Actualizar notificación
    public void actualizarNotificacion(Notificacion notificacionActualizada) {
        List<Notificacion> notificaciones = getNotificaciones();
        for (int i = 0; i < notificaciones.size(); i++) {
            if (notificaciones.get(i).getId().equals(notificacionActualizada.getId())) {
                notificaciones.set(i, notificacionActualizada);
                break;
            }
        }
        guardarNotificaciones(notificaciones);
    }

    // Borrar todas las notificaciones
    public void borrarTodasNotificaciones() {
        guardarNotificaciones(new ArrayList<>());
    }

    // Borrar notificación por ID
    public void borrarNotificacion(String id) {
        List<Notificacion> notificaciones = getNotificaciones();
        List<Notificacion> nuevas = new ArrayList<>();
        for (Notificacion n : notificaciones) {
            if (!n.getId().equals(id)) {
                nuevas.add(n);
            }
        }
        guardarNotificaciones(nuevas);
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

    public void eliminarNotificacion(String notificacionId) {
        List<Notificacion> notificaciones = getNotificaciones();
        List<Notificacion> notificacionesFiltradas = new ArrayList<>();
        for (Notificacion notificacion : notificaciones) {
            if (!notificacion.getId().equals(notificacionId)) {
                notificacionesFiltradas.add(notificacion);
            }
        }
        guardarNotificaciones(notificacionesFiltradas);
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
        try {
            if (sharedPreferences == null || gson == null) {
                android.util.Log.w("DataManager", "Advertencia: sharedPreferences o gson es null, retornando lista vacía");
                return new ArrayList<>();
            }
            
            String json = sharedPreferences.getString(KEY_EVENTOS, "[]");
            Type type = new TypeToken<ArrayList<Evento>>(){}.getType();
            List<Evento> eventos = gson.fromJson(json, type);
            
            // Verificar que la lista no sea null
            if (eventos == null) {
                android.util.Log.w("DataManager", "Advertencia: getEventos() devolvió null, retornando lista vacía");
                return new ArrayList<>();
            }
            
            return eventos;
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error al obtener eventos", e);
            return new ArrayList<>();
        }
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
        try {
            if (sharedPreferences == null || gson == null) {
                android.util.Log.w("DataManager", "Advertencia: sharedPreferences o gson es null en getUsuarioActual");
                return null;
            }
            
            String json = sharedPreferences.getString(KEY_USUARIO_ACTUAL, null);
            if (json == null) {
                return null;
            }
            
            return gson.fromJson(json, Usuario.class);
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error al obtener usuario actual", e);
            return null;
        }
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

    // Método para crear mensajes destacados de ejemplo
    public void crearMensajesDestacadosEjemplo() {
        List<Mensaje> mensajes = getMensajes();
        
        // Limpiar mensajes destacados existentes
        for (Mensaje mensaje : mensajes) {
            mensaje.setDestacado(false);
        }
        
        // Crear nuevos mensajes destacados
        Mensaje mensaje1 = new Mensaje("admin1", "José Antonio Suarez González", 
            "¡Bienvenidos al club CD Santiaguiño de Guizán! Este es el chat general para todos los miembros.", true);
        mensaje1.setDestacado(true);
        mensajes.add(mensaje1);
        
        Mensaje mensaje2 = new Mensaje("admin1", "José Antonio Suarez González", 
            "📅 Horarios de entrenamiento: Lunes y Miércoles de 18:00 a 20:00. ¡No falten!", true);
        mensaje2.setDestacado(true);
        mensajes.add(mensaje2);
        
        Mensaje mensaje3 = new Mensaje("admin1", "José Antonio Suarez González", 
            "Inicio dos Adestramentos Tempada 2025/2026", true);
        mensaje3.setDestacado(true);
        mensajes.add(mensaje3);
        
        Mensaje mensaje4 = new Mensaje("admin1", "José Antonio Suarez González", 
            "⚽ Próximo partido: Sábado 15 de febrero vs Club Deportivo Local. ¡Vamos equipo!", true);
        mensaje4.setDestacado(true);
        mensajes.add(mensaje4);
        
        guardarMensajes(mensajes);
    }

    // Métodos para gestionar resultados de partidos
    public List<ResultadoPartido> getResultadosPartidos() {
        if (sharedPreferences == null) return new ArrayList<>();
        
        String json = sharedPreferences.getString(KEY_RESULTADOS_PARTIDOS, "[]");
        Type type = new TypeToken<ArrayList<ResultadoPartido>>(){}.getType();
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            android.util.Log.e("DataManager", "Error al cargar resultados de partidos", e);
            return new ArrayList<>();
        }
    }

    public void guardarResultadosPartidos(List<ResultadoPartido> resultados) {
        if (sharedPreferences == null) return;
        
        String json = gson.toJson(resultados);
        sharedPreferences.edit().putString(KEY_RESULTADOS_PARTIDOS, json).apply();
    }

    public void agregarResultadoPartido(ResultadoPartido resultado) {
        List<ResultadoPartido> resultados = getResultadosPartidos();
        resultados.add(resultado);
        guardarResultadosPartidos(resultados);
    }

    public void actualizarResultadoPartido(ResultadoPartido resultadoActualizado) {
        List<ResultadoPartido> resultados = getResultadosPartidos();
        for (int i = 0; i < resultados.size(); i++) {
            if (resultados.get(i).getId().equals(resultadoActualizado.getId())) {
                resultados.set(i, resultadoActualizado);
                break;
            }
        }
        guardarResultadosPartidos(resultados);
    }

    public void eliminarResultadoPartido(String resultadoId) {
        List<ResultadoPartido> resultados = getResultadosPartidos();
        resultados.removeIf(resultado -> resultado.getId().equals(resultadoId));
        guardarResultadosPartidos(resultados);
    }

    public List<ResultadoPartido> getResultadosPorEquipo(String equipoId) {
        List<ResultadoPartido> todosLosResultados = getResultadosPartidos();
        List<ResultadoPartido> resultadosEquipo = new ArrayList<>();
        
        for (ResultadoPartido resultado : todosLosResultados) {
            if (resultado.getEquipoId() != null && resultado.getEquipoId().equals(equipoId)) {
                resultadosEquipo.add(resultado);
            }
        }
        
        // Ordenar por fecha (más recientes primero)
        resultadosEquipo.sort((r1, r2) -> {
            if (r1.getFechaPartido() == null && r2.getFechaPartido() == null) return 0;
            if (r1.getFechaPartido() == null) return 1;
            if (r2.getFechaPartido() == null) return -1;
            return r2.getFechaPartido().compareTo(r1.getFechaPartido());
        });
        
        return resultadosEquipo;
    }

    public List<ResultadoPartido> getUltimosResultados(int cantidad) {
        List<ResultadoPartido> todosLosResultados = getResultadosPartidos();
        
        // Ordenar por fecha (más recientes primero)
        todosLosResultados.sort((r1, r2) -> {
            if (r1.getFechaPartido() == null && r2.getFechaPartido() == null) return 0;
            if (r1.getFechaPartido() == null) return 1;
            if (r2.getFechaPartido() == null) return -1;
            return r2.getFechaPartido().compareTo(r1.getFechaPartido());
        });
        
        // Retornar solo los primeros 'cantidad' resultados
        if (todosLosResultados.size() <= cantidad) {
            return todosLosResultados;
        } else {
            return todosLosResultados.subList(0, cantidad);
        }
    }

    public void crearResultadosPartidosEjemplo() {
        List<ResultadoPartido> resultados = new ArrayList<>();
        
        // Crear algunos resultados de ejemplo
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date fecha1 = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, -14);
        Date fecha2 = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, -21);
        Date fecha3 = cal.getTime();
        
        // Resultado 1: Victoria
        ResultadoPartido resultado1 = new ResultadoPartido(
            "evento_partido_1", "Santiaguiño", "Club Deportivo Local", 
            "equipo_alevin_a", fecha1, "Campo de futbol A Bouza"
        );
        resultado1.setGolesLocal(4);
        resultado1.setGolesVisitante(2);
        resultado1.setGolesLocalPrimera(2);
        resultado1.setGolesVisitantePrimera(1);
        resultado1.setEstado("FINALIZADO");
        resultado1.setCreadorId("admin1");
        resultado1.setCreadorNombre("José Antonio Suarez González");
        resultados.add(resultado1);
        
        // Resultado 2: Empate
        ResultadoPartido resultado2 = new ResultadoPartido(
            "evento_partido_2", "Santiaguiño", "Xuvenil C", 
            "equipo_infantil_b", fecha2, "Estadio Municipal"
        );
        resultado2.setGolesLocal(2);
        resultado2.setGolesVisitante(2);
        resultado2.setGolesLocalPrimera(1);
        resultado2.setGolesVisitantePrimera(1);
        resultado2.setEstado("FINALIZADO");
        resultado2.setCreadorId("admin1");
        resultado2.setCreadorNombre("José Antonio Suarez González");
        resultados.add(resultado2);
        
        // Resultado 3: Derrota
        ResultadoPartido resultado3 = new ResultadoPartido(
            "evento_partido_3", "Santiaguiño", "Club Atlético", 
            "equipo_cadete_a", fecha3, "Cancha Municipal"
        );
        resultado3.setGolesLocal(1);
        resultado3.setGolesVisitante(3);
        resultado3.setGolesLocalPrimera(0);
        resultado3.setGolesVisitantePrimera(2);
        resultado3.setEstado("FINALIZADO");
        resultado3.setCreadorId("admin1");
        resultado3.setCreadorNombre("José Antonio Suarez González");
        resultados.add(resultado3);
        
        guardarResultadosPartidos(resultados);
    }
} 