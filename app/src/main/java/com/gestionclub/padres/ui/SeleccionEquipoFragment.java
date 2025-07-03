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
import com.gestionclub.padres.adapter.EquipoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class SeleccionEquipoFragment extends Fragment implements EquipoAdapter.OnEquipoClickListener {
    private RecyclerView recyclerViewEquipos;
    private TextView textViewTitulo;
    private EquipoAdapter equipoAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seleccion_equipo, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarEquipos();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewEquipos = view.findViewById(R.id.recyclerViewEquipos);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);
    }

    private void configurarRecyclerView() {
        equipoAdapter = new EquipoAdapter(dataManager.getEquipos(), this);
        recyclerViewEquipos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEquipos.setAdapter(equipoAdapter);
    }

    private void cargarEquipos() {
        List<Equipo> equipos = dataManager.getEquipos();
        equipoAdapter.actualizarEquipos(equipos);
        
        String equipoSeleccionado = dataManager.getEquipoSeleccionado();
        if (equipoSeleccionado != null) {
            Equipo equipo = dataManager.getEquipoPorId(equipoSeleccionado);
            if (equipo != null) {
                textViewTitulo.setText("Equipo seleccionado: " + equipo.getNombre());
            }
        } else {
            textViewTitulo.setText("Selecciona un equipo para gestionar");
        }
    }

    @Override
    public void onEquipoClick(Equipo equipo) {
        dataManager.setEquipoSeleccionado(equipo.getId());
        textViewTitulo.setText("Equipo seleccionado: " + equipo.getNombre());
        
        // Navegar al fragmento de estadísticas con el equipo seleccionado
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mostrarFragmento(new com.gestionclub.admin.ui.EstadisticasFragment());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEquipos();
    }
} 