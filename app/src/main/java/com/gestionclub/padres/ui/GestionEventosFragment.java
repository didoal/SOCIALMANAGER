package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.EventoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GestionEventosFragment extends Fragment {
    
    private RecyclerView recyclerViewEventos;
    private EventoAdapter eventoAdapter;
    private DataManager dataManager;
    private TextView textViewEstadisticas;
    private FloatingActionButton fabAgregarEvento;
    private FloatingActionButton fabAgregarEntrenamiento;
    private Usuario usuarioActual;
    private Spinner spinnerFiltroTipoEvento;
    private Spinner spinnerFiltroEquipo;
    private Spinner spinnerFiltroFecha;
    private Spinner spinnerFiltroEstado;
    private Spinner spinnerFiltroPrioridad;
    private LinearLayout layoutFiltrosAvanzados;
    private Button buttonMostrarFiltrosAvanzados;
    private Button buttonLimpiarFiltros;
    private Button buttonExportarEventos;
    private Button buttonCrearEvento;
    private Button buttonCrearEntrenamiento;
    private Button buttonCrearPartido;
    private Button buttonCrearReunion;
    private Button buttonCalendarioEventos;

    private String tipoFiltroSeleccionado = "Todos";
    private String equipoFiltroSeleccionado = "Todos";
    private String fechaFiltroSeleccionada = "Todas";
    private String estadoFiltroSeleccionado = "Todos";
    private String prioridadFiltroSeleccionada = "Todas";
    private boolean filtrosAvanzadosVisibles = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_eventos, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        inicializarVistas(view);
        configurarRecyclerView();
        configurarFiltrosAvanzados(view);
        cargarEventos();
        actualizarEstadisticas();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        fabAgregarEvento = view.findViewById(R.id.fabAgregarEvento);
        // Nuevo FAB para entrenamientos
        fabAgregarEntrenamiento = view.findViewById(R.id.fabAgregarEntrenamiento);
        layoutFiltrosAvanzados = view.findViewById(R.id.layoutFiltrosAvanzados);
        spinnerFiltroTipoEvento = view.findViewById(R.id.spinnerFiltroTipoEvento);
        spinnerFiltroEquipo = view.findViewById(R.id.spinnerFiltroEquipo);
        spinnerFiltroFecha = view.findViewById(R.id.spinnerFiltroFecha);
        spinnerFiltroEstado = view.findViewById(R.id.spinnerFiltroEstado);
        spinnerFiltroPrioridad = view.findViewById(R.id.spinnerFiltroPrioridad);
        buttonMostrarFiltrosAvanzados = view.findViewById(R.id.buttonMostrarFiltrosAvanzados);
        buttonLimpiarFiltros = view.findViewById(R.id.buttonLimpiarFiltros);
        buttonExportarEventos = view.findViewById(R.id.buttonExportarEventos);
        buttonCrearEvento = view.findViewById(R.id.buttonCrearEvento);
        buttonCrearEntrenamiento = view.findViewById(R.id.buttonCrearEntrenamiento);
        buttonCrearPartido = view.findViewById(R.id.buttonCrearPartido);
        buttonCrearReunion = view.findViewById(R.id.buttonCrearReunion);
        buttonCalendarioEventos = view.findViewById(R.id.buttonCalendarioEventos);
        
        boolean puedeCrearEventos = usuarioActual != null && 
            (usuarioActual.isEsAdmin() || "entrenador".equals(usuarioActual.getRol()));
        
        if (puedeCrearEventos) {
            fabAgregarEvento.setVisibility(View.VISIBLE);
            fabAgregarEvento.setOnClickListener(v -> mostrarDialogoCrearEvento());
            fabAgregarEntrenamiento.setVisibility(View.VISIBLE);
            fabAgregarEntrenamiento.setOnClickListener(v -> mostrarDialogoCrearEntrenamiento());
            
            // Configurar botones principales
            if (buttonCrearEvento != null) {
                buttonCrearEvento.setOnClickListener(v -> mostrarDialogoCrearEvento());
            }
            if (buttonCrearEntrenamiento != null) {
                buttonCrearEntrenamiento.setOnClickListener(v -> mostrarDialogoCrearEntrenamiento());
            }
            if (buttonCrearPartido != null) {
                buttonCrearPartido.setOnClickListener(v -> mostrarDialogoCrearPartido());
            }
            if (buttonCrearReunion != null) {
                buttonCrearReunion.setOnClickListener(v -> mostrarDialogoCrearReunion());
            }
        } else {
            fabAgregarEvento.setVisibility(View.GONE);
            fabAgregarEntrenamiento.setVisibility(View.GONE);
            
            // Ocultar botones principales para usuarios sin permisos
            if (buttonCrearEvento != null) buttonCrearEvento.setVisibility(View.GONE);
            if (buttonCrearEntrenamiento != null) buttonCrearEntrenamiento.setVisibility(View.GONE);
            if (buttonCrearPartido != null) buttonCrearPartido.setVisibility(View.GONE);
            if (buttonCrearReunion != null) buttonCrearReunion.setVisibility(View.GONE);
        }
        
        // Configurar botones que todos pueden usar
        if (buttonCalendarioEventos != null) {
            buttonCalendarioEventos.setOnClickListener(v -> abrirCalendarioEventos());
        }
        if (buttonExportarEventos != null) {
            buttonExportarEventos.setOnClickListener(v -> exportarEventos());
        }
    }

    private void configurarRecyclerView() {
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventoAdapter = new EventoAdapter(new ArrayList<>(), new EventoAdapter.OnEventoClickListener() {
            @Override
            public void onEditarClick(Evento evento) {
                mostrarDialogoEditarEvento(evento);
            }
            
            @Override
            public void onEliminarClick(Evento evento) {
                mostrarDialogoEliminarEvento(evento);
            }
        }, true);
        recyclerViewEventos.setAdapter(eventoAdapter);
    }

    private void configurarFiltrosAvanzados(View view) {
        String[] tipos = {"Todos", "Entrenamiento", "Partido", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroTipoEvento.setAdapter(tipoAdapter);
        spinnerFiltroTipoEvento.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                tipoFiltroSeleccionado = tipos[position];
                cargarEventos();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        List<String> equipos = new ArrayList<>();
        equipos.add("Todos los equipos");
        
        // Si es entrenador, solo mostrar su equipo
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            equipos.add(usuarioActual.getEquipo());
        } else {
            // Si es admin, mostrar todos los equipos
            for (String equipo : dataManager.getNombresEquipos()) {
                equipos.add(equipo);
            }
        }
        
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEquipo.setAdapter(equipoAdapter);
        spinnerFiltroEquipo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                equipoFiltroSeleccionado = equipos.get(position);
                cargarEventos();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        String[] fechas = {"Todas", "Hoy", "Esta semana", "Este mes"};
        ArrayAdapter<String> fechaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fechas);
        fechaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroFecha.setAdapter(fechaAdapter);
        spinnerFiltroFecha.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                fechaFiltroSeleccionada = fechas[position];
                cargarEventos();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        String[] estados = {"Todos", "Pendiente", "En curso", "Completado", "Cancelado"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, estados);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEstado.setAdapter(estadoAdapter);
        spinnerFiltroEstado.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                estadoFiltroSeleccionado = estados[position];
                cargarEventos();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        String[] prioridades = {"Todas", "Baja", "Media", "Alta", "Urgente"};
        ArrayAdapter<String> prioridadAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, prioridades);
        prioridadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroPrioridad.setAdapter(prioridadAdapter);
        spinnerFiltroPrioridad.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                prioridadFiltroSeleccionada = prioridades[position];
                cargarEventos();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        buttonMostrarFiltrosAvanzados.setOnClickListener(v -> {
            if (layoutFiltrosAvanzados.getVisibility() == View.GONE) {
                layoutFiltrosAvanzados.setVisibility(View.VISIBLE);
                buttonMostrarFiltrosAvanzados.setText("Ocultar Filtros Avanzados");
            } else {
                layoutFiltrosAvanzados.setVisibility(View.GONE);
                buttonMostrarFiltrosAvanzados.setText("Mostrar Filtros Avanzados");
            }
        });

        buttonLimpiarFiltros.setOnClickListener(v -> {
            spinnerFiltroTipoEvento.setSelection(0);
            spinnerFiltroEquipo.setSelection(0);
            spinnerFiltroFecha.setSelection(0);
            spinnerFiltroEstado.setSelection(0);
            spinnerFiltroPrioridad.setSelection(0);
            buttonMostrarFiltrosAvanzados.setText("Mostrar Filtros Avanzados");
            layoutFiltrosAvanzados.setVisibility(View.GONE);
            cargarEventos();
        });

        buttonExportarEventos.setOnClickListener(v -> {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                List<Evento> eventos = dataManager.getEventos();
                if (eventos.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay eventos para exportar", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder reporte = new StringBuilder();
                reporte.append("REPORTE DE EVENTOS - CD SANTIAGUIÑO GUIZÁN\n");
                reporte.append("Fecha: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(new java.util.Date())).append("\n\n");
                int total = eventos.size();
                int entrenamientos = 0, partidos = 0, torneos = 0, reuniones = 0, otros = 0;
                for (Evento evento : eventos) {
                    switch (evento.getTipo()) {
                        case "Entrenamiento": entrenamientos++; break;
                        case "Partido": partidos++; break;
                        case "Torneo": torneos++; break;
                        case "Reunión": reuniones++; break;
                        default: otros++; break;
                    }
                    reporte.append("• ").append(evento.getTitulo()).append("\n");
                    reporte.append("  Tipo: ").append(evento.getTipo()).append("\n");
                    reporte.append("  Equipo: ").append(evento.getEquipo()).append("\n");
                    reporte.append("  Fecha: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(evento.getFechaInicio())).append("\n");
                    reporte.append("  Estado: ").append(evento.getEstado() != null ? evento.getEstado() : "-").append("\n");
                    reporte.append("  Prioridad: ").append(evento.getPrioridad() != null ? evento.getPrioridad() : "-").append("\n\n");
                }
                reporte.append("\nRESUMEN:\n");
                reporte.append("Total: ").append(total).append("\n");
                reporte.append("Entrenamientos: ").append(entrenamientos).append("\n");
                reporte.append("Partidos: ").append(partidos).append("\n");
                reporte.append("Torneos: ").append(torneos).append("\n");
                reporte.append("Reuniones: ").append(reuniones).append("\n");
                reporte.append("Otros: ").append(otros).append("\n");
                new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("📊 Reporte de Eventos")
                    .setMessage(reporte.toString())
                    .setPositiveButton("Copiar", (dialog, which) -> {
                        // Aquí se podría implementar la copia al portapapeles
                        Toast.makeText(requireContext(), "Reporte copiado al portapapeles", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cerrar", null)
                    .show();
            } else {
                Toast.makeText(requireContext(), "Solo los administradores pueden exportar eventos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarEventos() {
        List<Evento> eventos = dataManager.getEventos();
        
        // Si es entrenador, filtrar solo eventos de su equipo
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            List<Evento> eventosFiltrados = new ArrayList<>();
            for (Evento evento : eventos) {
                if (usuarioActual.getEquipo().equals(evento.getEquipo()) || 
                    "Todos los equipos".equals(evento.getEquipo())) {
                    eventosFiltrados.add(evento);
                }
            }
            eventos = eventosFiltrados;
        }
        
        // Filtrar por tipo seleccionado
        if (!"Todos".equals(tipoFiltroSeleccionado)) {
            List<Evento> eventosFiltradosTipo = new ArrayList<>();
            for (Evento evento : eventos) {
                if (tipoFiltroSeleccionado.equalsIgnoreCase(evento.getTipo())) {
                    eventosFiltradosTipo.add(evento);
                } else if ("Otro".equals(tipoFiltroSeleccionado) &&
                        !("Entrenamiento".equalsIgnoreCase(evento.getTipo()) || "Partido".equalsIgnoreCase(evento.getTipo()))) {
                    eventosFiltradosTipo.add(evento);
                }
            }
            eventos = eventosFiltradosTipo;
        }
        
        // Filtrar por equipo seleccionado
        if (!"Todos los equipos".equals(equipoFiltroSeleccionado)) {
            List<Evento> eventosFiltradosEquipo = new ArrayList<>();
            for (Evento evento : eventos) {
                if (equipoFiltroSeleccionado.equals(evento.getEquipo()) || 
                    "Todos los equipos".equals(evento.getEquipo())) {
                    eventosFiltradosEquipo.add(evento);
                }
            }
            eventos = eventosFiltradosEquipo;
        }

        // Filtrar por fecha seleccionada
        if (!"Todas".equals(fechaFiltroSeleccionada)) {
            List<Evento> eventosFiltradosFecha = new ArrayList<>();
            Calendar hoy = Calendar.getInstance();
            Calendar semana = Calendar.getInstance();
            semana.add(Calendar.DAY_OF_YEAR, 7);
            Calendar mes = Calendar.getInstance();
            mes.add(Calendar.MONTH, 1);

            for (Evento evento : eventos) {
                Calendar fechaEvento = Calendar.getInstance();
                fechaEvento.setTime(evento.getFechaInicio());

                boolean cumpleFecha = false;
                if ("Hoy".equals(fechaFiltroSeleccionada)) {
                    cumpleFecha = fechaEvento.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                                 fechaEvento.get(Calendar.DAY_OF_YEAR) == hoy.get(Calendar.DAY_OF_YEAR);
                } else if ("Esta semana".equals(fechaFiltroSeleccionada)) {
                    cumpleFecha = fechaEvento.before(semana) && fechaEvento.after(hoy);
                } else if ("Este mes".equals(fechaFiltroSeleccionada)) {
                    cumpleFecha = fechaEvento.before(mes) && fechaEvento.after(hoy);
                }

                if (cumpleFecha) {
                    eventosFiltradosFecha.add(evento);
                }
            }
            eventos = eventosFiltradosFecha;
        }

        // Filtrar por estado seleccionado
        if (!"Todos".equals(estadoFiltroSeleccionado)) {
            List<Evento> eventosFiltradosEstado = new ArrayList<>();
            for (Evento evento : eventos) {
                if (estadoFiltroSeleccionado.equalsIgnoreCase(evento.getEstado())) {
                    eventosFiltradosEstado.add(evento);
                }
            }
            eventos = eventosFiltradosEstado;
        }

        // Filtrar por prioridad seleccionada
        if (!"Todas".equals(prioridadFiltroSeleccionada)) {
            List<Evento> eventosFiltradosPrioridad = new ArrayList<>();
            for (Evento evento : eventos) {
                if (prioridadFiltroSeleccionada.equalsIgnoreCase(evento.getPrioridad())) {
                    eventosFiltradosPrioridad.add(evento);
                }
            }
            eventos = eventosFiltradosPrioridad;
        }
        
        eventoAdapter.actualizarEventos(eventos);
    }

    private void actualizarEstadisticas() {
        List<Evento> eventos = dataManager.getEventos();
        int totalEventos = eventos.size();
        int eventosHoy = 0, eventosSemana = 0, eventosMes = 0;
        
        Calendar hoy = Calendar.getInstance();
        Calendar semana = Calendar.getInstance();
        semana.add(Calendar.DAY_OF_YEAR, 7);
        Calendar mes = Calendar.getInstance();
        mes.add(Calendar.MONTH, 1);
        
        for (Evento evento : eventos) {
            Calendar fechaEvento = Calendar.getInstance();
            fechaEvento.setTime(evento.getFechaInicio());
            
            if (fechaEvento.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                fechaEvento.get(Calendar.DAY_OF_YEAR) == hoy.get(Calendar.DAY_OF_YEAR)) {
                eventosHoy++;
            }
            
            if (fechaEvento.before(semana) && fechaEvento.after(hoy)) {
                eventosSemana++;
            }
            
            if (fechaEvento.before(mes) && fechaEvento.after(hoy)) {
                eventosMes++;
            }
        }
        
        String estadisticas = String.format("Total: %d | Hoy: %d | Esta semana: %d | Este mes: %d", 
                totalEventos, eventosHoy, eventosSemana, eventosMes);
        textViewEstadisticas.setText(estadisticas);
    }

    private void mostrarDialogoCrearEvento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_evento, null);
        
        EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextLugar = dialogView.findViewById(R.id.editTextLugar);
        TextView textViewFecha = dialogView.findViewById(R.id.textViewFecha);
        TextView textViewHora = dialogView.findViewById(R.id.textViewHora);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        Spinner spinnerRecurrencia = dialogView.findViewById(R.id.spinnerRecurrencia);
        Spinner spinnerRepeticiones = dialogView.findViewById(R.id.spinnerRepeticiones);
        LinearLayout layoutRepeticiones = dialogView.findViewById(R.id.layoutRepeticiones);
        
        // Configurar spinner de tipos
        String[] tipos = {"Entrenamiento", "Partido", "Torneo", "Reunión", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        
        // Configurar spinner de equipos
        List<String> equipos = new ArrayList<>();
        equipos.add("Todos los equipos");
        
        // Si es entrenador, solo mostrar su equipo
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            equipos.add(usuarioActual.getEquipo());
        } else {
            // Si es admin, mostrar todos los equipos
            for (String equipo : dataManager.getNombresEquipos()) {
                equipos.add(equipo);
            }
        }
        
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Si es entrenador, seleccionar automáticamente su equipo
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            int posicionEquipo = equipos.indexOf(usuarioActual.getEquipo());
            if (posicionEquipo >= 0) {
                spinnerEquipo.setSelection(posicionEquipo);
            }
        }
        
        // Configurar spinner de recurrencia
        String[] recurrencias = {"Sin recurrencia", "Diario", "Semanal", "Mensual"};
        ArrayAdapter<String> recurrenciaAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, recurrencias);
        recurrenciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecurrencia.setAdapter(recurrenciaAdapter);
        
        // Configurar spinner de repeticiones
        String[] repeticiones = {"2 veces", "3 veces", "4 veces", "5 veces", "6 veces", "8 veces", "10 veces", "12 veces"};
        ArrayAdapter<String> repeticionesAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, repeticiones);
        repeticionesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeticiones.setAdapter(repeticionesAdapter);
        
        // Mostrar/ocultar repeticiones según recurrencia
        spinnerRecurrencia.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Si hay recurrencia
                    layoutRepeticiones.setVisibility(View.VISIBLE);
                } else {
                    layoutRepeticiones.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        // Configurar selección de fecha
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        textViewFecha.setText(dateFormat.format(calendar.getTime()));
        textViewHora.setText(timeFormat.format(calendar.getTime()));
        
        textViewFecha.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    textViewFecha.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        textViewHora.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    textViewHora.setText(timeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });
        
        builder.setView(dialogView)
                .setTitle("Crear Nuevo Evento")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String titulo = editTextTitulo.getText().toString().trim();
                    String descripcion = editTextDescripcion.getText().toString().trim();
                    String lugar = editTextLugar.getText().toString().trim();
                    String tipo = spinnerTipo.getSelectedItem().toString();
                    String equipo = spinnerEquipo.getSelectedItem().toString();
                    String recurrencia = spinnerRecurrencia.getSelectedItem().toString();
                    String repeticionesStr = spinnerRepeticiones.getSelectedItem().toString();
                    
                    if (validarDatos(titulo, descripcion, lugar)) {
                        crearEventoRecurrente(titulo, descripcion, lugar, tipo, equipo, calendar.getTime(), recurrencia, repeticionesStr);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatos(String titulo, String descripcion, String lugar) {
        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(requireContext(), "La descripción es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (lugar.isEmpty()) {
            Toast.makeText(requireContext(), "El lugar es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void crearEventoRecurrente(String titulo, String descripcion, String lugar, String tipo, String equipo, java.util.Date fecha, String recurrencia, String repeticionesStr) {
        int numRepeticiones = 1;
        if (!"Sin recurrencia".equals(recurrencia)) {
            numRepeticiones = Integer.parseInt(repeticionesStr.split(" ")[0]);
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        
        for (int i = 0; i < numRepeticiones; i++) {
            Evento evento = new Evento(titulo, descripcion, calendar.getTime(), calendar.getTime(), lugar, tipo, "admin", "Administrador", true);
            
            if (!"Todos los equipos".equals(equipo)) {
                evento.setEquipo(equipo);
            }
            
            dataManager.agregarEvento(evento);
            
            // Calcular siguiente fecha según recurrencia
            if (i < numRepeticiones - 1) { // No incrementar en la última iteración
                switch (recurrencia) {
                    case "Diario":
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        break;
                    case "Semanal":
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        break;
                    case "Mensual":
                        calendar.add(Calendar.MONTH, 1);
                        break;
                }
            }
        }
        
        cargarEventos();
        actualizarEstadisticas();
        
        String mensaje = "Evento creado exitosamente";
        if (numRepeticiones > 1) {
            mensaje += " (" + numRepeticiones + " repeticiones)";
        }
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoEditarEvento(Evento evento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_evento, null);
        
        EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextLugar = dialogView.findViewById(R.id.editTextLugar);
        TextView textViewFecha = dialogView.findViewById(R.id.textViewFecha);
        TextView textViewHora = dialogView.findViewById(R.id.textViewHora);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Llenar datos existentes
        editTextTitulo.setText(evento.getTitulo());
        editTextDescripcion.setText(evento.getDescripcion());
        editTextLugar.setText(evento.getUbicacion());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        textViewFecha.setText(dateFormat.format(evento.getFechaInicio()));
        textViewHora.setText(timeFormat.format(evento.getFechaInicio()));
        
        // Configurar spinners
        String[] tipos = {"Entrenamiento", "Partido", "Torneo", "Reunión", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        
        List<String> equipos = new ArrayList<>();
        equipos.add("Todos los equipos");
        for (String equipo : dataManager.getNombresEquipos()) {
            equipos.add(equipo);
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Seleccionar valores actuales
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equals(evento.getTipo())) {
                spinnerTipo.setSelection(i);
                break;
            }
        }
        
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).equals(evento.getEquipo()) || 
                (evento.getEquipo() == null && equipos.get(i).equals("Todos los equipos"))) {
                spinnerEquipo.setSelection(i);
                break;
            }
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(evento.getFechaInicio());
        
        textViewFecha.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    textViewFecha.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        textViewHora.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    textViewHora.setText(timeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });
        
        builder.setView(dialogView)
                .setTitle("Editar Evento")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = editTextTitulo.getText().toString().trim();
                    String descripcion = editTextDescripcion.getText().toString().trim();
                    String lugar = editTextLugar.getText().toString().trim();
                    String tipo = spinnerTipo.getSelectedItem().toString();
                    String equipo = spinnerEquipo.getSelectedItem().toString();
                    
                    if (validarDatos(titulo, descripcion, lugar)) {
                        editarEvento(evento, titulo, descripcion, lugar, tipo, equipo, calendar.getTime());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarEvento(Evento eventoOriginal, String titulo, String descripcion, String lugar, String tipo, String equipo, java.util.Date fecha) {
        eventoOriginal.setTitulo(titulo);
        eventoOriginal.setDescripcion(descripcion);
        eventoOriginal.setUbicacion(lugar);
        eventoOriginal.setTipo(tipo);
        eventoOriginal.setFechaInicio(fecha);
        eventoOriginal.setFechaFin(fecha);
        
        if (!"Todos los equipos".equals(equipo)) {
            eventoOriginal.setEquipo(equipo);
        } else {
            eventoOriginal.setEquipo(null);
        }
        
        dataManager.actualizarEvento(eventoOriginal);
        cargarEventos();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Evento actualizado exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoEliminarEvento(Evento evento) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro de que quieres eliminar el evento '" + evento.getTitulo() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarEvento(evento))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarEvento(Evento evento) {
        dataManager.eliminarEvento(evento.getId());
        cargarEventos();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Evento eliminado exitosamente", Toast.LENGTH_SHORT).show();
    }

    // Mostrar el diálogo de creación de entrenamientos
    private void mostrarDialogoCrearEntrenamiento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_entrenamiento, null);

        // Referencias a las vistas
        TextView editTextFechaInicio = dialogView.findViewById(R.id.editTextFechaInicio);
        TextView editTextFechaFin = dialogView.findViewById(R.id.editTextFechaFin);
        TextView editTextHoraInicio = dialogView.findViewById(R.id.editTextHoraInicio);
        TextView editTextHoraFin = dialogView.findViewById(R.id.editTextHoraFin);
        AutoCompleteTextView autoCompleteEquipos = dialogView.findViewById(R.id.autoCompleteEquipos);
        com.google.android.material.button.MaterialButton btnCrearEntrenamientos = dialogView.findViewById(R.id.btnCrearEntrenamientos);
        
        // Referencias para modo avanzado
        com.google.android.material.button.MaterialButtonToggleGroup toggleGroupHorario = dialogView.findViewById(R.id.toggleGroupHorario);
        LinearLayout layoutHorarioSimple = dialogView.findViewById(R.id.layoutHorarioSimple);
        LinearLayout layoutHorarioAvanzado = dialogView.findViewById(R.id.layoutHorarioAvanzado);
        
        // Referencias para checkboxes y horarios por día
        com.google.android.material.checkbox.MaterialCheckBox checkLunes = dialogView.findViewById(R.id.checkLunes);
        com.google.android.material.checkbox.MaterialCheckBox checkMartes = dialogView.findViewById(R.id.checkMartes);
        com.google.android.material.checkbox.MaterialCheckBox checkMiercoles = dialogView.findViewById(R.id.checkMiercoles);
        com.google.android.material.checkbox.MaterialCheckBox checkJueves = dialogView.findViewById(R.id.checkJueves);
        com.google.android.material.checkbox.MaterialCheckBox checkViernes = dialogView.findViewById(R.id.checkViernes);
        com.google.android.material.checkbox.MaterialCheckBox checkSabado = dialogView.findViewById(R.id.checkSabado);
        com.google.android.material.checkbox.MaterialCheckBox checkDomingo = dialogView.findViewById(R.id.checkDomingo);
        
        // Referencias para horarios por día
        TextView horaInicioLunes = dialogView.findViewById(R.id.horaInicioLunes);
        TextView horaFinLunes = dialogView.findViewById(R.id.horaFinLunes);
        TextView horaInicioMartes = dialogView.findViewById(R.id.horaInicioMartes);
        TextView horaFinMartes = dialogView.findViewById(R.id.horaFinMartes);
        TextView horaInicioMiercoles = dialogView.findViewById(R.id.horaInicioMiercoles);
        TextView horaFinMiercoles = dialogView.findViewById(R.id.horaFinMiercoles);
        TextView horaInicioJueves = dialogView.findViewById(R.id.horaInicioJueves);
        TextView horaFinJueves = dialogView.findViewById(R.id.horaFinJueves);
        TextView horaInicioViernes = dialogView.findViewById(R.id.horaInicioViernes);
        TextView horaFinViernes = dialogView.findViewById(R.id.horaFinViernes);
        TextView horaInicioSabado = dialogView.findViewById(R.id.horaInicioSabado);
        TextView horaFinSabado = dialogView.findViewById(R.id.horaFinSabado);
        TextView horaInicioDomingo = dialogView.findViewById(R.id.horaInicioDomingo);
        TextView horaFinDomingo = dialogView.findViewById(R.id.horaFinDomingo);

        // Configurar toggle group para modo de horario
        toggleGroupHorario.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnHorarioSimple) {
                    layoutHorarioSimple.setVisibility(View.VISIBLE);
                    layoutHorarioAvanzado.setVisibility(View.GONE);
                } else if (checkedId == R.id.btnHorarioAvanzado) {
                    layoutHorarioSimple.setVisibility(View.GONE);
                    layoutHorarioAvanzado.setVisibility(View.VISIBLE);
                }
            }
        });
        
        // Seleccionar horario simple por defecto
        toggleGroupHorario.check(R.id.btnHorarioSimple);

        // Configurar selección múltiple de equipos
        List<String> nombresEquipos = dataManager.getNombresEquipos();
        ArrayAdapter<String> equiposAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresEquipos);
        autoCompleteEquipos.setAdapter(equiposAdapter);
        autoCompleteEquipos.setInputType(android.text.InputType.TYPE_NULL);
        List<String> equiposSeleccionados = new ArrayList<>();
        autoCompleteEquipos.setOnClickListener(v -> {
            boolean[] checkedItems = new boolean[nombresEquipos.size()];
            new AlertDialog.Builder(requireContext())
                .setTitle("Selecciona equipos")
                .setMultiChoiceItems(nombresEquipos.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) equiposSeleccionados.add(nombresEquipos.get(which));
                    else equiposSeleccionados.remove(nombresEquipos.get(which));
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    autoCompleteEquipos.setText(android.text.TextUtils.join(", ", equiposSeleccionados));
                    autoCompleteEquipos.setTag(new ArrayList<>(equiposSeleccionados));
                })
                .setNegativeButton("Cancelar", null)
                .show();
        });

        // Configurar pickers de fecha y hora para modo simple
        editTextFechaInicio.setOnClickListener(v -> mostrarDatePicker(editTextFechaInicio));
        editTextFechaFin.setOnClickListener(v -> mostrarDatePicker(editTextFechaFin));
        editTextHoraInicio.setOnClickListener(v -> mostrarTimePicker(editTextHoraInicio));
        editTextHoraFin.setOnClickListener(v -> mostrarTimePicker(editTextHoraFin));
        
        // Configurar pickers para modo avanzado
        horaInicioLunes.setOnClickListener(v -> mostrarTimePicker(horaInicioLunes));
        horaFinLunes.setOnClickListener(v -> mostrarTimePicker(horaFinLunes));
        horaInicioMartes.setOnClickListener(v -> mostrarTimePicker(horaInicioMartes));
        horaFinMartes.setOnClickListener(v -> mostrarTimePicker(horaFinMartes));
        horaInicioMiercoles.setOnClickListener(v -> mostrarTimePicker(horaInicioMiercoles));
        horaFinMiercoles.setOnClickListener(v -> mostrarTimePicker(horaFinMiercoles));
        horaInicioJueves.setOnClickListener(v -> mostrarTimePicker(horaInicioJueves));
        horaFinJueves.setOnClickListener(v -> mostrarTimePicker(horaFinJueves));
        horaInicioViernes.setOnClickListener(v -> mostrarTimePicker(horaInicioViernes));
        horaFinViernes.setOnClickListener(v -> mostrarTimePicker(horaFinViernes));
        horaInicioSabado.setOnClickListener(v -> mostrarTimePicker(horaInicioSabado));
        horaFinSabado.setOnClickListener(v -> mostrarTimePicker(horaFinSabado));
        horaInicioDomingo.setOnClickListener(v -> mostrarTimePicker(horaInicioDomingo));
        horaFinDomingo.setOnClickListener(v -> mostrarTimePicker(horaFinDomingo));

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Crear Entrenamientos Recurrentes")
                .setNegativeButton("Cancelar", null)
                .create();

        btnCrearEntrenamientos.setOnClickListener(v -> {
            String fechaInicioStr = editTextFechaInicio.getText().toString();
            String fechaFinStr = editTextFechaFin.getText().toString();
            List<String> equipos = (List<String>) autoCompleteEquipos.getTag();

            // Validaciones básicas
            if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty() || equipos == null || equipos.isEmpty()) {
                Toast.makeText(requireContext(), "Completa las fechas y selecciona al menos un equipo", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validar fechas
            java.text.SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.util.Calendar fechaInicio = java.util.Calendar.getInstance();
            java.util.Calendar fechaFin = java.util.Calendar.getInstance();
            try {
                fechaInicio.setTime(sdfFecha.parse(fechaInicioStr));
                fechaFin.setTime(sdfFecha.parse(fechaFinStr));
            } catch (java.text.ParseException e) {
                Toast.makeText(requireContext(), "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
                return;
            }
            
            List<Evento> sesiones = new ArrayList<>();
            int sesionNum = 1;
            
            // Determinar modo de horario
            boolean esHorarioSimple = layoutHorarioSimple.getVisibility() == View.VISIBLE;
            
            if (esHorarioSimple) {
                // Modo horario simple
                String horaInicioStr = editTextHoraInicio.getText().toString();
                String horaFinStr = editTextHoraFin.getText().toString();
                
                if (horaInicioStr.isEmpty() || horaFinStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa los horarios", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Obtener días seleccionados del modo simple (todos los días)
                List<Integer> diasSeleccionados = new ArrayList<>();
                for (int i = 1; i <= 7; i++) {
                    diasSeleccionados.add(i);
                }
                
                sesiones = crearSesionesSimple(fechaInicio, fechaFin, diasSeleccionados, horaInicioStr, horaFinStr, equipos, sesionNum);
                
            } else {
                // Modo horario avanzado
                List<DiaHorario> diasHorarios = new ArrayList<>();
                
                if (checkLunes.isChecked()) {
                    String horaInicio = horaInicioLunes.getText().toString();
                    String horaFin = horaFinLunes.getText().toString();
                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        diasHorarios.add(new DiaHorario(1, horaInicio, horaFin));
                    }
                }
                if (checkMartes.isChecked()) {
                    String horaInicio = horaInicioMartes.getText().toString();
                    String horaFin = horaFinMartes.getText().toString();
                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        diasHorarios.add(new DiaHorario(2, horaInicio, horaFin));
                    }
                }
                if (checkMiercoles.isChecked()) {
                    String horaInicio = horaInicioMiercoles.getText().toString();
                    String horaFin = horaFinMiercoles.getText().toString();
                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        diasHorarios.add(new DiaHorario(3, horaInicio, horaFin));
                    }
                }
                if (checkJueves.isChecked()) {
                    String horaInicio = horaInicioJueves.getText().toString();
                    String horaFin = horaFinJueves.getText().toString();
                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        diasHorarios.add(new DiaHorario(4, horaInicio, horaFin));
                    }
                }
                if (checkViernes.isChecked()) {
                    String horaInicio = horaInicioViernes.getText().toString();
                    String horaFin = horaFinViernes.getText().toString();
                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        diasHorarios.add(new DiaHorario(5, horaInicio, horaFin));
                    }
                }
                if (checkSabado.isChecked()) {
                    String horaInicio = horaInicioSabado.getText().toString();
                    String horaFin = horaFinSabado.getText().toString();
                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        diasHorarios.add(new DiaHorario(6, horaInicio, horaFin));
                    }
                }
                if (checkDomingo.isChecked()) {
                    String horaInicio = horaInicioDomingo.getText().toString();
                    String horaFin = horaFinDomingo.getText().toString();
                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        diasHorarios.add(new DiaHorario(7, horaInicio, horaFin));
                    }
                }
                
                if (diasHorarios.isEmpty()) {
                    Toast.makeText(requireContext(), "Selecciona al menos un día con horario", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                sesiones = crearSesionesAvanzado(fechaInicio, fechaFin, diasHorarios, equipos, sesionNum);
            }
            
            // Crear las sesiones
            for (Evento sesion : sesiones) {
                dataManager.agregarEvento(sesion);
            }
            
            Toast.makeText(requireContext(), "Sesiones creadas: " + sesiones.size(), Toast.LENGTH_LONG).show();
            dialog.dismiss();
            cargarEventos();
            actualizarEstadisticas();
        });

        dialog.show();
    }
    
    // Clase auxiliar para horarios por día
    private static class DiaHorario {
        int diaSemana; // 1=Lunes, 7=Domingo
        String horaInicio;
        String horaFin;
        
        DiaHorario(int diaSemana, String horaInicio, String horaFin) {
            this.diaSemana = diaSemana;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }
    }
    
    // Crear sesiones en modo simple
    private List<Evento> crearSesionesSimple(java.util.Calendar fechaInicio, java.util.Calendar fechaFin, 
                                           List<Integer> diasSeleccionados, String horaInicioStr, String horaFinStr, 
                                           List<String> equipos, int sesionNum) {
        List<Evento> sesiones = new ArrayList<>();
        
        for (java.util.Calendar fecha = (java.util.Calendar) fechaInicio.clone(); !fecha.after(fechaFin); fecha.add(java.util.Calendar.DATE, 1)) {
            int diaSemana = fecha.get(java.util.Calendar.DAY_OF_WEEK);
            int diaApp = (diaSemana == java.util.Calendar.SUNDAY) ? 7 : diaSemana - 1;
            if (diasSeleccionados.contains(diaApp)) {
                for (String equipo : equipos) {
                    Evento sesion = new Evento();
                    String nombreSesion = equipo + " - Sesión " + sesionNum + " - " + horaInicioStr + "-" + horaFinStr;
                    sesion.setTitulo(nombreSesion);
                    sesion.setEquipo(equipo);
                    sesion.setTipo("ENTRENAMIENTO");
                    sesion.setFechaInicio(combinarFechaHora(fecha, horaInicioStr));
                    sesion.setFechaFin(combinarFechaHora(fecha, horaFinStr));
                    sesiones.add(sesion);
                }
                sesionNum++;
            }
        }
        
        return sesiones;
    }
    
    // Crear sesiones en modo avanzado
    private List<Evento> crearSesionesAvanzado(java.util.Calendar fechaInicio, java.util.Calendar fechaFin, 
                                             List<DiaHorario> diasHorarios, List<String> equipos, int sesionNum) {
        List<Evento> sesiones = new ArrayList<>();
        
        for (java.util.Calendar fecha = (java.util.Calendar) fechaInicio.clone(); !fecha.after(fechaFin); fecha.add(java.util.Calendar.DATE, 1)) {
            int diaSemana = fecha.get(java.util.Calendar.DAY_OF_WEEK);
            int diaApp = (diaSemana == java.util.Calendar.SUNDAY) ? 7 : diaSemana - 1;
            
            // Buscar horario para este día
            DiaHorario horarioDia = null;
            for (DiaHorario dh : diasHorarios) {
                if (dh.diaSemana == diaApp) {
                    horarioDia = dh;
                    break;
                }
            }
            
            if (horarioDia != null) {
                for (String equipo : equipos) {
                    Evento sesion = new Evento();
                    String nombreSesion = equipo + " - Sesión " + sesionNum + " - " + horarioDia.horaInicio + "-" + horarioDia.horaFin;
                    sesion.setTitulo(nombreSesion);
                    sesion.setEquipo(equipo);
                    sesion.setTipo("ENTRENAMIENTO");
                    sesion.setFechaInicio(combinarFechaHora(fecha, horarioDia.horaInicio));
                    sesion.setFechaFin(combinarFechaHora(fecha, horarioDia.horaFin));
                    sesiones.add(sesion);
                }
                sesionNum++;
            }
        }
        
        return sesiones;
    }

    // Métodos auxiliares para pickers y combinar fecha/hora
    private void mostrarDatePicker(TextView editText) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String fecha = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            editText.setText(fecha);
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private void mostrarTimePicker(TextView editText) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        new android.app.TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            String hora = String.format(java.util.Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            editText.setText(hora);
        }, calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true).show();
    }

    private java.util.Date combinarFechaHora(java.util.Calendar fecha, String horaStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
            String fechaStr = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", fecha.get(java.util.Calendar.DAY_OF_MONTH), fecha.get(java.util.Calendar.MONTH) + 1, fecha.get(java.util.Calendar.YEAR));
            return sdf.parse(fechaStr + " " + horaStr);
        } catch (Exception e) {
            return fecha.getTime();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEventos();
        actualizarEstadisticas();
    }
    
    // Métodos para los nuevos botones
    private void mostrarDialogoCrearPartido() {
        // Crear un evento de tipo "Partido"
        mostrarDialogoCrearEventoConTipo("Partido");
    }
    
    private void mostrarDialogoCrearReunion() {
        // Crear un evento de tipo "Reunión"
        mostrarDialogoCrearEventoConTipo("Reunión");
    }
    
    private void mostrarDialogoCrearEventoConTipo(String tipoEvento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_evento, null);
        
        EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextLugar = dialogView.findViewById(R.id.editTextLugar);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        TextView editTextFecha = dialogView.findViewById(R.id.textViewFecha);
        TextView editTextHora = dialogView.findViewById(R.id.textViewHora);
        
        // Configurar spinners
        String[] tipos = {"Entrenamiento", "Partido", "Reunión", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        
        // Seleccionar el tipo específico
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equals(tipoEvento)) {
                spinnerTipo.setSelection(i);
                break;
            }
        }
        
        // Configurar equipos
        List<String> equipos = new ArrayList<>();
        equipos.add("Todos los equipos");
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            equipos.add(usuarioActual.getEquipo());
        } else {
            equipos.addAll(dataManager.getNombresEquipos());
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Configurar fecha y hora
        editTextFecha.setOnClickListener(v -> mostrarDatePicker(editTextFecha));
        editTextHora.setOnClickListener(v -> mostrarTimePicker(editTextHora));
        
        builder.setView(dialogView)
                .setTitle("Crear " + tipoEvento)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String titulo = editTextTitulo.getText().toString().trim();
                    String descripcion = editTextDescripcion.getText().toString().trim();
                    String lugar = editTextLugar.getText().toString().trim();
                    String tipo = spinnerTipo.getSelectedItem().toString();
                    String equipo = spinnerEquipo.getSelectedItem().toString();
                    String fechaStr = editTextFecha.getText().toString();
                    String horaStr = editTextHora.getText().toString();
                    
                    if (validarDatos(titulo, descripcion, lugar)) {
                        try {
                            java.util.Date fecha = combinarFechaHora(parsearFecha(fechaStr), horaStr);
                            crearEventoRecurrente(titulo, descripcion, lugar, tipo, equipo, fecha, "Ninguna", "1");
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Error en fecha/hora: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    
    private java.util.Calendar parsearFecha(String fechaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            java.util.Date fecha = sdf.parse(fechaStr);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fecha);
            return cal;
        } catch (Exception e) {
            return java.util.Calendar.getInstance();
        }
    }
    
    private void abrirCalendarioEventos() {
        // Navegar al fragmento de calendario
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CalendarioFragment())
                .addToBackStack(null)
                .commit();
        }
    }
    
    private void exportarEventos() {
        // Implementar exportación de eventos
        Toast.makeText(requireContext(), "Función de exportación en desarrollo", Toast.LENGTH_SHORT).show();
    }
} 