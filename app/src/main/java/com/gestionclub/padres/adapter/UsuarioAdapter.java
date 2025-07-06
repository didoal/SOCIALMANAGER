package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    private List<Usuario> usuarios;
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onEliminarClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> usuarios, OnUsuarioClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public void actualizarUsuarios(List<Usuario> nuevosUsuarios) {
        this.usuarios = nuevosUsuarios;
        notifyDataSetChanged();
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombre;
        private TextView textViewEmail;
        private TextView textViewRol;
        private TextView textViewJugador;
        private ImageButton buttonEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewRol = itemView.findViewById(R.id.textViewRol);
            textViewJugador = itemView.findViewById(R.id.textViewJugador);
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);
        }

        public void bind(Usuario usuario) {
            textViewNombre.setText(usuario.getNombre());
            textViewEmail.setText(usuario.getEmail());
            
            // Mostrar rol con color según tipo
            String rol = usuario.isEsAdmin() ? "Administrador" : usuario.getRol();
            textViewRol.setText(rol);
            
            // Configurar color del rol
            if (usuario.isEsAdmin()) {
                textViewRol.setTextColor(itemView.getContext().getResources().getColor(R.color.colorAccent));
            } else {
                textViewRol.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
            }
            
            // Mostrar jugador si existe
            if (usuario.getJugador() != null && !usuario.getJugador().isEmpty()) {
                textViewJugador.setText("Jugador: " + usuario.getJugador());
                textViewJugador.setVisibility(View.VISIBLE);
            } else {
                textViewJugador.setVisibility(View.GONE);
            }
            
            // Configurar botón eliminar (no mostrar para administradores)
            if (usuario.isEsAdmin()) {
                buttonEliminar.setVisibility(View.GONE);
            } else {
                buttonEliminar.setVisibility(View.VISIBLE);
                buttonEliminar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEliminarClick(usuario);
                    }
                });
            }
        }
    }
} 