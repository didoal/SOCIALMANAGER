package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.AsistenciaAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Asistencia;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AsistenciaFragment extends Fragment {
    private DataManager dataManager;
    private RecyclerView recyclerViewAsistencias;
    private AsistenciaAdapter asistenciaAdapter;
    private TextView textViewConfirmados, textViewNoAsisten, textViewPorcentaje, textViewResumenFiltros;
    private Button btnFechaDesde, btnFechaHasta, btnAplicarFiltros;
    private ImageButton btnExportarPdf;
    private Spinner spinnerTipoFiltro, spinnerEquipo, spinnerCategoria, spinnerJugador;
    private LinearLayout layoutFiltroEquipo, layoutFiltroCategoria, layoutFiltroJugador;
    private PieChart chartAsistencias;
    private FloatingActionButton fabRegistrarAsistencia;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Usuario usuarioActual;
    
    // Filtros
    private Date fechaDesde = null;
    private Date fechaHasta = null;
    private String tipoFiltro = "GLOBAL";
    private String filtroEquipo = "";
    private String filtroCategoria = "";
    private String filtroJugador = "";
    private List<Asistencia> asistenciasFiltradas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asistencia, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        inicializarVistas(view);
        configurarRecyclerView();
        configurarFiltros();
        configurarGrafico();
        configurarListeners();
        cargarAsistencias();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewAsistencias = view.findViewById(R.id.recyclerViewAsistencias);
        textViewConfirmados = view.findViewById(R.id.textViewConfirmados);
        textViewNoAsisten = view.findViewById(R.id.textViewNoAsisten);
        textViewPorcentaje = view.findViewById(R.id.textViewPorcentaje);
        textViewResumenFiltros = view.findViewById(R.id.textViewResumenFiltros);
        btnFechaDesde = view.findViewById(R.id.btnFechaDesde);
        btnFechaHasta = view.findViewById(R.id.btnFechaHasta);
        btnAplicarFiltros = view.findViewById(R.id.btnAplicarFiltros);
        btnExportarPdf = view.findViewById(R.id.btnExportarPdf);
        spinnerTipoFiltro = view.findViewById(R.id.spinnerTipoFiltro);
        spinnerEquipo = view.findViewById(R.id.spinnerEquipo);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        spinnerJugador = view.findViewById(R.id.spinnerJugador);
        layoutFiltroEquipo = view.findViewById(R.id.layoutFiltroEquipo);
        layoutFiltroCategoria = view.findViewById(R.id.layoutFiltroCategoria);
        layoutFiltroJugador = view.findViewById(R.id.layoutFiltroJugador);
        chartAsistencias = view.findViewById(R.id.chartAsistencias);
        fabRegistrarAsistencia = view.findViewById(R.id.fabRegistrarAsistencia);
    }

    private void configurarRecyclerView() {
        Context context = getContext();
        if (context != null) {
            recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(context));
            asistenciaAdapter = new AsistenciaAdapter(context, new ArrayList<>(), this::manejarClicEnAsistencia);
        recyclerViewAsistencias.setAdapter(asistenciaAdapter);
        }
    }

    private void configurarFiltros() {
        // Configurar spinner de tipo de filtro
        String[] tiposFiltro = {"GLOBAL", "POR EQUIPO", "POR CATEGORÍA", "POR JUGADOR"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tiposFiltro);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoFiltro.setAdapter(tipoAdapter);

        // Configurar spinner de equipos
        List<String> equipos = new ArrayList<>();
        equipos.add("Todos los equipos");
        
        // Si es entrenador, solo mostrar su equipo
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            equipos.add(usuarioActual.getEquipo());
        } else {
            // Si es admin, mostrar todos los equipos
        equipos.addAll(dataManager.getNombresEquipos());
        }
        
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);

        // Configurar spinner de categorías
        String[] categorias = {"Todas las categorías", "Entrenamiento", "Partido", "Torneo", "Evento especial"};
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        // Configurar spinner de jugadores
        List<String> jugadores = new ArrayList<>();
        jugadores.add("Todos los jugadores");
        List<Usuario> usuarios = dataManager.getUsuarios();
        for (Usuario usuario : usuarios) {
            if (usuario.getJugador() != null && !usuario.getJugador().isEmpty()) {
                // Si es entrenador, solo mostrar jugadores de su equipo
                if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
                    usuarioActual.getEquipo() != null) {
                    if (usuarioActual.getEquipo().equals(usuario.getEquipo())) {
                        jugadores.add(usuario.getJugador());
                    }
                } else {
                jugadores.add(usuario.getJugador());
                }
            }
        }
        ArrayAdapter<String> jugadorAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, jugadores);
        jugadorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJugador.setAdapter(jugadorAdapter);

        // Configurar filtro inicial para usuarios no admin y entrenadores
        if (usuarioActual != null && !usuarioActual.isEsAdmin() && usuarioActual.getEquipo() != null) {
            tipoFiltro = "POR EQUIPO";
            filtroEquipo = usuarioActual.getEquipo();
            int posicionEquipo = equipos.indexOf(filtroEquipo);
            if (posicionEquipo >= 0) {
                spinnerEquipo.setSelection(posicionEquipo);
            }
        }
    }

    private void configurarGrafico() {
        chartAsistencias.setUsePercentValues(true);
        chartAsistencias.getDescription().setEnabled(false);
        chartAsistencias.setExtraOffsets(5, 10, 5, 5);
        chartAsistencias.setDragDecelerationFrictionCoef(0.95f);
        chartAsistencias.setDrawHoleEnabled(true);
        chartAsistencias.setHoleColor(android.graphics.Color.WHITE);
        chartAsistencias.setTransparentCircleColor(android.graphics.Color.WHITE);
        chartAsistencias.setTransparentCircleAlpha(110);
        chartAsistencias.setHoleRadius(58f);
        chartAsistencias.setTransparentCircleRadius(61f);
        chartAsistencias.setDrawCenterText(true);
        chartAsistencias.setCenterText("Asistencias");
        chartAsistencias.setRotationAngle(0);
        chartAsistencias.setRotationEnabled(true);
        chartAsistencias.setHighlightPerTapEnabled(true);
        chartAsistencias.animateY(1400);
        chartAsistencias.getLegend().setEnabled(false);
    }

    private void configurarListeners() {
        btnFechaDesde.setOnClickListener(v -> mostrarDatePicker(true));
        btnFechaHasta.setOnClickListener(v -> mostrarDatePicker(false));
        btnAplicarFiltros.setOnClickListener(v -> aplicarFiltros());
        btnExportarPdf.setOnClickListener(v -> exportarPdf());
        
        // Configurar FAB según el rol del usuario
        boolean puedeRegistrarAsistencia = usuarioActual != null && 
            (usuarioActual.isEsAdmin() || "entrenador".equals(usuarioActual.getRol()));
        
        if (puedeRegistrarAsistencia) {
            fabRegistrarAsistencia.setVisibility(View.VISIBLE);
        fabRegistrarAsistencia.setOnClickListener(v -> mostrarDialogoRegistrarAsistencia());
        } else {
            fabRegistrarAsistencia.setVisibility(View.GONE);
        }

        spinnerTipoFiltro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                tipoFiltro = parent.getItemAtPosition(position).toString();
                mostrarFiltrosCondicionales();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void mostrarDatePicker(boolean esDesde) {
        Calendar calendar = Calendar.getInstance();
        if (esDesde && fechaDesde != null) {
            calendar.setTime(fechaDesde);
        } else if (!esDesde && fechaHasta != null) {
            calendar.setTime(fechaHasta);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                Date fechaSeleccionada = calendar.getTime();
                
                if (esDesde) {
                    fechaDesde = fechaSeleccionada;
                    btnFechaDesde.setText(dateFormat.format(fechaSeleccionada));
                } else {
                    fechaHasta = fechaSeleccionada;
                    btnFechaHasta.setText(dateFormat.format(fechaSeleccionada));
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void mostrarFiltrosCondicionales() {
        layoutFiltroEquipo.setVisibility(View.GONE);
        layoutFiltroCategoria.setVisibility(View.GONE);
        layoutFiltroJugador.setVisibility(View.GONE);

        switch (tipoFiltro) {
            case "POR EQUIPO":
                layoutFiltroEquipo.setVisibility(View.VISIBLE);
                break;
            case "POR CATEGORÍA":
                layoutFiltroCategoria.setVisibility(View.VISIBLE);
                break;
            case "POR JUGADOR":
                layoutFiltroJugador.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void aplicarFiltros() {
        // Obtener valores de los filtros
        if (spinnerEquipo.getVisibility() == View.VISIBLE) {
            filtroEquipo = spinnerEquipo.getSelectedItem().toString();
        }
        if (spinnerCategoria.getVisibility() == View.VISIBLE) {
            filtroCategoria = spinnerCategoria.getSelectedItem().toString();
        }
        if (spinnerJugador.getVisibility() == View.VISIBLE) {
            filtroJugador = spinnerJugador.getSelectedItem().toString();
        }

        cargarAsistencias();
        actualizarResumenFiltros();
    }

    private void cargarAsistencias() {
        List<Asistencia> todasAsistencias = dataManager.getAsistencias();
        asistenciasFiltradas = new ArrayList<>();
        
        for (Asistencia asistencia : todasAsistencias) {
            if (cumpleFiltros(asistencia)) {
                asistenciasFiltradas.add(asistencia);
            }
        }
        
        // Ordenar por fecha (más recientes primero)
        asistenciasFiltradas.sort((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()));
        
        asistenciaAdapter.actualizarAsistencias(asistenciasFiltradas);
        actualizarEstadisticas();
        actualizarGrafico();
    }

    private boolean cumpleFiltros(Asistencia asistencia) {
        // Filtro por fechas
        if (fechaDesde != null && asistencia.getFecha().before(fechaDesde)) {
            return false;
        }
        if (fechaHasta != null && asistencia.getFecha().after(fechaHasta)) {
            return false;
        }

        // Obtener evento asociado
        Evento evento = dataManager.getEventoById(asistencia.getEventoId());
        if (evento == null) return false;

        // Filtro por tipo
        switch (tipoFiltro) {
            case "POR EQUIPO":
                if (!filtroEquipo.equals("Todos los equipos") && 
                    !filtroEquipo.equals(evento.getEquipo())) {
                    return false;
                }
                break;
            case "POR CATEGORÍA":
                if (!filtroCategoria.equals("Todas las categorías") && 
                    !filtroCategoria.equals(evento.getTipo())) {
                    return false;
                }
                break;
            case "POR JUGADOR":
                if (!filtroJugador.equals("Todos los jugadores") && 
                    !filtroJugador.equals(asistencia.getJugadorNombre())) {
                    return false;
                }
                break;
        }

        return true;
    }

    private void actualizarEstadisticas() {
        int totalAsistencias = asistenciasFiltradas.size();
        int asistenciasPositivas = 0, asistenciasNegativas = 0;
        
        for (Asistencia asistencia : asistenciasFiltradas) {
            if (asistencia.isAsistio()) {
                asistenciasPositivas++;
            } else {
                asistenciasNegativas++;
            }
        }
        
        double porcentajeAsistencia = totalAsistencias > 0 ? 
            (double) asistenciasPositivas / totalAsistencias * 100 : 0;
        
        textViewConfirmados.setText(String.valueOf(asistenciasPositivas));
        textViewNoAsisten.setText(String.valueOf(asistenciasNegativas));
        textViewPorcentaje.setText(String.format(Locale.getDefault(), "%.1f%%", porcentajeAsistencia));
    }

    private void actualizarGrafico() {
        int asistenciasPositivas = 0, asistenciasNegativas = 0;
        
        for (Asistencia asistencia : asistenciasFiltradas) {
            if (asistencia.isAsistio()) {
                asistenciasPositivas++;
            } else {
                asistenciasNegativas++;
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        if (asistenciasPositivas > 0) {
            entries.add(new PieEntry(asistenciasPositivas, "Asistencias"));
        }
        if (asistenciasNegativas > 0) {
            entries.add(new PieEntry(asistenciasNegativas, "Ausencias"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Asistencias");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        PieData data = new PieData(dataSet);
        chartAsistencias.setData(data);
        chartAsistencias.invalidate();
    }

    private void actualizarResumenFiltros() {
        StringBuilder resumen = new StringBuilder("Filtros aplicados: ");
        
        if (fechaDesde != null || fechaHasta != null) {
            resumen.append("Fechas ");
            if (fechaDesde != null) resumen.append("desde ").append(dateFormat.format(fechaDesde));
            if (fechaHasta != null) resumen.append(" hasta ").append(dateFormat.format(fechaHasta));
            resumen.append(" | ");
        }
        
        switch (tipoFiltro) {
            case "GLOBAL":
                resumen.append("Todos los equipos");
                break;
            case "POR EQUIPO":
                resumen.append("Equipo: ").append(filtroEquipo);
                break;
            case "POR CATEGORÍA":
                resumen.append("Categoría: ").append(filtroCategoria);
                break;
            case "POR JUGADOR":
                resumen.append("Jugador: ").append(filtroJugador);
                break;
        }
        
        textViewResumenFiltros.setText(resumen.toString());
    }

    private void exportarPdf() {
        if (asistenciasFiltradas.isEmpty()) {
            Toast.makeText(requireContext(), "No hay datos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Crear archivo PDF
            File pdfFile = new File(requireContext().getExternalFilesDir(null), "reporte_asistencias.pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Configurar colores corporativos
            BaseColor colorAzul = new BaseColor(33, 150, 243); // #2196F3
            BaseColor colorGris = new BaseColor(128, 128, 128);

            // Título principal
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, colorAzul);
            Paragraph title = new Paragraph("📊 Reporte de Asistencias", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Subtítulo
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, colorGris);
            Paragraph subtitle = new Paragraph("CD Santiaguiño de Guizán", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(30f);
            document.add(subtitle);

            // Información de filtros
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Paragraph info = new Paragraph("🔍 Filtros aplicados: " + textViewResumenFiltros.getText(), infoFont);
            info.setSpacingAfter(15f);
            document.add(info);

            // Estadísticas resumidas
            Paragraph stats = new Paragraph(String.format("📈 Estadísticas: Total: %d | Asistencias: %s | Ausencias: %s | Porcentaje: %s", 
                asistenciasFiltradas.size(), textViewConfirmados.getText(), 
                textViewNoAsisten.getText(), textViewPorcentaje.getText()), infoFont);
            stats.setSpacingAfter(20f);
            document.add(stats);

            // Fecha de generación
            SimpleDateFormat dateFormatPdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Paragraph fechaGeneracion = new Paragraph("📅 Generado el: " + dateFormatPdf.format(new Date()), infoFont);
            fechaGeneracion.setSpacingAfter(25f);
            document.add(fechaGeneracion);

            // Tabla de asistencias
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(20f);

            // Encabezados de tabla
            String[] headers = {"Jugador", "Evento", "Fecha", "Estado", "Observaciones"};
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(colorAzul);
                cell.setPadding(8f);
                table.addCell(cell);
            }

            // Datos de la tabla
            SimpleDateFormat dateFormatTable = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            
            for (Asistencia asistencia : asistenciasFiltradas) {
                Evento evento = dataManager.getEventoById(asistencia.getEventoId());
                String nombreEvento = evento != null ? evento.getTitulo() : "Evento no encontrado";
                String fechaEvento = evento != null ? dateFormatTable.format(evento.getFechaInicio()) : "";
                String estado = asistencia.isAsistio() ? "✅ ASISTIÓ" : "❌ NO ASISTIÓ";
                String observaciones = asistencia.getObservaciones() != null ? asistencia.getObservaciones() : "";

                table.addCell(new PdfPCell(new Phrase(asistencia.getJugadorNombre(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(nombreEvento, dataFont)));
                table.addCell(new PdfPCell(new Phrase(fechaEvento, dataFont)));
                table.addCell(new PdfPCell(new Phrase(estado, dataFont)));
                table.addCell(new PdfPCell(new Phrase(observaciones, dataFont)));
            }

            document.add(table);

            // Pie de página
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Este reporte fue generado automáticamente por el sistema de gestión del club.", 
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, colorGris));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            // Mostrar mensaje de éxito y abrir el PDF
            Toast.makeText(requireContext(), "PDF exportado exitosamente", Toast.LENGTH_LONG).show();
            
            // Abrir el PDF
            Uri uri = FileProvider.getUriForFile(requireContext(), 
                requireContext().getPackageName() + ".provider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("Asistencia", "Error al exportar PDF", e);
            Toast.makeText(requireContext(), R.string.error_exportar_pdf, Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarDialogoRegistrarAsistencia() {
        Toast.makeText(requireContext(), "Funcionalidad de registro en desarrollo", Toast.LENGTH_SHORT).show();
    }

    private void manejarClicEnAsistencia(Asistencia asistencia) {
        Toast.makeText(requireContext(), "Clic en asistencia: " + asistencia.getJugadorNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarAsistencias();
    }
} 