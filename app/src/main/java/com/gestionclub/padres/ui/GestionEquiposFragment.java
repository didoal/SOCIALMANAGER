package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.EquipoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
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
        equipoAdapter = new EquipoAdapter(new ArrayList<>(), this::mostrarDialogoEliminarEquipo);
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
            // totalJugadores += equipo.getJugadores().size(); // Revisar si equipo tiene lista de jugadores
        }
        
        String estadisticas = String.format("Total Equipos: %d | Total Jugadores: %d", totalEquipos, totalJugadores);
        textViewEstadisticas.setText(estadisticas);
    }

    private void mostrarDialogoCrearEquipo() {
        Log.d(TAG, "mostrarDialogoCrearEquipo: Mostrando diálogo");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_equipo, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextCategoria = dialogView.findViewById(R.id.editTextCategoria);
        EditText editTextEntrenador = dialogView.findViewById(R.id.editTextEntrenador);
        
        builder.setView(dialogView)
                .setTitle("Crear Nuevo Equipo")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String categoria = editTextCategoria.getText().toString().trim();
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

    @Override
    public void onMarcarLeidaClick(Notificacion notificacion) {
        // tu lógica aquí
    }
} 