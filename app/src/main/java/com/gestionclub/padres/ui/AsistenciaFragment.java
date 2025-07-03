package com.gestionclub.padres.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Confirmacion;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asistencia, container, false);
        LinearLayout contenedor = view.findViewById(R.id.contenedorAsistencia);
        SessionManager sessionManager = new SessionManager(requireContext());
        String userJson = sessionManager.getUser();
        Usuario usuario = new Gson().fromJson(userJson, Usuario.class);
        if (usuario == null) return view;
        List<Evento> eventos = cargarEventos();
        List<Confirmacion> confirmaciones = cargarConfirmaciones();
        if (eventos.isEmpty()) {
            TextView tvVacio = new TextView(getContext());
            tvVacio.setText("No hay eventos para confirmar asistencia");
            tvVacio.setTextColor(getResources().getColor(R.color.gray));
            tvVacio.setTextSize(16);
            tvVacio.setPadding(0, 16, 0, 16);
            contenedor.addView(tvVacio);
        } else {
            for (Evento evento : eventos) {
                TextView tvEvento = new TextView(getContext());
                tvEvento.setText(evento.getTitulo() + " | " + evento.getTipo());
                tvEvento.setTextColor(getResources().getColor(R.color.white));
                tvEvento.setTextSize(16);
                tvEvento.setPadding(0, 8, 0, 8);
                contenedor.addView(tvEvento);
                Button btnPresente = new Button(getContext());
                btnPresente.setText("Confirmar asistencia");
                Button btnAusente = new Button(getContext());
                btnAusente.setText("Rechazar asistencia");
                Confirmacion conf = buscarConfirmacion(confirmaciones, usuario.getUsername(), evento.getId());
                if (conf != null && conf.isPresente()) {
                    btnPresente.setEnabled(false);
                    btnPresente.setText("Asistencia confirmada");
                } else if (conf != null) {
                    btnAusente.setEnabled(false);
                    btnAusente.setText("Ausencia confirmada");
                }
                btnPresente.setOnClickListener(v -> {
                    guardarConfirmacion(usuario.getUsername(), evento.getId(), true);
                    Toast.makeText(getContext(), "Asistencia confirmada", Toast.LENGTH_SHORT).show();
                    btnPresente.setEnabled(false);
                    btnPresente.setText("Asistencia confirmada");
                    btnAusente.setEnabled(true);
                    btnAusente.setText("Rechazar asistencia");
                });
                btnAusente.setOnClickListener(v -> {
                    guardarConfirmacion(usuario.getUsername(), evento.getId(), false);
                    Toast.makeText(getContext(), "Ausencia confirmada", Toast.LENGTH_SHORT).show();
                    btnAusente.setEnabled(false);
                    btnAusente.setText("Ausencia confirmada");
                    btnPresente.setEnabled(true);
                    btnPresente.setText("Confirmar asistencia");
                });
                LinearLayout botones = new LinearLayout(getContext());
                botones.setOrientation(LinearLayout.HORIZONTAL);
                botones.addView(btnPresente);
                botones.addView(btnAusente);
                contenedor.addView(botones);
            }
        }
        return view;
    }

    private List<Evento> cargarEventos() {
        String json = requireContext().getSharedPreferences("eventos", 0).getString("eventos", "[]");
        return new Gson().fromJson(json, new TypeToken<ArrayList<Evento>>(){}.getType());
    }

    private List<Confirmacion> cargarConfirmaciones() {
        String json = requireContext().getSharedPreferences("confirmaciones", 0).getString("confirmaciones", "[]");
        return new Gson().fromJson(json, new TypeToken<ArrayList<Confirmacion>>(){}.getType());
    }

    private Confirmacion buscarConfirmacion(List<Confirmacion> lista, String usuarioId, String eventoId) {
        for (Confirmacion c : lista) {
            if (c.getUsuarioId().equals(usuarioId) && c.getEventoId().equals(eventoId)) {
                return c;
            }
        }
        return null;
    }

    private void guardarConfirmacion(String usuarioId, String eventoId, boolean presente) {
        List<Confirmacion> lista = cargarConfirmaciones();
        boolean found = false;
        for (Confirmacion c : lista) {
            if (c.getUsuarioId().equals(usuarioId) && c.getEventoId().equals(eventoId)) {
                c.setPresente(presente);
                found = true;
                break;
            }
        }
        if (!found) {
            lista.add(new Confirmacion(usuarioId, eventoId, presente));
        }
        String json = new Gson().toJson(lista);
        requireContext().getSharedPreferences("confirmaciones", Context.MODE_PRIVATE).edit().putString("confirmaciones", json).apply();
    }
} 