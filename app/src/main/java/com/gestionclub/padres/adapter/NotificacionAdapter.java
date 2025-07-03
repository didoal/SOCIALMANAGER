package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Notificacion;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {
    private List<Notificacion> notificaciones;
    private SimpleDateFormat dateFormat;
    private OnNotificacionClickListener listener;

    public interface OnNotificacionClickListener {
        void onNotificacionClick(Notificacion notificacion);
    }

    public NotificacionAdapter(List<Notificacion> notificaciones, OnNotificacionClickListener listener) {
        this.notificaciones = notificaciones;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion, parent, false);
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
        private TextView textViewTitulo;
        private TextView textViewTipo;
        private TextView textViewMensaje;
        private TextView textViewRemitente;
        private TextView textViewFecha;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewTipo = itemView.findViewById(R.id.textViewTipo);
            textViewMensaje = itemView.findViewById(R.id.textViewMensaje);
            textViewRemitente = itemView.findViewById(R.id.textViewRemitente);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
        }

        public void bind(Notificacion notificacion) {
            textViewTitulo.setText(notificacion.getTitulo());
            textViewTipo.setText(notificacion.getTipo());
            textViewMensaje.setText(notificacion.getMensaje());
            textViewRemitente.setText("De: " + notificacion.getRemitenteNombre());
            textViewFecha.setText(dateFormat.format(notificacion.getFechaCreacion()));

            // Configurar color de fondo según si está leída o no
            if (!notificacion.isLeida()) {
                itemView.setBackgroundResource(R.drawable.notificacion_background);
                textViewTitulo.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            } else {
                itemView.setBackgroundResource(R.drawable.notificacion_background);
                textViewTitulo.setTextColor(itemView.getContext().getResources().getColor(R.color.gray));
            }

            // Configurar color del tipo
            switch (notificacion.getTipo()) {
                case "EVENTO":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_background);
                    break;
                case "MENSAJE":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_background);
                    break;
                case "OBJETO":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_background);
                    break;
                case "GENERAL":
                    textViewTipo.setBackgroundResource(R.drawable.tipo_background);
                    break;
            }

            // Configurar click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificacionClick(notificacion);
                }
            });
        }
    }
} 