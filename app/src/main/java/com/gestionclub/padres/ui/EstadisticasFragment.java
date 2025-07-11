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
            
            // Aplicar filtro de equipo
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
        if (textViewEstadisticasDetalladas != null) {
            StringBuilder estadisticas = new StringBuilder();
            
            if (!filtroEquipo.equals("TODOS")) {
                estadisticas.append("📊 Estadísticas del equipo: ").append(filtroEquipo).append("\n\n");
            }
            
            estadisticas.append("• Total de eventos: ").append(eventos.size()).append("\n");
            estadisticas.append("• Total de usuarios: ").append(usuarios.size()).append("\n");
            estadisticas.append("• Total de asistencias registradas: ").append(asistencias.size()).append("\n");
            
            // Contar eventos por tipo
            int eventosEntrenamiento = 0;
            int eventosPartido = 0;
            int eventosReunion = 0;
            
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
                    }
                }
            }
            
            estadisticas.append("• Eventos de entrenamiento: ").append(eventosEntrenamiento).append("\n");
            estadisticas.append("• Eventos de partido: ").append(eventosPartido).append("\n");
            estadisticas.append("• Reuniones: ").append(eventosReunion).append("\n");
            
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
            
            estadisticas.append("• Administradores: ").append(usuariosAdmin).append("\n");
            estadisticas.append("• Padres/Usuarios: ").append(usuariosPadres).append("\n");
            
            // Calcular estadísticas de asistencia
            if (!asistencias.isEmpty()) {
                double porcentajeAsistencia = calcularPorcentajeAsistencia(asistencias);
                estadisticas.append("• Porcentaje de asistencia: ").append(String.format("%.1f%%", porcentajeAsistencia)).append("\n");
            }
            
            textViewEstadisticasDetalladas.setText(estadisticas.toString());
        }
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
            // Crear el archivo PDF
            File pdfFile = new File(requireContext().getExternalFilesDir(null), "estadisticas_club.pdf");
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Título del documento
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Estadísticas del Club CD Santiaguiño Guizán", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Espacio
            
            // Fecha del reporte
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Paragraph fecha = new Paragraph("Reporte generado el: " + dateFormat.format(new Date()));
            fecha.setAlignment(Element.ALIGN_CENTER);
            document.add(fecha);
            document.add(new Paragraph(" ")); // Espacio
            
            // Estadísticas generales
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Paragraph subtitle = new Paragraph("Estadísticas Generales", subtitleFont);
            document.add(subtitle);
            document.add(new Paragraph(" ")); // Espacio
            
            List<Evento> eventos = filtrarEventosPorEquipo(dataManager.getEventos());
            List<Usuario> usuarios = filtrarUsuariosPorEquipo(dataManager.getUsuarios());
            List<Equipo> equipos = dataManager.getEquipos();
            List<Asistencia> asistencias = filtrarAsistenciasPorEquipo(dataManager.getAsistencias());
            
            // Tabla de estadísticas
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            
            // Agregar datos a la tabla
            agregarFilaTabla(table, "Total de Eventos", String.valueOf(eventos.size()));
            agregarFilaTabla(table, "Total de Usuarios", String.valueOf(usuarios.size()));
            agregarFilaTabla(table, "Total de Equipos", String.valueOf(equipos.size()));
            
            // Calcular porcentaje de asistencia
            double porcentajeAsistencia = calcularPorcentajeAsistencia(asistencias);
            agregarFilaTabla(table, "Porcentaje de Asistencia", String.format("%.1f%%", porcentajeAsistencia));
            
            document.add(table);
            document.add(new Paragraph(" ")); // Espacio
            
            // Estadísticas por equipo
            Paragraph subtitleEquipos = new Paragraph("Estadísticas por Equipo", subtitleFont);
            document.add(subtitleEquipos);
            document.add(new Paragraph(" ")); // Espacio
            
            for (Equipo equipo : equipos) {
                if (filtroEquipo.equals("TODOS") || equipo.getNombre().equals(filtroEquipo)) {
                    Paragraph equipoTitle = new Paragraph("Equipo: " + equipo.getNombre(), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
                    document.add(equipoTitle);
                    
                    List<Usuario> jugadoresEquipo = dataManager.getJugadoresPorEquipo(equipo.getId());
                    Paragraph jugadores = new Paragraph("Jugadores: " + jugadoresEquipo.size());
                    document.add(jugadores);
                    
                    // Eventos del equipo
                    int eventosEquipo = 0;
                    for (Evento evento : eventos) {
                        if (equipo.getNombre().equals(evento.getEquipo())) {
                            eventosEquipo++;
                        }
                    }
                    Paragraph eventosEquipoText = new Paragraph("Eventos: " + eventosEquipo);
                    document.add(eventosEquipoText);
                    document.add(new Paragraph(" ")); // Espacio
                }
            }
            
            document.close();
            outputStream.close();
            
            // Mostrar mensaje de éxito y abrir el PDF
            Toast.makeText(requireContext(), "PDF exportado exitosamente", Toast.LENGTH_LONG).show();
            
            // Abrir el PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(), 
                requireContext().getPackageName() + ".provider", 
                pdfFile
            );
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e("Estadisticas", "Error al exportar PDF", e);
            Toast.makeText(requireContext(), R.string.error_exportar_pdf, Toast.LENGTH_LONG).show();
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
} 