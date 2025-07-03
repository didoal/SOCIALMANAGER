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
import android.widget.EditText;
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
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        fechaSeleccionada = new Date();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        inicializarVistas(view);
        configurarCalendario();
        configurarRecyclerView();
        cargarEventosDelDia();
        configurarListeners();
        
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
    }

    private void cargarEventosDelDia() {
        List<Evento> todosEventos = dataManager.getEventos();
        List<Evento> eventosDelDia = new ArrayList<>();
        
        Calendar calSeleccionada = Calendar.getInstance();
        calSeleccionada.setTime(fechaSeleccionada);
        
        for (Evento evento : todosEventos) {
            Calendar calEvento = Calendar.getInstance();
            calEvento.setTime(evento.getFechaInicio());
            
            if (calSeleccionada.get(Calendar.YEAR) == calEvento.get(Calendar.YEAR) &&
                calSeleccionada.get(Calendar.DAY_OF_YEAR) == calEvento.get(Calendar.DAY_OF_YEAR)) {
                eventosDelDia.add(evento);
            }
        }
        
        eventoAdapter.actualizarEventos(eventosDelDia);
        textViewEventosDia.setText("Eventos del " + dateFormat.format(fechaSeleccionada) + 
                                 " (" + eventosDelDia.size() + ")");
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
        Button buttonFecha = dialogView.findViewById(R.id.buttonFecha);
        Button buttonHora = dialogView.findViewById(R.id.buttonHora);
        
        // Configurar spinner de tipos
        String[] tipos = {"ENTRENAMIENTO", "PARTIDO", "EVENTO", "REUNION"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);
        
        // Variables para fecha y hora
        final Calendar cal = Calendar.getInstance();
        if (eventoExistente != null) {
            cal.setTime(eventoExistente.getFechaInicio());
            editTextTitulo.setText(eventoExistente.getTitulo());
            editTextDescripcion.setText(eventoExistente.getDescripcion());
            editTextUbicacion.setText(eventoExistente.getUbicacion());
            
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
                    
                    if (titulo.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (eventoExistente != null) {
                        actualizarEvento(eventoExistente, titulo, descripcion, ubicacion, tipo, fechaSeleccionada[0]);
                    } else {
                        agregarEvento(titulo, descripcion, ubicacion, tipo, fechaSeleccionada[0]);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void agregarEvento(String titulo, String descripcion, String ubicacion, String tipo, Date fecha) {
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

        dataManager.agregarEvento(nuevoEvento);
        cargarEventosDelDia();
        
        Toast.makeText(requireContext(), "Evento creado exitosamente", Toast.LENGTH_SHORT).show();
        
        // Crear notificación
        crearNotificacionEvento(nuevoEvento);
    }

    private void actualizarEvento(Evento evento, String titulo, String descripcion, String ubicacion, String tipo, Date fecha) {
        evento.setTitulo(titulo);
        evento.setDescripcion(descripcion);
        evento.setUbicacion(ubicacion);
        evento.setTipo(tipo);
        evento.setFechaInicio(fecha);
        evento.setFechaFin(fecha);
        
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