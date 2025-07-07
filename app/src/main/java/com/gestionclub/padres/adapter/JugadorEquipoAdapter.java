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

public class JugadorEquipoAdapter extends RecyclerView.Adapter<JugadorEquipoAdapter.JugadorViewHolder> {
    private List<Usuario> jugadores;
    private OnJugadorClickListener listener;
    private boolean esListaEquipo; // true = quitar, false = agregar

    public interface OnJugadorClickListener {
        void onJugadorClick(Usuario jugador);
    }

    public JugadorEquipoAdapter(List<Usuario> jugadores, OnJugadorClickListener listener, boolean esListaEquipo) {
        this.jugadores = jugadores;
        this.listener = listener;
        this.esListaEquipo = esListaEquipo;
    }

    @NonNull
    @Override
    public JugadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jugador_equipo, parent, false);
        return new JugadorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JugadorViewHolder holder, int position) {
        Usuario jugador = jugadores.get(position);
        holder.bind(jugador);
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public void actualizarJugadores(List<Usuario> nuevosJugadores) {
        this.jugadores = nuevosJugadores;
        notifyDataSetChanged();
    }

    class JugadorViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombre;
        private TextView textViewJugador;
        private ImageButton buttonAccion;

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewJugador = itemView.findViewById(R.id.textViewJugador);
            buttonAccion = itemView.findViewById(R.id.buttonAccion);
        }

        public void bind(Usuario jugador) {
            textViewNombre.setText(jugador.getNombre());
            textViewJugador.setText(jugador.getJugador());
            buttonAccion.setImageResource(esListaEquipo ? R.drawable.ic_remove : R.drawable.ic_add);
            buttonAccion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJugadorClick(jugador);
                }
            });
        }
    }
} 