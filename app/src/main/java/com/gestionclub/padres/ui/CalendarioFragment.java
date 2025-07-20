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
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarioFragment extends Fragment {
    
    // Vistas del layout
    private TextView tabListado, tabDia, tabSemana, tabMes, tabAno;
    private LinearLayout viewDia, viewSemana, viewMes, viewAno;
    private RecyclerView recyclerViewListado;
    
    // Elementos de navegación
    private ImageView btnPrevDay, btnPrevWeek, btnPrevMonth, btnPrevYear;
    private ImageView btnNextMonth, btnNextYear;
    private TextView btnHoy, btnHoyWeek, btnHoyMonth, btnHoyYear;
    private ImageView btnFilter, btnFilterWeek;
    
    // Elementos de fecha
    private TextView textCurrentDate, textWeekInfo;
    private TextView textCurrentWeek, textWeekYear;
    private TextView textCurrentMonth;
    private TextView textCurrentYear;
    
    private DataManager dataManager;
    private Calendar currentDate;
    private SimpleDateFormat dateFormat, weekFormat, monthFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_calendario, container, false);
            
            dataManager = new DataManager(requireContext());
            currentDate = Calendar.getInstance();
            
            // Inicializar formatos de fecha
            dateFormat = new SimpleDateFormat("EEE, d MMM", new Locale("es"));
            weekFormat = new SimpleDateFormat("d - d MMM", new Locale("es"));
            monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es"));
            
            inicializarVistas(view);
            configurarTabs(view);
            configurarNavegacion(view);
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
        
        // Elementos de navegación
        btnPrevDay = view.findViewById(R.id.btnPrevDay);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnPrevYear = view.findViewById(R.id.btnPrevYear);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        btnNextYear = view.findViewById(R.id.btnNextYear);
        
        btnHoy = view.findViewById(R.id.btnHoy);
        btnHoyWeek = view.findViewById(R.id.btnHoyWeek);
        btnHoyMonth = view.findViewById(R.id.btnHoyMonth);
        btnHoyYear = view.findViewById(R.id.btnHoyYear);
        
        btnFilter = view.findViewById(R.id.btnFilter);
        btnFilterWeek = view.findViewById(R.id.btnFilterWeek);
        
        // Elementos de fecha
        textCurrentDate = view.findViewById(R.id.textCurrentDate);
        textWeekInfo = view.findViewById(R.id.textWeekInfo);
        textCurrentWeek = view.findViewById(R.id.textCurrentWeek);
        textWeekYear = view.findViewById(R.id.textWeekYear);
        textCurrentMonth = view.findViewById(R.id.textCurrentMonth);
        textCurrentYear = view.findViewById(R.id.textCurrentYear);
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

    private void configurarNavegacion(View view) {
        // Navegación de día
        if (btnPrevDay != null) {
            btnPrevDay.setOnClickListener(v -> {
                currentDate.add(Calendar.DAY_OF_MONTH, -1);
                actualizarFechaActual();
            });
        }
        
        if (btnHoy != null) {
            btnHoy.setOnClickListener(v -> {
                currentDate = Calendar.getInstance();
                actualizarFechaActual();
            });
        }
        
        // Navegación de semana
        if (btnPrevWeek != null) {
            btnPrevWeek.setOnClickListener(v -> {
                currentDate.add(Calendar.WEEK_OF_YEAR, -1);
                actualizarFechaActual();
            });
        }
        
        if (btnHoyWeek != null) {
            btnHoyWeek.setOnClickListener(v -> {
                currentDate = Calendar.getInstance();
                actualizarFechaActual();
            });
        }
        
        // Navegación de mes
        if (btnPrevMonth != null) {
            btnPrevMonth.setOnClickListener(v -> {
                currentDate.add(Calendar.MONTH, -1);
                actualizarFechaActual();
            });
        }
        
        if (btnNextMonth != null) {
            btnNextMonth.setOnClickListener(v -> {
                currentDate.add(Calendar.MONTH, 1);
                actualizarFechaActual();
            });
        }
        
        if (btnHoyMonth != null) {
            btnHoyMonth.setOnClickListener(v -> {
                currentDate = Calendar.getInstance();
                actualizarFechaActual();
            });
        }
        
        // Navegación de año
        if (btnPrevYear != null) {
            btnPrevYear.setOnClickListener(v -> {
                currentDate.add(Calendar.YEAR, -1);
                actualizarFechaActual();
            });
        }
        
        if (btnNextYear != null) {
            btnNextYear.setOnClickListener(v -> {
                currentDate.add(Calendar.YEAR, 1);
                actualizarFechaActual();
            });
        }
        
        if (btnHoyYear != null) {
            btnHoyYear.setOnClickListener(v -> {
                currentDate = Calendar.getInstance();
                actualizarFechaActual();
            });
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
        actualizarFechaActual();
    }

    private void mostrarVistaMes() {
        ocultarTodasLasVistas();
        viewMes.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabMes);
        actualizarFechaActual();
    }

    private void mostrarVistaAno() {
        ocultarTodasLasVistas();
        viewAno.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabAno);
        actualizarFechaActual();
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
        // Actualizar vista de día
        if (textCurrentDate != null) {
            String fechaActual = dateFormat.format(currentDate.getTime());
            textCurrentDate.setText(fechaActual);
        }
        
        if (textWeekInfo != null) {
            int semana = currentDate.get(Calendar.WEEK_OF_YEAR);
            int año = currentDate.get(Calendar.YEAR);
            textWeekInfo.setText("Semana " + semana + ", " + año);
        }
        
        // Actualizar vista de semana
        if (textCurrentWeek != null) {
            Calendar inicioSemana = (Calendar) currentDate.clone();
            inicioSemana.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Calendar finSemana = (Calendar) inicioSemana.clone();
            finSemana.add(Calendar.DAY_OF_WEEK, 6);
            
            String rangoSemana = weekFormat.format(inicioSemana.getTime()) + " " + 
                               new SimpleDateFormat("MMM", new Locale("es")).format(finSemana.getTime());
            textCurrentWeek.setText(rangoSemana);
        }
        
        if (textWeekYear != null) {
            int semana = currentDate.get(Calendar.WEEK_OF_YEAR);
            int año = currentDate.get(Calendar.YEAR);
            textWeekYear.setText("Semana " + semana + ", " + año);
        }
        
        // Actualizar vista de mes
        if (textCurrentMonth != null) {
            String mesActual = monthFormat.format(currentDate.getTime());
            textCurrentMonth.setText(mesActual);
        }
        
        // Actualizar vista de año
        if (textCurrentYear != null) {
            int año = currentDate.get(Calendar.YEAR);
            textCurrentYear.setText(String.valueOf(año));
        }
    }
} 