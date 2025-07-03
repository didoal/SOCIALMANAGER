package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.NotificacionAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class NotificacionesFragment extends Fragment implements NotificacionAdapter.OnNotificacionClickListener {
    private RecyclerView recyclerViewNotificaciones;
    private TextView textViewContador;
    private NotificacionAdapter notificacionAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarNotificaciones();
        actualizarContador();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewNotificaciones = view.findViewById(R.id.recyclerViewNotificaciones);
        textViewContador = view.findViewById(R.id.textViewContador);
    }

    private void configurarRecyclerView() {
        notificacionAdapter = new NotificacionAdapter(dataManager.getNotificaciones(), this);
        recyclerViewNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotificaciones.setAdapter(notificacionAdapter);
    }

    private void cargarNotificaciones() {
        List<Notificacion> notificaciones = dataManager.getNotificaciones();
        notificacionAdapter.actualizarNotificaciones(notificaciones);
    }

    private void actualizarContador() {
        int noLeidas = dataManager.getNotificacionesNoLeidas();
        textViewContador.setText(String.valueOf(noLeidas));
        
        if (noLeidas > 0) {
            textViewContador.setVisibility(View.VISIBLE);
        } else {
            textViewContador.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNotificacionClick(Notificacion notificacion) {
        // Marcar como leída
        if (!notificacion.isLeida()) {
            dataManager.marcarNotificacionComoLeida(notificacion.getId());
            cargarNotificaciones();
            actualizarContador();
        }
        
        // Mostrar detalles de la notificación
        mostrarDetallesNotificacion(notificacion);
    }

    private void mostrarDetallesNotificacion(Notificacion notificacion) {
        String mensaje = "Título: " + notificacion.getTitulo() + "\n\n" +
                        "Mensaje: " + notificacion.getMensaje() + "\n\n" +
                        "Tipo: " + notificacion.getTipo() + "\n" +
                        "De: " + notificacion.getRemitenteNombre();
        
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarNotificaciones();
        actualizarContador();
    }
} 