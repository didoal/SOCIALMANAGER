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
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import java.util.List;
import android.widget.LinearLayout;

public class DashboardFragment extends Fragment {
    private DataManager dataManager;
    private Usuario usuarioActual;

    // Vistas del nuevo layout
    private TextView textViewBienvenida;
    private TextView textViewUsuario;
    private RecyclerView recyclerViewEventos;
    private RecyclerView recyclerViewDestacados;
    private RecyclerView recyclerViewNotificaciones;
    private TextView textViewTotalEventosDashboard;
    private TextView textViewTotalUsuariosDashboard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        inicializarVistas(view);
        cargarDatosUsuario();
        cargarEventosProximos();
        cargarEstadisticasRapidas();

        // Acciones rápidas con IDs
        androidx.cardview.widget.CardView cardCrearEvento = view.findViewById(R.id.cardCrearEvento);
        androidx.cardview.widget.CardView cardNuevaPublicacion = view.findViewById(R.id.cardNuevaPublicacion);

        if (cardCrearEvento != null) {
            cardCrearEvento.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new GestionEventosFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
        if (cardNuevaPublicacion != null) {
            cardNuevaPublicacion.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MuroDestacadosFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        return view;
    }

    private void inicializarVistas(View view) {
        textViewBienvenida = view.findViewById(R.id.textViewBienvenida);
        textViewUsuario = view.findViewById(R.id.textViewUsuario);
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
        recyclerViewDestacados = view.findViewById(R.id.recyclerViewDestacados);
        textViewTotalEventosDashboard = view.findViewById(R.id.textViewTotalEventosDashboard);
        textViewTotalUsuariosDashboard = view.findViewById(R.id.textViewTotalUsuariosDashboard);
    }

    private void cargarDatosUsuario() {
        if (usuarioActual != null) {
            textViewUsuario.setText("Bienvenido, " + usuarioActual.getNombre());
        }
    }

    private void cargarEventosProximos() {
        List<Evento> eventos = dataManager.getEventos();
        // Aquí puedes filtrar los eventos próximos si lo deseas
        // Por simplicidad, se muestran todos
        if (recyclerViewEventos != null) {
            recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
            // recyclerViewEventos.setAdapter(new EventoAdapter(eventos, ...));
            // Implementa tu adaptador personalizado para mostrar los eventos
        }
    }

    private void cargarEstadisticasRapidas() {
        List<Evento> eventos = dataManager.getEventos();
        List<Usuario> usuarios = dataManager.getUsuarios();
        if (textViewTotalEventosDashboard != null) {
            textViewTotalEventosDashboard.setText(String.valueOf(eventos.size()));
        }
        if (textViewTotalUsuariosDashboard != null) {
            textViewTotalUsuariosDashboard.setText(String.valueOf(usuarios.size()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatosUsuario();
        cargarEventosProximos();
        cargarEstadisticasRapidas();
    }
} 