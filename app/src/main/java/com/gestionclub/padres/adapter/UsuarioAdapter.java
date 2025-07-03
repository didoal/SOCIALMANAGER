package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        void onUsuarioClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> usuarios, OnUsuarioClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
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
        private TextView textViewEquipo;
        private TextView textViewEstado;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewRol = itemView.findViewById(R.id.textViewRol);
            textViewEquipo = itemView.findViewById(R.id.textViewEquipo);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUsuarioClick(usuarios.get(position));
                }
            });
        }

        public void bind(Usuario usuario) {
            textViewNombre.setText(usuario.getNombre());
            textViewEmail.setText(usuario.getEmail());
            textViewRol.setText("Rol: " + usuario.getRol().toUpperCase());
            
            if (usuario.getEquipoNombre() != null) {
                textViewEquipo.setText("Equipo: " + usuario.getEquipoNombre());
            } else {
                textViewEquipo.setText("Equipo: Sin asignar");
            }
            
            if (usuario.isActivo()) {
                textViewEstado.setText("Activo");
                textViewEstado.setTextColor(itemView.getContext().getResources().getColor(R.color.green));
            } else {
                textViewEstado.setText("Inactivo");
                textViewEstado.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            }
        }
    }
} 