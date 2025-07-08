package com.gestionclub.padres.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.ObjetoPerdido;
import com.gestionclub.padres.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EstadisticasFragment extends Fragment {
    // Vistas principales del nuevo diseño
    private TextView tvTotalEventos, tvTotalAsistencias, tvTotalMensajes, tvTotalObjetos;
    private TextView tvEventosRecurrentes, tvPromedioAsistencia, tvObjetosEncontrados;
    private LinearLayout layoutGraficoEventos, layoutGraficoAsistencia;
    private TextView tvGraficoEventos, tvGraficoAsistencia;
    private DataManager dataManager;
    private Spinner spinnerEquipos, spinnerJugadores;
    private LinearLayout contenedorEstadisticasJugador;
    private Usuario jugadorSeleccionado = null;
    
    // Filtros de fecha
    private Button buttonFechaInicio, buttonFechaFin;
    private Date fechaInicio = null, fechaFin = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        try {
            dataManager = new DataManager(requireContext());
            inicializarVistas(view);
            configurarSpinners();
            cargarEstadisticas();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al cargar estadísticas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        // Vistas principales
        tvTotalEventos = view.findViewById(R.id.tvTotalEventos);
        tvTotalAsistencias = view.findViewById(R.id.tvTotalAsistencias);
        tvTotalMensajes = view.findViewById(R.id.tvTotalMensajes);
        tvTotalObjetos = view.findViewById(R.id.tvTotalObjetos);
        tvEventosRecurrentes = view.findViewById(R.id.tvEventosRecurrentes);
        tvPromedioAsistencia = view.findViewById(R.id.tvPromedioAsistencia);
        tvObjetosEncontrados = view.findViewById(R.id.tvObjetosEncontrados);
        layoutGraficoEventos = view.findViewById(R.id.layoutGraficoEventos);
        layoutGraficoAsistencia = view.findViewById(R.id.layoutGraficoAsistencia);
        tvGraficoEventos = view.findViewById(R.id.tvGraficoEventos);
        tvGraficoAsistencia = view.findViewById(R.id.tvGraficoAsistencia);
        
        // Spinners y contenedores
        spinnerEquipos = view.findViewById(R.id.spinnerEquipos);
        spinnerJugadores = view.findViewById(R.id.spinnerJugadores);
        contenedorEstadisticasJugador = view.findViewById(R.id.contenedorEstadisticasJugador);
        
        // Filtros de fecha
        buttonFechaInicio = view.findViewById(R.id.buttonFechaInicio);
        buttonFechaFin = view.findViewById(R.id.buttonFechaFin);
        Button buttonLimpiarFiltros = view.findViewById(R.id.buttonLimpiarFiltros);
        Button buttonExportarReporte = view.findViewById(R.id.buttonExportarReporte);
        
        // Configurar filtros solo si las vistas existen
        if (buttonFechaInicio != null && buttonFechaFin != null) {
            configurarFiltrosFecha();
        }
        if (buttonLimpiarFiltros != null) {
            configurarBotonLimpiar(buttonLimpiarFiltros);
        }
        if (buttonExportarReporte != null) {
            configurarBotonExportar(buttonExportarReporte);
        }
    }

    private void configurarSpinners() {
        if (spinnerEquipos != null) {
            // Configurar spinner de equipos
            List<Equipo> equipos = dataManager.getEquipos();
            List<String> nombresEquipos = new ArrayList<>();
            nombresEquipos.add("Todos los equipos");
            for (Equipo equipo : equipos) {
                nombresEquipos.add(equipo.getNombreCompleto());
            }

            ArrayAdapter<String> adapterEquipos = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, nombresEquipos);
            adapterEquipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEquipos.setAdapter(adapterEquipos);

            // Listeners con filtros mejorados
            spinnerEquipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        Equipo equipoSeleccionado = equipos.get(position - 1);
                        filtrarPorEquipo(equipoSeleccionado);
                    } else {
                        cargarEstadisticas();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        if (spinnerJugadores != null) {
            // Configurar spinner de jugadores
            List<Usuario> usuarios = dataManager.getUsuarios();
            List<String> nombresJugadores = new ArrayList<>();
            nombresJugadores.add("Todos los jugadores");
            for (Usuario usuario : usuarios) {
                if (usuario.isEsPadre() && usuario.getJugador() != null && !usuario.getJugador().isEmpty()) {
                    nombresJugadores.add(usuario.getJugador());
                }
            }

            ArrayAdapter<String> adapterJugadores = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, nombresJugadores);
            adapterJugadores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerJugadores.setAdapter(adapterJugadores);

            spinnerJugadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        String nombreJugador = nombresJugadores.get(position);
                        for (Usuario usuario : usuarios) {
                            if (usuario.getJugador() != null && usuario.getJugador().equals(nombreJugador)) {
                                jugadorSeleccionado = usuario;
                                mostrarEstadisticasJugador(usuario);
                                break;
                            }
                        }
                    } else {
                        jugadorSeleccionado = null;
                        if (contenedorEstadisticasJugador != null) {
                            contenedorEstadisticasJugador.removeAllViews();
                        }
                        cargarEstadisticas();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private void filtrarPorEquipo(Equipo equipo) {
        List<Usuario> jugadoresEquipo = dataManager.getJugadoresPorEquipo(equipo.getId());
        List<Asistencia> asistenciasEquipo = new ArrayList<>();
        List<Evento> eventos = dataManager.getEventos();
        List<Asistencia> todasAsistencias = dataManager.getAsistencias();

        // Filtrar asistencias del equipo
        for (Asistencia asistencia : todasAsistencias) {
            for (Usuario jugador : jugadoresEquipo) {
                if (asistencia.getJugadorNombre().equalsIgnoreCase(jugador.getJugador())) {
                    asistenciasEquipo.add(asistencia);
                    break;
                }
            }
        }

        // Filtrar eventos del equipo
        List<Evento> eventosEquipo = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.getEquipo() != null && evento.getEquipo().equals(equipo.getNombre())) {
                eventosEquipo.add(evento);
            }
        }

        mostrarEstadisticasEquipo(equipo, jugadoresEquipo, asistenciasEquipo, eventosEquipo);
    }

    private void mostrarEstadisticasEquipo(Equipo equipo, List<Usuario> jugadores, List<Asistencia> asistencias, List<Evento> eventos) {
        if (contenedorEstadisticasJugador != null) {
            contenedorEstadisticasJugador.removeAllViews();

            // Título del equipo
            TextView tvTituloEquipo = new TextView(requireContext());
            tvTituloEquipo.setText("Estadísticas del Equipo: " + equipo.getNombreCompleto());
            tvTituloEquipo.setTextSize(20);
            tvTituloEquipo.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTituloEquipo.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvTituloEquipo.setPadding(0, 16, 0, 16);
            contenedorEstadisticasJugador.addView(tvTituloEquipo);

            // Estadísticas del equipo
            int totalJugadores = jugadores.size();
            int totalAsistencias = asistencias.size();
            int asistenciasPositivas = 0;
            int totalEventos = eventos.size();

            for (Asistencia asistencia : asistencias) {
                if (asistencia.isAsistira()) {
                    asistenciasPositivas++;
                }
            }

            double promedioAsistencia = totalEventos > 0 ? (double) asistenciasPositivas / totalEventos * 100 : 0;

            // Crear vistas para mostrar estadísticas del equipo
            agregarEstadistica("Total de jugadores", String.valueOf(totalJugadores));
            agregarEstadistica("Total de eventos", String.valueOf(totalEventos));
            agregarEstadistica("Total de asistencias", String.valueOf(totalAsistencias));
            agregarEstadistica("Asistencias positivas", String.valueOf(asistenciasPositivas));
            agregarEstadistica("Promedio de asistencia", String.format("%.1f%%", promedioAsistencia));

            // Mostrar estadísticas por tipo de evento
            mostrarEstadisticasPorTipoEvento(eventos, asistencias);
        }
    }

    private void agregarEstadistica(String titulo, String valor) {
        if (contenedorEstadisticasJugador != null) {
            LinearLayout layoutEstadistica = new LinearLayout(requireContext());
            layoutEstadistica.setOrientation(LinearLayout.HORIZONTAL);
            layoutEstadistica.setPadding(0, 8, 0, 8);

            TextView tvTitulo = new TextView(requireContext());
            tvTitulo.setText(titulo + ": ");
            tvTitulo.setTextSize(16);
            tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvValor = new TextView(requireContext());
            tvValor.setText(valor);
            tvValor.setTextSize(16);

            layoutEstadistica.addView(tvTitulo);
            layoutEstadistica.addView(tvValor);
            contenedorEstadisticasJugador.addView(layoutEstadistica);
        }
    }

    private void mostrarEstadisticasPorTipoEvento(List<Evento> eventos, List<Asistencia> asistencias) {
        if (contenedorEstadisticasJugador != null) {
            Map<String, Integer> eventosPorTipo = new HashMap<>();
            Map<String, Integer> asistenciasPorTipo = new HashMap<>();

            // Contar eventos por tipo
            for (Evento evento : eventos) {
                String tipo = evento.getTipo();
                eventosPorTipo.put(tipo, eventosPorTipo.getOrDefault(tipo, 0) + 1);
            }

            // Contar asistencias por tipo de evento
            for (Asistencia asistencia : asistencias) {
                for (Evento evento : eventos) {
                    if (evento.getId().equals(asistencia.getEventoId())) {
                        String tipo = evento.getTipo();
                        asistenciasPorTipo.put(tipo, asistenciasPorTipo.getOrDefault(tipo, 0) + 1);
                        break;
                    }
                }
            }

            // Mostrar estadísticas por tipo
            TextView tvTituloTipos = new TextView(requireContext());
            tvTituloTipos.setText("Eventos por tipo:");
            tvTituloTipos.setTextSize(18);
            tvTituloTipos.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTituloTipos.setPadding(0, 16, 0, 8);
            contenedorEstadisticasJugador.addView(tvTituloTipos);

            for (Map.Entry<String, Integer> entry : eventosPorTipo.entrySet()) {
                String tipo = entry.getKey();
                int cantidadEventos = entry.getValue();
                int cantidadAsistencias = asistenciasPorTipo.getOrDefault(tipo, 0);
                agregarEstadistica(tipo, cantidadEventos + " eventos, " + cantidadAsistencias + " asistencias");
            }
        }
    }

    private void mostrarEstadisticasJugador(Usuario jugador) {
        if (contenedorEstadisticasJugador != null) {
            contenedorEstadisticasJugador.removeAllViews();

            // Título del jugador
            TextView tvTituloJugador = new TextView(requireContext());
            tvTituloJugador.setText("Estadísticas del Jugador: " + jugador.getJugador());
            tvTituloJugador.setTextSize(20);
            tvTituloJugador.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTituloJugador.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvTituloJugador.setPadding(0, 16, 0, 16);
            contenedorEstadisticasJugador.addView(tvTituloJugador);

            // Obtener asistencias del jugador
            List<Asistencia> asistenciasJugador = dataManager.getAsistenciasPorJugador(jugador.getJugador());
            List<Evento> eventos = dataManager.getEventos();

            int totalAsistencias = asistenciasJugador.size();
            int asistenciasPositivas = 0;
            int asistenciasNegativas = 0;
            int asistenciasPendientes = 0;

            for (Asistencia asistencia : asistenciasJugador) {
                if (asistencia.isAsistira() == null) {
                    asistenciasPendientes++;
                } else if (asistencia.isAsistira()) {
                    asistenciasPositivas++;
                } else {
                    asistenciasNegativas++;
                }
            }

            double porcentajeAsistencia = totalAsistencias > 0 ? (double) asistenciasPositivas / totalAsistencias * 100 : 0;

            // Mostrar estadísticas del jugador
            agregarEstadistica("Total de eventos", String.valueOf(totalAsistencias));
            agregarEstadistica("Asistencias confirmadas", String.valueOf(asistenciasPositivas));
            agregarEstadistica("Ausencias confirmadas", String.valueOf(asistenciasNegativas));
            agregarEstadistica("Pendientes de confirmar", String.valueOf(asistenciasPendientes));
            agregarEstadistica("Porcentaje de asistencia", String.format("%.1f%%", porcentajeAsistencia));

            // Mostrar últimos eventos
            if (!asistenciasJugador.isEmpty()) {
                TextView tvTituloEventos = new TextView(requireContext());
                tvTituloEventos.setText("Últimos eventos:");
                tvTituloEventos.setTextSize(18);
                tvTituloEventos.setTypeface(null, android.graphics.Typeface.BOLD);
                tvTituloEventos.setPadding(0, 16, 0, 8);
                contenedorEstadisticasJugador.addView(tvTituloEventos);

                // Mostrar los últimos 5 eventos
                int contador = 0;
                for (Asistencia asistencia : asistenciasJugador) {
                    if (contador >= 5) break;
                    
                    for (Evento evento : eventos) {
                        if (evento.getId().equals(asistencia.getEventoId())) {
                            String estado = asistencia.isAsistira() == null ? "Pendiente" : 
                                          asistencia.isAsistira() ? "Asistirá" : "No asistirá";
                            agregarEstadistica(evento.getTitulo(), estado);
                            contador++;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void cargarEstadisticas() {
        try {
            List<Evento> eventos = dataManager.getEventos();
            List<Asistencia> asistencias = dataManager.getAsistencias();
            List<ObjetoPerdido> objetos = dataManager.getObjetosPerdidos();

            // Aplicar filtros de fecha si están configurados
            if (fechaInicio != null && fechaFin != null) {
                eventos = filtrarEventosPorFecha(eventos);
                asistencias = filtrarAsistenciasPorFecha(asistencias, eventos);
            }

            // Cargar métricas principales
            cargarMetricasDetalladas(eventos, asistencias, objetos);

            // Generar gráficos
            generarGraficoTiposEventos(eventos);
            generarGraficoAsistenciaMensual(asistencias);

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al cargar estadísticas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private List<Evento> filtrarEventosPorFecha(List<Evento> eventos) {
        List<Evento> eventosFiltrados = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.getFechaInicio().after(fechaInicio) && evento.getFechaInicio().before(fechaFin)) {
                eventosFiltrados.add(evento);
            }
        }
        return eventosFiltrados;
    }

    private List<Asistencia> filtrarAsistenciasPorFecha(List<Asistencia> asistencias, List<Evento> eventosFiltrados) {
        List<Asistencia> asistenciasFiltradas = new ArrayList<>();
        List<String> idsEventosFiltrados = new ArrayList<>();
        
        for (Evento evento : eventosFiltrados) {
            idsEventosFiltrados.add(evento.getId());
        }
        
        for (Asistencia asistencia : asistencias) {
            if (idsEventosFiltrados.contains(asistencia.getEventoId())) {
                asistenciasFiltradas.add(asistencia);
            }
        }
        
        return asistenciasFiltradas;
    }

    private void cargarMetricasDetalladas(List<Evento> eventos, List<Asistencia> asistencias, List<ObjetoPerdido> objetos) {
        // Métricas principales
        if (tvTotalEventos != null) {
            tvTotalEventos.setText(String.valueOf(eventos.size()));
        }
        if (tvTotalAsistencias != null) {
            tvTotalAsistencias.setText(String.valueOf(asistencias.size()));
        }
        if (tvTotalObjetos != null) {
            tvTotalObjetos.setText(String.valueOf(objetos.size()));
        }

        // Calcular eventos recurrentes
        int eventosRecurrentes = 0;
        for (Evento evento : eventos) {
            if (evento.isEsRecurrente()) {
                eventosRecurrentes++;
            }
        }
        if (tvEventosRecurrentes != null) {
            tvEventosRecurrentes.setText(String.valueOf(eventosRecurrentes));
        }

        // Calcular promedio de asistencia
        int asistenciasPositivas = 0;
        for (Asistencia asistencia : asistencias) {
            if (asistencia.isAsistira() != null && asistencia.isAsistira()) {
                asistenciasPositivas++;
            }
        }
        double promedioAsistencia = asistencias.size() > 0 ? (double) asistenciasPositivas / asistencias.size() * 100 : 0;
        if (tvPromedioAsistencia != null) {
            tvPromedioAsistencia.setText(String.format("%.1f%%", promedioAsistencia));
        }

        // Calcular objetos encontrados
        int objetosEncontrados = 0;
        for (ObjetoPerdido objeto : objetos) {
            if (objeto.isEncontrado()) {
                objetosEncontrados++;
            }
        }
        if (tvObjetosEncontrados != null) {
            tvObjetosEncontrados.setText(String.valueOf(objetosEncontrados));
        }
    }

    private void generarGraficoTiposEventos(List<Evento> eventos) {
        if (layoutGraficoEventos != null && tvGraficoEventos != null) {
            Map<String, Integer> eventosPorTipo = new HashMap<>();
            
            for (Evento evento : eventos) {
                String tipo = evento.getTipo();
                eventosPorTipo.put(tipo, eventosPorTipo.getOrDefault(tipo, 0) + 1);
            }
            
            StringBuilder grafico = new StringBuilder();
            grafico.append("Distribución por tipo:\n");
            
            for (Map.Entry<String, Integer> entry : eventosPorTipo.entrySet()) {
                String tipo = entry.getKey();
                int cantidad = entry.getValue();
                int porcentaje = eventos.size() > 0 ? (cantidad * 100) / eventos.size() : 0;
                
                grafico.append(tipo).append(": ").append(cantidad)
                       .append(" (").append(porcentaje).append("%)\n");
                
                // Crear barra visual
                StringBuilder barra = new StringBuilder();
                int longitudBarra = porcentaje / 5; // Cada 5% = un carácter
                for (int i = 0; i < longitudBarra; i++) {
                    barra.append("█");
                }
                grafico.append(barra.toString()).append("\n\n");
            }
            
            tvGraficoEventos.setText(grafico.toString());
        }
    }

    private void generarGraficoAsistenciaMensual(List<Asistencia> asistencias) {
        if (layoutGraficoAsistencia != null && tvGraficoAsistencia != null) {
            Map<String, Integer> asistenciasPorMes = new HashMap<>();
            Map<String, Integer> totalPorMes = new HashMap<>();
            
            for (Asistencia asistencia : asistencias) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(asistencia.getFechaEvento());
                String mes = String.format("%d/%d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
                
                totalPorMes.put(mes, totalPorMes.getOrDefault(mes, 0) + 1);
                
                if (asistencia.isAsistira() != null && asistencia.isAsistira()) {
                    asistenciasPorMes.put(mes, asistenciasPorMes.getOrDefault(mes, 0) + 1);
                }
            }
            
            StringBuilder grafico = new StringBuilder();
            grafico.append("Asistencia mensual:\n\n");
            
            for (Map.Entry<String, Integer> entry : totalPorMes.entrySet()) {
                String mes = entry.getKey();
                int total = entry.getValue();
                int asistenciasPositivas = asistenciasPorMes.getOrDefault(mes, 0);
                int porcentaje = total > 0 ? (asistenciasPositivas * 100) / total : 0;
                
                grafico.append(mes).append(": ").append(asistenciasPositivas)
                       .append("/").append(total).append(" (").append(porcentaje).append("%)\n");
                
                // Crear barra visual
                StringBuilder barra = new StringBuilder();
                int longitudBarra = porcentaje / 5; // Cada 5% = un carácter
                for (int i = 0; i < longitudBarra; i++) {
                    barra.append("█");
                }
                grafico.append(barra.toString()).append("\n\n");
            }
            
            tvGraficoAsistencia.setText(grafico.toString());
        }
    }

    private void configurarFiltrosFecha() {
        if (buttonFechaInicio != null) {
            buttonFechaInicio.setOnClickListener(v -> mostrarSelectorFecha(true));
        }
        if (buttonFechaFin != null) {
            buttonFechaFin.setOnClickListener(v -> mostrarSelectorFecha(false));
        }
    }

    private void configurarBotonLimpiar(Button buttonLimpiarFiltros) {
        if (buttonLimpiarFiltros != null) {
            buttonLimpiarFiltros.setOnClickListener(v -> {
                fechaInicio = null;
                fechaFin = null;
                if (buttonFechaInicio != null) {
                    buttonFechaInicio.setText("Seleccionar fecha inicio");
                }
                if (buttonFechaFin != null) {
                    buttonFechaFin.setText("Seleccionar fecha fin");
                }
                cargarEstadisticas();
            });
        }
    }

    private void configurarBotonExportar(Button buttonExportarReporte) {
        if (buttonExportarReporte != null) {
            buttonExportarReporte.setOnClickListener(v -> generarYCompartirReporte());
        }
    }

    private void generarYCompartirReporte() {
        try {
            StringBuilder reporte = new StringBuilder();
            reporte.append("REPORTE DE ESTADÍSTICAS\n");
            reporte.append("========================\n\n");
            
            // Obtener datos actuales
            List<Evento> eventos = dataManager.getEventos();
            List<Asistencia> asistencias = dataManager.getAsistencias();
            List<ObjetoPerdido> objetos = dataManager.getObjetosPerdidos();
            
            // Aplicar filtros si están configurados
            if (fechaInicio != null && fechaFin != null) {
                eventos = filtrarEventosPorFecha(eventos);
                asistencias = filtrarAsistenciasPorFecha(asistencias, eventos);
                reporte.append("Período: ").append(dateFormat.format(fechaInicio))
                       .append(" - ").append(dateFormat.format(fechaFin)).append("\n\n");
            }
            
            // Métricas generales
            reporte.append("MÉTRICAS GENERALES:\n");
            reporte.append("- Total de eventos: ").append(eventos.size()).append("\n");
            reporte.append("- Total de asistencias: ").append(asistencias.size()).append("\n");
            reporte.append("- Total de objetos perdidos: ").append(objetos.size()).append("\n");
            
            // Calcular eventos recurrentes
            int eventosRecurrentes = 0;
            for (Evento evento : eventos) {
                if (evento.isEsRecurrente()) {
                    eventosRecurrentes++;
                }
            }
            reporte.append("- Eventos recurrentes: ").append(eventosRecurrentes).append("\n");
            
            // Calcular promedio de asistencia
            int asistenciasPositivas = 0;
            for (Asistencia asistencia : asistencias) {
                if (asistencia.isAsistira() != null && asistencia.isAsistira()) {
                    asistenciasPositivas++;
                }
            }
            double promedioAsistencia = asistencias.size() > 0 ? (double) asistenciasPositivas / asistencias.size() * 100 : 0;
            reporte.append("- Promedio de asistencia: ").append(String.format("%.1f%%", promedioAsistencia)).append("\n\n");
            
            // Estadísticas por tipo de evento
            Map<String, Integer> eventosPorTipo = new HashMap<>();
            for (Evento evento : eventos) {
                String tipo = evento.getTipo();
                eventosPorTipo.put(tipo, eventosPorTipo.getOrDefault(tipo, 0) + 1);
            }
            
            reporte.append("EVENTOS POR TIPO:\n");
            for (Map.Entry<String, Integer> entry : eventosPorTipo.entrySet()) {
                reporte.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            
            // Mostrar reporte en un diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Reporte de Estadísticas");
            builder.setMessage(reporte.toString());
            builder.setPositiveButton("Compartir", (dialog, which) -> {
                // Aquí se podría implementar la funcionalidad de compartir
                Toast.makeText(requireContext(), "Funcionalidad de compartir en desarrollo", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cerrar", null);
            builder.show();
            
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al generar reporte: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarSelectorFecha(boolean esFechaInicio) {
        Calendar calendar = Calendar.getInstance();
        if (esFechaInicio && fechaInicio != null) {
            calendar.setTime(fechaInicio);
        } else if (!esFechaInicio && fechaFin != null) {
            calendar.setTime(fechaFin);
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                Date selectedDate = selectedCalendar.getTime();
                
                if (esFechaInicio) {
                    fechaInicio = selectedDate;
                    if (buttonFechaInicio != null) {
                        buttonFechaInicio.setText(dateFormat.format(selectedDate));
                    }
                } else {
                    fechaFin = selectedDate;
                    if (buttonFechaFin != null) {
                        buttonFechaFin.setText(dateFormat.format(selectedDate));
                    }
                }
                
                aplicarFiltrosFecha();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void aplicarFiltrosFecha() {
        if (fechaInicio != null && fechaFin != null) {
            if (fechaInicio.after(fechaFin)) {
                Toast.makeText(requireContext(), "La fecha de inicio debe ser anterior a la fecha de fin", Toast.LENGTH_LONG).show();
                return;
            }
            cargarEstadisticas();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEstadisticas();
    }
} 