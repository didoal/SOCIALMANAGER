package com.gestionclub.padres.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DiaCalendarioAdapter extends RecyclerView.Adapter<DiaCalendarioAdapter.DiaViewHolder> {
    private List<Calendar> dias;
    private Calendar diaSeleccionado;
    private OnDiaClickListener listener;

    public interface OnDiaClickListener {
        void onDiaClick(Calendar dia);
    }

    public DiaCalendarioAdapter(List<Calendar> dias, Calendar diaSeleccionado) {
        this.dias = dias;
        this.diaSeleccionado = diaSeleccionado;
    }

    public void setOnDiaClickListener(OnDiaClickListener listener) {
        this.listener = listener;
    }

    public void actualizarDias(List<Calendar> nuevosDias, Calendar nuevoDiaSeleccionado) {
        this.dias = nuevosDias;
        this.diaSeleccionado = nuevoDiaSeleccionado;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dia_calendario, parent, false);
        return new DiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaViewHolder holder, int position) {
        Calendar dia = dias.get(position);
        holder.bind(dia);
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    public class DiaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDia;

        public DiaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDia = itemView.findViewById(R.id.textViewDia);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDiaClick(dias.get(position));
                }
            });
        }

        public void bind(Calendar dia) {
            int diaDelMes = dia.get(Calendar.DAY_OF_MONTH);
            textViewDia.setText(String.valueOf(diaDelMes));

            // Verificar si es el día seleccionado
            if (diaSeleccionado != null && 
                dia.get(Calendar.YEAR) == diaSeleccionado.get(Calendar.YEAR) &&
                dia.get(Calendar.MONTH) == diaSeleccionado.get(Calendar.MONTH) &&
                dia.get(Calendar.DAY_OF_MONTH) == diaSeleccionado.get(Calendar.DAY_OF_MONTH)) {
                
                textViewDia.setBackgroundResource(R.drawable.circle_button_blue);
                textViewDia.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
                textViewDia.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            } else {
                textViewDia.setBackgroundResource(R.drawable.circle_background_white);
                textViewDia.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
                textViewDia.setTypeface(android.graphics.Typeface.DEFAULT);
            }

            // Verificar si es hoy
            Calendar hoy = Calendar.getInstance();
            if (dia.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                dia.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                dia.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH)) {
                
                textViewDia.setBackgroundResource(R.drawable.circle_gold);
                textViewDia.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            }
        }
    }
} 