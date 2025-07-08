package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GestionEventosFragment extends Fragment {
    private static final String TAG = "GestionEventosFragment";
    
    private RecyclerView recyclerViewEventos;
    private EventoAdapter eventoAdapter;
    private DataManager dataManager;
    private TextView textViewEstadisticas;
    private FloatingActionButton fabAgregarEvento;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creando vista de gestión de eventos");
        View view = inflater.inflate(R.layout.fragment_gestion_eventos, container, false);
        
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        configurarRecyclerView();
        cargarEventos();
        actualizarEstadisticas();
        
        return view;
    }

    private void inicializarVistas(View view) {
        Log.d(TAG, "inicializarVistas: Inicializando vistas");
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        fabAgregarEvento = view.findViewById(R.id.fabAgregarEvento);
        
        fabAgregarEvento.setOnClickListener(v -> mostrarDialogoCrearEvento());
    }

    private void configurarRecyclerView() {
        Log.d(TAG, "configurarRecyclerView: Configurando RecyclerView");
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
        }, true); // mostrarAcciones = true
        recyclerViewEventos.setAdapter(eventoAdapter);
    }

    private void cargarEventos() {
        Log.d(TAG, "cargarEventos: Cargando lista de eventos");
        List<Evento> eventos = dataManager.getEventos();
        eventoAdapter.actualizarEventos(eventos);
        Log.d(TAG, "cargarEventos: " + eventos.size() + " eventos cargados");
    }

    private void actualizarEstadisticas() {
        Log.d(TAG, "actualizarEstadisticas: Actualizando estadísticas");
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
        Log.d(TAG, "mostrarDialogoCrearEvento: Mostrando diálogo");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_evento, null);
        
        EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextLugar = dialogView.findViewById(R.id.editTextLugar);
        TextView textViewFecha = dialogView.findViewById(R.id.textViewFecha);
        TextView textViewHora = dialogView.findViewById(R.id.textViewHora);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Configurar spinner de tipos
        String[] tipos = {"Entrenamiento", "Partido", "Torneo", "Reunión", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        
        // Configurar spinner de equipos
        List<String> equipos = new ArrayList<>();
        equipos.add("Todos los equipos");
        for (String equipo : dataManager.getNombresEquipos()) {
            equipos.add(equipo);
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
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
                    
                    if (validarDatos(titulo, descripcion, lugar)) {
                        crearEvento(titulo, descripcion, lugar, tipo, equipo, calendar.getTime());
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

    private void crearEvento(String titulo, String descripcion, String lugar, String tipo, String equipo, java.util.Date fecha) {
        Log.d(TAG, "crearEvento: Creando evento " + titulo);
        
        Evento nuevoEvento = new Evento(titulo, descripcion, fecha, fecha, lugar, tipo, 
                "admin1", "Administrador", true);
        nuevoEvento.setEquipo(equipo);
        
        dataManager.agregarEvento(nuevoEvento);
        
        // Crear notificación automática para el evento
        dataManager.crearNotificacionEvento(nuevoEvento);
        
        // Crear solicitudes de confirmación de asistencia para padres del equipo
        dataManager.crearSolicitudesConfirmacionAsistencia(nuevoEvento);
        
        cargarEventos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Evento creado exitosamente. Se han enviado notificaciones a los padres.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "crearEvento: Evento " + titulo + " creado correctamente con notificaciones");
    }

    private void mostrarDialogoEditarEvento(Evento evento) {
        Log.d(TAG, "mostrarDialogoEditarEvento: Mostrando diálogo para " + evento.getTitulo());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_evento, null);
        
        EditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextLugar = dialogView.findViewById(R.id.editTextLugar);
        TextView textViewFecha = dialogView.findViewById(R.id.textViewFecha);
        TextView textViewHora = dialogView.findViewById(R.id.textViewHora);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Pre-llenar campos con datos actuales
        editTextTitulo.setText(evento.getTitulo());
        editTextDescripcion.setText(evento.getDescripcion());
        editTextLugar.setText(evento.getUbicacion());
        
        // Configurar spinner de tipos
        String[] tipos = {"Entrenamiento", "Partido", "Torneo", "Reunión", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        
        // Seleccionar tipo actual
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equals(evento.getTipo())) {
                spinnerTipo.setSelection(i);
                break;
            }
        }
        
        // Configurar spinner de equipos
        List<String> equipos = new ArrayList<>();
        equipos.add("Todos los equipos");
        for (String equipo : dataManager.getNombresEquipos()) {
            equipos.add(equipo);
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Seleccionar equipo actual
        String equipoActual = evento.getEquipo();
        if (equipoActual != null && !equipoActual.isEmpty()) {
            for (int i = 0; i < equipos.size(); i++) {
                if (equipos.get(i).equals(equipoActual)) {
                    spinnerEquipo.setSelection(i);
                    break;
                }
            }
        } else {
            spinnerEquipo.setSelection(0); // "Todos los equipos"
        }
        
        // Configurar fecha y hora actuales
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(evento.getFechaInicio());
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
        Log.d(TAG, "editarEvento: Editando evento " + eventoOriginal.getTitulo());
        
        // Actualizar datos del evento
        eventoOriginal.setTitulo(titulo);
        eventoOriginal.setDescripcion(descripcion);
        eventoOriginal.setUbicacion(lugar);
        eventoOriginal.setTipo(tipo);
        eventoOriginal.setEquipo(equipo.equals("Todos los equipos") ? null : equipo);
        eventoOriginal.setFechaInicio(fecha);
        
        // Guardar cambios
        dataManager.actualizarEvento(eventoOriginal);
        cargarEventos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Evento actualizado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "editarEvento: Evento " + titulo + " actualizado correctamente");
    }

    private void mostrarDialogoEliminarEvento(Evento evento) {
        Log.d(TAG, "mostrarDialogoEliminarEvento: Mostrando diálogo para " + evento.getTitulo());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro de que quieres eliminar el evento '" + evento.getTitulo() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarEvento(evento);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarEvento(Evento evento) {
        Log.d(TAG, "eliminarEvento: Eliminando evento " + evento.getTitulo());
        
        dataManager.eliminarEvento(evento.getId());
        cargarEventos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Evento eliminado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "eliminarEvento: Evento " + evento.getTitulo() + " eliminado correctamente");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarEventos();
        actualizarEstadisticas();
    }
} 