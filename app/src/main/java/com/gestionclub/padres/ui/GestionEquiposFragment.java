package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class GestionEquiposFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewTotalEquipos;
    private TextView textViewTotalJugadores;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_equipos, container, false);
        try {
        dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
        inicializarVistas(view);
            configurarBotones(view);
        cargarEquipos();
        actualizarEstadisticas();
        } catch (Exception e) {
            Log.e("GestionEquiposFragment", "Error al cargar gestión de equipos", e);
            Toast.makeText(requireContext(), "Error al cargar la gestión de equipos", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        textViewTotalEquipos = view.findViewById(R.id.textViewTotalEquipos);
        textViewTotalJugadores = view.findViewById(R.id.textViewTotalJugadores);
    }

    private void configurarBotones(View view) {


        // Configurar botón de agregar equipo
        View btnAddTeam = view.findViewById(R.id.btnAddTeam);
        if (btnAddTeam != null) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                btnAddTeam.setVisibility(View.VISIBLE);
                btnAddTeam.setOnClickListener(v -> {
                    mostrarDialogoCrearEquipo();
                });
        } else {
                btnAddTeam.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarDialogoCrearEquipo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_equipo, null);
        builder.setView(dialogView);

        // Obtener referencias a los elementos del diálogo
        TextInputEditText editTextNombreEquipo = dialogView.findViewById(R.id.editTextNombreEquipo);
        TextInputEditText editTextCategoria = dialogView.findViewById(R.id.editTextCategoria);
        TextInputEditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        TextInputEditText editTextEntrenador = dialogView.findViewById(R.id.editTextEntrenador);
        TextInputEditText editTextColor = dialogView.findViewById(R.id.editTextColor);

        // Configurar botones del diálogo
        builder.setPositiveButton("Crear", (dialog, which) -> {
            crearEquipo(editTextNombreEquipo, editTextCategoria, editTextDescripcion, 
                       editTextEntrenador, editTextColor);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void crearEquipo(TextInputEditText editTextNombreEquipo, TextInputEditText editTextCategoria,
                           TextInputEditText editTextDescripcion, TextInputEditText editTextEntrenador,
                           TextInputEditText editTextColor) {
        
        try {
            String nombre = editTextNombreEquipo.getText().toString().trim();
            String categoria = editTextCategoria.getText().toString().trim();
            String descripcion = editTextDescripcion.getText().toString().trim();
            String entrenador = editTextEntrenador.getText().toString().trim();
            String color = editTextColor.getText().toString().trim();

            // Validar campos obligatorios
            if (nombre.isEmpty()) {
                Toast.makeText(requireContext(), "El nombre del equipo es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (categoria.isEmpty()) {
                Toast.makeText(requireContext(), "La categoría es obligatoria", Toast.LENGTH_SHORT).show();
                return;
            }

            if (entrenador.isEmpty()) {
                Toast.makeText(requireContext(), "El entrenador es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el equipo usando el constructor disponible
            Equipo nuevoEquipo = new Equipo(nombre, categoria, entrenador);

            // Guardar el equipo
            dataManager.agregarEquipo(nuevoEquipo);

            Toast.makeText(requireContext(), "Equipo creado exitosamente", Toast.LENGTH_SHORT).show();
            
            // Actualizar la vista
            cargarEquipos();
            actualizarEstadisticas();

        } catch (Exception e) {
            Log.e("GestionEquiposFragment", "Error al crear equipo", e);
            Toast.makeText(requireContext(), "Error al crear el equipo", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEquipos() {
        try {
        List<Equipo> equipos = dataManager.getEquipos();
            // Aquí se cargarían los equipos en el layout
            // Por ahora, los equipos están hardcodeados en el layout XML
            
            Log.d("GestionEquiposFragment", "Cargando " + equipos.size() + " equipos");
        } catch (Exception e) {
            Log.e("GestionEquiposFragment", "Error al cargar equipos", e);
        }
    }

    private void actualizarEstadisticas() {
        try {
        List<Equipo> equipos = dataManager.getEquipos();
        
            // Actualizar total de equipos
            if (textViewTotalEquipos != null) {
                textViewTotalEquipos.setText(String.valueOf(equipos.size()));
            }
            
            // Calcular total de jugadores
        int totalJugadores = 0;
            for (Equipo equipo : equipos) {
                totalJugadores += equipo.getJugadoresIds().size();
        }
        
        if (textViewTotalJugadores != null) {
            textViewTotalJugadores.setText(String.valueOf(totalJugadores));
        }
        
        } catch (Exception e) {
            Log.e("GestionEquiposFragment", "Error al actualizar estadísticas", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEquipos();
        actualizarEstadisticas();
    }
} 