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
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.adapter.EventoAdapter;
import com.gestionclub.padres.adapter.DiaCalendarioAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarioFragment extends Fragment {
    
    // Vistas del layout
    private TextView tabListado, tabDia, tabSemana, tabMes, tabAno;
    private LinearLayout viewDia, viewSemana, viewMes, viewAno;
    private RecyclerView recyclerViewListado;
    private RecyclerView recyclerViewDias;
    private EventoAdapter eventoAdapter;
    private DiaCalendarioAdapter diaAdapter;
    
    // Elementos de navegación
    private ImageView btnPrevDay, btnPrevWeek, btnPrevMonth, btnPrevYear;
    private ImageView btnNextDay, btnNextWeek, btnNextMonth, btnNextYear;
    private TextView btnHoy, btnHoyWeek, btnHoyMonth, btnHoyYear;
    
    // Elementos de fecha
    private TextView textCurrentDate, textWeekInfo;
    private TextView textCurrentWeek, textWeekYear;
    private TextView textCurrentMonth;
    private TextView textCurrentYear;
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private Calendar currentDate;
    private SimpleDateFormat dateFormat, weekFormat, monthFormat;
    
    // Variable para rastrear la vista actual
    private String vistaActual = "listado"; // "listado", "dia", "semana", "mes", "ano"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_calendario, container, false);
            
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            currentDate = Calendar.getInstance();
            
            // Inicializar formatos de fecha
            dateFormat = new SimpleDateFormat("EEE, d MMM", new Locale("es"));
            weekFormat = new SimpleDateFormat("d - d MMM", new Locale("es"));
            monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es"));
            
            inicializarVistas(view);
            configurarTabs(view);
            configurarNavegacion(view);
            configurarBotones(view);
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
        
        // RecyclerView para días del calendario
        recyclerViewDias = view.findViewById(R.id.recyclerViewDias);
        
        // Elementos de navegación
        btnPrevDay = view.findViewById(R.id.btnPrevDay);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnPrevYear = view.findViewById(R.id.btnPrevYear);
        btnNextDay = view.findViewById(R.id.btnNextDay);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        btnNextYear = view.findViewById(R.id.btnNextYear);
        
        btnHoy = view.findViewById(R.id.btnHoy);
        btnHoyWeek = view.findViewById(R.id.btnHoyWeek);
        btnHoyMonth = view.findViewById(R.id.btnHoyMonth);
        btnHoyYear = view.findViewById(R.id.btnHoyYear);
        

        
        // Elementos de fecha
        textCurrentDate = view.findViewById(R.id.textCurrentDate);
        textWeekInfo = view.findViewById(R.id.textWeekInfo);
        textCurrentWeek = view.findViewById(R.id.textCurrentWeek);
        textWeekYear = view.findViewById(R.id.textWeekYear);
        textCurrentMonth = view.findViewById(R.id.textCurrentMonth);
        textCurrentYear = view.findViewById(R.id.textCurrentYear);
    }

    private void configurarBotones(View view) {
        // Configurar botón flotante para agregar evento
        FloatingActionButton fabAgregarEvento = view.findViewById(R.id.fabAgregarEvento);
        if (fabAgregarEvento != null) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                fabAgregarEvento.setVisibility(View.VISIBLE);
                fabAgregarEvento.setOnClickListener(v -> {
                    // Navegar a la gestión de eventos
                    requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new GestionEventosFragment())
                        .addToBackStack(null)
                        .commit();
                });
            } else {
                fabAgregarEvento.setVisibility(View.GONE);
            }
        }
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
        // Navegación unificada - anterior
        if (btnPrevDay != null) {
            btnPrevDay.setOnClickListener(v -> navegarAnterior());
        }
        
        if (btnPrevWeek != null) {
            btnPrevWeek.setOnClickListener(v -> navegarAnterior());
        }
        
        if (btnPrevMonth != null) {
            btnPrevMonth.setOnClickListener(v -> navegarAnterior());
        }
        
        if (btnPrevYear != null) {
            btnPrevYear.setOnClickListener(v -> navegarAnterior());
        }
        
        // Navegación unificada - siguiente
        if (btnNextDay != null) {
            btnNextDay.setOnClickListener(v -> navegarSiguiente());
        }
        
        if (btnNextWeek != null) {
            btnNextWeek.setOnClickListener(v -> navegarSiguiente());
        }
        
        if (btnNextMonth != null) {
            btnNextMonth.setOnClickListener(v -> navegarSiguiente());
        }
        
        if (btnNextYear != null) {
            btnNextYear.setOnClickListener(v -> navegarSiguiente());
        }
        
        // Botones "Hoy" unificados
        if (btnHoy != null) {
            btnHoy.setOnClickListener(v -> irAHoy());
        }
        
        if (btnHoyWeek != null) {
            btnHoyWeek.setOnClickListener(v -> irAHoy());
        }
        
        if (btnHoyMonth != null) {
            btnHoyMonth.setOnClickListener(v -> irAHoy());
        }
        
        if (btnHoyYear != null) {
            btnHoyYear.setOnClickListener(v -> irAHoy());
        }
    }

    private void navegarAnterior() {
        switch (vistaActual) {
            case "dia":
                currentDate.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case "semana":
                currentDate.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case "mes":
                currentDate.add(Calendar.MONTH, -1);
                break;
            case "ano":
                currentDate.add(Calendar.YEAR, -1);
                break;
            default:
                // Para listado, no hay navegación temporal
                break;
        }
        actualizarFechaActual();
        actualizarVistaActual();
    }

    private void navegarSiguiente() {
        switch (vistaActual) {
            case "dia":
                currentDate.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case "semana":
                currentDate.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "mes":
                currentDate.add(Calendar.MONTH, 1);
                break;
            case "ano":
                currentDate.add(Calendar.YEAR, 1);
                break;
            default:
                // Para listado, no hay navegación temporal
                break;
        }
        actualizarFechaActual();
        actualizarVistaActual();
    }

    private void irAHoy() {
        currentDate = Calendar.getInstance();
        actualizarFechaActual();
        actualizarVistaActual();
    }

    private void actualizarVistaActual() {
        // Actualizar la vista según el tipo actual
        switch (vistaActual) {
            case "dia":
                if (diaAdapter != null) {
                    List<Calendar> diasSemana = obtenerDiasSemana();
                    diaAdapter.actualizarDias(diasSemana, currentDate);
                }
                cargarEventosDelDia();
                break;
            case "semana":
                // Actualizar vista de semana si es necesario
                break;
            case "mes":
                // Actualizar vista de mes si es necesario
                break;
            case "ano":
                // Actualizar vista de año si es necesario
                break;
            default:
                break;
        }
    }

    private void mostrarVistaListado() {
        ocultarTodasLasVistas();
        recyclerViewListado.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabListado);
        vistaActual = "listado";
        cargarVistaListado();
    }

    private void mostrarVistaDia() {
        ocultarTodasLasVistas();
        viewDia.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabDia);
        vistaActual = "dia";
        actualizarFechaActual();
        cargarDiasSemana();
    }

    private void mostrarVistaSemana() {
        ocultarTodasLasVistas();
        viewSemana.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabSemana);
        vistaActual = "semana";
        actualizarFechaActual();
    }

    private void mostrarVistaMes() {
        ocultarTodasLasVistas();
        viewMes.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabMes);
        vistaActual = "mes";
        actualizarFechaActual();
    }

    private void mostrarVistaAno() {
        ocultarTodasLasVistas();
        viewAno.setVisibility(View.VISIBLE);
        actualizarTabSeleccionado(tabAno);
        vistaActual = "ano";
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
            
            eventoAdapter = new EventoAdapter(eventos);
            recyclerViewListado.setAdapter(eventoAdapter);
            
            // Configurar listener del adaptador
            eventoAdapter.setOnEventoClickListener(evento -> {
                mostrarDetallesEvento(evento);
            });
        }
    }

    private void mostrarDetallesEvento(Evento evento) {
        // Aquí puedes mostrar un diálogo con los detalles del evento
        // Por ahora solo mostraremos un Toast
        android.widget.Toast.makeText(requireContext(), 
            "Evento: " + evento.getTitulo(), 
            android.widget.Toast.LENGTH_SHORT).show();
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

    private void cargarDiasSemana() {
        if (recyclerViewDias != null) {
            List<Calendar> diasSemana = obtenerDiasSemana();
            
            recyclerViewDias.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            
            diaAdapter = new DiaCalendarioAdapter(diasSemana, currentDate);
            recyclerViewDias.setAdapter(diaAdapter);
            
            // Configurar listener del adaptador
            diaAdapter.setOnDiaClickListener(dia -> {
                currentDate = dia;
                actualizarFechaActual();
                cargarEventosDelDia();
            });
        }
    }

    private List<Calendar> obtenerDiasSemana() {
        List<Calendar> dias = new ArrayList<>();
        Calendar inicioSemana = (Calendar) currentDate.clone();
        
        // Ir al lunes de la semana actual
        int diaSemana = inicioSemana.get(Calendar.DAY_OF_WEEK);
        if (diaSemana == Calendar.SUNDAY) {
            inicioSemana.add(Calendar.DAY_OF_WEEK, -6);
        } else {
            inicioSemana.add(Calendar.DAY_OF_WEEK, -(diaSemana - Calendar.MONDAY));
        }
        
        // Agregar los 7 días de la semana
        for (int i = 0; i < 7; i++) {
            Calendar dia = (Calendar) inicioSemana.clone();
            dia.add(Calendar.DAY_OF_WEEK, i);
            dias.add(dia);
        }
        
        return dias;
    }

    private void cargarEventosDelDia() {
        // Aquí se cargarían los eventos del día seleccionado
        // Por ahora solo actualizamos la fecha
        if (textCurrentDate != null) {
            String fechaActual = dateFormat.format(currentDate.getTime());
            textCurrentDate.setText(fechaActual);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar eventos cuando se vuelve a la vista
        if (eventoAdapter != null) {
            List<Evento> eventos = dataManager.getEventos();
            eventoAdapter.actualizarEventos(eventos);
        }
        
        // Actualizar días si estamos en vista de día
        if (diaAdapter != null && viewDia.getVisibility() == View.VISIBLE) {
            List<Calendar> diasSemana = obtenerDiasSemana();
            diaAdapter.actualizarDias(diasSemana, currentDate);
        }
    }
} 