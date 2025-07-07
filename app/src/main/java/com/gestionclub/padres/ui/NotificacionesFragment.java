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
            Toast.makeText(requireContext(), "No se pudo cargar las notificaciones. Intenta más tarde.", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        Log.d(TAG, "inicializarVistas: Inicializando vistas");
        recyclerViewNotificaciones = view.findViewById(R.id.recyclerViewNotificaciones);
        textViewNoLeidas = view.findViewById(R.id.textViewNoLeidas);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        buttonMarcarLeidas = view.findViewById(R.id.buttonMarcarLeidas);
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
        
        // Mostrar/ocultar mensaje de sin notificaciones
        if (notificacionesFiltradas.isEmpty()) {
            layoutNoNotificaciones.setVisibility(View.VISIBLE);
            recyclerViewNotificaciones.setVisibility(View.GONE);
        } else {
            layoutNoNotificaciones.setVisibility(View.GONE);
            recyclerViewNotificaciones.setVisibility(View.VISIBLE);
        }
    }

    private void generarRecordatoriosEventos() {
        Log.d(TAG, "generarRecordatoriosEventos: Generando recordatorios automáticos");
        
        // Usar el nuevo método automático del DataManager
        dataManager.verificarRecordatoriosAutomaticos();
    }

    private void actualizarEstadisticas() {
        Log.d(TAG, "actualizarEstadisticas: Actualizando estadísticas");
        List<Notificacion> notificaciones = dataManager.getNotificaciones();
        int totalNotificaciones = notificaciones.size();
        int noLeidas = 0, recordatorios = 0, mensajes = 0, eventos = 0, objetos = 0, solicitudes = 0;
        
        for (Notificacion notificacion : notificaciones) {
            if (!notificacion.isLeida()) noLeidas++;
            
            switch (notificacion.getTipo()) {
                case "RECORDATORIO":
                case "RECORDATORIO_OBJETOS":
                    recordatorios++;
                    break;
                case "MENSAJE":
                    mensajes++;
                    break;
                case "EVENTO":
                case "EVENTO_EQUIPO":
                    eventos++;
                    break;
                case "OBJETO":
                case "OBJETO_EQUIPO":
                    objetos++;
                    break;
                case "SOLICITUD":
                    solicitudes++;
                    break;
            }
        }
        
        String estadisticas = String.format("Total: %d | No leídas: %d | Eventos: %d | Objetos: %d | Recordatorios: %d", 
                totalNotificaciones, noLeidas, eventos, objetos, recordatorios);
        
        textViewEstadisticas.setText(estadisticas);
        textViewNoLeidas.setText(String.valueOf(noLeidas));
        textViewTotal.setText(String.valueOf(totalNotificaciones));
    }

    private void configurarListeners() {
        Log.d(TAG, "configurarListeners: Configurando listeners");
        
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
        
        buttonMarcarLeidas.setOnClickListener(v -> marcarTodasComoLeidas());
        
        // Listeners para spinners
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
        Log.d(TAG, "marcarTodasComoLeidas: Marcando todas como leídas");
        List<Notificacion> notificaciones = dataManager.getNotificaciones();
        for (Notificacion notificacion : notificaciones) {
            if (!notificacion.isLeida()) {
                dataManager.marcarNotificacionComoLeida(notificacion.getId());
            }
        }
        cargarNotificaciones();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Todas las notificaciones marcadas como leídas", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarcarLeidaClick(Notificacion notificacion) {
        Log.d(TAG, "onMarcarLeidaClick: Marcando notificación como leída");
        dataManager.marcarNotificacionComoLeida(notificacion.getId());
        cargarNotificaciones();
        actualizarEstadisticas();
        mostrarDetallesNotificacion(notificacion);
    }

    private void mostrarDetallesNotificacion(Notificacion notificacion) {
        Log.d(TAG, "mostrarDetallesNotificacion: Mostrando detalles de notificación");
        String fecha = dateFormat.format(notificacion.getFechaCreacion());
        String detalles = "Título: " + notificacion.getTitulo() + "\n\n" +
                         "Mensaje: " + notificacion.getMensaje() + "\n\n" +
                         "Tipo: " + notificacion.getTipo() + "\n" +
                         "Fecha: " + fecha + "\n" +
                         "Remitente: " + notificacion.getRemitenteNombre();
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Detalles de la Notificación")
                .setMessage(detalles)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarNotificaciones();
        actualizarEstadisticas();
    }

    private void mostrarDialogoCrearNotificacion() {
        Log.d(TAG, "mostrarDialogoCrearNotificacion: Mostrando diálogo");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_notificacion, null);
        
        android.widget.EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        android.widget.EditText editTextMensaje = dialogView.findViewById(R.id.editTextMensaje);
        
        builder.setView(dialogView)
                .setTitle("Crear Notificación")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String titulo = editTextTitulo.getText().toString().trim();
                    String mensaje = editTextMensaje.getText().toString().trim();
                    
                    if (validarDatos(titulo, mensaje)) {
                        crearNotificacion(titulo, mensaje);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatos(String titulo, String mensaje) {
        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mensaje.isEmpty()) {
            Toast.makeText(requireContext(), "El mensaje es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void crearNotificacion(String titulo, String mensaje) {
        Log.d(TAG, "crearNotificacion: Creando notificación");
        dataManager.crearNotificacionSolicitud(titulo, mensaje, "GENERAL");
        cargarNotificaciones();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Notificación creada exitosamente", Toast.LENGTH_SHORT).show();
    }
} 