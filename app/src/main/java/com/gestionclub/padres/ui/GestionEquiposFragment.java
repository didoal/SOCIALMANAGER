package com.gestionclub.padres.ui;

import android.app.AlertDialog;
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
import com.gestionclub.padres.adapter.EquipoAdapter;
import com.gestionclub.padres.adapter.JugadorEquipoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GestionEquiposFragment extends Fragment {
    private static final String TAG = "GestionEquiposFragment";
    
    private RecyclerView recyclerViewEquipos;
    private EquipoAdapter equipoAdapter;
    private DataManager dataManager;
    private TextView textViewEstadisticas;
    private FloatingActionButton fabAgregarEquipo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creando vista de gestión de equipos");
        View view = inflater.inflate(R.layout.fragment_gestion_equipos, container, false);
        
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        configurarRecyclerView();
        cargarEquipos();
        actualizarEstadisticas();
        
        return view;
    }

    private void inicializarVistas(View view) {
        Log.d(TAG, "inicializarVistas: Inicializando vistas");
        recyclerViewEquipos = view.findViewById(R.id.recyclerViewEquipos);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        fabAgregarEquipo = view.findViewById(R.id.fabAgregarEquipo);
        
        fabAgregarEquipo.setOnClickListener(v -> mostrarDialogoCrearEquipo());
    }

    private void configurarRecyclerView() {
        Log.d(TAG, "configurarRecyclerView: Configurando RecyclerView");
        recyclerViewEquipos.setLayoutManager(new LinearLayoutManager(requireContext()));
        equipoAdapter = new EquipoAdapter(new ArrayList<>(), 
            this::mostrarDialogoEliminarEquipo,
            this::mostrarDialogoEditarEquipo,
            this::mostrarDialogoGestionarJugadores);
        recyclerViewEquipos.setAdapter(equipoAdapter);
    }

    private void cargarEquipos() {
        Log.d(TAG, "cargarEquipos: Cargando lista de equipos");
        List<Equipo> equipos = dataManager.getEquipos();
        equipoAdapter.actualizarEquipos(equipos);
        Log.d(TAG, "cargarEquipos: " + equipos.size() + " equipos cargados");
    }

    private void actualizarEstadisticas() {
        Log.d(TAG, "actualizarEstadisticas: Actualizando estadísticas");
        List<Equipo> equipos = dataManager.getEquipos();
        int totalEquipos = equipos.size();
        int totalJugadores = 0;
        
        for (Equipo equipo : equipos) {
            if (equipo.getJugadoresIds() != null) {
                totalJugadores += equipo.getJugadoresIds().size();
            }
        }
        
        String estadisticas = String.format("Total Equipos: %d | Total Jugadores: %d", totalEquipos, totalJugadores);
        textViewEstadisticas.setText(estadisticas);
    }

    private void mostrarDialogoCrearEquipo() {
        Log.d(TAG, "mostrarDialogoCrearEquipo: Mostrando diálogo");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_equipo, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinnerCategoria);
        EditText editTextEntrenador = dialogView.findViewById(R.id.editTextEntrenador);
        
        // Configurar spinner de categorías
        String[] categorias = {"Biberones", "Prebenjamín", "Benjamín", "Alevín", "Infantil", "Cadete", "Juvenil"};
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);
        
        builder.setView(dialogView)
                .setTitle("Crear Nuevo Equipo")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String categoria = spinnerCategoria.getSelectedItem().toString();
                    String entrenador = editTextEntrenador.getText().toString().trim();
                    
                    if (validarDatos(nombre, categoria)) {
                        crearEquipo(nombre, categoria, entrenador);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatos(String nombre, String categoria) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre del equipo es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (categoria.isEmpty()) {
            Toast.makeText(requireContext(), "La categoría es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void crearEquipo(String nombre, String categoria, String entrenador) {
        Log.d(TAG, "crearEquipo: Creando equipo " + nombre);
        
        // Verificar si el equipo ya existe
        if (dataManager.existeEquipo(nombre)) {
            Toast.makeText(requireContext(), "Ya existe un equipo con ese nombre", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Equipo nuevoEquipo = new Equipo(nombre, categoria, entrenador);
        nuevoEquipo.setJugadoresIds(new ArrayList<>());
        
        dataManager.agregarEquipo(nuevoEquipo);
        cargarEquipos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Equipo creado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "crearEquipo: Equipo " + nombre + " creado correctamente");
    }

    private void mostrarDialogoEditarEquipo(Equipo equipo) {
        Log.d(TAG, "mostrarDialogoEditarEquipo: Mostrando diálogo para " + equipo.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_equipo, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinnerCategoria);
        EditText editTextEntrenador = dialogView.findViewById(R.id.editTextEntrenador);
        
        // Pre-llenar campos
        editTextNombre.setText(equipo.getNombre());
        editTextEntrenador.setText(equipo.getEntrenador());
        
        // Configurar spinner de categorías
        String[] categorias = {"Biberones", "Prebenjamín", "Benjamín", "Alevín", "Infantil", "Cadete", "Juvenil"};
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);
        
        // Seleccionar categoría actual
        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(equipo.getCategoria())) {
                spinnerCategoria.setSelection(i);
                break;
            }
        }
        
        builder.setView(dialogView)
                .setTitle("Editar Equipo")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String categoria = spinnerCategoria.getSelectedItem().toString();
                    String entrenador = editTextEntrenador.getText().toString().trim();
                    
                    if (validarDatosEdicion(nombre, categoria, equipo.getNombre())) {
                        editarEquipo(equipo, nombre, categoria, entrenador);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatosEdicion(String nombre, String categoria, String nombreOriginal) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre del equipo es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (categoria.isEmpty()) {
            Toast.makeText(requireContext(), "La categoría es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Verificar si el nombre ya existe (excepto para el equipo actual)
        if (!nombre.equalsIgnoreCase(nombreOriginal) && dataManager.existeEquipo(nombre)) {
            Toast.makeText(requireContext(), "Ya existe un equipo con ese nombre", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void editarEquipo(Equipo equipoOriginal, String nombre, String categoria, String entrenador) {
        Log.d(TAG, "editarEquipo: Editando equipo " + equipoOriginal.getNombre());
        
        // Actualizar datos del equipo
        equipoOriginal.setNombre(nombre);
        equipoOriginal.setCategoria(categoria);
        equipoOriginal.setEntrenador(entrenador);
        
        // Guardar cambios
        dataManager.actualizarEquipo(equipoOriginal);
        cargarEquipos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Equipo actualizado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "editarEquipo: Equipo " + nombre + " actualizado correctamente");
    }

    private void mostrarDialogoGestionarJugadores(Equipo equipo) {
        Log.d(TAG, "mostrarDialogoGestionarJugadores: Mostrando diálogo para " + equipo.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gestionar_jugadores, null);
        
        RecyclerView recyclerViewJugadoresDisponibles = dialogView.findViewById(R.id.recyclerViewJugadoresDisponibles);
        RecyclerView recyclerViewJugadoresEquipo = dialogView.findViewById(R.id.recyclerViewJugadoresEquipo);
        TextView textViewTitulo = dialogView.findViewById(R.id.textViewTitulo);
        
        textViewTitulo.setText("Gestionar Jugadores - " + equipo.getNombre());
        
        // Obtener todos los usuarios que son padres/tutores con jugadores
        List<Usuario> todosUsuarios = dataManager.getUsuarios();
        List<Usuario> jugadoresDisponibles = new ArrayList<>();
        List<Usuario> jugadoresEquipo = new ArrayList<>();
        
        for (Usuario usuario : todosUsuarios) {
            if (usuario.isEsPadre() && usuario.getJugador() != null && !usuario.getJugador().isEmpty()) {
                if (equipo.getJugadoresIds() != null && equipo.getJugadoresIds().contains(usuario.getId())) {
                    jugadoresEquipo.add(usuario);
                } else {
                    jugadoresDisponibles.add(usuario);
                }
            }
        }
        
        // Adaptadores como arreglos finales para referencia en lambdas
        final JugadorEquipoAdapter[] adapterDisponibles = new JugadorEquipoAdapter[1];
        final JugadorEquipoAdapter[] adapterEquipo = new JugadorEquipoAdapter[1];

        adapterDisponibles[0] = new JugadorEquipoAdapter(
            jugadoresDisponibles,
            jugador -> {
                jugadoresDisponibles.remove(jugador);
                jugadoresEquipo.add(jugador);
                adapterDisponibles[0].actualizarJugadores(jugadoresDisponibles);
                adapterEquipo[0].actualizarJugadores(jugadoresEquipo);
            },
            false // agregar
        );

        adapterEquipo[0] = new JugadorEquipoAdapter(
            jugadoresEquipo,
            jugador -> {
                jugadoresEquipo.remove(jugador);
                jugadoresDisponibles.add(jugador);
                adapterDisponibles[0].actualizarJugadores(jugadoresDisponibles);
                adapterEquipo[0].actualizarJugadores(jugadoresEquipo);
            },
            true // quitar
        );

        recyclerViewJugadoresDisponibles.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewJugadoresEquipo.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewJugadoresDisponibles.setAdapter(adapterDisponibles[0]);
        recyclerViewJugadoresEquipo.setAdapter(adapterEquipo[0]);
        
        builder.setView(dialogView)
                .setTitle("Gestionar Jugadores")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Guardar cambios en el equipo
                    guardarCambiosEquipo(equipo, jugadoresEquipo);
                    cargarEquipos();
                    actualizarEstadisticas();
                    Toast.makeText(requireContext(), "Jugadores actualizados", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarCambiosEquipo(Equipo equipo, List<Usuario> jugadoresEquipo) {
        // Actualizar la lista de IDs de jugadores en el equipo
        List<String> nuevosJugadoresIds = new ArrayList<>();
        for (Usuario jugador : jugadoresEquipo) {
            nuevosJugadoresIds.add(jugador.getId());
        }
        equipo.setJugadoresIds(nuevosJugadoresIds);
        
        // Guardar el equipo actualizado
        dataManager.actualizarEquipo(equipo);
    }

    private void mostrarDialogoEliminarEquipo(Equipo equipo) {
        Log.d(TAG, "mostrarDialogoEliminarEquipo: Mostrando diálogo para " + equipo.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Equipo")
                .setMessage("¿Estás seguro de que quieres eliminar el equipo '" + equipo.getNombre() + "'?\n\nEsto también eliminará todas las asociaciones con jugadores.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarEquipo(equipo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarEquipo(Equipo equipo) {
        Log.d(TAG, "eliminarEquipo: Eliminando equipo " + equipo.getNombre());
        
        dataManager.eliminarEquipo(equipo.getNombre());
        cargarEquipos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Equipo eliminado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "eliminarEquipo: Equipo " + equipo.getNombre() + " eliminado correctamente");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarEquipos();
        actualizarEstadisticas();
    }
} 