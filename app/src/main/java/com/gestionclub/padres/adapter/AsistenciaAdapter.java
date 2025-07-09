package com.gestionclub.padres.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Evento;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder> {
    
    private List<Asistencia> asistencias;
    private OnAsistenciaClickListener listener;
    private DataManager dataManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnAsistenciaClickListener {
        void onConfirmarClick(Asistencia asistencia);
    }

    public AsistenciaAdapter(Context context, List<Asistencia> asistencias, OnAsistenciaClickListener listener) {
        this.asistencias = asistencias;
        this.listener = listener;
        if (context != null) {
            this.dataManager = new DataManager(context);
        }
    }

    @NonNull
    @Override
    public AsistenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_asistencia, parent, false);
        return new AsistenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsistenciaViewHolder holder, int position) {
        Asistencia asistencia = asistencias.get(position);
        holder.bind(asistencia);
    }

    @Override
    public int getItemCount() {
        return asistencias.size();
    }

    public void actualizarAsistencias(List<Asistencia> nuevasAsistencias) {
        this.asistencias = nuevasAsistencias;
        notifyDataSetChanged();
    }

    class AsistenciaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewJugador;
        private TextView textViewEvento;
        private TextView textViewEstado;
        private TextView textViewFecha;
        private TextView textViewMotivo;
        private ImageButton buttonConfirmar;

        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJugador = itemView.findViewById(R.id.textViewJugador);
            textViewEvento = itemView.findViewById(R.id.textViewEvento);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewMotivo = itemView.findViewById(R.id.textViewMotivo);
            buttonConfirmar = itemView.findViewById(R.id.buttonConfirmar);
        }

        public void bind(Asistencia asistencia) {
            textViewJugador.setText("Jugador: " + asistencia.getJugadorNombre());
            
            // Buscar información del evento
            String nombreEvento = "Evento no encontrado";
            String fechaEvento = "";
            List<Evento> eventos = dataManager.getEventos();
            for (Evento evento : eventos) {
                if (evento.getId().equals(asistencia.getEventoId())) {
                    nombreEvento = evento.getTitulo();
                    fechaEvento = dateFormat.format(evento.getFechaInicio());
                    break;
                }
            }
            
            textViewEvento.setText("Evento: " + nombreEvento);
            textViewFecha.setText("Fecha: " + fechaEvento);
            
            // Configurar estado con color
            if (asistencia.isAsistio()) {
                textViewEstado.setText("ASISTIÓ");
                textViewEstado.setTextColor(itemView.getContext().getResources().getColor(R.color.green));
                textViewMotivo.setVisibility(View.GONE);
            } else {
                textViewEstado.setText("NO ASISTIÓ");
                textViewEstado.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                if (asistencia.getObservaciones() != null && !asistencia.getObservaciones().isEmpty()) {
                    textViewMotivo.setText("Motivo: " + asistencia.getObservaciones());
                    textViewMotivo.setVisibility(View.VISIBLE);
                } else {
                    textViewMotivo.setVisibility(View.GONE);
                }
            }
            
            // Configurar botón confirmar (solo para asistencias recientes)
            long tiempoTranscurrido = System.currentTimeMillis() - asistencia.getFecha().getTime();
            long horasTranscurridas = tiempoTranscurrido / (1000 * 60 * 60);
            
            if (horasTranscurridas < 24) { // Solo mostrar para asistencias de las últimas 24 horas
                buttonConfirmar.setVisibility(View.VISIBLE);
                buttonConfirmar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onConfirmarClick(asistencia);
                    }
                });
            } else {
                buttonConfirmar.setVisibility(View.GONE);
            }
        }
    }
} 