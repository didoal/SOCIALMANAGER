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
import com.gestionclub.padres.model.ResultadoPartido;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ResultadoPartidoAdapter extends RecyclerView.Adapter<ResultadoPartidoAdapter.ResultadoPartidoViewHolder> {
    private List<ResultadoPartido> resultados;
    private SimpleDateFormat dateFormat;
    private OnResultadoPartidoClickListener listener;

    public interface OnResultadoPartidoClickListener {
        void onResultadoClick(ResultadoPartido resultado);
    }

    public ResultadoPartidoAdapter(List<ResultadoPartido> resultados) {
        this.resultados = resultados;
        this.dateFormat = new SimpleDateFormat("EEE, d MMM", new Locale("es", "ES"));
    }

    public void setOnResultadoPartidoClickListener(OnResultadoPartidoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultadoPartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultado_partido, parent, false);
        return new ResultadoPartidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoPartidoViewHolder holder, int position) {
        ResultadoPartido resultado = resultados.get(position);
        holder.bind(resultado);
    }

    @Override
    public int getItemCount() {
        return resultados.size();
    }

    public void actualizarResultados(List<ResultadoPartido> nuevosResultados) {
        this.resultados = nuevosResultados;
        notifyDataSetChanged();
    }

    public class ResultadoPartidoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewFecha;
        private TextView textViewEquipoLocal;
        private TextView textViewEquipoVisitante;
        private TextView textViewResultado;
        private TextView textViewResultadoPrimera;
        private ImageView imageViewEquipoLocal;
        private ImageView imageViewEquipoVisitante;
        private View viewEstado;

        public ResultadoPartidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewEquipoLocal = itemView.findViewById(R.id.textViewEquipoLocal);
            textViewEquipoVisitante = itemView.findViewById(R.id.textViewEquipoVisitante);
            textViewResultado = itemView.findViewById(R.id.textViewResultado);
            textViewResultadoPrimera = itemView.findViewById(R.id.textViewResultadoPrimera);
            imageViewEquipoLocal = itemView.findViewById(R.id.imageViewEquipoLocal);
            imageViewEquipoVisitante = itemView.findViewById(R.id.imageViewEquipoVisitante);
            viewEstado = itemView.findViewById(R.id.viewEstado);

            // Configurar click en el item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onResultadoClick(resultados.get(position));
                }
            });
        }

        public void bind(ResultadoPartido resultado) {
            Context context = itemView.getContext();

            // Configurar fecha
            if (resultado.getFechaPartido() != null) {
                String fecha = dateFormat.format(resultado.getFechaPartido()) + " - " + resultado.getUbicacion();
                textViewFecha.setText(fecha);
            } else {
                textViewFecha.setText("Fecha no disponible");
            }

            // Configurar equipos
            textViewEquipoLocal.setText(resultado.getEquipoLocal());
            textViewEquipoVisitante.setText(resultado.getEquipoVisitante());

            // Configurar resultado
            if (resultado.isFinalizado()) {
                textViewResultado.setText(resultado.getResultadoCompleto());
                textViewResultadoPrimera.setText(resultado.getResultadoPrimera());
                
                // Configurar color del estado según el resultado
                if (resultado.isVictoria()) {
                    viewEstado.setBackgroundResource(R.drawable.circle_button_green);
                    textViewEquipoLocal.setTextColor(context.getResources().getColor(R.color.green));
                } else if (resultado.isEmpate()) {
                    viewEstado.setBackgroundResource(R.drawable.circle_button_gold);
                    textViewEquipoLocal.setTextColor(context.getResources().getColor(R.color.gold));
                } else {
                    viewEstado.setBackgroundResource(R.drawable.circle_button_red);
                    textViewEquipoLocal.setTextColor(context.getResources().getColor(R.color.red));
                }
            } else if (resultado.isEnCurso()) {
                textViewResultado.setText("EN CURSO");
                textViewResultadoPrimera.setText("");
                viewEstado.setBackgroundResource(R.drawable.circle_button_blue);
                textViewEquipoLocal.setTextColor(context.getResources().getColor(R.color.blue_profesional));
            } else {
                textViewResultado.setText("PENDIENTE");
                textViewResultadoPrimera.setText("");
                viewEstado.setBackgroundResource(R.drawable.circle_button_gray);
                textViewEquipoLocal.setTextColor(context.getResources().getColor(R.color.text_secondary));
            }

            // Configurar avatares de equipos
            imageViewEquipoLocal.setImageResource(R.drawable.logo_santiaguino_guizan);
            imageViewEquipoVisitante.setImageResource(R.drawable.ic_team);
        }
    }
} 