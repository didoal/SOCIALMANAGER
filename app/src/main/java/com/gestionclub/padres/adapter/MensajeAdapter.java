package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Mensaje;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {
    private List<Mensaje> mensajes;
    private SimpleDateFormat dateFormat;

    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
        this.dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
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
        private TextView textViewContenido;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAutor = itemView.findViewById(R.id.textViewAutor);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
        }

        public void bind(Mensaje mensaje) {
            textViewAutor.setText(mensaje.getAutorNombre());
            textViewFecha.setText(dateFormat.format(mensaje.getFechaCreacion()));
            textViewContenido.setText(mensaje.getContenido());

            // Cambiar color del autor si es admin
            if (mensaje.isEsAdmin()) {
                textViewAutor.setTextColor(itemView.getContext().getResources().getColor(R.color.purple_700));
            } else {
                textViewAutor.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            }
        }
    }
} 