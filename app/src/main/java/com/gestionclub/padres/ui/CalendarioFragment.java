package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
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
import com.gestionclub.padres.adapter.EventoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarioFragment extends Fragment implements EventoAdapter.OnEventoClickListener {
    private CalendarView calendarView;
    private RecyclerView recyclerViewEventos;
    private TextView textViewEventosDia;
    private Button buttonAgregarEvento;
    private EventoAdapter eventoAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private Date fechaSeleccionada;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            fechaSeleccionada = new Date();
            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            inicializarVistas(view);
            configurarCalendario();
            configurarRecyclerView();
            cargarEventosDelDia();
            configurarListeners();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al cargar el calendario: " + e.getMessage(), Toast.LENGTH_LONG).show();
            TextView tvSinEventos = view.findViewById(R.id.tv_sin_eventos);
            if (tvSinEventos != null) {
                tvSinEventos.setText("No se pudo cargar el calendario. Intenta más tarde.");
                tvSinEventos.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    private void inicializarVistas(View view) {
        calendarView = view.findViewById(R.id.calendarView);
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
        textViewEventosDia = view.findViewById(R.id.textViewEventosDia);
        buttonAgregarEvento = view.findViewById(R.id.buttonAgregarEvento);
    }

    private void configurarCalendario() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            fechaSeleccionada = cal.getTime();
            cargarEventosDelDia();
        });
    }

    private void configurarRecyclerView() {
        boolean mostrarAcciones = usuarioActual != null && usuarioActual.isEsAdmin();
        eventoAdapter = new EventoAdapter(new ArrayList<>(), this, mostrarAcciones);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEventos.setAdapter(eventoAdapter);
        
        // Mostrar botón agregar solo para administradores
        if (buttonAgregarEvento != null) {
            buttonAgregarEvento.setVisibility(mostrarAcciones ? View.VISIBLE : View.GONE);
        }
    }

    private void cargarEventosDelDia() {
        List<Evento> todosEventos = new ArrayList<>();
        try {
            if (dataManager != null) {
                List<Evento> eventos = dataManager.getEventos();
                if (eventos != null) todosEventos = eventos;
            }
        } catch (Exception e) {
            // Si hay error, la lista queda vacía
        }
        
        List<Evento> eventosDelDia = new ArrayList<>();
        Calendar calSeleccionada = Calendar.getInstance();
        calSeleccionada.setTime(fechaSeleccionada);
        
        // Obtener el equipo/categoría del usuario actual para filtrar
        String equipoUsuario = usuarioActual != null ? usuarioActual.getEquipo() : null;
        
        for (Evento evento : todosEventos) {
            // Filtrar por equipo/categoría si el usuario no es administrador
            if (usuarioActual != null && !usuarioActual.isEsAdmin() && equipoUsuario != null) {
                // Si el evento tiene equipo específico, solo mostrarlo si coincide
                if (evento.getEquipo() != null && !evento.getEquipo().equals(equipoUsuario)) {
                    continue; // Saltar este evento
                }
            }
            
            // Verificar eventos normales
            Calendar calEvento = Calendar.getInstance();
            calEvento.setTime(evento.getFechaInicio());
            
            if (calSeleccionada.get(Calendar.YEAR) == calEvento.get(Calendar.YEAR) &&
                calSeleccionada.get(Calendar.DAY_OF_YEAR) == calEvento.get(Calendar.DAY_OF_YEAR)) {
                eventosDelDia.add(evento);
            }
            
            // Verificar eventos recurrentes
            if (evento.isEsRecurrente() && evento.getFechaFinRecurrencia() != null) {
                Calendar calFinRecurrencia = Calendar.getInstance();
                calFinRecurrencia.setTime(evento.getFechaFinRecurrencia());
                
                // Solo procesar si la fecha seleccionada está dentro del rango de recurrencia
                if (!calSeleccionada.before(calEvento) && !calSeleccionada.after(calFinRecurrencia)) {
                    if (esEventoRecurrenteEnFecha(evento, calSeleccionada)) {
                        // Crear una copia del evento para esta fecha específica
                        Evento eventoRecurrente = new Evento(
                            evento.getTitulo(),
                            evento.getDescripcion(),
                            calSeleccionada.getTime(),
                            calSeleccionada.getTime(),
                            evento.getUbicacion(),
                            evento.getTipo(),
                            evento.getCreadorId(),
                            evento.getCreadorNombre(),
                            evento.isEsAdmin()
                        );
                        eventoRecurrente.setEsRecurrente(true);
                        eventoRecurrente.setFrecuencia(evento.getFrecuencia());
                        eventoRecurrente.setColorMarcador(evento.getColorMarcador());
                        eventoRecurrente.setEquipo(evento.getEquipo()); // Mantener el equipo
                        eventosDelDia.add(eventoRecurrente);
                    }
                }
            }
        }
        
        if (eventoAdapter != null) {
            eventoAdapter.actualizarEventos(eventosDelDia);
        }
        if (textViewEventosDia != null) {
            textViewEventosDia.setText("Eventos del " + dateFormat.format(fechaSeleccionada) +
                                 " (" + eventosDelDia.size() + ")");
        }
        // Mostrar mensaje si no hay eventos
        View root = getView() != null ? getView() : getActivity().findViewById(android.R.id.content);
        TextView tvSinEventos = root != null ? root.findViewById(R.id.tv_sin_eventos) : null;
        if (tvSinEventos != null) {
            tvSinEventos.setVisibility(eventosDelDia.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
    
    private boolean esEventoRecurrenteEnFecha(Evento evento, Calendar fechaSeleccionada) {
        Calendar calEvento = Calendar.getInstance();
        calEvento.setTime(evento.getFechaInicio());
        
        // Si es el mismo día, no es recurrencia
        if (calEvento.get(Calendar.YEAR) == fechaSeleccionada.get(Calendar.YEAR) &&
            calEvento.get(Calendar.DAY_OF_YEAR) == fechaSeleccionada.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }
        
        String frecuencia = evento.getFrecuencia();
        if (frecuencia == null) return false;
        
        switch (frecuencia) {
            case "DIARIA":
                return true; // Todos los días
                
            case "SEMANAL":
                // Mismo día de la semana
                return calEvento.get(Calendar.DAY_OF_WEEK) == fechaSeleccionada.get(Calendar.DAY_OF_WEEK);
                
            case "MENSUAL":
                // Mismo día del mes
                return calEvento.get(Calendar.DAY_OF_MONTH) == fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
                
            default:
                return false;
        }
    }

    private void configurarListeners() {
        buttonAgregarEvento.setOnClickListener(v -> mostrarDialogoEvento(null));
    }

    private void mostrarDialogoEvento(Evento eventoExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_evento, null);
        
        EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextUbicacion = dialogView.findViewById(R.id.editTextUbicacion);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerFrecuencia = dialogView.findViewById(R.id.spinnerFrecuencia);
        Spinner spinnerColorMarcador = dialogView.findViewById(R.id.spinnerColorMarcador);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        CheckBox checkBoxRecurrente = dialogView.findViewById(R.id.checkBoxRecurrente);
        LinearLayout layoutRecurrencia = dialogView.findViewById(R.id.layoutRecurrencia);
        Button buttonFecha = dialogView.findViewById(R.id.buttonFecha);
        Button buttonHora = dialogView.findViewById(R.id.buttonHora);
        Button buttonFechaFinRecurrencia = dialogView.findViewById(R.id.buttonFechaFinRecurrencia);
        
        // Configurar spinner de tipo
        String[] tipos = {"ENTRENAMIENTO", "PARTIDO", "EVENTO", "REUNION"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        
        // Configurar spinner de equipo/categoría
        List<String> opcionesEquipo = new ArrayList<>();
        opcionesEquipo.add("Todo el club"); // Opción para eventos globales
        
        // Agregar equipos disponibles
        List<Equipo> equipos = dataManager.getEquipos();
        for (Equipo equipo : equipos) {
            opcionesEquipo.add(equipo.getNombreCompleto());
        }
        
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, opcionesEquipo);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Configurar spinner de frecuencias
        String[] frecuencias = {"DIARIA", "SEMANAL", "MENSUAL"};
        ArrayAdapter<String> adapterFrecuencia = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, frecuencias);
        adapterFrecuencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrecuencia.setAdapter(adapterFrecuencia);
        
        // Configurar spinner de colores de marcador
        String[] colores = {"Dorado (#FFD700)", "Rojo (#FF0000)", "Verde (#4CAF50)", "Azul (#2196F3)", "Naranja (#FF9800)"};
        String[] valoresColores = {"#FFD700", "#FF0000", "#4CAF50", "#2196F3", "#FF9800"};
        ArrayAdapter<String> adapterColor = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, colores);
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColorMarcador.setAdapter(adapterColor);
        
        // Configurar checkbox de recurrencia
        checkBoxRecurrente.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutRecurrencia.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        
        // Variables para fecha y hora
        final Calendar cal = Calendar.getInstance();
        if (eventoExistente != null) {
            cal.setTime(eventoExistente.getFechaInicio());
            editTextTitulo.setText(eventoExistente.getTitulo());
            editTextDescripcion.setText(eventoExistente.getDescripcion());
            editTextUbicacion.setText(eventoExistente.getUbicacion());
            
            // Configurar recurrencia si existe
            if (eventoExistente.isEsRecurrente()) {
                checkBoxRecurrente.setChecked(true);
                layoutRecurrencia.setVisibility(View.VISIBLE);
                
                // Seleccionar frecuencia
                for (int i = 0; i < frecuencias.length; i++) {
                    if (frecuencias[i].equals(eventoExistente.getFrecuencia())) {
                        spinnerFrecuencia.setSelection(i);
                        break;
                    }
                }
                
                // Configurar fecha fin recurrencia
                if (eventoExistente.getFechaFinRecurrencia() != null) {
                    Calendar calFin = Calendar.getInstance();
                    calFin.setTime(eventoExistente.getFechaFinRecurrencia());
                    buttonFechaFinRecurrencia.setText(dateFormat.format(eventoExistente.getFechaFinRecurrencia()));
                }
            }
            
            // Seleccionar color de marcador
            for (int i = 0; i < valoresColores.length; i++) {
                if (valoresColores[i].equals(eventoExistente.getColorMarcador())) {
                    spinnerColorMarcador.setSelection(i);
                    break;
                }
            }
            
            // Seleccionar tipo en spinner
            for (int i = 0; i < tipos.length; i++) {
                if (tipos[i].equals(eventoExistente.getTipo())) {
                    spinnerTipo.setSelection(i);
                    break;
                }
            }
        } else {
            cal.setTime(fechaSeleccionada);
        }
        
        final Date[] fechaSeleccionada = {cal.getTime()};
        final Date[] fechaFinRecurrencia = {null};
        
        // Configurar botón de fecha
        buttonFecha.setText(dateFormat.format(fechaSeleccionada[0]));
        buttonFecha.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    cal.set(year, month, dayOfMonth);
                    fechaSeleccionada[0] = cal.getTime();
                    buttonFecha.setText(dateFormat.format(fechaSeleccionada[0]));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        // Configurar botón de fecha fin recurrencia
        buttonFechaFinRecurrencia.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar calFin = Calendar.getInstance();
                    calFin.set(year, month, dayOfMonth);
                    fechaFinRecurrencia[0] = calFin.getTime();
                    buttonFechaFinRecurrencia.setText(dateFormat.format(fechaFinRecurrencia[0]));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        // Configurar botón de hora
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        buttonHora.setText(timeFormat.format(fechaSeleccionada[0]));
        buttonHora.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    fechaSeleccionada[0] = cal.getTime();
                    buttonHora.setText(timeFormat.format(fechaSeleccionada[0]));
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });
        
        String tituloDialogo = eventoExistente != null ? "Editar Evento" : "Nuevo Evento";
        
        builder.setView(dialogView)
                .setTitle(tituloDialogo)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = editTextTitulo.getText().toString().trim();
                    String descripcion = editTextDescripcion.getText().toString().trim();
                    String ubicacion = editTextUbicacion.getText().toString().trim();
                    String tipo = spinnerTipo.getSelectedItem().toString();
                    String equipoSeleccionado = spinnerEquipo.getSelectedItem().toString();
                    boolean esRecurrente = checkBoxRecurrente.isChecked();
                    String frecuencia = esRecurrente ? spinnerFrecuencia.getSelectedItem().toString() : null;
                    String colorMarcador = valoresColores[spinnerColorMarcador.getSelectedItemPosition()];
                    
                    if (titulo.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (esRecurrente && fechaFinRecurrencia[0] == null) {
                        Toast.makeText(requireContext(), "Por favor selecciona una fecha de fin para eventos recurrentes", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (eventoExistente != null) {
                        actualizarEvento(eventoExistente, titulo, descripcion, ubicacion, tipo, equipoSeleccionado,
                                       fechaSeleccionada[0], esRecurrente, frecuencia, fechaFinRecurrencia[0], colorMarcador);
                    } else {
                        agregarEvento(titulo, descripcion, ubicacion, tipo, equipoSeleccionado, fechaSeleccionada[0], 
                                    esRecurrente, frecuencia, fechaFinRecurrencia[0], colorMarcador);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void agregarEvento(String titulo, String descripcion, String ubicacion, String tipo, String equipoSeleccionado, Date fecha,
                              boolean esRecurrente, String frecuencia, Date fechaFinRecurrencia, String colorMarcador) {
        if (usuarioActual == null) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Evento nuevoEvento = new Evento(
            titulo,
            descripcion,
            fecha,
            fecha,
            ubicacion,
            tipo,
            usuarioActual.getId(),
            usuarioActual.getNombre(),
            usuarioActual.isEsAdmin()
        );
        
        // Configurar campos de recurrencia
        nuevoEvento.setEsRecurrente(esRecurrente);
        nuevoEvento.setFrecuencia(frecuencia);
        nuevoEvento.setFechaFinRecurrencia(fechaFinRecurrencia);
        nuevoEvento.setColorMarcador(colorMarcador);
        nuevoEvento.setEquipo(equipoSeleccionado);

        dataManager.agregarEvento(nuevoEvento);
        cargarEventosDelDia();
        
        Toast.makeText(requireContext(), "Evento creado exitosamente", Toast.LENGTH_SHORT).show();
        
        // Crear notificación
        crearNotificacionEvento(nuevoEvento);
    }

    private void actualizarEvento(Evento evento, String titulo, String descripcion, String ubicacion, String tipo, String equipoSeleccionado,
                                 Date fecha, boolean esRecurrente, String frecuencia, Date fechaFinRecurrencia, String colorMarcador) {
        evento.setTitulo(titulo);
        evento.setDescripcion(descripcion);
        evento.setUbicacion(ubicacion);
        evento.setTipo(tipo);
        evento.setFechaInicio(fecha);
        evento.setFechaFin(fecha);
        evento.setEsRecurrente(esRecurrente);
        evento.setFrecuencia(frecuencia);
        evento.setFechaFinRecurrencia(fechaFinRecurrencia);
        evento.setColorMarcador(colorMarcador);
        evento.setEquipo(equipoSeleccionado);
        
        dataManager.actualizarEvento(evento);
        cargarEventosDelDia();
        
        Toast.makeText(requireContext(), "Evento actualizado exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditarClick(Evento evento) {
        mostrarDialogoEvento(evento);
    }

    @Override
    public void onEliminarClick(Evento evento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro de que quieres eliminar este evento?")
                .setPositiveButton("Sí, Eliminar", (dialog, which) -> {
                    dataManager.eliminarEvento(evento.getId());
                    cargarEventosDelDia();
                    Toast.makeText(requireContext(), "Evento eliminado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearNotificacionEvento(Evento evento) {
        dataManager.crearNotificacionEvento(evento);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEventosDelDia();
    }
} 