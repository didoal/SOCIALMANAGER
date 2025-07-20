package com.gestionclub.padres.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Evento;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {
    private List<Evento> eventos;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private OnEventoClickListener listener;

    public interface OnEventoClickListener {
        void onEventoClick(Evento evento);
    }

    public EventoAdapter(List<Evento> eventos) {
        this.eventos = eventos;
        this.dateFormat = new SimpleDateFormat("EEE, d MMM", new Locale("es"));
        this.timeFormat = new SimpleDateFormat("HH:mm", new Locale("es"));
    }

    public void setOnEventoClickListener(OnEventoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        holder.bind(evento);
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public void actualizarEventos(List<Evento> nuevosEventos) {
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }

    public class EventoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitulo;
        private TextView textViewFecha;
        private TextView textViewHora;
        private TextView textViewUbicacion;
        private TextView textViewTipo;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewHora = itemView.findViewById(R.id.textViewHora);
            textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
            textViewTipo = itemView.findViewById(R.id.textViewTipo);

            // Configurar click en el item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEventoClick(eventos.get(position));
                }
            });
        }

        public void bind(Evento evento) {
            Context context = itemView.getContext();

            // Configurar título
            textViewTitulo.setText(evento.getTitulo());

            // Configurar fecha
            if (evento.getFechaInicio() != null) {
                String fecha = dateFormat.format(evento.getFechaInicio());
                textViewFecha.setText(fecha);
            } else {
                textViewFecha.setText("Fecha no disponible");
            }

            // Configurar hora
            if (evento.getFechaInicio() != null) {
                String horaInicio = timeFormat.format(evento.getFechaInicio());
                String horaFin = evento.getFechaFin() != null ? timeFormat.format(evento.getFechaFin()) : "";
                String hora = horaInicio + (horaFin.isEmpty() ? "" : " - " + horaFin);
                textViewHora.setText(hora);
            } else {
                textViewHora.setText("Hora no disponible");
            }

            // Configurar ubicación
            if (evento.getUbicacion() != null && !evento.getUbicacion().isEmpty()) {
                textViewUbicacion.setText(evento.getUbicacion());
                textViewUbicacion.setVisibility(View.VISIBLE);
            } else {
                textViewUbicacion.setVisibility(View.GONE);
            }

            // Configurar tipo de evento
            if (evento.getTipo() != null && !evento.getTipo().isEmpty()) {
                textViewTipo.setText(evento.getTipo());
                textViewTipo.setVisibility(View.VISIBLE);
                
                // Configurar color según el tipo
                switch (evento.getTipo().toLowerCase()) {
                    case "partido":
                        textViewTipo.setBackgroundResource(R.drawable.tipo_partido_background);
                        break;
                    case "entrenamiento":
                        textViewTipo.setBackgroundResource(R.drawable.tipo_entrenamiento_background);
                        break;
                    case "reunión":
                        textViewTipo.setBackgroundResource(R.drawable.tipo_reunion_background);
                        break;
                    default:
                        textViewTipo.setBackgroundResource(R.drawable.tipo_evento_background);
                        break;
                }
            } else {
                textViewTipo.setVisibility(View.GONE);
            }
        }
    }
} 