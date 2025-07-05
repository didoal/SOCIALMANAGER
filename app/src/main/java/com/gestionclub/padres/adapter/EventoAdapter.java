package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private boolean mostrarAcciones;

    public interface OnEventoClickListener {
        void onEditarClick(Evento evento);
        void onEliminarClick(Evento evento);
    }

    public EventoAdapter(List<Evento> eventos, OnEventoClickListener listener, boolean mostrarAcciones) {
        this.eventos = eventos;
        this.listener = listener;
        this.mostrarAcciones = mostrarAcciones;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
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

    class EventoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitulo;
        private TextView textViewTipo;
        private TextView textViewDescripcion;
        private TextView textViewFecha;
        private TextView textViewUbicacion;
        private Button buttonEditar;
        private Button buttonEliminar;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewTipo = itemView.findViewById(R.id.textViewTipo);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
            buttonEditar = itemView.findViewById(R.id.buttonEditar);
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);
        }

        public void bind(Evento evento) {
            textViewTitulo.setText(evento.getTitulo());
            textViewTipo.setText(evento.getTipo());
            textViewDescripcion.setText(evento.getDescripcion());
            
            String fechaHora = dateFormat.format(evento.getFechaInicio()) + " " + 
                             timeFormat.format(evento.getFechaInicio());
            textViewFecha.setText(fechaHora);
            textViewUbicacion.setText(evento.getUbicacion());

            // Configurar color del tipo según el tipo de evento
            switch (evento.getTipo()) {
                case "ENTRENAMIENTO":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_entrenamiento_background);
                    textViewTipo.setTextColor(0xFFFFFFFF); // Blanco
                    break;
                case "PARTIDO":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_partido_background);
                    textViewTipo.setTextColor(0xFFFFFFFF); // Blanco
                    break;
                case "EVENTO":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_evento_background);
                    textViewTipo.setTextColor(0xFFFFFFFF); // Blanco
                    break;
                case "REUNION":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_reunion_background);
                    textViewTipo.setTextColor(0xFFFFFFFF); // Blanco
                    break;
                default:
                    textViewTipo.setBackgroundResource(R.drawable.tipo_background);
                    textViewTipo.setTextColor(0xFF000000); // Negro
                    break;
            }

            // Mostrar/ocultar botones de acción
            if (mostrarAcciones && evento.isEsAdmin()) {
                buttonEditar.setVisibility(View.VISIBLE);
                buttonEliminar.setVisibility(View.VISIBLE);
                
                buttonEditar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditarClick(evento);
                    }
                });
                
                buttonEliminar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEliminarClick(evento);
                    }
                });
            } else {
                buttonEditar.setVisibility(View.GONE);
                buttonEliminar.setVisibility(View.GONE);
            }
        }
    }
} 