package com.gestionclub.padres.adapter;

import android.content.Context;
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

public class MensajeMuroAdapter extends RecyclerView.Adapter<MensajeMuroAdapter.MensajeMuroViewHolder> {
    private List<Mensaje> mensajes;
    private Usuario usuarioActual;
    private SimpleDateFormat dateFormat;
    private OnMensajeMuroClickListener listener;

    public interface OnMensajeMuroClickListener {
        void onMensajeClick(Mensaje mensaje);
        void onMensajeLongClick(Mensaje mensaje, View view);
    }

    public MensajeMuroAdapter(List<Mensaje> mensajes, Usuario usuarioActual) {
        this.mensajes = mensajes;
        this.usuarioActual = usuarioActual;
        this.dateFormat = new SimpleDateFormat("EEEE, h:mm a", new Locale("es", "ES"));
    }

    public void setOnMensajeMuroClickListener(OnMensajeMuroClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MensajeMuroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) { // Mensaje normal
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_muro, parent, false);
        } else { // Mensaje vacío
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_vacio, parent, false);
        }
        return new MensajeMuroViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.isEmpty() ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeMuroViewHolder holder, int position) {
        if (mensajes.isEmpty()) {
            holder.bindVacio();
        } else {
            Mensaje mensaje = mensajes.get(position);
            holder.bind(mensaje);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.isEmpty() ? 1 : mensajes.size();
    }

    public void actualizarMensajes(List<Mensaje> nuevosMensajes) {
        this.mensajes = nuevosMensajes;
        notifyDataSetChanged();
    }

    public class MensajeMuroViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewAvatar;
        private TextView textViewAutor;
        private TextView textViewFecha;
        private TextView textViewContenido;
        private ImageView imageViewMenu;

        public MensajeMuroViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            textViewAutor = itemView.findViewById(R.id.textViewAutor);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
            imageViewMenu = itemView.findViewById(R.id.imageViewMenu);

            // Configurar click en el item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMensajeClick(mensajes.get(position));
                }
            });

            // Configurar click largo para editar/borrar (solo para admins)
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMensajeLongClick(mensajes.get(position), v);
                }
                return true;
            });

            // Configurar click en el menú
            imageViewMenu.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMensajeLongClick(mensajes.get(position), v);
                }
            });
        }

        public void bind(Mensaje mensaje) {
            Context context = itemView.getContext();

            // Configurar autor
            textViewAutor.setText(mensaje.getAutorNombre());
            if (mensaje.isEsAdmin()) {
                textViewAutor.setTextColor(context.getResources().getColor(R.color.gold));
            } else {
                textViewAutor.setTextColor(context.getResources().getColor(R.color.text_primary));
            }

            // Configurar fecha
            if (mensaje.getFechaCreacion() != null) {
                textViewFecha.setText(dateFormat.format(mensaje.getFechaCreacion()));
            } else {
                textViewFecha.setText("Fecha no disponible");
            }

            // Configurar contenido
            textViewContenido.setText(mensaje.getContenido());

            // Mostrar menú solo para admins
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                imageViewMenu.setVisibility(View.VISIBLE);
            } else {
                imageViewMenu.setVisibility(View.GONE);
            }

            // Configurar avatar
            if (mensaje.isEsAdmin()) {
                imageViewAvatar.setImageResource(R.drawable.logo_santiaguino_guizan);
            } else {
                imageViewAvatar.setImageResource(R.drawable.ic_person);
            }
        }

        public void bindVacio() {
            // No hacer nada para el mensaje vacío, el layout ya tiene el contenido estático
        }
    }
} 