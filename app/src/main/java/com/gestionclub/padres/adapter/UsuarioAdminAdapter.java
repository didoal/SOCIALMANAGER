package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class UsuarioAdminAdapter extends RecyclerView.Adapter<UsuarioAdminAdapter.UsuarioViewHolder> {
    private List<Usuario> usuarios;
    private OnUsuarioAdminListener listener;

    public interface OnUsuarioAdminListener {
        void onUsuarioDelete(Usuario usuario);
        void onUsuarioEdit(Usuario usuario);
    }

    public UsuarioAdminAdapter(List<Usuario> usuarios, OnUsuarioAdminListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario_admin, parent, false);
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
        private TextView tvNombre, tvEmail, tvJugador, tvRol;
        private Button btnEditar, btnEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvJugador = itemView.findViewById(R.id.tvJugador);
            tvRol = itemView.findViewById(R.id.tvRol);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(Usuario usuario) {
            tvNombre.setText(usuario.getNombre());
            tvEmail.setText(usuario.getEmail() != null ? usuario.getEmail() : "Sin email");
            tvJugador.setText(usuario.getJugador() != null ? usuario.getJugador() : "Sin jugador");
            tvRol.setText(usuario.getRol());

            // Configurar color del rol
            if (usuario.isEsAdmin()) {
                tvRol.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            } else {
                tvRol.setTextColor(itemView.getContext().getResources().getColor(R.color.gold));
            }

            btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUsuarioEdit(usuario);
                }
            });

            btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUsuarioDelete(usuario);
                }
            });
        }
    }
} 