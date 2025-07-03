package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.JugadorEstadisticaAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class EstadisticasFragment extends Fragment {
    private RecyclerView recyclerViewJugadores;
    private JugadorEstadisticaAdapter adapter;
    private DataManager dataManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        dataManager = new DataManager(requireContext());
        recyclerViewJugadores = view.findViewById(R.id.recyclerViewJugadores);
        recyclerViewJugadores.setLayoutManager(new LinearLayoutManager(requireContext()));
        cargarEstadisticas();
        return view;
    }

    private void cargarEstadisticas() {
        List<Usuario> jugadores = dataManager.getUsuarios();
        List<Evento> eventos = dataManager.getEventos();
        List<Asistencia> asistencias = dataManager.getAsistencias();
        adapter = new JugadorEstadisticaAdapter(jugadores, dataManager, eventos, asistencias);
        recyclerViewJugadores.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEstadisticas();
    }
} 