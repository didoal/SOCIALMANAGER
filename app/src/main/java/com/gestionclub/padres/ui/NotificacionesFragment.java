package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private LinearLayout layoutNoNotificaciones;
    private NotificacionAdapter notificacionAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private String filtroActual = "TODAS";
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
        layoutNoNotificaciones = view.findViewById(R.id.layoutNoNotificaciones);
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
            switch (filtroActual) {
                case "TODAS":
                    notificacionesFiltradas.add(notificacion);
                    break;
                case "NO_LEIDAS":
                    if (!notificacion.isLeida()) {
                        notificacionesFiltradas.add(notificacion);
                    }
                    break;
                case "LEIDAS":
                    if (notificacion.isLeida()) {
                        notificacionesFiltradas.add(notificacion);
                    }
                    break;
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
        
        // Mostrar/ocultar botón de marcar como leídas
        if (noLeidas > 0) {
            buttonMarcarLeidas.setVisibility(View.VISIBLE);
        } else {
            buttonMarcarLeidas.setVisibility(View.GONE);
        }
    }

    private void configurarListeners() {
        buttonMarcarLeidas.setOnClickListener(v -> marcarTodasComoLeidas());
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
    }

    private void marcarTodasComoLeidas() {
        List<Notificacion> todasNotificaciones = dataManager.getNotificaciones();
        for (Notificacion notificacion : todasNotificaciones) {
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
        // Marcar como leída
        if (!notificacion.isLeida()) {
            dataManager.marcarNotificacionComoLeida(notificacion.getId());
            cargarNotificaciones();
            actualizarEstadisticas();
        }
        // Mostrar detalles de la notificación si lo deseas
        mostrarDetallesNotificacion(notificacion);
    }

    private void mostrarDetallesNotificacion(Notificacion notificacion) {
        String mensaje = "Título: " + notificacion.getTitulo() + "\n\n" +
                        "Mensaje: " + notificacion.getMensaje() + "\n\n" +
                        "Tipo: " + notificacion.getTipo() + "\n" +
                        "De: " + notificacion.getRemitenteNombre() + "\n" +
                        "Fecha: " + dateFormat.format(notificacion.getFechaCreacion());
        
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarNotificaciones();
        generarRecordatoriosEventos();
        actualizarEstadisticas();
    }

    private void mostrarDialogoCrearNotificacion() {
        Log.d(TAG, "mostrarDialogoCrearNotificacion: Mostrando diálogo");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_notificacion, null);
        
        TextView editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        TextView editTextMensaje = dialogView.findViewById(R.id.editTextMensaje);
        
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
        Log.d(TAG, "crearNotificacion: Creando notificación " + titulo);
        
        Notificacion nuevaNotificacion = new Notificacion();
        nuevaNotificacion.setTitulo(titulo);
        nuevaNotificacion.setMensaje(mensaje);
        nuevaNotificacion.setTipo("Mensaje");
        nuevaNotificacion.setFechaCreacion(new Date());
        nuevaNotificacion.setLeida(false);
        nuevaNotificacion.setId(String.valueOf(System.currentTimeMillis()));
        
        dataManager.agregarNotificacion(nuevaNotificacion);
        cargarNotificaciones();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Notificación creada exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "crearNotificacion: Notificación " + titulo + " creada correctamente");
    }
} 