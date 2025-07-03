package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Equipo;
import java.util.List;

public class EquipoAdapter extends RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder> {
    private List<Equipo> equipos;
    private OnEquipoClickListener listener;

    public interface OnEquipoClickListener {
        void onEquipoClick(Equipo equipo);
    }

    public EquipoAdapter(List<Equipo> equipos, OnEquipoClickListener listener) {
        this.equipos = equipos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipo, parent, false);
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
        private TextView textViewDescripcion;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewEntrenador = itemView.findViewById(R.id.textViewEntrenador);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEquipoClick(equipos.get(position));
                }
            });
        }

        public void bind(Equipo equipo) {
            textViewNombre.setText(equipo.getNombre());
            textViewCategoria.setText("Categoría: " + equipo.getCategoria());
            textViewEntrenador.setText("Entrenador: " + equipo.getEntrenadorNombre());
            textViewDescripcion.setText(equipo.getDescripcion());
        }
    }
} 