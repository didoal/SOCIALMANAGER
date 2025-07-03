package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Notificacion;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {
    private List<Notificacion> notificaciones;
    private OnNotificacionClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnNotificacionClickListener {
        void onNotificacionClick(Notificacion notificacion);
    }

    public NotificacionAdapter(List<Notificacion> notificaciones, OnNotificacionClickListener listener) {
        this.notificaciones = notificaciones;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificacion notificacion = notificaciones.get(position);
        holder.bind(notificacion);
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    public void actualizarNotificaciones(List<Notificacion> nuevasNotificaciones) {
        this.notificaciones = nuevasNotificaciones;
        notifyDataSetChanged();
    }

    class NotificacionViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewTipo;
        private TextView textViewTitulo;
        private TextView textViewMensaje;
        private TextView textViewRemitente;
        private TextView textViewFecha;
        private View viewNoLeida;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTipo = itemView.findViewById(R.id.imageViewTipo);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewMensaje = itemView.findViewById(R.id.textViewMensaje);
            textViewRemitente = itemView.findViewById(R.id.textViewRemitente);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            viewNoLeida = itemView.findViewById(R.id.viewNoLeida);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onNotificacionClick(notificaciones.get(position));
                }
            });
        }

        public void bind(Notificacion notificacion) {
            textViewTitulo.setText(notificacion.getTitulo());
            textViewMensaje.setText(notificacion.getMensaje());
            textViewRemitente.setText("De: " + notificacion.getRemitenteNombre());
            
            // Formatear fecha
            String fechaFormateada = dateFormat.format(notificacion.getFechaCreacion());
            textViewFecha.setText(fechaFormateada);

            // Configurar icono según tipo
            configurarIconoTipo(notificacion.getTipo());

            // Mostrar indicador de no leída
            if (notificacion.isLeida()) {
                viewNoLeida.setVisibility(View.GONE);
            } else {
                viewNoLeida.setVisibility(View.VISIBLE);
            }
        }

        private void configurarIconoTipo(String tipo) {
            switch (tipo) {
                case "EVENTO":
                    imageViewTipo.setImageResource(R.drawable.ic_calendar);
                    break;
                case "MENSAJE":
                    imageViewTipo.setImageResource(R.drawable.ic_message);
                    break;
                case "OBJETO":
                    imageViewTipo.setImageResource(R.drawable.ic_object);
                    break;
                case "SOLICITUD":
                    imageViewTipo.setImageResource(R.drawable.ic_request);
                    break;
                default:
                    imageViewTipo.setImageResource(R.drawable.ic_notification);
                    break;
            }
        }
    }
} 