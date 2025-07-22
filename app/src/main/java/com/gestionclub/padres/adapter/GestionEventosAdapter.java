package com.gestionclub.padres.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GestionEventosAdapter extends RecyclerView.Adapter<GestionEventosAdapter.EventoViewHolder> {
    
    private List<Evento> eventos;
    private Context context;
    private DataManager dataManager;
    private OnEventoActionListener listener;

    public interface OnEventoActionListener {
        void onEventoEditado(Evento evento);
        void onEventoEliminado(Evento evento);
    }

    public GestionEventosAdapter(Context context, List<Evento> eventos, OnEventoActionListener listener) {
        this.context = context;
        this.eventos = eventos;
        this.dataManager = new DataManager(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gestion_evento, parent, false);
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
        // Ordenar eventos por fecha más reciente primero
        nuevosEventos.sort((e1, e2) -> {
            if (e1.getFechaInicio() == null && e2.getFechaInicio() == null) return 0;
            if (e1.getFechaInicio() == null) return 1;
            if (e2.getFechaInicio() == null) return -1;
            return e2.getFechaInicio().compareTo(e1.getFechaInicio()); // Orden descendente (más reciente primero)
        });
        
        this.eventos = nuevosEventos;
        notifyDataSetChanged();
    }

    public class EventoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewIcono;
        private TextView textViewTitulo;
        private TextView textViewUbicacion;
        private TextView textViewDescripcion;
        private TextView textViewFecha;
        private TextView textViewTipo;
        private Button buttonEditar;
        private Button buttonEliminar;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageViewIcono = itemView.findViewById(R.id.imageViewIcono);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewTipo = itemView.findViewById(R.id.textViewTipo);
            buttonEditar = itemView.findViewById(R.id.buttonEditar);
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);
        }

        public void bind(Evento evento) {
            // Configurar título
            textViewTitulo.setText(evento.getTitulo());
            
            // Configurar ubicación
            if (evento.getUbicacion() != null && !evento.getUbicacion().isEmpty()) {
                textViewUbicacion.setText(evento.getUbicacion());
                textViewUbicacion.setVisibility(View.VISIBLE);
            } else {
                textViewUbicacion.setVisibility(View.GONE);
            }
            
            // Configurar descripción
            if (evento.getDescripcion() != null && !evento.getDescripcion().isEmpty()) {
                textViewDescripcion.setText(evento.getDescripcion());
                textViewDescripcion.setVisibility(View.VISIBLE);
            } else {
                textViewDescripcion.setVisibility(View.GONE);
            }
            
            // Configurar fecha
            if (evento.getFechaInicio() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, HH:mm", new Locale("es"));
                String fecha = dateFormat.format(evento.getFechaInicio());
                textViewFecha.setText(fecha);
            } else {
                textViewFecha.setText("Fecha no disponible");
            }
            
            // Configurar tipo y icono
            if (evento.getTipo() != null && !evento.getTipo().isEmpty()) {
                textViewTipo.setText(evento.getTipo().toUpperCase());
                
                switch (evento.getTipo().toLowerCase()) {
                    case "partido":
                        imageViewIcono.setImageResource(R.drawable.ic_team);
                        imageViewIcono.setBackgroundResource(R.drawable.circle_button_green);
                        textViewTipo.setBackgroundResource(R.drawable.badge_evento_background);
                        break;
                    case "entrenamiento":
                        imageViewIcono.setImageResource(R.drawable.ic_event);
                        imageViewIcono.setBackgroundResource(R.drawable.circle_button_blue);
                        textViewTipo.setBackgroundResource(R.drawable.badge_evento_background);
                        break;
                    case "reunión":
                        imageViewIcono.setImageResource(R.drawable.ic_group);
                        imageViewIcono.setBackgroundResource(R.drawable.circle_button_blue);
                        textViewTipo.setBackgroundResource(R.drawable.badge_evento_background);
                        break;
                    default:
                        imageViewIcono.setImageResource(R.drawable.ic_event);
                        imageViewIcono.setBackgroundResource(R.drawable.circle_button_blue);
                        textViewTipo.setBackgroundResource(R.drawable.badge_evento_background);
                        break;
                }
            }
            
            // Configurar botones
            buttonEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventoEditado(evento);
                }
            });
            
            buttonEliminar.setOnClickListener(v -> {
                mostrarDialogoConfirmacionEliminacion(evento);
            });
        }

        private void mostrarDialogoConfirmacionEliminacion(Evento evento) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmar eliminación");
            builder.setMessage("¿Estás seguro de que quieres eliminar el evento '" + evento.getTitulo() + "'? Esta acción no se puede deshacer.");
            
            builder.setPositiveButton("Eliminar", (dialog, which) -> {
                // Eliminar el evento
                dataManager.eliminarEvento(evento.getId());
                
                // Notificar al listener
                if (listener != null) {
                    listener.onEventoEliminado(evento);
                }
                
                // Mostrar mensaje de confirmación
                android.widget.Toast.makeText(context, "Evento eliminado exitosamente", android.widget.Toast.LENGTH_SHORT).show();
            });
            
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
} 