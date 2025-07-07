package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Equipo;

import java.util.List;

public class EquipoAdapter extends RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder> {
    
    private List<Equipo> equipos;
    private OnEliminarClickListener listenerEliminar;
    private OnEditarClickListener listenerEditar;
    private OnGestionarJugadoresListener listenerGestionarJugadores;

    public interface OnEliminarClickListener {
        void onEliminarClick(Equipo equipo);
    }

    public interface OnEditarClickListener {
        void onEditarClick(Equipo equipo);
    }

    public interface OnGestionarJugadoresListener {
        void onGestionarJugadoresClick(Equipo equipo);
    }

    public EquipoAdapter(List<Equipo> equipos, OnEliminarClickListener listenerEliminar, 
                        OnEditarClickListener listenerEditar, OnGestionarJugadoresListener listenerGestionarJugadores) {
        this.equipos = equipos;
        this.listenerEliminar = listenerEliminar;
        this.listenerEditar = listenerEditar;
        this.listenerGestionarJugadores = listenerGestionarJugadores;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipo, parent, false);
        return new EquipoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        Equipo equipo = equipos.get(position);
        holder.bind(equipo);
    }

    @Override
    public int getItemCount() {
        return equipos.size();
    }

    public void actualizarEquipos(List<Equipo> nuevosEquipos) {
        this.equipos = nuevosEquipos;
        notifyDataSetChanged();
    }

    class EquipoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombre;
        private TextView textViewCategoria;
        private TextView textViewEntrenador;
        private TextView textViewJugadores;
        private ImageButton buttonEditar;
        private ImageButton buttonGestionarJugadores;
        private ImageButton buttonEliminar;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewEntrenador = itemView.findViewById(R.id.textViewEntrenador);
            textViewJugadores = itemView.findViewById(R.id.textViewJugadores);
            buttonEditar = itemView.findViewById(R.id.buttonEditar);
            buttonGestionarJugadores = itemView.findViewById(R.id.buttonGestionarJugadores);
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);
        }

        public void bind(Equipo equipo) {
            textViewNombre.setText(equipo.getNombre());
            textViewCategoria.setText("Categoría: " + equipo.getCategoria());
            
            if (equipo.getEntrenador() != null && !equipo.getEntrenador().isEmpty()) {
                textViewEntrenador.setText("Entrenador: " + equipo.getEntrenador());
                textViewEntrenador.setVisibility(View.VISIBLE);
            } else {
                textViewEntrenador.setVisibility(View.GONE);
            }
            
            // Mostrar número de jugadores
            int numJugadores = equipo.getJugadoresIds() != null ? equipo.getJugadoresIds().size() : 0;
            textViewJugadores.setText(numJugadores + " jugador" + (numJugadores != 1 ? "es" : ""));
            
            // Configurar botones
            buttonEditar.setOnClickListener(v -> {
                if (listenerEditar != null) {
                    listenerEditar.onEditarClick(equipo);
                }
            });
            
            buttonGestionarJugadores.setOnClickListener(v -> {
                if (listenerGestionarJugadores != null) {
                    listenerGestionarJugadores.onGestionarJugadoresClick(equipo);
                }
            });
            
            buttonEliminar.setOnClickListener(v -> {
                if (listenerEliminar != null) {
                    listenerEliminar.onEliminarClick(equipo);
                }
            });
        }
    }
} 