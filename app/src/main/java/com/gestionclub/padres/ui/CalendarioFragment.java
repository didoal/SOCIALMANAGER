package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarioFragment extends Fragment {
    
    // Vistas del nuevo layout
    private TextView tabListado, tabDia, tabSemana, tabMes, tabAno;
    private LinearLayout viewDia, viewSemana, viewMes, viewAno;
    private RecyclerView recyclerViewListado;
    private TextView textCurrentDate, textWeekInfo;
    private DataManager dataManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_calendario, container, false);
            
            dataManager = new DataManager(requireContext());
            inicializarVistas(view);
            configurarTabs(view);
            cargarVistaListado();
            actualizarFechaActual();
            
            return view;
        } catch (Exception e) {
            Log.e("CalendarioFragment", "Error crítico al crear vista de CalendarioFragment", e);
            TextView errorView = new TextView(requireContext());
            errorView.setText("Error al cargar calendario");
            return errorView;
        }
    }

    private void inicializarVistas(View view) {
        // Tabs
        tabListado = view.findViewById(R.id.tabListado);
        tabDia = view.findViewById(R.id.tabDia);
        tabSemana = view.findViewById(R.id.tabSemana);
        tabMes = view.findViewById(R.id.tabMes);
        tabAno = view.findViewById(R.id.tabAno);
        
        // Vistas
        viewDia = view.findViewById(R.id.viewDia);
        viewSemana = view.findViewById(R.id.viewSemana);
        viewMes = view.findViewById(R.id.viewMes);
        viewAno = view.findViewById(R.id.viewAno);
        
        // RecyclerView para listado
        recyclerViewListado = view.findViewById(R.id.recyclerViewListado);
        
        // Elementos de la vista día
        textCurrentDate = view.findViewById(R.id.textCurrentDate);
        textWeekInfo = view.findViewById(R.id.textWeekInfo);
    }

    private void configurarTabs(View view) {
        // Configurar listeners para las pestañas
        if (tabListado != null) {
            tabListado.setOnClickListener(v -> mostrarVistaListado());
        }
        if (tabDia != null) {
            tabDia.setOnClickListener(v -> mostrarVistaDia());
        }
        if (tabSemana != null) {
            tabSemana.setOnClickListener(v -> mostrarVistaSemana());
        }
        if (tabMes != null) {
            tabMes.setOnClickListener(v -> mostrarVistaMes());
        }
        if (tabAno != null) {
            tabAno.setOnClickListener(v -> mostrarVistaAno());
        }
    }

    private void mostrarVistaListado() {
        ocultarTodasLasVistas();
        recyclerViewListado.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabListado);
        cargarVistaListado();
    }

    private void mostrarVistaDia() {
        ocultarTodasLasVistas();
        viewDia.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabDia);
        actualizarFechaActual();
    }

    private void mostrarVistaSemana() {
        ocultarTodasLasVistas();
        viewSemana.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabSemana);
    }

    private void mostrarVistaMes() {
        ocultarTodasLasVistas();
        viewMes.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabMes);
    }

    private void mostrarVistaAno() {
        ocultarTodasLasVistas();
        viewAno.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabAno);
    }

    private void ocultarTodasLasVistas() {
        if (recyclerViewListado != null) recyclerViewListado.setVisibility(View.GONE);
        if (viewDia != null) viewDia.setVisibility(View.GONE);
        if (viewSemana != null) viewSemana.setVisibility(View.GONE);
        if (viewMes != null) viewMes.setVisibility(View.GONE);
        if (viewAno != null) viewAno.setVisibility(View.GONE);
    }

    private void actualizarTabSeleccionado(TextView tabSeleccionado) {
        // Resetear todos los tabs
        if (tabListado != null) tabListado.setBackground(null);
        if (tabDia != null) tabDia.setBackground(null);
        if (tabSemana != null) tabSemana.setBackground(null);
        if (tabMes != null) tabMes.setBackground(null);
        if (tabAno != null) tabAno.setBackground(null);
        
        // Marcar el tab seleccionado
        if (tabSeleccionado != null) {
            tabSeleccionado.setBackgroundResource(R.drawable.tab_selected_background);
        }
    }

    private void cargarVistaListado() {
        if (recyclerViewListado != null) {
            List<Evento> eventos = dataManager.getEventos();
            recyclerViewListado.setLayoutManager(new LinearLayoutManager(requireContext()));
            // Aquí puedes configurar un adaptador para mostrar los eventos
            // recyclerViewListado.setAdapter(new EventoAdapter(eventos));
        }
    }

    private void actualizarFechaActual() {
        if (textCurrentDate != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM", new Locale("es"));
            String fechaActual = dateFormat.format(calendar.getTime());
            textCurrentDate.setText(fechaActual);
        }
        
        if (textWeekInfo != null) {
            Calendar calendar = Calendar.getInstance();
            int semana = calendar.get(Calendar.WEEK_OF_YEAR);
            int año = calendar.get(Calendar.YEAR);
            textWeekInfo.setText("Semana " + semana + ", " + año);
        }
    }
} 