package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {
    private List<Mensaje> mensajes;
    private Usuario usuarioActual;
    private SimpleDateFormat dateFormat;
    private OnMensajeClickListener listener;

    public interface OnMensajeClickListener {
        void onMensajeLongClick(Mensaje mensaje, View view);
    }

    public MensajeAdapter(List<Mensaje> mensajes, Usuario usuarioActual) {
        this.mensajes = mensajes;
        this.usuarioActual = usuarioActual;
        this.dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
    }

    public void setOnMensajeClickListener(OnMensajeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.bind(mensaje);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public void actualizarMensajes(List<Mensaje> nuevosMensajes) {
        this.mensajes = nuevosMensajes;
        notifyDataSetChanged();
    }

    class MensajeViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewAutor;
        private TextView textViewFecha;
        private TextView textViewMensaje;
        private ImageView imageViewDestacado;
        private View layoutMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAutor = itemView.findViewById(R.id.textViewAutor);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewMensaje = itemView.findViewById(R.id.textViewMensaje);
            imageViewDestacado = itemView.findViewById(R.id.imageViewDestacado);
            layoutMensaje = itemView.findViewById(R.id.layoutMensaje);
        }

        public void bind(Mensaje mensaje) {
            textViewAutor.setText(mensaje.getAutorNombre());
            textViewFecha.setText(dateFormat.format(mensaje.getFechaCreacion()));
            textViewMensaje.setText(mensaje.getContenido());

            // Configurar destacado
            if (mensaje.isDestacado()) {
                imageViewDestacado.setVisibility(View.VISIBLE);
                layoutMensaje.setBackgroundResource(R.drawable.mensaje_destacado_background);
            } else {
                imageViewDestacado.setVisibility(View.GONE);
                layoutMensaje.setBackgroundResource(R.drawable.mensaje_background);
            }

            // Configurar colores según el tipo de usuario
            if (mensaje.isEsAdmin()) {
                // Mensaje de administrador
                textViewAutor.setTextColor(itemView.getContext().getResources().getColor(R.color.gold));
                textViewAutor.setText(mensaje.getAutorNombre() + " (Admin)");
            } else if (usuarioActual != null && mensaje.getAutorId().equals(usuarioActual.getId())) {
                // Mensaje propio
                textViewAutor.setTextColor(itemView.getContext().getResources().getColor(R.color.blue_profesional));
                textViewAutor.setText("Tú");
            } else {
                // Mensaje de otro usuario
                textViewAutor.setTextColor(itemView.getContext().getResources().getColor(R.color.text_primary));
            }

            // Configurar click largo para destacar/desdestacar (solo para admins)
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                itemView.setOnLongClickListener(v -> {
                    if (listener != null) {
                        listener.onMensajeLongClick(mensaje, v);
                    }
                    return true;
                });
            }
        }
    }
} 