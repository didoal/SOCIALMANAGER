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
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
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
        // Configurar botón de retroceso
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

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
        // Aquí se mostraría un diálogo para crear un nuevo equipo
        Toast.makeText(requireContext(), "Función de crear equipo próximamente", Toast.LENGTH_SHORT).show();
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