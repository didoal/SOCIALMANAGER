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

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    private List<Usuario> usuarios;
    private OnUsuarioDeleteListener deleteListener;

    public interface OnUsuarioDeleteListener {
        void onUsuarioDelete(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> usuarios, OnUsuarioDeleteListener deleteListener) {
        this.usuarios = usuarios;
        this.deleteListener = deleteListener;
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
        holder.textViewNombre.setText(usuario.getNombre());
        holder.textViewJugador.setText(usuario.getJugador());
        holder.buttonEliminar.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onUsuarioDelete(usuario);
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public void actualizarUsuarios(List<Usuario> nuevosUsuarios) {
        this.usuarios = nuevosUsuarios;
        notifyDataSetChanged();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewJugador;
        Button buttonEliminar;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewJugador = itemView.findViewById(R.id.textViewJugador);
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);
        }
    }
} 