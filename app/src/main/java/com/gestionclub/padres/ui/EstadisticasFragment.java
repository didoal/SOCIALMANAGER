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
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        configurarSpinners();
        cargarEstadisticas();
        return view;
    }

    private void inicializarVistas(View view) {
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
        
        spinnerEquipos = view.findViewById(R.id.spinnerEquipos);
        spinnerJugadores = view.findViewById(R.id.spinnerJugadores);
        contenedorEstadisticasJugador = view.findViewById(R.id.contenedorEstadisticasJugador);
        
        // Inicializar filtros de fecha
        buttonFechaInicio = view.findViewById(R.id.buttonFechaInicio);
        buttonFechaFin = view.findViewById(R.id.buttonFechaFin);
        Button buttonLimpiarFiltros = view.findViewById(R.id.buttonLimpiarFiltros);
        
        configurarFiltrosFecha();
        configurarBotonLimpiar(buttonLimpiarFiltros);
    }

    private void configurarSpinners() {
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
                    contenedorEstadisticasJugador.removeAllViews();
                    cargarEstadisticas();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
        
        for (Asistencia a : asistencias) {
            if (a.isAsistio()) asistenciasPositivas++;
        }

        double porcentajeAsistencia = totalAsistencias > 0 ? (double) asistenciasPositivas / totalAsistencias * 100 : 0;

        // Mostrar estadísticas mejoradas
        TextView tvStatsEquipo = new TextView(requireContext());
        tvStatsEquipo.setText(String.format("Jugadores: %d\nTotal Eventos: %d\nTotal Asistencias: %d\nAsistencias: %d\nPorcentaje: %.1f%%",
                totalJugadores, totalEventos, totalAsistencias, asistenciasPositivas, porcentajeAsistencia));
        tvStatsEquipo.setTextSize(16);
        tvStatsEquipo.setTextColor(getResources().getColor(R.color.colorTextSecondary));
        tvStatsEquipo.setPadding(0, 8, 0, 16);
        contenedorEstadisticasJugador.addView(tvStatsEquipo);

        // Estadísticas por tipo de evento
        mostrarEstadisticasPorTipoEvento(eventos, asistencias);

        // Lista de jugadores del equipo con sus estadísticas
        TextView tvJugadoresTitulo = new TextView(requireContext());
        tvJugadoresTitulo.setText("Jugadores del Equipo:");
        tvJugadoresTitulo.setTextSize(18);
        tvJugadoresTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvJugadoresTitulo.setTextColor(getResources().getColor(R.color.colorAccent));
        tvJugadoresTitulo.setPadding(0, 16, 0, 8);
        contenedorEstadisticasJugador.addView(tvJugadoresTitulo);

        for (Usuario jugador : jugadores) {
            // Calcular estadísticas del jugador
            int asistenciasJugador = 0;
            int totalAsistenciasJugador = 0;
            for (Asistencia asistencia : asistencias) {
                if (asistencia.getJugadorNombre().equalsIgnoreCase(jugador.getJugador())) {
                    totalAsistenciasJugador++;
                    if (asistencia.isAsistio()) {
                        asistenciasJugador++;
                    }
                }
            }
            
            double porcentajeJugador = totalAsistenciasJugador > 0 ? 
                (double) asistenciasJugador / totalAsistenciasJugador * 100 : 0;

            TextView tvJugador = new TextView(requireContext());
            tvJugador.setText(String.format("• %s (Padre: %s)\n  Asistencias: %d/%d (%.1f%%)", 
                    jugador.getJugador(), jugador.getNombre(), 
                    asistenciasJugador, totalAsistenciasJugador, porcentajeJugador));
            tvJugador.setTextSize(14);
            tvJugador.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            tvJugador.setPadding(0, 4, 0, 4);
            contenedorEstadisticasJugador.addView(tvJugador);
        }
    }

    private void mostrarEstadisticasPorTipoEvento(List<Evento> eventos, List<Asistencia> asistencias) {
        // Agrupar eventos por tipo
        Map<String, Integer> eventosPorTipo = new HashMap<>();
        Map<String, Integer> asistenciasPorTipo = new HashMap<>();
        
        for (Evento evento : eventos) {
            String tipo = evento.getTipo();
            eventosPorTipo.put(tipo, eventosPorTipo.getOrDefault(tipo, 0) + 1);
        }
        
        for (Asistencia asistencia : asistencias) {
            // Buscar el evento correspondiente
            for (Evento evento : eventos) {
                if (evento.getId().equals(asistencia.getEventoId())) {
                    String tipo = evento.getTipo();
                    asistenciasPorTipo.put(tipo, asistenciasPorTipo.getOrDefault(tipo, 0) + 1);
                    break;
                }
            }
        }
        
        // Mostrar estadísticas por tipo
        TextView tvTipoTitulo = new TextView(requireContext());
        tvTipoTitulo.setText("Estadísticas por Tipo de Evento:");
        tvTipoTitulo.setTextSize(16);
        tvTipoTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTipoTitulo.setTextColor(getResources().getColor(R.color.colorAccent));
        tvTipoTitulo.setPadding(0, 16, 0, 8);
        contenedorEstadisticasJugador.addView(tvTipoTitulo);
        
        for (String tipo : eventosPorTipo.keySet()) {
            int numEventos = eventosPorTipo.get(tipo);
            int numAsistencias = asistenciasPorTipo.getOrDefault(tipo, 0);
            
            TextView tvTipo = new TextView(requireContext());
            tvTipo.setText(String.format("• %s: %d eventos, %d asistencias", tipo, numEventos, numAsistencias));
            tvTipo.setTextSize(14);
            tvTipo.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            tvTipo.setPadding(0, 4, 0, 4);
            contenedorEstadisticasJugador.addView(tvTipo);
        }
    }

    private void mostrarEstadisticasJugador(Usuario jugador) {
        contenedorEstadisticasJugador.removeAllViews();

        // Título del jugador
        TextView tvTituloJugador = new TextView(requireContext());
        tvTituloJugador.setText("Estadísticas de: " + jugador.getJugador());
        tvTituloJugador.setTextSize(20);
        tvTituloJugador.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTituloJugador.setTextColor(getResources().getColor(R.color.rojo_club));
        tvTituloJugador.setPadding(0, 16, 0, 16);
        contenedorEstadisticasJugador.addView(tvTituloJugador);

        // Calcular estadísticas del jugador
        List<Asistencia> asistencias = dataManager.getAsistencias();
        List<Asistencia> asistenciasJugador = new ArrayList<>();
        
        for (Asistencia a : asistencias) {
            if (a.getJugadorNombre().equalsIgnoreCase(jugador.getJugador())) {
                asistenciasJugador.add(a);
            }
        }

        int totalEventos = asistenciasJugador.size();
        int asistenciasPositivas = 0;
        int ausencias = 0;

        for (Asistencia a : asistenciasJugador) {
            if (a.isAsistio()) {
                asistenciasPositivas++;
            } else {
                ausencias++;
            }
        }

        double porcentajeAsistencia = totalEventos > 0 ? (double) asistenciasPositivas / totalEventos * 100 : 0;

        // Mostrar estadísticas
        TextView tvStatsJugador = new TextView(requireContext());
        tvStatsJugador.setText(String.format("Total Eventos: %d\nAsistencias: %d\nAusencias: %d\nPorcentaje Asistencia: %.1f%%",
                totalEventos, asistenciasPositivas, ausencias, porcentajeAsistencia));
        tvStatsJugador.setTextSize(16);
        tvStatsJugador.setTextColor(getResources().getColor(R.color.white));
        tvStatsJugador.setPadding(0, 8, 0, 16);
        contenedorEstadisticasJugador.addView(tvStatsJugador);

        // Historial detallado
        if (!asistenciasJugador.isEmpty()) {
            TextView tvHistorialTitulo = new TextView(requireContext());
            tvHistorialTitulo.setText("Historial de Asistencias:");
            tvHistorialTitulo.setTextSize(18);
            tvHistorialTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
            tvHistorialTitulo.setTextColor(getResources().getColor(R.color.gold));
            tvHistorialTitulo.setPadding(0, 16, 0, 8);
            contenedorEstadisticasJugador.addView(tvHistorialTitulo);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            List<Evento> eventos = dataManager.getEventos();

            for (Asistencia asistencia : asistenciasJugador) {
                // Buscar el evento correspondiente
                Evento evento = null;
                for (Evento e : eventos) {
                    if (e.getId().equals(asistencia.getEventoId())) {
                        evento = e;
                        break;
                    }
                }

                if (evento != null) {
                    TextView tvEvento = new TextView(requireContext());
                    String estado = asistencia.isAsistio() ? "✓ ASISTIÓ" : "✗ NO ASISTIÓ";
                    int colorEstado = asistencia.isAsistio() ? R.color.green : R.color.red;
                    
                    tvEvento.setText(String.format("%s - %s (%s)", 
                            sdf.format(evento.getFechaInicio()), 
                            evento.getTitulo(), 
                            estado));
                    tvEvento.setTextSize(14);
                    tvEvento.setTextColor(getResources().getColor(colorEstado));
                    tvEvento.setPadding(16, 4, 0, 4);
                    contenedorEstadisticasJugador.addView(tvEvento);
                }
            }
        }
    }

    private void cargarEstadisticas() {
        List<Evento> eventos = dataManager.getEventos();
        List<Asistencia> asistencias = dataManager.getAsistencias();
        List<ObjetoPerdido> objetos = dataManager.getObjetosPerdidos();
        
        // Aplicar filtros de fecha
        eventos = filtrarEventosPorFecha(eventos);
        asistencias = filtrarAsistenciasPorFecha(asistencias, eventos);
        
        // Actualizar estadísticas generales
        tvTotalEventos.setText("Eventos: " + eventos.size());
        tvTotalAsistencias.setText("Asistencias: " + asistencias.size());
        tvTotalMensajes.setText("Mensajes: " + dataManager.getMensajes().size());
        tvTotalObjetos.setText("Objetos: " + objetos.size());
        
        // Calcular promedio de asistencia
        int asistenciasPositivas = 0;
        for (Asistencia a : asistencias) {
            if (a.isAsistio()) asistenciasPositivas++;
        }
        double promedioAsistencia = asistencias.size() > 0 ? (double) asistenciasPositivas / asistencias.size() * 100 : 0;
        tvPromedioAsistencia.setText(String.format("Promedio Asistencia: %.1f%%", promedioAsistencia));
        
        // Cargar métricas detalladas
        cargarMetricasDetalladas(eventos, asistencias, objetos);
        generarGraficoTiposEventos(eventos);
        generarGraficoAsistenciaMensual(asistencias);
    }

    private List<Evento> filtrarEventosPorFecha(List<Evento> eventos) {
        if (fechaInicio == null && fechaFin == null) {
            return eventos;
        }
        
        List<Evento> eventosFiltrados = new ArrayList<>();
        for (Evento evento : eventos) {
            boolean incluir = true;
            
            if (fechaInicio != null && evento.getFechaInicio().before(fechaInicio)) {
                incluir = false;
            }
            
            if (fechaFin != null && evento.getFechaInicio().after(fechaFin)) {
                incluir = false;
            }
            
            if (incluir) {
                eventosFiltrados.add(evento);
            }
        }
        
        return eventosFiltrados;
    }

    private List<Asistencia> filtrarAsistenciasPorFecha(List<Asistencia> asistencias, List<Evento> eventosFiltrados) {
        if (fechaInicio == null && fechaFin == null) {
            return asistencias;
        }
        
        List<Asistencia> asistenciasFiltradas = new ArrayList<>();
        List<Evento> todosEventos = dataManager.getEventos();
        
        for (Asistencia asistencia : asistencias) {
            // Buscar el evento correspondiente
            for (Evento evento : todosEventos) {
                if (evento.getId().equals(asistencia.getEventoId())) {
                    // Verificar si el evento está en la lista filtrada
                    boolean eventoIncluido = false;
                    for (Evento eventoFiltrado : eventosFiltrados) {
                        if (eventoFiltrado.getId().equals(evento.getId())) {
                            eventoIncluido = true;
                            break;
                        }
                    }
                    
                    if (eventoIncluido) {
                        asistenciasFiltradas.add(asistencia);
                    }
                    break;
                }
            }
        }
        
        return asistenciasFiltradas;
    }

    private void cargarMetricasDetalladas(List<Evento> eventos, List<Asistencia> asistencias, List<ObjetoPerdido> objetos) {
        // Eventos recurrentes
        int eventosRecurrentes = 0;
        for (Evento evento : eventos) {
            if (evento.isEsRecurrente()) {
                eventosRecurrentes++;
            }
        }
        tvEventosRecurrentes.setText("Eventos Recurrentes: " + eventosRecurrentes);
        
        // Objetos encontrados
        int objetosEncontrados = 0;
        for (ObjetoPerdido objeto : objetos) {
            if (objeto.isEncontrado()) {
                objetosEncontrados++;
            }
        }
        tvObjetosEncontrados.setText("Objetos Encontrados: " + objetosEncontrados + "/" + objetos.size());
    }

    private void generarGraficoTiposEventos(List<Evento> eventos) {
        layoutGraficoEventos.removeAllViews();
        
        if (eventos.isEmpty()) {
            tvGraficoEventos.setText("No hay eventos para mostrar");
            return;
        }
        
        // Contar tipos de eventos
        Map<String, Integer> tiposEventos = new HashMap<>();
        for (Evento evento : eventos) {
            String tipo = evento.getTipo();
            int count = tiposEventos.containsKey(tipo) ? tiposEventos.get(tipo) : 0;
            tiposEventos.put(tipo, count + 1);
        }
        
        // Encontrar el máximo para escalar las barras
        int maximo = 0;
        for (int cantidad : tiposEventos.values()) {
            if (cantidad > maximo) {
                maximo = cantidad;
            }
        }
        
        // Crear barras del gráfico
        for (Map.Entry<String, Integer> entry : tiposEventos.entrySet()) {
            String tipo = entry.getKey();
            int cantidad = entry.getValue();
            
            LinearLayout barraContainer = new LinearLayout(requireContext());
            barraContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            barraContainer.setOrientation(LinearLayout.HORIZONTAL);
            barraContainer.setPadding(0, 8, 0, 8);
            
            // Etiqueta del tipo
            TextView tvTipo = new TextView(requireContext());
            tvTipo.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0.3f
            ));
            tvTipo.setText(tipo);
            tvTipo.setTextColor(getResources().getColor(R.color.white));
            tvTipo.setTextSize(12);
            
            // Barra
            View barra = new View(requireContext());
            int alturaBarra = maximo > 0 ? (cantidad * 100) / maximo : 0;
            barra.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                Math.max(alturaBarra, 20),
                0.5f
            ));
            barra.setBackgroundResource(R.drawable.bar_chart_background);
            barra.setPadding(8, 0, 8, 0);
            
            // Cantidad
            TextView tvCantidad = new TextView(requireContext());
            tvCantidad.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0.2f
            ));
            tvCantidad.setText(String.valueOf(cantidad));
            tvCantidad.setTextColor(getResources().getColor(R.color.gold));
            tvCantidad.setTextSize(12);
            tvCantidad.setGravity(android.view.Gravity.END);
            
            barraContainer.addView(tvTipo);
            barraContainer.addView(barra);
            barraContainer.addView(tvCantidad);
            
            layoutGraficoEventos.addView(barraContainer);
        }
        
        tvGraficoEventos.setVisibility(View.GONE);
    }

    private void generarGraficoAsistenciaMensual(List<Asistencia> asistencias) {
        layoutGraficoAsistencia.removeAllViews();
        
        if (asistencias.isEmpty()) {
            tvGraficoAsistencia.setText("No hay asistencias para mostrar");
            return;
        }
        
        // Contar asistencias por mes
        Map<String, Integer> asistenciasPorMes = new HashMap<>();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                         "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        
        for (String mes : meses) {
            asistenciasPorMes.put(mes, 0);
        }
        
        Calendar cal = Calendar.getInstance();
        for (Asistencia asistencia : asistencias) {
            cal.setTime(asistencia.getFecha());
            int mes = cal.get(Calendar.MONTH);
            String nombreMes = meses[mes];
            asistenciasPorMes.put(nombreMes, asistenciasPorMes.get(nombreMes) + 1);
        }
        
        // Encontrar el máximo para escalar las barras
        int maximo = 0;
        for (int cantidad : asistenciasPorMes.values()) {
            if (cantidad > maximo) {
                maximo = cantidad;
            }
        }
        
        // Crear gráfico horizontal
        LinearLayout graficoContainer = new LinearLayout(requireContext());
        graficoContainer.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        graficoContainer.setOrientation(LinearLayout.VERTICAL);
        
        for (String mes : meses) {
            int cantidad = asistenciasPorMes.get(mes);
            
            LinearLayout fila = new LinearLayout(requireContext());
            fila.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, 4, 0, 4);
            
            // Etiqueta del mes
            TextView tvMes = new TextView(requireContext());
            tvMes.setLayoutParams(new LinearLayout.LayoutParams(
                60,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tvMes.setText(mes);
            tvMes.setTextColor(getResources().getColor(R.color.white));
            tvMes.setTextSize(10);
            
            // Barra
            View barra = new View(requireContext());
            int anchoBarra = maximo > 0 ? (cantidad * 200) / maximo : 0;
            barra.setLayoutParams(new LinearLayout.LayoutParams(
                Math.max(anchoBarra, 20),
                20
            ));
            barra.setBackgroundResource(R.drawable.bar_chart_red);
            
            // Cantidad
            TextView tvCantidad = new TextView(requireContext());
            tvCantidad.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tvCantidad.setText(String.valueOf(cantidad));
            tvCantidad.setTextColor(getResources().getColor(R.color.gold));
            tvCantidad.setTextSize(10);
            tvCantidad.setPadding(8, 0, 0, 0);
            
            fila.addView(tvMes);
            fila.addView(barra);
            fila.addView(tvCantidad);
            
            graficoContainer.addView(fila);
        }
        
        layoutGraficoAsistencia.addView(graficoContainer);
        tvGraficoAsistencia.setVisibility(View.GONE);
    }

    private void configurarFiltrosFecha() {
        buttonFechaInicio.setOnClickListener(v -> mostrarSelectorFecha(true));
        buttonFechaFin.setOnClickListener(v -> mostrarSelectorFecha(false));
    }

    private void configurarBotonLimpiar(Button buttonLimpiarFiltros) {
        buttonLimpiarFiltros.setOnClickListener(v -> {
            fechaInicio = null;
            fechaFin = null;
            buttonFechaInicio.setText("Seleccionar");
            buttonFechaFin.setText("Seleccionar");
            spinnerEquipos.setSelection(0);
            spinnerJugadores.setSelection(0);
            jugadorSeleccionado = null;
            contenedorEstadisticasJugador.removeAllViews();
            cargarEstadisticas();
        });
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
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                if (esFechaInicio) {
                    fechaInicio = calendar.getTime();
                    buttonFechaInicio.setText(dateFormat.format(fechaInicio));
                } else {
                    fechaFin = calendar.getTime();
                    buttonFechaFin.setText(dateFormat.format(fechaFin));
                }
                
                // Aplicar filtros automáticamente
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
                Toast.makeText(requireContext(), "La fecha de inicio debe ser anterior a la fecha de fin", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Recargar estadísticas con filtros de fecha
        cargarEstadisticas();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEstadisticas();
    }
} 