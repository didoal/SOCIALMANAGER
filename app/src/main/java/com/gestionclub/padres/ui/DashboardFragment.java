package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;

public class DashboardFragment extends Fragment {
    private DataManager dataManager;
    private Usuario usuarioActual;

    // Vistas del nuevo layout
    private TextView textViewNombreUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        inicializarVistas(view);
        cargarDatosUsuario();

        return view;
    }

    private void inicializarVistas(View view) {
        textViewNombreUsuario = view.findViewById(R.id.textViewNombreUsuario);
    }

    private void cargarDatosUsuario() {
        if (usuarioActual != null && textViewNombreUsuario != null) {
            textViewNombreUsuario.setText(usuarioActual.getNombre());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatosUsuario();
    }
} 