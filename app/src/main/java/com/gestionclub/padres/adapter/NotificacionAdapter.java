package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnNotificacionClickListener {
        void onMarcarLeidaClick(Notificacion notificacion);
        void onBorrarClick(Notificacion notificacion);
        void onAprobarClick(Notificacion notificacion);
        void onRechazarClick(Notificacion notificacion);
    }

    public NotificacionAdapter(List<Notificacion> notificaciones, OnNotificacionClickListener listener) {
        this.notificaciones = notificaciones;
        this.listener = listener;
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
        private TextView textViewMensaje;
        private TextView textViewTipo;
        private TextView textViewFecha;
        private ImageButton buttonMarcarLeida;
        private ImageButton buttonAprobar;
        private ImageButton buttonRechazar;
        private ImageButton buttonBorrar;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewMensaje = itemView.findViewById(R.id.textViewMensaje);
            textViewTipo = itemView.findViewById(R.id.textViewTipo);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            buttonMarcarLeida = itemView.findViewById(R.id.buttonMarcarLeida);
            buttonAprobar = itemView.findViewById(R.id.buttonAprobar);
            buttonRechazar = itemView.findViewById(R.id.buttonRechazar);
            buttonBorrar = itemView.findViewById(R.id.buttonBorrar);
        }

        public void bind(Notificacion notificacion) {
            textViewTitulo.setText(notificacion.getTitulo());
            textViewMensaje.setText(notificacion.getMensaje());
            textViewFecha.setText("Fecha: " + dateFormat.format(notificacion.getFechaCreacion()));
            
            // Configurar tipo con color y estilo según el tipo de notificación
            textViewTipo.setText(notificacion.getTipo());
            
            switch (notificacion.getTipo()) {
                case "RECORDATORIO":
                case "RECORDATORIO_OBJETOS":
                    textViewTipo.setTextColor(itemView.getContext().getResources().getColor(R.color.colorAccent));
                    textViewTipo.setBackgroundResource(R.drawable.badge_recordatorio_background);
                    break;
                case "EVENTO":
                case "EVENTO_EQUIPO":
                    textViewTipo.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    textViewTipo.setBackgroundResource(R.drawable.badge_evento_background);
                    break;
                case "OBJETO":
                case "OBJETO_EQUIPO":
                    textViewTipo.setTextColor(itemView.getContext().getResources().getColor(R.color.colorWarning));
                    textViewTipo.setBackgroundResource(R.drawable.badge_objeto_background);
                    break;
                case "SOLICITUD":
                case "SOLICITUD_ASISTENCIA":
                    textViewTipo.setTextColor(itemView.getContext().getResources().getColor(R.color.colorInfo));
                    textViewTipo.setBackgroundResource(R.drawable.badge_solicitud_background);
                    break;
                case "MENSAJE":
                default:
                    textViewTipo.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    textViewTipo.setBackgroundResource(R.drawable.badge_mensaje_background);
                    break;
            }
            
            // Configurar estado visual y botones
            if (notificacion.isLeida()) {
                itemView.setAlpha(0.6f);
                buttonMarcarLeida.setVisibility(View.GONE);
            } else {
                itemView.setAlpha(1.0f);
                buttonMarcarLeida.setVisibility(View.VISIBLE);
                buttonMarcarLeida.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onMarcarLeidaClick(notificacion);
                    }
                });
            }
            
            // Configurar botones de aprobar/rechazar solo para solicitudes
            if (notificacion.getTipo().contains("SOLICITUD") && !notificacion.isLeida()) {
                buttonAprobar.setVisibility(View.VISIBLE);
                buttonRechazar.setVisibility(View.VISIBLE);
                
                buttonAprobar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAprobarClick(notificacion);
                    }
                });
                
                buttonRechazar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onRechazarClick(notificacion);
                    }
                });
            } else {
                buttonAprobar.setVisibility(View.GONE);
                buttonRechazar.setVisibility(View.GONE);
            }
            
            // Configurar botón de borrar
            buttonBorrar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBorrarClick(notificacion);
                }
            });
        }
    }
} 