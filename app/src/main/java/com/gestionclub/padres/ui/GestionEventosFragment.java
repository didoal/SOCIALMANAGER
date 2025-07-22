package com.gestionclub.padres.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.GestionEventosAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GestionEventosFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewTotalEventos;
    private TextView textViewEventosActivos;
    private RecyclerView recyclerViewEventos;
    private GestionEventosAdapter eventosAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_eventos, container, false);
        try {
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        inicializarVistas(view);
            configurarBotones(view);
        cargarEventos();
        actualizarEstadisticas();
        } catch (Exception e) {
            Log.e("GestionEventosFragment", "Error al cargar gestión de eventos", e);
            Toast.makeText(requireContext(), "Error al cargar la gestión de eventos", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        textViewTotalEventos = view.findViewById(R.id.textViewTotalEventos);
        textViewEventosActivos = view.findViewById(R.id.textViewEventosActivos);
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
    }

    private void configurarBotones(View view) {


        // Configurar botón de agregar evento
        View btnAddEvent = view.findViewById(R.id.btnAddEvent);
        if (btnAddEvent != null) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                btnAddEvent.setVisibility(View.VISIBLE);
                btnAddEvent.setOnClickListener(v -> {
                    mostrarDialogoCrearEvento();
                });
            } else {
                btnAddEvent.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarDialogoCrearEvento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_evento, null);
        builder.setView(dialogView);

        // Obtener referencias a los elementos del diálogo
        TextInputEditText editTextTitulo = dialogView.findViewById(R.id.editTextTitulo);
        TextInputEditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        TextInputEditText editTextLugar = dialogView.findViewById(R.id.editTextLugar);
        TextView textViewFecha = dialogView.findViewById(R.id.textViewFecha);
        TextView textViewHora = dialogView.findViewById(R.id.textViewHora);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipo);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        Spinner spinnerRecurrencia = dialogView.findViewById(R.id.spinnerRecurrencia);

        // Configurar spinners
        configurarSpinners(spinnerTipo, spinnerEquipo, spinnerRecurrencia);

        // Configurar selección de fecha
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("es"));
        
        textViewFecha.setText(dateFormat.format(calendar.getTime()));
        textViewHora.setText(timeFormat.format(calendar.getTime()));

        textViewFecha.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
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

        // Configurar botones del diálogo
        builder.setPositiveButton("Crear", (dialog, which) -> {
            crearEvento(editTextTitulo, editTextDescripcion, editTextLugar, 
                       textViewFecha, textViewHora, spinnerTipo, spinnerEquipo, spinnerRecurrencia);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void configurarSpinners(Spinner spinnerTipo, Spinner spinnerEquipo, Spinner spinnerRecurrencia) {
        // Configurar spinner de tipo de evento
        String[] tiposEvento = {"Partido", "Entrenamiento", "Reunión", "Evento"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, tiposEvento);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        // Configurar spinner de equipos
        List<Equipo> equipos = dataManager.getEquipos();
        String[] nombresEquipos = new String[equipos.size()];
        for (int i = 0; i < equipos.size(); i++) {
            nombresEquipos[i] = equipos.get(i).getNombre();
        }
        ArrayAdapter<String> adapterEquipo = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, nombresEquipos);
        adapterEquipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(adapterEquipo);

        // Configurar spinner de recurrencia
        String[] recurrencias = {"Sin recurrencia", "Diaria", "Semanal", "Mensual"};
        ArrayAdapter<String> adapterRecurrencia = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, recurrencias);
        adapterRecurrencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecurrencia.setAdapter(adapterRecurrencia);
    }

    private void crearEvento(TextInputEditText editTextTitulo, TextInputEditText editTextDescripcion,
                           TextInputEditText editTextLugar, TextView textViewFecha, TextView textViewHora,
                           Spinner spinnerTipo, Spinner spinnerEquipo, Spinner spinnerRecurrencia) {
        
        try {
            String titulo = editTextTitulo.getText().toString().trim();
            String descripcion = editTextDescripcion.getText().toString().trim();
            String ubicacion = editTextLugar.getText().toString().trim();
            String fechaStr = textViewFecha.getText().toString();
            String horaStr = textViewHora.getText().toString();
            String tipo = spinnerTipo.getSelectedItem().toString();
            String equipoNombre = spinnerEquipo.getSelectedItem().toString();

            // Validar campos obligatorios
            if (titulo.isEmpty()) {
                Toast.makeText(requireContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear fecha combinando fecha y hora
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("es"));
            Date fechaInicio = dateTimeFormat.parse(fechaStr + " " + horaStr);
            
            if (fechaInicio == null) {
                Toast.makeText(requireContext(), "Error al procesar la fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener equipo seleccionado
            List<Equipo> equipos = dataManager.getEquipos();
            Equipo equipoSeleccionado = null;
            for (Equipo equipo : equipos) {
                if (equipo.getNombre().equals(equipoNombre)) {
                    equipoSeleccionado = equipo;
                    break;
                }
            }

            // Crear el evento
            Evento nuevoEvento = new Evento();
            nuevoEvento.setTitulo(titulo);
            nuevoEvento.setDescripcion(descripcion);
            nuevoEvento.setUbicacion(ubicacion);
            nuevoEvento.setFechaInicio(fechaInicio);
            nuevoEvento.setFechaFin(fechaInicio); // Por defecto, mismo día
            nuevoEvento.setTipo(tipo);
            nuevoEvento.setEquipoId(equipoSeleccionado != null ? equipoSeleccionado.getId() : "");
            nuevoEvento.setCreadorId(usuarioActual.getId());
            nuevoEvento.setCreadorNombre(usuarioActual.getNombre());
            nuevoEvento.setEsAdmin(usuarioActual.isEsAdmin());

            // Guardar el evento
            dataManager.agregarEvento(nuevoEvento);

            // Crear notificaciones de confirmación de asistencia si hay equipo seleccionado
            if (equipoSeleccionado != null) {
                crearNotificacionesConfirmacionAsistencia(nuevoEvento, equipoSeleccionado);
            }

            Toast.makeText(requireContext(), "Evento creado exitosamente", Toast.LENGTH_SHORT).show();
            
            // Actualizar la vista
            cargarEventos();
            actualizarEstadisticas();

        } catch (Exception e) {
            Log.e("GestionEventosFragment", "Error al crear evento", e);
            Toast.makeText(requireContext(), "Error al crear el evento", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEventos() {
        try {
            List<Evento> eventos = dataManager.getEventos();
            
            // Ordenar eventos por fecha más reciente primero
            eventos.sort((e1, e2) -> {
                if (e1.getFechaInicio() == null && e2.getFechaInicio() == null) return 0;
                if (e1.getFechaInicio() == null) return 1;
                if (e2.getFechaInicio() == null) return -1;
                return e2.getFechaInicio().compareTo(e1.getFechaInicio()); // Orden descendente (más reciente primero)
            });
            
            // Configurar RecyclerView
            if (recyclerViewEventos != null) {
                recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
                
                eventosAdapter = new GestionEventosAdapter(requireContext(), eventos, new GestionEventosAdapter.OnEventoActionListener() {
                    @Override
                    public void onEventoEditado(Evento evento) {
                        // TODO: Implementar edición de evento
                        Toast.makeText(requireContext(), "Editar evento: " + evento.getTitulo(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEventoEliminado(Evento evento) {
                        // El evento ya fue eliminado en el adaptador, solo actualizar la vista
                        cargarEventos();
                        actualizarEstadisticas();
                    }
                });
                
                recyclerViewEventos.setAdapter(eventosAdapter);
            }
            
            Log.d("GestionEventosFragment", "Cargando " + eventos.size() + " eventos ordenados por fecha");
        } catch (Exception e) {
            Log.e("GestionEventosFragment", "Error al cargar eventos", e);
        }
    }

    private void actualizarEstadisticas() {
        try {
            List<Evento> eventos = dataManager.getEventos();
            
            // Actualizar total de eventos
            if (textViewTotalEventos != null) {
                textViewTotalEventos.setText(String.valueOf(eventos.size()));
            }
            
            // Calcular eventos activos (eventos futuros)
            int eventosActivos = 0;
            java.util.Date ahora = new java.util.Date();
            for (Evento evento : eventos) {
                if (evento.getFechaInicio().after(ahora)) {
                    eventosActivos++;
                }
            }
            
            if (textViewEventosActivos != null) {
                textViewEventosActivos.setText(String.valueOf(eventosActivos));
            }
            
        } catch (Exception e) {
            Log.e("GestionEventosFragment", "Error al actualizar estadísticas", e);
        }
    }

    private void crearNotificacionesConfirmacionAsistencia(Evento evento, Equipo equipo) {
        try {
            // Obtener todos los usuarios (por ahora, crearemos notificaciones para todos)
            List<Usuario> todosUsuarios = dataManager.getUsuarios();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("es"));
            String fechaEvento = dateFormat.format(evento.getFechaInicio());
            
            for (Usuario usuario : todosUsuarios) {
                // Crear notificación de confirmación de asistencia
                Notificacion notificacion = new Notificacion(
                    "Confirmación de Asistencia",
                    "¿Confirmas tu asistencia al evento '" + evento.getTitulo() + "' el " + fechaEvento + "?",
                    "SOLICITUD",
                    usuario.getId(),
                    usuarioActual.getId(),
                    usuarioActual.getNombre(),
                    usuarioActual.isEsAdmin()
                );
                
                // Configurar campos adicionales
                notificacion.setPrioridad("ALTA");
                notificacion.setEquipoDestinatario(equipo.getNombre());
                notificacion.setEstado("PENDIENTE");
                notificacion.setCreador(usuarioActual.getNombre());
                
                // Guardar la notificación
                dataManager.agregarNotificacion(notificacion);
            }
            
            Log.d("GestionEventosFragment", "Creadas " + todosUsuarios.size() + " notificaciones de confirmación");
            
        } catch (Exception e) {
            Log.e("GestionEventosFragment", "Error al crear notificaciones de confirmación", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEventos();
        actualizarEstadisticas();
        
        // Si el adaptador ya existe, actualizar la lista
        if (eventosAdapter != null) {
            List<Evento> eventos = dataManager.getEventos();
            eventosAdapter.actualizarEventos(eventos);
        }
    }
} 