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
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AsistenciaFragment extends Fragment {
    private DataManager dataManager;
    private LinearLayout contenedorJugadores;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asistencia, container, false);
        dataManager = new DataManager(requireContext());
        contenedorJugadores = view.findViewById(R.id.contenedorJugadores);
        mostrarAsistencias();
        return view;
    }

    private void mostrarAsistencias() {
        contenedorJugadores.removeAllViews();
        List<Usuario> usuarios = dataManager.getUsuarios();
        List<Evento> eventos = dataManager.getEventos();
        List<Asistencia> asistencias = dataManager.getAsistencias();

        // Filtrar solo los jugadores registrados por el usuario actual (rol padre/tutor)
        List<Usuario> jugadores = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.isEsPadre()) {
                jugadores.add(u);
            }
        }

        if (jugadores.isEmpty()) {
            TextView tvVacio = new TextView(getContext());
            tvVacio.setText("No tienes jugadores registrados. Ve a Perfil para añadirlos.");
            tvVacio.setTextColor(getResources().getColor(R.color.gray));
            tvVacio.setTextSize(16);
            tvVacio.setPadding(0, 16, 0, 16);
            contenedorJugadores.addView(tvVacio);
            return;
        }

        if (eventos.isEmpty()) {
            TextView tvVacio = new TextView(getContext());
            tvVacio.setText("No hay eventos programados.");
            tvVacio.setTextColor(getResources().getColor(R.color.gray));
            tvVacio.setTextSize(16);
            tvVacio.setPadding(0, 16, 0, 16);
            contenedorJugadores.addView(tvVacio);
            return;
        }

        for (Usuario jugador : jugadores) {
            TextView tvJugador = new TextView(getContext());
            tvJugador.setText("Jugador: " + jugador.getJugador());
            tvJugador.setTextColor(getResources().getColor(R.color.gold));
            tvJugador.setTextSize(18);
            tvJugador.setPadding(0, 24, 0, 8);
            contenedorJugadores.addView(tvJugador);

            // Mostrar eventos próximos (hoy o futuros)
            boolean tieneEventos = false;
            for (Evento evento : eventos) {
                if (evento.getFechaInicio().after(new Date()) || esHoy(evento.getFechaInicio())) {
                    tieneEventos = true;
                    LinearLayout layoutEvento = new LinearLayout(getContext());
                    layoutEvento.setOrientation(LinearLayout.VERTICAL);
                    layoutEvento.setBackgroundResource(R.drawable.card_background);
                    layoutEvento.setPadding(16, 16, 16, 16);
                    layoutEvento.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layoutEvento.setElevation(4f);

                    TextView tvEvento = new TextView(getContext());
                    tvEvento.setText(evento.getTitulo() + " | " + evento.getTipo() + "\n" + dateFormat.format(evento.getFechaInicio()));
                    tvEvento.setTextColor(getResources().getColor(R.color.white));
                    tvEvento.setTextSize(16);
                    tvEvento.setPadding(0, 0, 0, 8);
                    layoutEvento.addView(tvEvento);

                    // Buscar asistencia previa
                    Asistencia asistencia = buscarAsistencia(asistencias, evento.getId(), jugador.getJugador());

                    Button btnPresente = new Button(getContext());
                    btnPresente.setText("Asistió");
                    btnPresente.setBackgroundResource(R.drawable.button_gold_background);
                    btnPresente.setTextColor(getResources().getColor(R.color.black));
                    btnPresente.setPadding(16, 8, 16, 8);
                    btnPresente.setTextSize(14);

                    Button btnAusente = new Button(getContext());
                    btnAusente.setText("No asistió");
                    btnAusente.setBackgroundResource(R.drawable.button_danger_background);
                    btnAusente.setTextColor(getResources().getColor(R.color.white));
                    btnAusente.setPadding(16, 8, 16, 8);
                    btnAusente.setTextSize(14);

                    if (asistencia != null) {
                        if (asistencia.isAsistio()) {
                            btnPresente.setEnabled(false);
                            btnPresente.setText("Asistencia registrada");
                            btnAusente.setEnabled(true);
                        } else {
                            btnAusente.setEnabled(false);
                            btnAusente.setText("Ausencia registrada");
                            btnPresente.setEnabled(true);
                        }
                    }

                    btnPresente.setOnClickListener(v -> {
                        guardarAsistencia(evento.getId(), jugador.getJugador(), true);
                        Toast.makeText(getContext(), "Asistencia registrada", Toast.LENGTH_SHORT).show();
                        mostrarAsistencias();
                    });
                    btnAusente.setOnClickListener(v -> {
                        guardarAsistencia(evento.getId(), jugador.getJugador(), false);
                        Toast.makeText(getContext(), "Ausencia registrada", Toast.LENGTH_SHORT).show();
                        mostrarAsistencias();
                    });

                    LinearLayout botones = new LinearLayout(getContext());
                    botones.setOrientation(LinearLayout.HORIZONTAL);
                    botones.addView(btnPresente);
                    botones.addView(btnAusente);
                    layoutEvento.addView(botones);

                    contenedorJugadores.addView(layoutEvento);
                }
            }
            if (!tieneEventos) {
                TextView tvSinEventos = new TextView(getContext());
                tvSinEventos.setText("No hay eventos próximos para este jugador.");
                tvSinEventos.setTextColor(getResources().getColor(R.color.gray));
                tvSinEventos.setTextSize(14);
                tvSinEventos.setPadding(0, 8, 0, 8);
                contenedorJugadores.addView(tvSinEventos);
            }

            // Historial de asistencias
            TextView tvHistorial = new TextView(getContext());
            tvHistorial.setText("Historial de asistencias:");
            tvHistorial.setTextColor(getResources().getColor(R.color.gold));
            tvHistorial.setTextSize(16);
            tvHistorial.setPadding(0, 16, 0, 8);
            contenedorJugadores.addView(tvHistorial);

            boolean tieneHistorial = false;
            for (Asistencia asistencia : asistencias) {
                if (asistencia.getJugadorNombre().equalsIgnoreCase(jugador.getJugador())) {
                    // Buscar el evento correspondiente
                    Evento evento = null;
                    for (Evento e : eventos) {
                        if (e.getId().equals(asistencia.getEventoId())) {
                            evento = e;
                            break;
                        }
                    }
                    if (evento != null && evento.getFechaInicio().before(new Date()) && !esHoy(evento.getFechaInicio())) {
                        tieneHistorial = true;
                        LinearLayout layoutHistorial = new LinearLayout(getContext());
                        layoutHistorial.setOrientation(LinearLayout.HORIZONTAL);
                        layoutHistorial.setPadding(0, 4, 0, 4);
                        layoutHistorial.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        TextView tvEventoHist = new TextView(getContext());
                        tvEventoHist.setText(evento.getTitulo() + " | " + dateFormat.format(evento.getFechaInicio()));
                        tvEventoHist.setTextColor(getResources().getColor(R.color.white));
                        tvEventoHist.setTextSize(14);
                        tvEventoHist.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                        TextView tvEstado = new TextView(getContext());
                        if (asistencia.isAsistio()) {
                            tvEstado.setText("ASISTIÓ");
                            tvEstado.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            tvEstado.setText("NO ASISTIÓ");
                            tvEstado.setTextColor(getResources().getColor(R.color.red));
                        }
                        tvEstado.setTextSize(14);
                        tvEstado.setPadding(16, 0, 0, 0);

                        layoutHistorial.addView(tvEventoHist);
                        layoutHistorial.addView(tvEstado);
                        contenedorJugadores.addView(layoutHistorial);
                    }
                }
            }
            if (!tieneHistorial) {
                TextView tvSinHistorial = new TextView(getContext());
                tvSinHistorial.setText("Sin historial de asistencias para este jugador.");
                tvSinHistorial.setTextColor(getResources().getColor(R.color.gray));
                tvSinHistorial.setTextSize(14);
                tvSinHistorial.setPadding(0, 4, 0, 8);
                contenedorJugadores.addView(tvSinHistorial);
            }
        }
    }

    private boolean esHoy(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(fecha).equals(sdf.format(new Date()));
    }

    private Asistencia buscarAsistencia(List<Asistencia> asistencias, String eventoId, String jugadorNombre) {
        for (Asistencia a : asistencias) {
            if (a.getEventoId().equals(eventoId) && a.getJugadorNombre().equalsIgnoreCase(jugadorNombre)) {
                return a;
            }
        }
        return null;
    }

    private void guardarAsistencia(String eventoId, String jugadorNombre, boolean asistio) {
        List<Asistencia> lista = dataManager.getAsistencias();
        boolean found = false;
        for (Asistencia a : lista) {
            if (a.getEventoId().equals(eventoId) && a.getJugadorNombre().equalsIgnoreCase(jugadorNombre)) {
                a.setAsistio(asistio);
                found = true;
                break;
            }
        }
        if (!found) {
            Asistencia nueva = new Asistencia(eventoId, null, jugadorNombre, asistio);
            lista.add(nueva);
        }
        dataManager.guardarAsistencias(lista);
    }
} 