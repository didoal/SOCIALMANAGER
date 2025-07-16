package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CalendarioListadoFragment extends Fragment {
    
    private RecyclerView recyclerViewEventos;
    private EventoAdapter eventoAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewNoEventos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_listado, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarEventos();
        
        return view;
    }
    
    private void inicializarVistas(View view) {
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
        textViewNoEventos = view.findViewById(R.id.textViewNoEventos);
    }
    
    private void configurarRecyclerView() {
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventoAdapter = new EventoAdapter(new ArrayList<>(), new EventoAdapter.OnEventoClickListener() {
            @Override
            public void onEditarClick(Evento evento) {
                // Solo permitir editar si es admin o entrenador
                if (usuarioActual != null && (usuarioActual.isEsAdmin() || "entrenador".equals(usuarioActual.getRol()))) {
                    // Aquí podrías abrir el diálogo de edición
                }
            }
            
            @Override
            public void onEliminarClick(Evento evento) {
                // Solo permitir eliminar si es admin
                if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                    // Aquí podrías mostrar el diálogo de confirmación
                }
            }
        }, false); // false para no mostrar botones de edición/eliminación por defecto
        recyclerViewEventos.setAdapter(eventoAdapter);
    }
    
    private void cargarEventos() {
        List<Evento> eventos = dataManager.getEventos();
        
        // Si es entrenador, filtrar solo eventos de su equipo
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            List<Evento> eventosFiltrados = new ArrayList<>();
            for (Evento evento : eventos) {
                if (usuarioActual.getEquipo().equals(evento.getEquipo()) || 
                    "Todos los equipos".equals(evento.getEquipo())) {
                    eventosFiltrados.add(evento);
                }
            }
            eventos = eventosFiltrados;
        }
        
        // Ordenar eventos por fecha
        Collections.sort(eventos, new Comparator<Evento>() {
            @Override
            public int compare(Evento e1, Evento e2) {
                return e1.getFechaInicio().compareTo(e2.getFechaInicio());
            }
        });
        
        eventoAdapter.actualizarEventos(eventos);
        
        if (eventos.isEmpty()) {
            textViewNoEventos.setVisibility(View.VISIBLE);
            recyclerViewEventos.setVisibility(View.GONE);
        } else {
            textViewNoEventos.setVisibility(View.GONE);
            recyclerViewEventos.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        cargarEventos(); // Recargar eventos cuando se vuelve a la pantalla
    }
} 