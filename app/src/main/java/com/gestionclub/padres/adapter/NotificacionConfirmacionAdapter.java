package com.gestionclub.padres.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Notificacion;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificacionConfirmacionAdapter extends RecyclerView.Adapter<NotificacionConfirmacionAdapter.NotificacionViewHolder> {
    
    private List<Notificacion> notificaciones;
    private Context context;
    private DataManager dataManager;
    private OnNotificacionActionListener listener;

    public interface OnNotificacionActionListener {
        void onNotificacionRespondida(Notificacion notificacion, String respuesta, String motivo);
    }

    public NotificacionConfirmacionAdapter(Context context, List<Notificacion> notificaciones, OnNotificacionActionListener listener) {
        this.context = context;
        this.notificaciones = notificaciones;
        this.dataManager = new DataManager(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion_confirmacion, parent, false);
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

    public class NotificacionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitulo;
        private TextView textViewEquipo;
        private TextView textViewMensaje;
        private TextView textViewFecha;
        private TextView textViewRemitente;
        private TextView textViewEstado;
        private Button buttonAceptar;
        private Button buttonNo;
        private Button buttonConfirmarNo;
        private LinearLayout layoutMotivo;
        private TextInputEditText editTextMotivo;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewEquipo = itemView.findViewById(R.id.textViewEquipo);
            textViewMensaje = itemView.findViewById(R.id.textViewMensaje);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewRemitente = itemView.findViewById(R.id.textViewRemitente);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            buttonAceptar = itemView.findViewById(R.id.buttonAceptar);
            buttonNo = itemView.findViewById(R.id.buttonNo);
            buttonConfirmarNo = itemView.findViewById(R.id.buttonConfirmarNo);
            layoutMotivo = itemView.findViewById(R.id.layoutMotivo);
            editTextMotivo = itemView.findViewById(R.id.editTextMotivo);
        }

        public void bind(Notificacion notificacion) {
            // Configurar título
            textViewTitulo.setText(notificacion.getTitulo());
            
            // Configurar equipo
            if (notificacion.getEquipoDestinatario() != null && !notificacion.getEquipoDestinatario().isEmpty()) {
                textViewEquipo.setText(notificacion.getEquipoDestinatario());
                textViewEquipo.setVisibility(View.VISIBLE);
            } else {
                textViewEquipo.setVisibility(View.GONE);
            }
            
            // Configurar mensaje
            textViewMensaje.setText(notificacion.getMensaje());
            
            // Configurar fecha
            if (notificacion.getFechaCreacion() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("es"));
                textViewFecha.setText(dateFormat.format(notificacion.getFechaCreacion()));
            } else {
                textViewFecha.setText("Fecha no disponible");
            }
            
            // Configurar remitente
            if (notificacion.getRemitenteNombre() != null && !notificacion.getRemitenteNombre().isEmpty()) {
                textViewRemitente.setText(notificacion.getRemitenteNombre());
            } else {
                textViewRemitente.setText("Sistema");
            }
            
            // Configurar estado
            textViewEstado.setText(notificacion.getEstado());
            switch (notificacion.getEstado()) {
                case "PENDIENTE":
                    textViewEstado.setTextColor(context.getResources().getColor(R.color.orange));
                    textViewEstado.setBackgroundResource(R.drawable.badge_solicitud_background);
                    break;
                case "APROBADA":
                    textViewEstado.setTextColor(context.getResources().getColor(R.color.green));
                    textViewEstado.setBackgroundResource(R.drawable.badge_evento_background);
                    break;
                case "RECHAZADA":
                    textViewEstado.setTextColor(context.getResources().getColor(R.color.red));
                    textViewEstado.setBackgroundResource(R.drawable.badge_mensaje_background);
                    break;
            }
            
            // Configurar botones según el estado
            if ("PENDIENTE".equals(notificacion.getEstado())) {
                buttonAceptar.setVisibility(View.VISIBLE);
                buttonNo.setVisibility(View.VISIBLE);
                layoutMotivo.setVisibility(View.GONE);
            } else {
                buttonAceptar.setVisibility(View.GONE);
                buttonNo.setVisibility(View.GONE);
                layoutMotivo.setVisibility(View.GONE);
            }
            
            // Configurar listeners de botones
            buttonAceptar.setOnClickListener(v -> {
                responderNotificacion(notificacion, "APROBADA", "");
            });
            
            buttonNo.setOnClickListener(v -> {
                layoutMotivo.setVisibility(View.VISIBLE);
                buttonNo.setVisibility(View.GONE);
                buttonAceptar.setVisibility(View.GONE);
            });
            
            buttonConfirmarNo.setOnClickListener(v -> {
                String motivo = editTextMotivo.getText().toString().trim();
                responderNotificacion(notificacion, "RECHAZADA", motivo);
            });
        }

        private void responderNotificacion(Notificacion notificacion, String respuesta, String motivo) {
            // Actualizar el estado de la notificación
            notificacion.setEstado(respuesta);
            dataManager.actualizarNotificacion(notificacion);
            
            // Notificar al listener
            if (listener != null) {
                listener.onNotificacionRespondida(notificacion, respuesta, motivo);
            }
            
            // Mostrar mensaje de confirmación
            String mensaje = respuesta.equals("APROBADA") ? 
                "Asistencia confirmada" : "Asistencia rechazada";
            android.widget.Toast.makeText(context, mensaje, android.widget.Toast.LENGTH_SHORT).show();
            
            // Actualizar la vista
            notifyItemChanged(getAdapterPosition());
        }
    }
} 