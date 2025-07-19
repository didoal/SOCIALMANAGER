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
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class GestionEventosFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewTotalEventos;
    private TextView textViewEventosActivos;

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
    }

    private void configurarBotones(View view) {
        // Configurar botón de retroceso
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

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
        // Aquí se mostraría un diálogo para crear un nuevo evento
        Toast.makeText(requireContext(), "Función de crear evento próximamente", Toast.LENGTH_SHORT).show();
    }

    private void cargarEventos() {
        try {
            List<Evento> eventos = dataManager.getEventos();
            // Aquí se cargarían los eventos en el layout
            // Por ahora, los eventos están hardcodeados en el layout XML
            
            Log.d("GestionEventosFragment", "Cargando " + eventos.size() + " eventos");
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

    @Override
    public void onResume() {
        super.onResume();
        cargarEventos();
        actualizarEstadisticas();
    }
} 