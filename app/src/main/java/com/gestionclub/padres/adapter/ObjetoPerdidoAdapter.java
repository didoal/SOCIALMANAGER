package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.ObjetoPerdido;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ObjetoPerdidoAdapter extends RecyclerView.Adapter<ObjetoPerdidoAdapter.ObjetoViewHolder> {
    private List<ObjetoPerdido> objetos;
    private SimpleDateFormat dateFormat;
    private OnObjetoClickListener listener;

    public interface OnObjetoClickListener {
        void onReclamarClick(ObjetoPerdido objeto);
    }

    public ObjetoPerdidoAdapter(List<ObjetoPerdido> objetos, OnObjetoClickListener listener) {
        this.objetos = objetos;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ObjetoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_objeto_perdido, parent, false);
        return new ObjetoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjetoViewHolder holder, int position) {
        ObjetoPerdido objeto = objetos.get(position);
        holder.bind(objeto);
    }

    @Override
    public int getItemCount() {
        return objetos.size();
    }

    public void actualizarObjetos(List<ObjetoPerdido> nuevosObjetos) {
        this.objetos = nuevosObjetos;
        notifyDataSetChanged();
    }

    class ObjetoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombre;
        private TextView textViewEstado;
        private TextView textViewDescripcion;
        private TextView textViewUbicacion;
        private TextView textViewReportadoPor;
        private TextView textViewFecha;
        private Button buttonReclamar;

        public ObjetoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewUbicacion = itemView.findViewById(R.id.textViewUbicacion);
            textViewReportadoPor = itemView.findViewById(R.id.textViewReportadoPor);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            buttonReclamar = itemView.findViewById(R.id.buttonReclamar);
        }

        public void bind(ObjetoPerdido objeto) {
            textViewNombre.setText(objeto.getNombre());
            textViewEstado.setText(objeto.getEstado());
            textViewDescripcion.setText(objeto.getDescripcion());
            textViewUbicacion.setText("Ubicación: " + objeto.getUbicacion());
            textViewReportadoPor.setText("Reportado por: " + objeto.getReportadoPorNombre());
            textViewFecha.setText("Fecha: " + dateFormat.format(objeto.getFechaReporte()));

            // Configurar estado visual
            switch (objeto.getEstado()) {
                case "PERDIDO":
                    textViewEstado.setBackgroundResource(R.drawable.estado_background);
                    textViewEstado.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                    break;
                case "ENCONTRADO":
                    textViewEstado.setBackgroundResource(R.drawable.estado_background);
                    textViewEstado.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                    break;
                case "RECLAMADO":
                    textViewEstado.setBackgroundResource(R.drawable.estado_background);
                    textViewEstado.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                    break;
            }

            // Mostrar botón de reclamar solo si está encontrado
            if ("ENCONTRADO".equals(objeto.getEstado())) {
                buttonReclamar.setVisibility(View.VISIBLE);
                buttonReclamar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onReclamarClick(objeto);
                    }
                });
            } else {
                buttonReclamar.setVisibility(View.GONE);
            }
        }
    }
} 