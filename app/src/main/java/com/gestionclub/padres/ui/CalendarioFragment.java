package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Evento;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class CalendarioFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        
        TextView tvTitulo = view.findViewById(R.id.tv_titulo);
        tvTitulo.setText("Calendario de Eventos");
        LinearLayout contenedor = view.findViewById(R.id.contenedorEventos);

        List<Evento> eventos = cargarEventos();
        if (eventos.isEmpty()) {
            TextView tvVacio = new TextView(getContext());
            tvVacio.setText("No hay eventos disponibles");
            tvVacio.setTextColor(getResources().getColor(R.color.gray));
            tvVacio.setTextSize(16);
            tvVacio.setPadding(0, 16, 0, 16);
            contenedor.addView(tvVacio);
        } else {
            for (Evento evento : eventos) {
                TextView tvEvento = new TextView(getContext());
                tvEvento.setText(evento.getTitulo() + " | " + evento.getTipo());
                tvEvento.setTextColor(getResources().getColor(R.color.white));
                tvEvento.setTextSize(16);
                tvEvento.setPadding(0, 8, 0, 8);
                contenedor.addView(tvEvento);
            }
        }
        return view;
    }

    private List<Evento> cargarEventos() {
        String json = requireContext().getSharedPreferences("eventos", 0).getString("eventos", "[]");
        return new Gson().fromJson(json, new TypeToken<ArrayList<Evento>>(){}.getType());
    }
} 