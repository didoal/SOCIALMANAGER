package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.gestionclub.padres.adapter.NotificacionAdapter;
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

public class NotificacionesFragment extends Fragment implements NotificacionAdapter.OnNotificacionClickListener {
    private RecyclerView recyclerViewNotificaciones;
    private TextView textViewNoLeidas;
    private TextView textViewTotal;
    private Button buttonMarcarLeidas;
    private Button buttonBorrarNotificaciones;
    private Button buttonFiltroTodas;
    private Button buttonFiltroNoLeidas;
    private Button buttonFiltroLeidas;
    private Button buttonFiltrosAvanzados;
    private LinearLayout layoutNoNotificaciones;
    private LinearLayout layoutFiltrosAvanzados;
    private Spinner spinnerTipoNotificacion;
    private Spinner spinnerEquipo;
    private NotificacionAdapter notificacionAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private String filtroActual = "TODAS";
    private String filtroTipo = "TODOS";
    private String filtroEquipo = "TODOS";
    private static final String TAG = "NotificacionesFragment";
    private TextView textViewEstadisticas;
    private FloatingActionButton fabCrearNotificacion;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creando vista de notificaciones");
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarRecyclerView();
            configurarFiltros();
            configurarSpinners();
            cargarNotificaciones();
            generarRecordatoriosEventos();
            actualizarEstadisticas();
            configurarListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar notificaciones", e);
            mostrarErrorCarga(view);
        }
        return view;
    }
    
    private void mostrarErrorCarga(View view) {
        // Ocultar vistas principales
        if (recyclerViewNotificaciones != null) {
            recyclerViewNotificaciones.setVisibility(View.GONE);
        }
        if (layoutNoNotificaciones != null) {
            layoutNoNotificaciones.setVisibility(View.GONE);
        }
        
        // Mostrar solo el Toast de error
        Toast.makeText(requireContext(), R.string.error_cargar_notificaciones, Toast.LENGTH_LONG).show();
    }

    private void inicializarVistas(View view) {
        Log.d(TAG, "inicializarVistas: Inicializando vistas");
        recyclerViewNotificaciones = view.findViewById(R.id.recyclerViewNotificaciones);
        textViewNoLeidas = view.findViewById(R.id.textViewNoLeidas);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        buttonMarcarLeidas = view.findViewById(R.id.buttonMarcarLeidas);
        buttonBorrarNotificaciones = view.findViewById(R.id.buttonBorrarNotificaciones);
        buttonFiltroTodas = view.findViewById(R.id.buttonFiltroTodas);
        buttonFiltroNoLeidas = view.findViewById(R.id.buttonFiltroNoLeidas);
        buttonFiltroLeidas = view.findViewById(R.id.buttonFiltroLeidas);
        buttonFiltrosAvanzados = view.findViewById(R.id.buttonFiltrosAvanzados);
        layoutNoNotificaciones = view.findViewById(R.id.layoutNoNotificaciones);
        layoutFiltrosAvanzados = view.findViewById(R.id.layoutFiltrosAvanzados);
        spinnerTipoNotificacion = view.findViewById(R.id.spinnerTipoNotificacion);
        spinnerEquipo = view.findViewById(R.id.spinnerEquipo);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        fabCrearNotificacion = view.findViewById(R.id.fabCrearNotificacion);
        
        fabCrearNotificacion.setOnClickListener(v -> mostrarDialogoCrearNotificacion());
    }

    private void configurarRecyclerView() {
        Log.d(TAG, "configurarRecyclerView: Configurando RecyclerView");
        notificacionAdapter = new NotificacionAdapter(new ArrayList<>(), this);
        recyclerViewNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotificaciones.setAdapter(notificacionAdapter);
    }

    private void configurarFiltros() {
        // Configurar estado inicial de los filtros
        actualizarEstadosFiltros();
    }

    private void configurarSpinners() {
        // Configurar spinner de tipos de notificación
        String[] tipos = {"TODOS", "EVENTO", "MENSAJE", "OBJETO", "RECORDATORIO", "SOLICITUD"};
        android.widget.ArrayAdapter<String> adapterTipos = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, tipos);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoNotificacion.setAdapter(adapterTipos);

        // Configurar spinner de equipos
        String[] equipos = {"TODOS", "Biberones", "Prebenjamín A", "Prebenjamín B", "Benjamín A", "Benjamín B", 
                           "Alevín A", "Alevín B", "Infantil", "Cadete", "Juvenil"};
        android.widget.ArrayAdapter<String> adapterEquipos = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, equipos);
        adapterEquipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(adapterEquipos);
    }

    private void actualizarEstadosFiltros() {
        // Resetear todos los botones
        buttonFiltroTodas.setBackgroundResource(R.drawable.button_secondary_background);
        buttonFiltroTodas.setTextColor(getResources().getColor(R.color.text_primary));
        buttonFiltroNoLeidas.setBackgroundResource(R.drawable.button_secondary_background);
        buttonFiltroNoLeidas.setTextColor(getResources().getColor(R.color.text_primary));
        buttonFiltroLeidas.setBackgroundResource(R.drawable.button_secondary_background);
        buttonFiltroLeidas.setTextColor(getResources().getColor(R.color.text_primary));

        // Activar el filtro actual
        switch (filtroActual) {
            case "TODAS":
                buttonFiltroTodas.setBackgroundResource(R.drawable.button_gold_background);
                buttonFiltroTodas.setTextColor(getResources().getColor(R.color.black));
                break;
            case "NO_LEIDAS":
                buttonFiltroNoLeidas.setBackgroundResource(R.drawable.button_gold_background);
                buttonFiltroNoLeidas.setTextColor(getResources().getColor(R.color.black));
                break;
            case "LEIDAS":
                buttonFiltroLeidas.setBackgroundResource(R.drawable.button_gold_background);
                buttonFiltroLeidas.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }

    private void cargarNotificaciones() {
        Log.d(TAG, "cargarNotificaciones: Cargando lista de notificaciones");
        List<Notificacion> todasNotificaciones = dataManager.getNotificaciones();
        List<Notificacion> notificacionesFiltradas = new ArrayList<>();
        
        for (Notificacion notificacion : todasNotificaciones) {
            boolean cumpleFiltroLeida = true;
            boolean cumpleFiltroTipo = true;
            boolean cumpleFiltroEquipo = true;
            
            // Filtro por estado de lectura
            switch (filtroActual) {
                case "NO_LEIDAS":
                    if (notificacion.isLeida()) cumpleFiltroLeida = false;
                    break;
                case "LEIDAS":
                    if (!notificacion.isLeida()) cumpleFiltroLeida = false;
                    break;
            }
            
            // Filtro por tipo
            if (!filtroTipo.equals("TODOS") && !notificacion.getTipo().equals(filtroTipo)) {
                cumpleFiltroTipo = false;
            }
            
            // Filtro por equipo (si aplica)
            if (!filtroEquipo.equals("TODOS")) {
                // Aquí podrías implementar lógica específica por equipo
                // Por ahora, solo filtramos notificaciones globales vs específicas
            }
            
            if (cumpleFiltroLeida && cumpleFiltroTipo && cumpleFiltroEquipo) {
                notificacionesFiltradas.add(notificacion);
            }
        }
        
        notificacionAdapter.actualizarNotificaciones(notificacionesFiltradas);
        
        if (notificacionesFiltradas.isEmpty()) {
            layoutNoNotificaciones.setVisibility(View.VISIBLE);
            recyclerViewNotificaciones.setVisibility(View.GONE);
        } else {
            layoutNoNotificaciones.setVisibility(View.GONE);
            recyclerViewNotificaciones.setVisibility(View.VISIBLE);
        }
    }

    private void generarRecordatoriosEventos() {
        // Generar recordatorios automáticos para eventos próximos
        List<Evento> eventos = dataManager.getEventos();
        Calendar ahora = Calendar.getInstance();
        Calendar proximaSemana = Calendar.getInstance();
        proximaSemana.add(Calendar.DAY_OF_YEAR, 7);
        
        for (Evento evento : eventos) {
            if (evento.getFecha().after(ahora.getTime()) && evento.getFecha().before(proximaSemana.getTime())) {
                // Verificar si ya existe un recordatorio para este evento
                boolean recordatorioExiste = false;
                for (Notificacion notif : dataManager.getNotificaciones()) {
                    if (notif.getTitulo().contains("Recordatorio: " + evento.getTitulo())) {
                        recordatorioExiste = true;
                        break;
                    }
                }
                
                if (!recordatorioExiste) {
                    Notificacion recordatorio = new Notificacion();
                    recordatorio.setTitulo("Recordatorio: " + evento.getTitulo());
                    recordatorio.setMensaje("El evento '" + evento.getTitulo() + "' será el " + 
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(evento.getFecha()) + 
                        " a las " + evento.getHora() + ".");
                    recordatorio.setTipo("RECORDATORIO");
                    recordatorio.setFechaCreacion(new Date());
                    recordatorio.setLeida(false);
                    dataManager.agregarNotificacion(recordatorio);
                }
            }
        }
    }

    private void actualizarEstadisticas() {
        List<Notificacion> todasNotificaciones = dataManager.getNotificaciones();
        int total = todasNotificaciones.size();
        int noLeidas = 0;
        int recordatorios = 0;
        int mensajes = 0;
        
        for (Notificacion notif : todasNotificaciones) {
            if (!notif.isLeida()) noLeidas++;
            if (notif.getTipo().contains("RECORDATORIO")) recordatorios++;
            if (notif.getTipo().contains("MENSAJE")) mensajes++;
        }
        
        textViewTotal.setText(String.valueOf(total));
        textViewNoLeidas.setText(String.valueOf(noLeidas));
        textViewEstadisticas.setText("Total: " + total + " | No leídas: " + noLeidas + 
                                   " | Recordatorios: " + recordatorios + " | Mensajes: " + mensajes);
    }

    private void configurarListeners() {
        buttonMarcarLeidas.setOnClickListener(v -> marcarTodasComoLeidas());
        buttonBorrarNotificaciones.setOnClickListener(v -> mostrarDialogoBorrarNotificaciones());
        buttonFiltroTodas.setOnClickListener(v -> {
            filtroActual = "TODAS";
            actualizarEstadosFiltros();
            cargarNotificaciones();
        });
        buttonFiltroNoLeidas.setOnClickListener(v -> {
            filtroActual = "NO_LEIDAS";
            actualizarEstadosFiltros();
            cargarNotificaciones();
        });
        buttonFiltroLeidas.setOnClickListener(v -> {
            filtroActual = "LEIDAS";
            actualizarEstadosFiltros();
            cargarNotificaciones();
        });
        buttonFiltrosAvanzados.setOnClickListener(v -> {
            if (layoutFiltrosAvanzados.getVisibility() == View.VISIBLE) {
                layoutFiltrosAvanzados.setVisibility(View.GONE);
                buttonFiltrosAvanzados.setText("🔍 Filtros Avanzados");
            } else {
                layoutFiltrosAvanzados.setVisibility(View.VISIBLE);
                buttonFiltrosAvanzados.setText("🔍 Ocultar Filtros");
            }
        });

        spinnerTipoNotificacion.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtroTipo = parent.getItemAtPosition(position).toString();
                cargarNotificaciones();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerEquipo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtroEquipo = parent.getItemAtPosition(position).toString();
                cargarNotificaciones();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void marcarTodasComoLeidas() {
        List<Notificacion> notificaciones = dataManager.getNotificaciones();
        for (Notificacion notif : notificaciones) {
            if (!notif.isLeida()) {
                notif.setLeida(true);
                dataManager.actualizarNotificacion(notif);
            }
        }
        cargarNotificaciones();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Todas las notificaciones han sido marcadas como leídas", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoBorrarNotificaciones() {
        new AlertDialog.Builder(requireContext())
            .setTitle("🗑️ Borrar Notificaciones")
            .setMessage("¿Estás seguro de que quieres borrar todas las notificaciones? Esta acción no se puede deshacer.")
            .setPositiveButton("Borrar", (dialog, which) -> {
                dataManager.borrarTodasNotificaciones();
                cargarNotificaciones();
                actualizarEstadisticas();
                Toast.makeText(requireContext(), "Todas las notificaciones han sido borradas", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onMarcarLeidaClick(Notificacion notificacion) {
        notificacion.setLeida(true);
        dataManager.actualizarNotificacion(notificacion);
        cargarNotificaciones();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Notificación marcada como leída", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBorrarClick(Notificacion notificacion) {
        new AlertDialog.Builder(requireContext())
            .setTitle("🗑️ Borrar Notificación")
            .setMessage("¿Estás seguro de que quieres borrar esta notificación?")
            .setPositiveButton("Borrar", (dialog, which) -> {
                dataManager.borrarNotificacion(notificacion.getId());
                cargarNotificaciones();
                actualizarEstadisticas();
                Toast.makeText(requireContext(), "Notificación borrada", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onAprobarClick(Notificacion notificacion) {
        new AlertDialog.Builder(requireContext())
            .setTitle("✅ Aprobar Solicitud")
            .setMessage("¿Estás seguro de que quieres aprobar esta solicitud?")
            .setPositiveButton("Aprobar", (dialog, which) -> {
                notificacion.setLeida(true);
                dataManager.actualizarNotificacion(notificacion);
                cargarNotificaciones();
                actualizarEstadisticas();
                Toast.makeText(requireContext(), "Solicitud aprobada", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onRechazarClick(Notificacion notificacion) {
        new AlertDialog.Builder(requireContext())
            .setTitle("❌ Rechazar Solicitud")
            .setMessage("¿Estás seguro de que quieres rechazar esta solicitud?")
            .setPositiveButton("Rechazar", (dialog, which) -> {
                notificacion.setLeida(true);
                dataManager.actualizarNotificacion(notificacion);
                cargarNotificaciones();
                actualizarEstadisticas();
                Toast.makeText(requireContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void mostrarDetallesNotificacion(Notificacion notificacion) {
        new AlertDialog.Builder(requireContext())
            .setTitle(notificacion.getTitulo())
            .setMessage(notificacion.getMensaje() + "\n\nFecha: " + 
                dateFormat.format(notificacion.getFechaCreacion()) + 
                "\nTipo: " + notificacion.getTipo())
            .setPositiveButton("OK", null)
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarNotificaciones();
        actualizarEstadisticas();
    }

    private void mostrarDialogoCrearNotificacion() {
        // Implementar diálogo para crear notificación
        Toast.makeText(requireContext(), "Función de crear notificación en desarrollo", Toast.LENGTH_SHORT).show();
    }

    private boolean validarDatos(String titulo, String mensaje) {
        return titulo != null && !titulo.trim().isEmpty() && 
               mensaje != null && !mensaje.trim().isEmpty();
    }

        private void crearNotificacion(String titulo, String mensaje) {
        Notificacion nuevaNotificacion = new Notificacion();
        nuevaNotificacion.setTitulo(titulo);
        nuevaNotificacion.setMensaje(mensaje);
        nuevaNotificacion.setTipo("MENSAJE");
        nuevaNotificacion.setFechaCreacion(new Date());
        nuevaNotificacion.setLeida(false);
        
        dataManager.agregarNotificacion(nuevaNotificacion);
        cargarNotificaciones();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Notificación creada exitosamente", Toast.LENGTH_SHORT).show();
    }
} 