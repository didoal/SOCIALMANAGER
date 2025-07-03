package com.gestionclub.admin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.JugadorEstadisticasAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasFragment extends Fragment {
    private RecyclerView recyclerViewJugadores;
    private TextView textViewTitulo;
    private LinearLayout contenedorEstadisticas;
    private JugadorEstadisticasAdapter jugadorAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarEstadisticas();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewJugadores = view.findViewById(R.id.recyclerViewJugadores);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);
        contenedorEstadisticas = view.findViewById(R.id.contenedorEstadisticas);
    }

    private void configurarRecyclerView() {
        jugadorAdapter = new JugadorEstadisticasAdapter(new ArrayList<>());
        recyclerViewJugadores.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewJugadores.setAdapter(jugadorAdapter);
    }

    private void cargarEstadisticas() {
        if (usuarioActual == null) return;

        List<Usuario> jugadores;
        String titulo;

        if (usuarioActual.isEsAdmin()) {
            // Para administradores, usar datos según equipo seleccionado
            jugadores = dataManager.getUsuariosSegunRol(usuarioActual);
            String equipoSeleccionado = dataManager.getEquipoSeleccionado();
            if (equipoSeleccionado != null) {
                com.gestionclub.padres.model.Equipo equipo = dataManager.getEquipoPorId(equipoSeleccionado);
                titulo = "Estadísticas - " + (equipo != null ? equipo.getNombre() : "Equipo");
            } else {
                titulo = "Estadísticas - Todos los Equipos";
            }
        } else {
            // Para entrenadores y otros roles, usar solo su equipo
            jugadores = dataManager.getUsuariosSegunRol(usuarioActual);
            titulo = "Estadísticas - " + usuarioActual.getEquipoNombre();
        }

        // Filtrar solo jugadores
        List<Usuario> jugadoresFiltrados = new ArrayList<>();
        for (Usuario usuario : jugadores) {
            if (usuario.isEsJugador()) {
                jugadoresFiltrados.add(usuario);
            }
        }

        jugadorAdapter.actualizarJugadores(jugadoresFiltrados);
        textViewTitulo.setText(titulo);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEstadisticas();
    }
}