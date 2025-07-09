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
    
    private RecyclerView recyclerViewEventos;
    private EventoAdapter eventoAdapter;
    private DataManager dataManager;
    private TextView textViewEstadisticas;
    private FloatingActionButton fabAgregarEvento;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_eventos, container, false);
        
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        configurarRecyclerView();
        cargarEventos();
        actualizarEstadisticas();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        fabAgregarEvento = view.findViewById(R.id.fabAgregarEvento);
        
        fabAgregarEvento.setOnClickListener(v -> mostrarDialogoCrearEvento());
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

    private void cargarEventos() {
        List<Evento> eventos = dataManager.getEventos();
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
        for (String equipo : dataManager.getNombresEquipos()) {
            equipos.add(equipo);
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
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

    @Override
    public void onResume() {
        super.onResume();
        cargarEventos();
        actualizarEstadisticas();
    }
} 