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
import java.util.Calendar;
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
        private TextView textViewDia;
        private TextView textViewEvento;
        private TextView textViewHora;
        private TextView textViewConvocatoria;
        private TextView textViewUbicacion;
        private TextView textViewTipo;
        private View layoutConvocatoria;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDia = itemView.findViewById(R.id.textViewDia);
            textViewEvento = itemView.findViewById(R.id.textViewEvento);
            textViewHora = itemView.findViewById(R.id.textViewHora);
            textViewConvocatoria = itemView.findViewById(R.id.textViewConvocatoria);
            textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
            textViewTipo = itemView.findViewById(R.id.textViewTipo);
            layoutConvocatoria = itemView.findViewById(R.id.layoutConvocatoria);

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

            // Configurar día
            if (evento.getFechaInicio() != null) {
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es"));
                String dia = dayFormat.format(evento.getFechaInicio());
                textViewDia.setText(dia);
            } else {
                textViewDia.setText("Día no disponible");
            }

            // Configurar tipo de evento y fondo del card
            if (evento.getTipo() != null && !evento.getTipo().isEmpty()) {
                textViewEvento.setText(evento.getTipo());
                textViewTipo.setText(evento.getTipo().toUpperCase());
                textViewTipo.setVisibility(View.VISIBLE);
                
                // Configurar color según el tipo
                switch (evento.getTipo().toLowerCase()) {
                    case "partido":
                        textViewTipo.setBackgroundResource(R.drawable.tipo_partido_background);
                        itemView.setBackgroundResource(R.drawable.evento_partido_background);
                        // Mostrar convocatoria para partidos
                        layoutConvocatoria.setVisibility(View.VISIBLE);
                        if (evento.getFechaInicio() != null) {
                            // Calcular hora de convocatoria (1 hora antes)
                            Calendar convocatoria = Calendar.getInstance();
                            convocatoria.setTime(evento.getFechaInicio());
                            convocatoria.add(Calendar.HOUR, -1);
                            String horaConvocatoria = timeFormat.format(convocatoria.getTime());
                            textViewConvocatoria.setText("Convocatoria: " + horaConvocatoria);
                        }
                        break;
                    case "entrenamiento":
                        textViewTipo.setBackgroundResource(R.drawable.tipo_entrenamiento_background);
                        itemView.setBackgroundResource(R.drawable.evento_entrenamiento_background);
                        layoutConvocatoria.setVisibility(View.GONE);
                        break;
                    case "reunión":
                        textViewTipo.setBackgroundResource(R.drawable.tipo_reunion_background);
                        itemView.setBackgroundResource(R.drawable.evento_reunion_background);
                        layoutConvocatoria.setVisibility(View.GONE);
                        break;
                    default:
                        textViewTipo.setBackgroundResource(R.drawable.tipo_evento_background);
                        itemView.setBackgroundResource(R.drawable.evento_general_background);
                        layoutConvocatoria.setVisibility(View.GONE);
                        break;
                }
            } else {
                textViewEvento.setText("Evento");
                textViewTipo.setVisibility(View.GONE);
                layoutConvocatoria.setVisibility(View.GONE);
                itemView.setBackgroundResource(R.drawable.evento_general_background);
            }

            // Configurar hora
            if (evento.getFechaInicio() != null) {
                String horaInicio = timeFormat.format(evento.getFechaInicio());
                String horaFin = evento.getFechaFin() != null ? timeFormat.format(evento.getFechaFin()) : "";
                String hora = horaInicio + (horaFin.isEmpty() ? "" : " - " + horaFin);
                textViewHora.setText("Hora: " + hora);
            } else {
                textViewHora.setText("Hora no disponible");
            }

            // Configurar ubicación
            if (evento.getUbicacion() != null && !evento.getUbicacion().isEmpty()) {
                textViewUbicacion.setText("Ubicación: " + evento.getUbicacion());
                textViewUbicacion.setVisibility(View.VISIBLE);
            } else {
                textViewUbicacion.setVisibility(View.GONE);
            }
        }
    }
} 