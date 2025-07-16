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
    private Button buttonLimpiarFiltros;
    private Button buttonExportarNotificaciones;
    private LinearLayout layoutNoNotificaciones;
    private LinearLayout layoutFiltrosAvanzados;
    private Spinner spinnerTipoNotificacion;
    private Spinner spinnerEquipo;
    private Spinner spinnerFecha;
    private Spinner spinnerPrioridad;
    private NotificacionAdapter notificacionAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private String filtroActual = "TODAS";
    private String filtroTipo = "TODOS";
    private String filtroEquipo = "TODOS";
    private String filtroFecha = "TODAS";
    private String filtroPrioridad = "TODAS";
    private boolean filtrosAvanzadosVisibles = false;
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
            configurarVisibilidadPorRol();
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
        buttonLimpiarFiltros = view.findViewById(R.id.buttonLimpiarFiltros);
        buttonExportarNotificaciones = view.findViewById(R.id.buttonExportarNotificaciones);
        layoutNoNotificaciones = view.findViewById(R.id.layoutNoNotificaciones);
        layoutFiltrosAvanzados = view.findViewById(R.id.layoutFiltrosAvanzados);
        spinnerTipoNotificacion = view.findViewById(R.id.spinnerTipoNotificacion);
        spinnerEquipo = view.findViewById(R.id.spinnerEquipo);
        spinnerFecha = view.findViewById(R.id.spinnerFecha);
        spinnerPrioridad = view.findViewById(R.id.spinnerPrioridad);
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
        String[] tipos = {"TODOS", "EVENTO", "MENSAJE", "OBJETO", "RECORDATORIO", "SOLICITUD", "URGENTE"};
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

        // Configurar spinner de fechas
        String[] fechas = {"TODAS", "HOY", "ÚLTIMA SEMANA", "ÚLTIMO MES", "ÚLTIMO AÑO"};
        android.widget.ArrayAdapter<String> adapterFechas = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, fechas);
        adapterFechas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFecha.setAdapter(adapterFechas);

        // Configurar spinner de prioridad
        String[] prioridades = {"TODAS", "ALTA", "MEDIA", "BAJA"};
        android.widget.ArrayAdapter<String> adapterPrioridades = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, prioridades);
        adapterPrioridades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridad.setAdapter(adapterPrioridades);
    }

    private void configurarVisibilidadPorRol() {
        if (usuarioActual != null) {
            String rol = usuarioActual.getRol();
            
            // Configurar visibilidad según el rol
            switch (rol) {
                case "ADMIN":
                    // Administradores ven todo
                    fabCrearNotificacion.setVisibility(View.VISIBLE);
                    buttonExportarNotificaciones.setVisibility(View.VISIBLE);
                    break;
                case "ENTRENADOR":
                    // Entrenadores pueden crear notificaciones para sus equipos
                    fabCrearNotificacion.setVisibility(View.VISIBLE);
                    buttonExportarNotificaciones.setVisibility(View.GONE);
                    break;
                case "USUARIO":
                default:
                    // Usuarios normales solo ven notificaciones
                    fabCrearNotificacion.setVisibility(View.GONE);
                    buttonExportarNotificaciones.setVisibility(View.GONE);
                    break;
            }
        }
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
            if (cumpleFiltros(notificacion)) {
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

    private boolean cumpleFiltros(Notificacion notificacion) {
        // Filtro por estado de lectura
        boolean cumpleFiltroLeida = true;
        switch (filtroActual) {
            case "NO_LEIDAS":
                if (notificacion.isLeida()) cumpleFiltroLeida = false;
                break;
            case "LEIDAS":
                if (!notificacion.isLeida()) cumpleFiltroLeida = false;
                break;
        }
        
        // Filtro por tipo
        boolean cumpleFiltroTipo = filtroTipo.equals("TODOS") || notificacion.getTipo().equals(filtroTipo);
        
        // Filtro por equipo (si aplica)
        boolean cumpleFiltroEquipo = filtroEquipo.equals("TODOS") || 
                                   (notificacion.getEquipoDestinatario() != null && 
                                    notificacion.getEquipoDestinatario().equals(filtroEquipo));
        
        // Filtro por fecha
        boolean cumpleFiltroFecha = cumpleFiltroFecha(notificacion);
        
        // Filtro por prioridad
        boolean cumpleFiltroPrioridad = filtroPrioridad.equals("TODAS") || 
                                      (notificacion.getPrioridad() != null && 
                                       notificacion.getPrioridad().equals(filtroPrioridad));
        
        return cumpleFiltroLeida && cumpleFiltroTipo && cumpleFiltroEquipo && 
               cumpleFiltroFecha && cumpleFiltroPrioridad;
    }

    private boolean cumpleFiltroFecha(Notificacion notificacion) {
        if (filtroFecha.equals("TODAS")) return true;
        
        Calendar fechaNotificacion = Calendar.getInstance();
        fechaNotificacion.setTime(notificacion.getFechaCreacion());
        
        Calendar ahora = Calendar.getInstance();
        Calendar inicioDia = Calendar.getInstance();
        inicioDia.set(Calendar.HOUR_OF_DAY, 0);
        inicioDia.set(Calendar.MINUTE, 0);
        inicioDia.set(Calendar.SECOND, 0);
        inicioDia.set(Calendar.MILLISECOND, 0);
        
        switch (filtroFecha) {
            case "HOY":
                return fechaNotificacion.after(inicioDia) && fechaNotificacion.before(ahora);
            case "ÚLTIMA SEMANA":
                Calendar haceUnaSemana = Calendar.getInstance();
                haceUnaSemana.add(Calendar.DAY_OF_YEAR, -7);
                return fechaNotificacion.after(haceUnaSemana);
            case "ÚLTIMO MES":
                Calendar haceUnMes = Calendar.getInstance();
                haceUnMes.add(Calendar.MONTH, -1);
                return fechaNotificacion.after(haceUnMes);
            case "ÚLTIMO AÑO":
                Calendar haceUnAno = Calendar.getInstance();
                haceUnAno.add(Calendar.YEAR, -1);
                return fechaNotificacion.after(haceUnAno);
            default:
                return true;
        }
    }

    private void generarRecordatoriosEventos() {
        // Generar recordatorios automáticos para eventos próximos
        List<Evento> eventos = dataManager.getEventos();
        Calendar ahora = Calendar.getInstance();
        Calendar proximaSemana = Calendar.getInstance();
        proximaSemana.add(Calendar.DAY_OF_YEAR, 7);
        
        for (Evento evento : eventos) {
            if (evento.getFechaInicio().after(ahora.getTime()) && evento.getFechaInicio().before(proximaSemana.getTime())) {
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
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(evento.getFechaInicio()) + 
                        " a las " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(evento.getFechaInicio()) + ".");
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
        buttonLimpiarFiltros.setOnClickListener(v -> limpiarFiltros());
        buttonExportarNotificaciones.setOnClickListener(v -> exportarNotificaciones());
        
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

        spinnerFecha.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtroFecha = parent.getItemAtPosition(position).toString();
                cargarNotificaciones();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spinnerPrioridad.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filtroPrioridad = parent.getItemAtPosition(position).toString();
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

    private void limpiarFiltros() {
        filtroActual = "TODAS";
        filtroTipo = "TODOS";
        filtroEquipo = "TODOS";
        filtroFecha = "TODAS";
        filtroPrioridad = "TODAS";
        
        // Resetear spinners
        spinnerTipoNotificacion.setSelection(0);
        spinnerEquipo.setSelection(0);
        spinnerFecha.setSelection(0);
        spinnerPrioridad.setSelection(0);
        
        actualizarEstadosFiltros();
        cargarNotificaciones();
        Toast.makeText(requireContext(), "Filtros limpiados", Toast.LENGTH_SHORT).show();
    }

    private void exportarNotificaciones() {
        if (usuarioActual != null && "ADMIN".equals(usuarioActual.getRol())) {
            List<Notificacion> notificaciones = dataManager.getNotificaciones();
            if (notificaciones.isEmpty()) {
                Toast.makeText(requireContext(), "No hay notificaciones para exportar", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Crear reporte de notificaciones
            StringBuilder reporte = new StringBuilder();
            reporte.append("REPORTE DE NOTIFICACIONES - CD SANTIAGUIÑO GUIZÁN\n");
            reporte.append("Fecha: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date())).append("\n\n");
            
            int total = notificaciones.size();
            int noLeidas = 0;
            int leidas = 0;
            int urgentes = 0;
            
            for (Notificacion notif : notificaciones) {
                if (!notif.isLeida()) noLeidas++;
                else leidas++;
                if ("URGENTE".equals(notif.getTipo())) urgentes++;
                
                reporte.append("• ").append(notif.getTitulo()).append("\n");
                reporte.append("  Tipo: ").append(notif.getTipo()).append("\n");
                reporte.append("  Fecha: ").append(dateFormat.format(notif.getFechaCreacion())).append("\n");
                reporte.append("  Estado: ").append(notif.isLeida() ? "Leída" : "No leída").append("\n\n");
            }
            
            reporte.append("\nRESUMEN:\n");
            reporte.append("Total: ").append(total).append("\n");
            reporte.append("No leídas: ").append(noLeidas).append("\n");
            reporte.append("Leídas: ").append(leidas).append("\n");
            reporte.append("Urgentes: ").append(urgentes).append("\n");
            
            // Mostrar reporte en diálogo
            new AlertDialog.Builder(requireContext())
                .setTitle("📊 Reporte de Notificaciones")
                .setMessage(reporte.toString())
                .setPositiveButton("Copiar", (dialog, which) -> {
                    // Aquí se podría implementar la copia al portapapeles
                    Toast.makeText(requireContext(), "Reporte copiado al portapapeles", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cerrar", null)
                .show();
        } else {
            Toast.makeText(requireContext(), "Solo los administradores pueden exportar notificaciones", Toast.LENGTH_SHORT).show();
        }
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
        // Lógica para aprobar solicitudes
        notificacion.setEstado("APROBADA");
        dataManager.actualizarNotificacion(notificacion);
        cargarNotificaciones();
        Toast.makeText(requireContext(), "Solicitud aprobada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRechazarClick(Notificacion notificacion) {
        // Lógica para rechazar solicitudes
        notificacion.setEstado("RECHAZADA");
        dataManager.actualizarNotificacion(notificacion);
        cargarNotificaciones();
        Toast.makeText(requireContext(), "Solicitud rechazada", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDetallesNotificacion(Notificacion notificacion) {
        new AlertDialog.Builder(requireContext())
            .setTitle(notificacion.getTitulo())
            .setMessage(notificacion.getMensaje() + "\n\nFecha: " + dateFormat.format(notificacion.getFechaCreacion()))
            .setPositiveButton("Cerrar", null)
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarNotificaciones();
        actualizarEstadisticas();
    }

    private void mostrarDialogoCrearNotificacion() {
        if (usuarioActual == null || (!"ADMIN".equals(usuarioActual.getRol()) && !"ENTRENADOR".equals(usuarioActual.getRol()))) {
            Toast.makeText(requireContext(), "No tienes permisos para crear notificaciones", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear diálogo personalizado para crear notificación
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_notificacion, null);
        
        // Configurar spinners del diálogo
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerPrioridad = dialogView.findViewById(R.id.spinnerPrioridad);
        
        // Configurar spinner de tipos
        String[] tipos = {"EVENTO", "MENSAJE", "OBJETO", "RECORDATORIO", "SOLICITUD", "URGENTE"};
        android.widget.ArrayAdapter<String> adapterTipos = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, tipos);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipos);
        
        // Configurar spinner de prioridades
        String[] prioridades = {"ALTA", "MEDIA", "BAJA"};
        android.widget.ArrayAdapter<String> adapterPrioridades = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, prioridades);
        adapterPrioridades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridad.setAdapter(adapterPrioridades);
        
        new AlertDialog.Builder(requireContext())
            .setTitle("📢 Crear Nueva Notificación")
            .setView(dialogView)
            .setPositiveButton("Crear", (dialog, which) -> {
                // Obtener datos del diálogo
                TextView editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
                TextView editTextMensaje = dialogView.findViewById(R.id.editTextMensaje);
                
                String titulo = editTextTitulo.getText().toString().trim();
                String mensaje = editTextMensaje.getText().toString().trim();
                String tipo = spinnerTipo.getSelectedItem().toString();
                String prioridad = spinnerPrioridad.getSelectedItem().toString();
                
                if (validarDatos(titulo, mensaje)) {
                    crearNotificacion(titulo, mensaje, tipo, prioridad);
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

    private void crearNotificacion(String titulo, String mensaje, String tipo, String prioridad) {
        Notificacion nuevaNotificacion = new Notificacion();
        nuevaNotificacion.setTitulo(titulo);
        nuevaNotificacion.setMensaje(mensaje);
        nuevaNotificacion.setTipo(tipo);
        nuevaNotificacion.setPrioridad(prioridad);
        nuevaNotificacion.setFechaCreacion(new Date());
        nuevaNotificacion.setLeida(false);
        nuevaNotificacion.setCreador(usuarioActual.getNombre());
        
        // Si es entrenador, asignar a su equipo
        if ("ENTRENADOR".equals(usuarioActual.getRol()) && usuarioActual.getEquipo() != null) {
            nuevaNotificacion.setEquipoDestinatario(usuarioActual.getEquipo());
        }
        
        dataManager.agregarNotificacion(nuevaNotificacion);
        cargarNotificaciones();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Notificación creada exitosamente", Toast.LENGTH_SHORT).show();
    }
} 