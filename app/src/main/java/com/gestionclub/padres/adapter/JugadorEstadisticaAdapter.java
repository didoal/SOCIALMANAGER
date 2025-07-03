package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class JugadorEstadisticaAdapter extends RecyclerView.Adapter<JugadorEstadisticaAdapter.JugadorViewHolder> {
    private List<Usuario> jugadores;
    private DataManager dataManager;
    private List<Evento> eventos;
    private List<Asistencia> asistencias;

    public JugadorEstadisticaAdapter(List<Usuario> jugadores, DataManager dataManager, List<Evento> eventos, List<Asistencia> asistencias) {
        this.jugadores = jugadores;
        this.dataManager = dataManager;
        this.eventos = eventos;
        this.asistencias = asistencias;
    }

    @NonNull
    @Override
    public JugadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jugador_estadistica, parent, false);
        return new JugadorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JugadorViewHolder holder, int position) {
        Usuario jugador = jugadores.get(position);
        holder.bind(jugador);
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    class JugadorViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreJugador, textViewEntrenamientos, textViewPartidos, textViewEventos, textViewPorcentaje;
        private ProgressBar progressBarAsistencia;

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreJugador = itemView.findViewById(R.id.textViewNombreJugador);
            textViewEntrenamientos = itemView.findViewById(R.id.textViewEntrenamientos);
            textViewPartidos = itemView.findViewById(R.id.textViewPartidos);
            textViewEventos = itemView.findViewById(R.id.textViewEventos);
            textViewPorcentaje = itemView.findViewById(R.id.textViewPorcentaje);
            progressBarAsistencia = itemView.findViewById(R.id.progressBarAsistencia);
        }

        public void bind(Usuario jugador) {
            textViewNombreJugador.setText(jugador.getNombre());
            int entrenamientos = 0, partidos = 0, eventos = 0, total = 0, asistio = 0;
            for (Asistencia asistencia : asistencias) {
                if (asistencia.getJugadorId().equals(jugador.getId())) {
                    Evento evento = buscarEvento(asistencia.getEventoId());
                    if (evento != null) {
                        switch (evento.getTipo()) {
                            case "ENTRENAMIENTO": entrenamientos++; break;
                            case "PARTIDO": partidos++; break;
                            case "EVENTO": eventos++; break;
                        }
                        total++;
                        if (asistencia.isAsistio()) asistio++;
                    }
                }
            }
            textViewEntrenamientos.setText("Entrenamientos: " + entrenamientos);
            textViewPartidos.setText("Partidos: " + partidos);
            textViewEventos.setText("Eventos: " + eventos);
            int porcentaje = total > 0 ? (int) ((asistio * 100.0f) / total) : 0;
            textViewPorcentaje.setText(porcentaje + "%");
            progressBarAsistencia.setProgress(porcentaje);
        }

        private Evento buscarEvento(String eventoId) {
            for (Evento evento : eventos) {
                if (evento.getId().equals(eventoId)) {
                    return evento;
                }
            }
            return null;
        }
    }
} 