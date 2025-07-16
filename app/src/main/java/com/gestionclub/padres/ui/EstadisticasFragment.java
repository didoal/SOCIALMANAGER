package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EstadisticasFragment extends Fragment {
    private DataManager dataManager;
    private Usuario usuarioActual;
    private String filtroEquipo = "TODOS";
    
    // Vistas del nuevo layout
    private TextView textViewTotalEventos;
    private TextView textViewPorcentajeAsistencia;
    private TextView textViewTotalUsuarios;
    private TextView textViewTotalEquipos;
    private LinearLayout layoutGraficoBarras;
    private RecyclerView recyclerViewEstadisticasEquipos;
    private TextView textViewEstadisticasDetalladas;
    private Button buttonFiltroEquipo;
    private Button buttonExportarPDF;
    private Button buttonExportarPDFDetallado;
    private Button buttonVerAsistenciaDetallada;
    
    // Vistas de filtros avanzados
    private LinearLayout layoutHeaderFiltros;
    private LinearLayout layoutContenidoFiltros;
    private ImageView imageViewExpandirFiltros;
    private Spinner spinnerFiltroEquipo;
    private Spinner spinnerFiltroPeriodo;
    private Spinner spinnerFiltroTipoEvento;
    private Spinner spinnerFiltroEstadoAsistencia;
    private Button buttonFechaDesde;
    private Button buttonFechaHasta;
    private Button buttonAplicarFiltros;
    private Button buttonLimpiarFiltros;
    
    // Variables de filtros
    private String filtroEquipoAvanzado = "TODOS";
    private String filtroPeriodo = "TODOS";
    private String filtroTipoEvento = "TODOS";
    private String filtroEstadoAsistencia = "TODOS";
    private Date fechaDesde = null;
    private Date fechaHasta = null;
    private boolean filtrosExpandidos = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarFiltroEquipo();
            cargarEstadisticas();
        } catch (Exception e) {
            Log.e("Estadisticas", "Error al cargar estadísticas", e);
            mostrarErrorCarga(view);
        }
        
        return view;
    }
    
    private void mostrarErrorCarga(View view) {
        // Ocultar vistas principales
        if (layoutGraficoBarras != null) {
            layoutGraficoBarras.setVisibility(View.GONE);
        }
        if (recyclerViewEstadisticasEquipos != null) {
            recyclerViewEstadisticasEquipos.setVisibility(View.GONE);
        }
        
        // Mostrar solo el Toast de error
        Toast.makeText(requireContext(), R.string.error_cargar_estadisticas, Toast.LENGTH_LONG).show();
    }

    private void inicializarVistas(View view) {
        textViewTotalEventos = view.findViewById(R.id.textViewTotalEventos);
        textViewPorcentajeAsistencia = view.findViewById(R.id.textViewPorcentajeAsistencia);
        textViewTotalUsuarios = view.findViewById(R.id.textViewTotalUsuarios);
        textViewTotalEquipos = view.findViewById(R.id.textViewTotalEquipos);
        layoutGraficoBarras = view.findViewById(R.id.layoutGraficoBarras);
        recyclerViewEstadisticasEquipos = view.findViewById(R.id.recyclerViewEstadisticasEquipos);
        textViewEstadisticasDetalladas = view.findViewById(R.id.textViewEstadisticasDetalladas);
        buttonFiltroEquipo = view.findViewById(R.id.buttonFiltroEquipo);
        buttonExportarPDF = view.findViewById(R.id.buttonExportarPDF);
        buttonExportarPDFDetallado = view.findViewById(R.id.buttonExportarPDFDetallado);
        buttonVerAsistenciaDetallada = view.findViewById(R.id.buttonVerAsistenciaDetallada);
        
        // Inicializar vistas de filtros avanzados
        layoutHeaderFiltros = view.findViewById(R.id.layoutHeaderFiltros);
        layoutContenidoFiltros = view.findViewById(R.id.layoutContenidoFiltros);
        imageViewExpandirFiltros = view.findViewById(R.id.imageViewExpandirFiltros);
        spinnerFiltroEquipo = view.findViewById(R.id.spinnerFiltroEquipo);
        spinnerFiltroPeriodo = view.findViewById(R.id.spinnerFiltroPeriodo);
        spinnerFiltroTipoEvento = view.findViewById(R.id.spinnerFiltroTipoEvento);
        spinnerFiltroEstadoAsistencia = view.findViewById(R.id.spinnerFiltroEstadoAsistencia);
        buttonFechaDesde = view.findViewById(R.id.buttonFechaDesde);
        buttonFechaHasta = view.findViewById(R.id.buttonFechaHasta);
        buttonAplicarFiltros = view.findViewById(R.id.buttonAplicarFiltros);
        buttonLimpiarFiltros = view.findViewById(R.id.buttonLimpiarFiltros);
        
        if (buttonFiltroEquipo != null) {
            buttonFiltroEquipo.setOnClickListener(v -> mostrarDialogoFiltro());
        }
        
        if (buttonExportarPDF != null) {
            buttonExportarPDF.setOnClickListener(v -> exportarEstadisticasPDF());
        }
        
        if (buttonExportarPDFDetallado != null) {
            buttonExportarPDFDetallado.setOnClickListener(v -> exportarEstadisticasPDF());
        }
        
        if (buttonVerAsistenciaDetallada != null) {
            buttonVerAsistenciaDetallada.setOnClickListener(v -> verAsistenciaDetallada());
        }
        
        // Configurar filtros avanzados
        configurarFiltrosAvanzados();
    }

    private void configurarFiltroEquipo() {
        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            if (buttonFiltroEquipo != null) {
                buttonFiltroEquipo.setVisibility(View.VISIBLE);
            }
        } else {
            if (buttonFiltroEquipo != null) {
                buttonFiltroEquipo.setVisibility(View.GONE);
            }
            // Para usuarios no admin, filtrar por su equipo
            if (usuarioActual != null && usuarioActual.getEquipo() != null) {
                filtroEquipo = usuarioActual.getEquipo();
            }
        }
    }
    
    private void configurarFiltrosAvanzados() {
        // Configurar expansión/colapso de filtros
        if (layoutHeaderFiltros != null) {
            layoutHeaderFiltros.setOnClickListener(v -> toggleFiltros());
        }
        
        // Configurar spinners
        configurarSpinnerEquipo();
        configurarSpinnerPeriodo();
        configurarSpinnerTipoEvento();
        configurarSpinnerEstadoAsistencia();
        
        // Configurar botones de fecha
        if (buttonFechaDesde != null) {
            buttonFechaDesde.setOnClickListener(v -> mostrarDatePickerDesde());
        }
        
        if (buttonFechaHasta != null) {
            buttonFechaHasta.setOnClickListener(v -> mostrarDatePickerHasta());
        }
        
        // Configurar botones de acción
        if (buttonAplicarFiltros != null) {
            buttonAplicarFiltros.setOnClickListener(v -> aplicarFiltros());
        }
        
        if (buttonLimpiarFiltros != null) {
            buttonLimpiarFiltros.setOnClickListener(v -> limpiarFiltros());
        }
        
        // Configurar visibilidad según rol
        configurarVisibilidadFiltros();
    }
    
    private void configurarVisibilidadFiltros() {
        if (usuarioActual == null) return;
        
        if (usuarioActual.isEsAdmin()) {
            // Administradores ven todos los filtros
            if (layoutHeaderFiltros != null) {
                layoutHeaderFiltros.setVisibility(View.VISIBLE);
            }
        } else {
            // Usuarios normales solo ven filtros básicos
            if (layoutHeaderFiltros != null) {
                layoutHeaderFiltros.setVisibility(View.GONE);
            }
            if (layoutContenidoFiltros != null) {
                layoutContenidoFiltros.setVisibility(View.GONE);
            }
        }
    }
    
    private void toggleFiltros() {
        if (layoutContenidoFiltros == null || imageViewExpandirFiltros == null) return;
        
        filtrosExpandidos = !filtrosExpandidos;
        
        if (filtrosExpandidos) {
            layoutContenidoFiltros.setVisibility(View.VISIBLE);
            imageViewExpandirFiltros.setRotation(180f);
        } else {
            layoutContenidoFiltros.setVisibility(View.GONE);
            imageViewExpandirFiltros.setRotation(0f);
        }
    }
    
    private void configurarSpinnerEquipo() {
        if (spinnerFiltroEquipo == null) return;
        
        List<String> equipos = new ArrayList<>();
        equipos.add("TODOS");
        equipos.addAll(dataManager.getNombresEquipos());
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEquipo.setAdapter(adapter);
        
        spinnerFiltroEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEquipoAvanzado = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroEquipoAvanzado = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerPeriodo() {
        if (spinnerFiltroPeriodo == null) return;
        
        String[] periodos = {"TODOS", "ÚLTIMA SEMANA", "ÚLTIMO MES", "ÚLTIMO TRIMESTRE", "ÚLTIMO AÑO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, periodos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroPeriodo.setAdapter(adapter);
        
        spinnerFiltroPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroPeriodo = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroPeriodo = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerTipoEvento() {
        if (spinnerFiltroTipoEvento == null) return;
        
        String[] tipos = {"TODOS", "ENTRENAMIENTO", "PARTIDO", "REUNIÓN", "EVENTO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroTipoEvento.setAdapter(adapter);
        
        spinnerFiltroTipoEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroTipoEvento = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroTipoEvento = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerEstadoAsistencia() {
        if (spinnerFiltroEstadoAsistencia == null) return;
        
        String[] estados = {"TODOS", "ASISTIÓ", "NO ASISTIÓ", "PENDIENTE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEstadoAsistencia.setAdapter(adapter);
        
        spinnerFiltroEstadoAsistencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEstadoAsistencia = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroEstadoAsistencia = "TODOS";
            }
        });
    }
    
    private void mostrarDatePickerDesde() {
        Calendar calendar = Calendar.getInstance();
        if (fechaDesde != null) {
            calendar.setTime(fechaDesde);
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    fechaDesde = selectedCalendar.getTime();
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    buttonFechaDesde.setText(sdf.format(fechaDesde));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void mostrarDatePickerHasta() {
        Calendar calendar = Calendar.getInstance();
        if (fechaHasta != null) {
            calendar.setTime(fechaHasta);
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    fechaHasta = selectedCalendar.getTime();
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    buttonFechaHasta.setText(sdf.format(fechaHasta));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void aplicarFiltros() {
        // Aplicar filtros y recargar estadísticas
        cargarEstadisticas();
        Toast.makeText(requireContext(), "Filtros aplicados correctamente", Toast.LENGTH_SHORT).show();
    }
    
    private void limpiarFiltros() {
        // Limpiar todos los filtros
        filtroEquipoAvanzado = "TODOS";
        filtroPeriodo = "TODOS";
        filtroTipoEvento = "TODOS";
        filtroEstadoAsistencia = "TODOS";
        fechaDesde = null;
        fechaHasta = null;
        
        // Resetear spinners
        if (spinnerFiltroEquipo != null) spinnerFiltroEquipo.setSelection(0);
        if (spinnerFiltroPeriodo != null) spinnerFiltroPeriodo.setSelection(0);
        if (spinnerFiltroTipoEvento != null) spinnerFiltroTipoEvento.setSelection(0);
        if (spinnerFiltroEstadoAsistencia != null) spinnerFiltroEstadoAsistencia.setSelection(0);
        
        // Resetear botones de fecha
        if (buttonFechaDesde != null) buttonFechaDesde.setText("SELECCIONAR FECHA");
        if (buttonFechaHasta != null) buttonFechaHasta.setText("SELECCIONAR FECHA");
        
        // Recargar estadísticas
        cargarEstadisticas();
        Toast.makeText(requireContext(), "Filtros limpiados", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoFiltro() {
        if (usuarioActual == null || !usuarioActual.isEsAdmin()) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filtro_equipo, null);
        
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Obtener equipos disponibles
        List<String> equipos = new ArrayList<>();
        equipos.add("TODOS");
        equipos.addAll(dataManager.getNombresEquipos());
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(adapter);
        
        // Seleccionar el filtro actual
        int posicionActual = equipos.indexOf(filtroEquipo);
        if (posicionActual >= 0) {
            spinnerEquipo.setSelection(posicionActual);
        }
        
        builder.setView(dialogView)
                .setTitle("Filtrar por Equipo")
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    filtroEquipo = spinnerEquipo.getSelectedItem().toString();
                    cargarEstadisticas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cargarEstadisticas() {
        try {
            List<Evento> eventos = dataManager.getEventos();
            List<Usuario> usuarios = dataManager.getUsuarios();
            List<Asistencia> asistencias = dataManager.getAsistencias();
            
            // Aplicar filtros avanzados
            eventos = aplicarFiltrosAvanzadosEventos(eventos);
            usuarios = aplicarFiltrosAvanzadosUsuarios(usuarios);
            asistencias = aplicarFiltrosAvanzadosAsistencias(asistencias);
            
            // Aplicar filtro de equipo básico (para compatibilidad)
            eventos = filtrarEventosPorEquipo(eventos);
            usuarios = filtrarUsuariosPorEquipo(usuarios);
            asistencias = filtrarAsistenciasPorEquipo(asistencias);
            
            // Cargar total de eventos
            if (textViewTotalEventos != null) {
                textViewTotalEventos.setText(String.valueOf(eventos.size()));
            }
            
            // Calcular porcentaje de asistencia real
            if (textViewPorcentajeAsistencia != null) {
                double porcentaje = calcularPorcentajeAsistencia(asistencias);
                textViewPorcentajeAsistencia.setText(String.format("%.1f%%", porcentaje));
            }
            
            // Cargar total de usuarios
            if (textViewTotalUsuarios != null) {
                textViewTotalUsuarios.setText(String.valueOf(usuarios.size()));
            }
            
            // Cargar total de equipos
            if (textViewTotalEquipos != null) {
                List<Equipo> equipos = dataManager.getEquipos();
                textViewTotalEquipos.setText(String.valueOf(equipos.size()));
            }
            
            // Cargar estadísticas detalladas
            cargarEstadisticasDetalladas(eventos, usuarios, asistencias);
            
            // Configurar RecyclerView para estadísticas por equipo
            if (recyclerViewEstadisticasEquipos != null) {
                recyclerViewEstadisticasEquipos.setLayoutManager(new LinearLayoutManager(requireContext()));
                // Aquí puedes configurar un adaptador para mostrar estadísticas por equipo
            }
            
        } catch (Exception e) {
            // Manejar errores
        }
    }
    
    private List<Evento> filtrarEventosPorEquipo(List<Evento> eventos) {
        if (filtroEquipo.equals("TODOS")) {
            return eventos;
        }
        
        List<Evento> eventosFiltrados = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.getEquipo() != null && evento.getEquipo().equals(filtroEquipo)) {
                eventosFiltrados.add(evento);
            }
        }
        return eventosFiltrados;
    }

    private List<Usuario> filtrarUsuariosPorEquipo(List<Usuario> usuarios) {
        if (filtroEquipo.equals("TODOS")) {
            return usuarios;
        }
        
        List<Usuario> usuariosFiltrados = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.getEquipo() != null && usuario.getEquipo().equals(filtroEquipo)) {
                usuariosFiltrados.add(usuario);
            }
        }
        return usuariosFiltrados;
    }

    private List<Asistencia> filtrarAsistenciasPorEquipo(List<Asistencia> asistencias) {
        if (filtroEquipo.equals("TODOS")) {
            return asistencias;
        }
        
        List<Asistencia> asistenciasFiltradas = new ArrayList<>();
        for (Asistencia asistencia : asistencias) {
            // Buscar el evento asociado para verificar el equipo
            Evento evento = dataManager.getEventoById(asistencia.getEventoId());
            if (evento != null && evento.getEquipo() != null && evento.getEquipo().equals(filtroEquipo)) {
                asistenciasFiltradas.add(asistencia);
            }
        }
        return asistenciasFiltradas;
    }

    private double calcularPorcentajeAsistencia(List<Asistencia> asistencias) {
        if (asistencias.isEmpty()) {
            return 0.0;
        }
        
        int asistenciasPositivas = 0;
        for (Asistencia asistencia : asistencias) {
            if (asistencia.isAsistio()) {
                asistenciasPositivas++;
            }
        }
        
        return (double) asistenciasPositivas / asistencias.size() * 100;
    }

    private void cargarEstadisticasDetalladas(List<Evento> eventos, List<Usuario> usuarios, List<Asistencia> asistencias) {
        if (textViewEstadisticasDetalladas == null) return;
        
        StringBuilder estadisticas = new StringBuilder();
        
        // Información de filtros aplicados
        boolean hayFiltrosAplicados = !filtroEquipoAvanzado.equals("TODOS") || 
                                     !filtroPeriodo.equals("TODOS") || 
                                     !filtroTipoEvento.equals("TODOS") || 
                                     !filtroEstadoAsistencia.equals("TODOS") ||
                                     fechaDesde != null || fechaHasta != null;
        
        if (hayFiltrosAplicados) {
            estadisticas.append("FILTROS APLICADOS:\n");
            estadisticas.append("═══════════════════\n");
            
            if (!filtroEquipoAvanzado.equals("TODOS")) {
                estadisticas.append("• Equipo: ").append(filtroEquipoAvanzado).append("\n");
            }
            if (!filtroPeriodo.equals("TODOS")) {
                estadisticas.append("• Período: ").append(filtroPeriodo).append("\n");
            }
            if (!filtroTipoEvento.equals("TODOS")) {
                estadisticas.append("• Tipo de Evento: ").append(filtroTipoEvento).append("\n");
            }
            if (!filtroEstadoAsistencia.equals("TODOS")) {
                estadisticas.append("• Estado Asistencia: ").append(filtroEstadoAsistencia).append("\n");
            }
            if (fechaDesde != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                estadisticas.append("• Desde: ").append(sdf.format(fechaDesde)).append("\n");
            }
            if (fechaHasta != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                estadisticas.append("• Hasta: ").append(sdf.format(fechaHasta)).append("\n");
            }
            
            estadisticas.append("\n");
        }
        
        // Estadísticas generales
        estadisticas.append("ESTADÍSTICAS GENERALES:\n");
        estadisticas.append("═══════════════════════\n");
        estadisticas.append("• Total de eventos: ").append(eventos.size()).append("\n");
        estadisticas.append("• Total de usuarios: ").append(usuarios.size()).append("\n");
        estadisticas.append("• Total de asistencias: ").append(asistencias.size()).append("\n");
        
        // Contar eventos por tipo
        int eventosEntrenamiento = 0;
        int eventosPartido = 0;
        int eventosReunion = 0;
        int eventosOtros = 0;
        
        for (Evento evento : eventos) {
            String tipo = evento.getTipo();
            if (tipo != null) {
                switch (tipo.toLowerCase()) {
                    case "entrenamiento":
                        eventosEntrenamiento++;
                        break;
                    case "partido":
                        eventosPartido++;
                        break;
                    case "reunión":
                    case "reunion":
                        eventosReunion++;
                        break;
                    default:
                        eventosOtros++;
                        break;
                }
            }
        }
        
        estadisticas.append("\nEVENTOS POR TIPO:\n");
        estadisticas.append("═══════════════════\n");
        estadisticas.append("• Entrenamientos: ").append(eventosEntrenamiento).append("\n");
        estadisticas.append("• Partidos: ").append(eventosPartido).append("\n");
        estadisticas.append("• Reuniones: ").append(eventosReunion).append("\n");
        if (eventosOtros > 0) {
            estadisticas.append("• Otros: ").append(eventosOtros).append("\n");
        }
        
        // Contar usuarios por tipo
        int usuariosAdmin = 0;
        int usuariosPadres = 0;
        
        for (Usuario usuario : usuarios) {
            if (usuario.isEsAdmin()) {
                usuariosAdmin++;
            } else {
                usuariosPadres++;
            }
        }
        
        estadisticas.append("\nUSUARIOS POR TIPO:\n");
        estadisticas.append("═══════════════════\n");
        estadisticas.append("• Administradores: ").append(usuariosAdmin).append("\n");
        estadisticas.append("• Padres/Usuarios: ").append(usuariosPadres).append("\n");
        
        // Calcular estadísticas de asistencia
        if (!asistencias.isEmpty()) {
            estadisticas.append("\nESTADÍSTICAS DE ASISTENCIA:\n");
            estadisticas.append("═══════════════════════\n");
            
            double porcentajeAsistencia = calcularPorcentajeAsistencia(asistencias);
            estadisticas.append("• Porcentaje de asistencia: ").append(String.format("%.1f%%", porcentajeAsistencia)).append("\n");
            
            // Contar por estado
            int asistenciasPositivas = 0;
            int asistenciasNegativas = 0;
            int asistenciasPendientes = 0;
            
            for (Asistencia asistencia : asistencias) {
                if (asistencia.isAsistio()) {
                    asistenciasPositivas++;
                } else if (asistencia.getEstado().equals("PENDIENTE")) {
                    asistenciasPendientes++;
                } else {
                    asistenciasNegativas++;
                }
            }
            
            estadisticas.append("• Asistió: ").append(asistenciasPositivas).append("\n");
            estadisticas.append("• No asistió: ").append(asistenciasNegativas).append("\n");
            if (asistenciasPendientes > 0) {
                estadisticas.append("• Pendientes: ").append(asistenciasPendientes).append("\n");
            }
        }
        
        // Estadísticas por equipo
        Map<String, Integer> usuariosPorEquipo = new HashMap<>();
        for (Usuario usuario : usuarios) {
            String equipo = usuario.getEquipo() != null ? usuario.getEquipo() : "Sin equipo";
            usuariosPorEquipo.put(equipo, usuariosPorEquipo.getOrDefault(equipo, 0) + 1);
        }
        
        if (!usuariosPorEquipo.isEmpty()) {
            estadisticas.append("\nUSUARIOS POR EQUIPO:\n");
            estadisticas.append("═══════════════════\n");
            for (Map.Entry<String, Integer> entry : usuariosPorEquipo.entrySet()) {
                estadisticas.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" usuarios\n");
            }
        }
        
        textViewEstadisticasDetalladas.setText(estadisticas.toString());
    }

    private void verAsistenciaDetallada() {
        // Navegar a la pantalla de asistencia detallada
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.mostrarFragmento(new AsistenciaFragment());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEstadisticas();
    }

    private void exportarEstadisticasPDF() {
        try {
            // Crear directorio si no existe
            File directory = new File(requireContext().getExternalFilesDir(null), "Reportes");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generar nombre de archivo con timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            String fileName = "Estadisticas_Club_" + timestamp + ".pdf";
            File file = new File(directory, fileName);
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            
            // Configurar fuentes
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            
            // Título principal
            Paragraph title = new Paragraph("REPORTE DE ESTADÍSTICAS", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Subtítulo del club
            Paragraph clubTitle = new Paragraph("CD SANTIAGUIÑO GUIZÁN", subtitleFont);
            clubTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(clubTitle);
            
            Paragraph lema = new Paragraph("SANTIAGUIÑO, A NOSA FAMILIA", smallFont);
            lema.setAlignment(Element.ALIGN_CENTER);
            document.add(lema);
            
            // Información de filtros aplicados
            if (!filtroEquipoAvanzado.equals("TODOS") || !filtroPeriodo.equals("TODOS") || 
                !filtroTipoEvento.equals("TODOS") || !filtroEstadoAsistencia.equals("TODOS") ||
                fechaDesde != null || fechaHasta != null) {
                
                document.add(new Paragraph(" ", normalFont));
                Paragraph filtrosTitle = new Paragraph("FILTROS APLICADOS:", subtitleFont);
                document.add(filtrosTitle);
                
                PdfPTable filtrosTable = new PdfPTable(2);
                filtrosTable.setWidthPercentage(100);
                
                if (!filtroEquipoAvanzado.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Equipo:", filtroEquipoAvanzado);
                }
                if (!filtroPeriodo.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Período:", filtroPeriodo);
                }
                if (!filtroTipoEvento.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Tipo de Evento:", filtroTipoEvento);
                }
                if (!filtroEstadoAsistencia.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Estado Asistencia:", filtroEstadoAsistencia);
                }
                if (fechaDesde != null) {
                    SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    agregarFilaTabla(filtrosTable, "Fecha Desde:", sdfFecha.format(fechaDesde));
                }
                if (fechaHasta != null) {
                    SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    agregarFilaTabla(filtrosTable, "Fecha Hasta:", sdfFecha.format(fechaHasta));
                }
                
                document.add(filtrosTable);
            }
            
            // Fecha de generación
            document.add(new Paragraph(" ", normalFont));
            Paragraph fechaGeneracion = new Paragraph("Fecha de generación: " + 
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()), smallFont);
            document.add(fechaGeneracion);
            
            // Separador
            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph("=".repeat(50), normalFont));
            document.add(new Paragraph(" ", normalFont));
            
            // Obtener datos filtrados
            List<Evento> eventos = dataManager.getEventos();
            List<Usuario> usuarios = dataManager.getUsuarios();
            List<Asistencia> asistencias = dataManager.getAsistencias();
            
            // Aplicar filtros avanzados
            eventos = aplicarFiltrosAvanzadosEventos(eventos);
            usuarios = aplicarFiltrosAvanzadosUsuarios(usuarios);
            asistencias = aplicarFiltrosAvanzadosAsistencias(asistencias);
            
            // Aplicar filtro de equipo básico
            eventos = filtrarEventosPorEquipo(eventos);
            usuarios = filtrarUsuariosPorEquipo(usuarios);
            asistencias = filtrarAsistenciasPorEquipo(asistencias);
            
            // Resumen general
            Paragraph resumenTitle = new Paragraph("RESUMEN GENERAL", subtitleFont);
            document.add(resumenTitle);
            
            PdfPTable resumenTable = new PdfPTable(2);
            resumenTable.setWidthPercentage(100);
            
            agregarFilaTabla(resumenTable, "Total de Eventos:", String.valueOf(eventos.size()));
            agregarFilaTabla(resumenTable, "Total de Usuarios:", String.valueOf(usuarios.size()));
            agregarFilaTabla(resumenTable, "Total de Equipos:", String.valueOf(dataManager.getEquipos().size()));
            
            double porcentajeAsistencia = calcularPorcentajeAsistencia(asistencias);
            agregarFilaTabla(resumenTable, "Porcentaje de Asistencia:", String.format("%.1f%%", porcentajeAsistencia));
            
            document.add(resumenTable);
            
            // Estadísticas por tipo de evento
            document.add(new Paragraph(" ", normalFont));
            Paragraph tiposTitle = new Paragraph("ESTADÍSTICAS POR TIPO DE EVENTO", subtitleFont);
            document.add(tiposTitle);
            
            Map<String, Integer> eventosPorTipo = new HashMap<>();
            for (Evento evento : eventos) {
                String tipo = evento.getTipo();
                eventosPorTipo.put(tipo, eventosPorTipo.getOrDefault(tipo, 0) + 1);
            }
            
            PdfPTable tiposTable = new PdfPTable(2);
            tiposTable.setWidthPercentage(100);
            
            for (Map.Entry<String, Integer> entry : eventosPorTipo.entrySet()) {
                agregarFilaTabla(tiposTable, entry.getKey() + ":", entry.getValue().toString());
            }
            
            document.add(tiposTable);
            
            // Estadísticas por equipo
            document.add(new Paragraph(" ", normalFont));
            Paragraph equiposTitle = new Paragraph("ESTADÍSTICAS POR EQUIPO", subtitleFont);
            document.add(equiposTitle);
            
            Map<String, Integer> usuariosPorEquipo = new HashMap<>();
            for (Usuario usuario : usuarios) {
                String equipo = usuario.getEquipo() != null ? usuario.getEquipo() : "Sin equipo";
                usuariosPorEquipo.put(equipo, usuariosPorEquipo.getOrDefault(equipo, 0) + 1);
            }
            
            PdfPTable equiposTable = new PdfPTable(2);
            equiposTable.setWidthPercentage(100);
            
            for (Map.Entry<String, Integer> entry : usuariosPorEquipo.entrySet()) {
                agregarFilaTabla(equiposTable, entry.getKey() + ":", entry.getValue().toString() + " usuarios");
            }
            
            document.add(equiposTable);
            
            // Estadísticas de asistencia
            document.add(new Paragraph(" ", normalFont));
            Paragraph asistenciaTitle = new Paragraph("ESTADÍSTICAS DE ASISTENCIA", subtitleFont);
            document.add(asistenciaTitle);
            
            Map<String, Integer> asistenciasPorEstado = new HashMap<>();
            for (Asistencia asistencia : asistencias) {
                String estado = asistencia.getEstado();
                asistenciasPorEstado.put(estado, asistenciasPorEstado.getOrDefault(estado, 0) + 1);
            }
            
            PdfPTable asistenciaTable = new PdfPTable(2);
            asistenciaTable.setWidthPercentage(100);
            
            for (Map.Entry<String, Integer> entry : asistenciasPorEstado.entrySet()) {
                agregarFilaTabla(asistenciaTable, entry.getKey() + ":", entry.getValue().toString());
            }
            
            document.add(asistenciaTable);
            
            document.close();
            
            // Mostrar mensaje de éxito
            Toast.makeText(requireContext(), "Reporte exportado: " + fileName, Toast.LENGTH_LONG).show();
            
            // Abrir el archivo
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e("Estadisticas", "Error al exportar PDF", e);
            Toast.makeText(requireContext(), "Error al exportar el reporte", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarFilaTabla(PdfPTable table, String label, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label));
        PdfPCell cell2 = new PdfPCell(new Phrase(value));
        cell1.setBorder(PdfPCell.NO_BORDER);
        cell2.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell1);
        table.addCell(cell2);
    }
    
    private List<Evento> aplicarFiltrosAvanzadosEventos(List<Evento> eventos) {
        List<Evento> eventosFiltrados = new ArrayList<>();
        
        for (Evento evento : eventos) {
            boolean cumpleFiltros = true;
            
            // Filtro por equipo
            if (!filtroEquipoAvanzado.equals("TODOS")) {
                if (!filtroEquipoAvanzado.equals(evento.getEquipo())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por tipo de evento
            if (!filtroTipoEvento.equals("TODOS")) {
                if (!filtroTipoEvento.equals(evento.getTipo())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por período
            if (!filtroPeriodo.equals("TODOS")) {
                if (!cumpleFiltroPeriodo(evento.getFecha())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por fechas específicas
            if (fechaDesde != null && evento.getFecha().before(fechaDesde)) {
                cumpleFiltros = false;
            }
            if (fechaHasta != null && evento.getFecha().after(fechaHasta)) {
                cumpleFiltros = false;
            }
            
            if (cumpleFiltros) {
                eventosFiltrados.add(evento);
            }
        }
        
        return eventosFiltrados;
    }
    
    private List<Usuario> aplicarFiltrosAvanzadosUsuarios(List<Usuario> usuarios) {
        List<Usuario> usuariosFiltrados = new ArrayList<>();
        
        for (Usuario usuario : usuarios) {
            boolean cumpleFiltros = true;
            
            // Filtro por equipo
            if (!filtroEquipoAvanzado.equals("TODOS")) {
                if (!filtroEquipoAvanzado.equals(usuario.getEquipo())) {
                    cumpleFiltros = false;
                }
            }
            
            if (cumpleFiltros) {
                usuariosFiltrados.add(usuario);
            }
        }
        
        return usuariosFiltrados;
    }
    
    private List<Asistencia> aplicarFiltrosAvanzadosAsistencias(List<Asistencia> asistencias) {
        List<Asistencia> asistenciasFiltradas = new ArrayList<>();
        
        for (Asistencia asistencia : asistencias) {
            boolean cumpleFiltros = true;
            
            // Filtro por estado de asistencia
            if (!filtroEstadoAsistencia.equals("TODOS")) {
                if (!filtroEstadoAsistencia.equals(asistencia.getEstado())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por período
            if (!filtroPeriodo.equals("TODOS")) {
                if (!cumpleFiltroPeriodo(asistencia.getFecha())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por fechas específicas
            if (fechaDesde != null && asistencia.getFecha().before(fechaDesde)) {
                cumpleFiltros = false;
            }
            if (fechaHasta != null && asistencia.getFecha().after(fechaHasta)) {
                cumpleFiltros = false;
            }
            
            if (cumpleFiltros) {
                asistenciasFiltradas.add(asistencia);
            }
        }
        
        return asistenciasFiltradas;
    }
    
    private boolean cumpleFiltroPeriodo(Date fecha) {
        if (filtroPeriodo.equals("TODOS")) return true;
        
        Calendar fechaEvento = Calendar.getInstance();
        fechaEvento.setTime(fecha);
        
        Calendar fechaActual = Calendar.getInstance();
        
        switch (filtroPeriodo) {
            case "ÚLTIMA SEMANA":
                fechaActual.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case "ÚLTIMO MES":
                fechaActual.add(Calendar.MONTH, -1);
                break;
            case "ÚLTIMO TRIMESTRE":
                fechaActual.add(Calendar.MONTH, -3);
                break;
            case "ÚLTIMO AÑO":
                fechaActual.add(Calendar.YEAR, -1);
                break;
            default:
                return true;
        }
        
        return fechaEvento.after(fechaActual);
    }
} 