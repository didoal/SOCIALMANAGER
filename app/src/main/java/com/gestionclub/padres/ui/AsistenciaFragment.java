package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.AsistenciaAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AsistenciaFragment extends Fragment {
    private static final String TAG = "AsistenciaFragment";
    
    private DataManager dataManager;
    private RecyclerView recyclerViewAsistencias;
    private AsistenciaAdapter asistenciaAdapter;
    private TextView textViewEstadisticas;
    private TextView textViewConfirmados;
    private TextView textViewEnEspera;
    private TextView textViewNoAsisten;
    private Spinner spinnerEquipo;
    private Button buttonFiltroTodos;
    private Button buttonFiltroConfirmados;
    private Button buttonFiltroEnEspera;
    private Button buttonFiltroNoAsisten;
    private FloatingActionButton fabRegistrarAsistencia;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private Usuario usuarioActual;
    
    // Filtros
    private String filtroEquipo = "TODOS";
    private String filtroEstado = "TODOS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creando vista de asistencia");
        View view = inflater.inflate(R.layout.fragment_asistencia, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        inicializarVistas(view);
        configurarRecyclerView();
        cargarAsistencias();
        
        return view;
    }

    private void inicializarVistas(View view) {
        Log.d(TAG, "inicializarVistas: Inicializando vistas");
        recyclerViewAsistencias = view.findViewById(R.id.recyclerViewAsistencias);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        textViewConfirmados = view.findViewById(R.id.textViewConfirmados);
        textViewEnEspera = view.findViewById(R.id.textViewEnEspera);
        textViewNoAsisten = view.findViewById(R.id.textViewNoAsisten);
        spinnerEquipo = view.findViewById(R.id.spinnerEquipo);
        buttonFiltroTodos = view.findViewById(R.id.buttonFiltroTodos);
        buttonFiltroConfirmados = view.findViewById(R.id.buttonFiltroConfirmados);
        buttonFiltroEnEspera = view.findViewById(R.id.buttonFiltroEnEspera);
        buttonFiltroNoAsisten = view.findViewById(R.id.buttonFiltroNoAsisten);
        fabRegistrarAsistencia = view.findViewById(R.id.fabRegistrarAsistencia);
        
        fabRegistrarAsistencia.setOnClickListener(v -> mostrarDialogoRegistrarAsistencia());
        
        // Configurar filtros
        configurarFiltros();
    }

    private void configurarFiltros() {
        // Configurar spinner de equipos
        List<String> equipos = new ArrayList<>();
        equipos.add("TODOS");
        equipos.addAll(dataManager.getNombresEquipos());
        
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Configurar listeners de filtros
        spinnerEquipo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtroEquipo = parent.getItemAtPosition(position).toString();
                cargarAsistencias();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        // Configurar botones de filtro de estado
        buttonFiltroTodos.setOnClickListener(v -> {
            filtroEstado = "TODOS";
            cargarAsistencias();
        });
        
        buttonFiltroConfirmados.setOnClickListener(v -> {
            filtroEstado = "CONFIRMADOS";
            cargarAsistencias();
        });
        
        buttonFiltroEnEspera.setOnClickListener(v -> {
            filtroEstado = "EN_ESPERA";
            cargarAsistencias();
        });
        
        buttonFiltroNoAsisten.setOnClickListener(v -> {
            filtroEstado = "NO_ASISTEN";
            cargarAsistencias();
        });
        
        // Configurar filtro inicial para usuarios no admin
        if (usuarioActual != null && !usuarioActual.isEsAdmin() && usuarioActual.getEquipo() != null) {
            filtroEquipo = usuarioActual.getEquipo();
            int posicionEquipo = equipos.indexOf(filtroEquipo);
            if (posicionEquipo >= 0) {
                spinnerEquipo.setSelection(posicionEquipo);
            }
        }
    }

    private void configurarRecyclerView() {
        Log.d(TAG, "configurarRecyclerView: Configurando RecyclerView");
        recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(requireContext()));
        asistenciaAdapter = new AsistenciaAdapter(requireContext(), new ArrayList<>(), this::manejarClicEnAsistencia);
        recyclerViewAsistencias.setAdapter(asistenciaAdapter);
    }

    private void cargarAsistencias() {
        Log.d(TAG, "cargarAsistencias: Cargando asistencias");
        
        List<Asistencia> todasAsistencias = dataManager.getAsistencias();
        List<Asistencia> asistenciasFiltradas = new ArrayList<>();
        
        // Aplicar filtros
        for (Asistencia asistencia : todasAsistencias) {
            boolean cumpleFiltroEquipo = true;
            boolean cumpleFiltroEstado = true;
            
            // Filtro por equipo
            if (!filtroEquipo.equals("TODOS")) {
                Evento evento = dataManager.getEventoById(asistencia.getEventoId());
                if (evento != null && evento.getEquipo() != null) {
                    cumpleFiltroEquipo = evento.getEquipo().equals(filtroEquipo);
                } else {
                    cumpleFiltroEquipo = false;
                }
            }
            
            // Filtro por estado
            if (!filtroEstado.equals("TODOS")) {
                switch (filtroEstado) {
                    case "CONFIRMADOS":
                        cumpleFiltroEstado = asistencia.isAsistio();
                        break;
                    case "NO_ASISTEN":
                        cumpleFiltroEstado = !asistencia.isAsistio();
                        break;
                    case "EN_ESPERA":
                        // Para este ejemplo, consideramos "en espera" las asistencias sin confirmar
                        // En una implementación real, necesitarías un campo adicional en el modelo
                        cumpleFiltroEstado = asistencia.getObservaciones() == null || asistencia.getObservaciones().isEmpty();
                        break;
                }
            }
            
            if (cumpleFiltroEquipo && cumpleFiltroEstado) {
                asistenciasFiltradas.add(asistencia);
            }
        }
        
        // Ordenar por fecha (más recientes primero)
        asistenciasFiltradas.sort((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()));
        
        asistenciaAdapter.actualizarAsistencias(asistenciasFiltradas);
        
        // Actualizar estadísticas
        actualizarEstadisticas(asistenciasFiltradas);
        
        // Verificar eventos pendientes de confirmación
        verificarEventosPendientesConfirmacion();
    }

    private void actualizarEstadisticas(List<Asistencia> asistencias) {
        Log.d(TAG, "actualizarEstadisticas: Actualizando estadísticas");
        
        int totalAsistencias = asistencias.size();
        int asistenciasPositivas = 0, asistenciasNegativas = 0, enEspera = 0;
        
        for (Asistencia asistencia : asistencias) {
            if (asistencia.isAsistio()) {
                asistenciasPositivas++;
            } else {
                asistenciasNegativas++;
            }
            
            // Contar en espera (sin observaciones)
            if (asistencia.getObservaciones() == null || asistencia.getObservaciones().isEmpty()) {
                enEspera++;
            }
        }
        
        double porcentajeAsistencia = totalAsistencias > 0 ? 
            (double) asistenciasPositivas / totalAsistencias * 100 : 0;
        
        // Actualizar tarjetas de estadísticas
        if (textViewConfirmados != null) {
            textViewConfirmados.setText(String.valueOf(asistenciasPositivas));
        }
        
        if (textViewEnEspera != null) {
            textViewEnEspera.setText(String.valueOf(enEspera));
        }
        
        if (textViewNoAsisten != null) {
            textViewNoAsisten.setText(String.valueOf(asistenciasNegativas));
        }
        
        // Actualizar estadísticas generales
        String estadisticas = String.format("Total: %d | Asistencias: %d | Ausencias: %d | Porcentaje: %.1f%%", 
                totalAsistencias, asistenciasPositivas, asistenciasNegativas, porcentajeAsistencia);
        
        if (textViewEstadisticas != null) {
            textViewEstadisticas.setText(estadisticas);
        }
    }

    private void mostrarDialogoRegistrarAsistencia() {
        Log.d(TAG, "mostrarDialogoRegistrarAsistencia: Mostrando diálogo");
        
        // Obtener eventos próximos y jugadores disponibles
        List<Evento> eventos = dataManager.getEventos();
        List<Usuario> usuarios = dataManager.getUsuarios();
        List<Evento> eventosProximos = new ArrayList<>();
        List<String> jugadores = new ArrayList<>();
        
        // Filtrar eventos próximos (hoy o futuros)
        Calendar hoy = Calendar.getInstance();
        for (Evento evento : eventos) {
            if (evento.getFechaInicio().after(hoy.getTime()) || esHoy(evento.getFechaInicio())) {
                eventosProximos.add(evento);
            }
        }
        
        // Obtener jugadores del usuario actual
        if (usuarioActual != null && usuarioActual.getJugador() != null) {
            jugadores.add(usuarioActual.getJugador());
        }
        
        // También agregar jugadores de otros usuarios si es admin
        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            for (Usuario usuario : usuarios) {
                if (usuario.getJugador() != null && !usuario.getJugador().isEmpty() && 
                    !jugadores.contains(usuario.getJugador())) {
                    jugadores.add(usuario.getJugador());
                }
            }
        }
        
        if (eventosProximos.isEmpty()) {
            Toast.makeText(requireContext(), "No hay eventos próximos para registrar asistencia", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (jugadores.isEmpty()) {
            Toast.makeText(requireContext(), "No hay jugadores disponibles", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Crear diálogo para seleccionar evento y jugador
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_seleccionar_asistencia, null);
        
        Spinner spinnerEvento = dialogView.findViewById(R.id.spinnerEvento);
        Spinner spinnerJugador = dialogView.findViewById(R.id.spinnerJugador);
        TextView textViewInfoEvento = dialogView.findViewById(R.id.textViewInfoEvento);
        
        // Configurar spinner de eventos
        List<String> nombresEventos = new ArrayList<>();
        for (Evento evento : eventosProximos) {
            String nombreEvento = evento.getTitulo() + " - " + dateFormat.format(evento.getFechaInicio());
            nombresEventos.add(nombreEvento);
        }
        
        ArrayAdapter<String> eventoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, nombresEventos);
        eventoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvento.setAdapter(eventoAdapter);
        
        // Configurar spinner de jugadores
        ArrayAdapter<String> jugadorAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, jugadores);
        jugadorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJugador.setAdapter(jugadorAdapter);
        
        // Mostrar información del evento seleccionado
        spinnerEvento.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < eventosProximos.size()) {
                    Evento evento = eventosProximos.get(position);
                    String info = String.format("Tipo: %s\nLugar: %s\nEquipo: %s", 
                            evento.getTipo(), evento.getUbicacion(), 
                            evento.getEquipo() != null ? evento.getEquipo() : "Todos");
                    textViewInfoEvento.setText(info);
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        builder.setView(dialogView)
                .setTitle("Seleccionar Evento y Jugador")
                .setPositiveButton("Continuar", (dialog, which) -> {
                    int posEvento = spinnerEvento.getSelectedItemPosition();
                    int posJugador = spinnerJugador.getSelectedItemPosition();
                    
                    if (posEvento >= 0 && posJugador >= 0) {
                        Evento eventoSeleccionado = eventosProximos.get(posEvento);
                        String jugadorSeleccionado = jugadores.get(posJugador);
                        mostrarDialogoConfirmarAsistencia(eventoSeleccionado, jugadorSeleccionado);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoConfirmarAsistencia(Evento evento, String jugador) {
        Log.d(TAG, "mostrarDialogoConfirmarAsistencia: Mostrando diálogo para " + jugador);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmar_asistencia, null);
        
        Button buttonAsisto = dialogView.findViewById(R.id.buttonAsisto);
        Button buttonNoAsisto = dialogView.findViewById(R.id.buttonNoAsisto);
        EditText editTextMotivo = dialogView.findViewById(R.id.editTextMotivo);
        View layoutMotivo = dialogView.findViewById(R.id.layoutMotivo);
        
        // Actualizar información del evento
        TextView textViewInfoEvento = dialogView.findViewById(R.id.textViewInfoEvento);
        if (textViewInfoEvento != null) {
            String info = String.format("Evento: %s\nJugador: %s\nFecha: %s", 
                    evento.getTitulo(), jugador, dateFormat.format(evento.getFechaInicio()));
            textViewInfoEvento.setText(info);
        }
        
        // Configurar botones
        buttonAsisto.setOnClickListener(v -> {
            String motivo = editTextMotivo.getText().toString().trim();
            registrarAsistencia(evento, jugador, true, motivo);
            ((AlertDialog) v.getTag()).dismiss();
        });
        
        buttonNoAsisto.setOnClickListener(v -> {
            // Mostrar campo de motivo cuando se selecciona "NO ASISTO"
            layoutMotivo.setVisibility(View.VISIBLE);
            editTextMotivo.requestFocus();
            
            // Cambiar el texto del botón para confirmar
            buttonNoAsisto.setText("CONFIRMAR AUSENCIA");
            buttonNoAsisto.setOnClickListener(confirmV -> {
                String motivo = editTextMotivo.getText().toString().trim();
                if (motivo.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor, indica el motivo de la ausencia", Toast.LENGTH_SHORT).show();
                    return;
                }
                registrarAsistencia(evento, jugador, false, motivo);
                ((AlertDialog) confirmV.getTag()).dismiss();
            });
        });
        
        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Confirmar Asistencia")
                .setCancelable(true)
                .create();
        
        buttonAsisto.setTag(dialog);
        buttonNoAsisto.setTag(dialog);
        dialog.show();
    }

    private void registrarAsistencia(Evento evento, String jugador, boolean asistio, String motivo) {
        Log.d(TAG, "registrarAsistencia: Registrando asistencia para " + jugador + " - " + (asistio ? "Asistió" : "No asistió"));
        
        // Verificar si ya existe una asistencia para este evento y jugador
        List<Asistencia> asistencias = dataManager.getAsistencias();
        for (Asistencia asistencia : asistencias) {
            if (asistencia.getEventoId().equals(evento.getId()) && 
                asistencia.getJugadorNombre().equalsIgnoreCase(jugador)) {
                Toast.makeText(requireContext(), "Ya existe un registro de asistencia para este evento y jugador", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Crear nueva asistencia
        Asistencia nuevaAsistencia = new Asistencia();
        nuevaAsistencia.setAsistio(asistio);
        nuevaAsistencia.setObservaciones(motivo);
        nuevaAsistencia.setFecha(new Date());
        nuevaAsistencia.setJugadorNombre(jugador);
        nuevaAsistencia.setEventoId(evento.getId());
        
        dataManager.agregarAsistencia(nuevaAsistencia);
        cargarAsistencias();
        
        String mensaje = asistio ? "Asistencia registrada exitosamente" : "Ausencia registrada exitosamente";
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "registrarAsistencia: " + mensaje);
    }

    private boolean esHoy(Date fecha) {
        Calendar fechaEvento = Calendar.getInstance();
        fechaEvento.setTime(fecha);
        Calendar hoy = Calendar.getInstance();
        
        return fechaEvento.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
               fechaEvento.get(Calendar.DAY_OF_YEAR) == hoy.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarAsistencias();
    }

    private void manejarClicEnAsistencia(Asistencia asistencia) {
        Log.d(TAG, "manejarClicEnAsistencia: Manejando clic para la asistencia ID: " + asistencia.getId());
        Evento evento = dataManager.getEventoById(asistencia.getEventoId());
        String jugadorNombre = asistencia.getJugadorNombre();
        if (evento != null && jugadorNombre != null && !jugadorNombre.isEmpty()) {
            mostrarDialogoConfirmarAsistencia(evento, jugadorNombre);
        } else {
            Log.e(TAG, "manejarClicEnAsistencia: Evento o JugadorNombre no encontrado para la asistencia ID: " + asistencia.getId());
            Toast.makeText(requireContext(), "Error al cargar los detalles de la asistencia.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Verifica si hay eventos próximos que requieren confirmación de asistencia
     */
    private void verificarEventosPendientesConfirmacion() {
        if (usuarioActual == null) return;
        
        List<Evento> eventos = dataManager.getEventos();
        List<Asistencia> asistencias = dataManager.getAsistencias();
        Calendar ahora = Calendar.getInstance();
        Calendar proximas72h = Calendar.getInstance();
        proximas72h.add(Calendar.HOUR, 72); // Próximas 72 horas
        
        for (Evento evento : eventos) {
            // Verificar si el evento está en las próximas 72 horas
            if (evento.getFechaInicio().after(ahora.getTime()) && 
                evento.getFechaInicio().before(proximas72h.getTime())) {
                
                // Verificar si el usuario debe recibir notificación de este evento
                boolean debeNotificar = false;
                
                if (evento.getEquipo() != null && !evento.getEquipo().isEmpty()) {
                    // Evento específico para un equipo
                    if (evento.getEquipo().equals(usuarioActual.getEquipo())) {
                        debeNotificar = true;
                    }
                } else {
                    // Evento global
                    debeNotificar = true;
                }
                
                if (debeNotificar) {
                    // Verificar si ya existe una asistencia para este evento y jugador
                    boolean existeAsistencia = false;
                    for (Asistencia asistencia : asistencias) {
                        if (asistencia.getEventoId().equals(evento.getId()) && 
                            asistencia.getJugadorNombre() != null &&
                            asistencia.getJugadorNombre().equals(usuarioActual.getJugador())) {
                            existeAsistencia = true;
                            break;
                        }
                    }
                    
                    // Si no existe asistencia, crear solicitud de confirmación
                    if (!existeAsistencia) {
                        crearSolicitudConfirmacionAsistencia(evento);
                    }
                }
            }
        }
    }
    
    /**
     * Crea una solicitud de confirmación de asistencia para un evento
     */
    private void crearSolicitudConfirmacionAsistencia(Evento evento) {
        try {
            // Crear notificación de solicitud de confirmación
            Notificacion solicitud = new Notificacion(
                "Confirmar asistencia: " + evento.getTitulo(),
                "Por favor confirma tu asistencia al evento:\n" + evento.getDescripcion() + 
                "\nFecha: " + dateFormat.format(evento.getFechaInicio()) + 
                "\nUbicación: " + evento.getUbicacion() +
                "\n\nVe a la sección Asistencia para confirmar.",
                "SOLICITUD_ASISTENCIA",
                usuarioActual.getId(), // Notificación específica para este usuario
                "sistema",
                "Sistema Automático",
                true
            );
            
            dataManager.agregarNotificacion(solicitud);
        } catch (Exception e) {
            Log.e(TAG, "Error creando solicitud de confirmación: " + e.getMessage());
        }
    }
} 