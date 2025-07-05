package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private LinearLayout layoutNoNotificaciones;
    private NotificacionAdapter notificacionAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarRecyclerView();
            cargarNotificaciones();
            actualizarContador();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "No se pudo cargar las notificaciones. Intenta más tarde.", Toast.LENGTH_LONG).show();
            if (view.findViewById(R.id.recyclerViewNotificaciones) != null) {
                view.findViewById(R.id.recyclerViewNotificaciones).setVisibility(View.GONE);
            }
            if (view.findViewById(R.id.layoutNoNotificaciones) != null) {
                view.findViewById(R.id.layoutNoNotificaciones).setVisibility(View.VISIBLE);
            }
            // Mensaje amigable
            TextView tvError = new TextView(requireContext());
            tvError.setText("No se pudo cargar las notificaciones. Intenta más tarde.");
            tvError.setTextColor(getResources().getColor(R.color.gray));
            tvError.setTextSize(16);
            tvError.setPadding(0, 16, 0, 16);
            ((ViewGroup) view).addView(tvError);
        }
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewNotificaciones = view.findViewById(R.id.recyclerViewNotificaciones);
        textViewContador = view.findViewById(R.id.textViewContador);
        layoutNoNotificaciones = view.findViewById(R.id.layoutNoNotificaciones);
    }

    private void configurarRecyclerView() {
        notificacionAdapter = new NotificacionAdapter(dataManager.getNotificaciones(), this);
        recyclerViewNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotificaciones.setAdapter(notificacionAdapter);
    }

    private void cargarNotificaciones() {
        List<Notificacion> notificaciones = dataManager.getNotificaciones();
        notificacionAdapter.actualizarNotificaciones(notificaciones);
        
        // Mostrar mensaje si no hay notificaciones
        if (notificaciones.isEmpty()) {
            recyclerViewNotificaciones.setVisibility(View.GONE);
            layoutNoNotificaciones.setVisibility(View.VISIBLE);
        } else {
            recyclerViewNotificaciones.setVisibility(View.VISIBLE);
            layoutNoNotificaciones.setVisibility(View.GONE);
        }
    }

    private void actualizarContador() {
        int noLeidas = dataManager.getNotificacionesNoLeidas();
        
        if (noLeidas > 0) {
            textViewContador.setText(String.valueOf(noLeidas));
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
                        "De: " + notificacion.getRemitenteNombre() + "\n" +
                        "Fecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(notificacion.getFechaCreacion());
        
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarNotificaciones();
        actualizarContador();
    }
} 