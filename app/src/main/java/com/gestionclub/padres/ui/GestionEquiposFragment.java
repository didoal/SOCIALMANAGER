package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.EquipoAdapter;
import com.gestionclub.padres.adapter.JugadorEquipoAdapter;
import com.gestionclub.padres.adapter.UsuarioAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Button;
import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import android.widget.LinearLayout;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileOutputStream;

public class GestionEquiposFragment extends Fragment {
    private static final String TAG = "GestionEquiposFragment";
    
    private RecyclerView recyclerViewEquipos;
    private EquipoAdapter equipoAdapter;
    private DataManager dataManager;
    private TextView textViewEstadisticas;
    private FloatingActionButton fabAgregarEquipo;
    private RecyclerView recyclerViewJugadoresEquipo;
    private UsuarioAdapter jugadorAdapter;
    private Spinner spinnerEquipos;
    private List<Equipo> listaEquipos;
    
    // Vistas de filtros avanzados
    private LinearLayout layoutHeaderFiltros;
    private LinearLayout layoutContenidoFiltros;
    private ImageView imageViewExpandirFiltros;
    private Spinner spinnerFiltroCategoria;
    private Spinner spinnerFiltroEstado;
    private Spinner spinnerFiltroNumJugadores;
    private Spinner spinnerFiltroEntrenador;
    private Button buttonAplicarFiltros;
    private Button buttonLimpiarFiltros;
    
    // Vistas de estadísticas
    private TextView textViewTotalEquipos;
    private TextView textViewPromedioJugadores;
    private TextView textViewTotalCategorias;
    
    // Vistas de controles
    private Button buttonExportarEquipos;
    private Button buttonCrearEquipo;
    private Button buttonImportarExcel;
    private Button buttonDescargarPlantilla;
    
    // Variables de filtros
    private String filtroCategoria = "TODOS";
    private String filtroEstado = "TODOS";
    private String filtroNumJugadores = "TODOS";
    private String filtroEntrenador = "TODOS";
    private boolean filtrosExpandidos = false;
    
    // Para manejo de archivos Excel
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "onCreateView: Creando vista de gestión de equipos");
            View view = inflater.inflate(R.layout.fragment_gestion_equipos, container, false);
            
            dataManager = new DataManager(requireContext());
            inicializarVistas(view);
            
            // Configurar launcher para selección de archivos
            configurarFilePicker();
            
            // Solo continuar si la inicialización fue exitosa
            if (recyclerViewEquipos != null) {
                configurarRecyclerView();
                cargarEquipos();
                actualizarEstadisticas();
            }
            
            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreateView", e);
            // Retornar una vista simple en caso de error
            TextView errorView = new TextView(requireContext());
            errorView.setText("Error al cargar la gestión de equipos");
            errorView.setGravity(android.view.Gravity.CENTER);
            errorView.setPadding(50, 50, 50, 50);
            return errorView;
        }
    }

    private void inicializarVistas(View view) {
        try {
            Log.d(TAG, "inicializarVistas: Inicializando vistas");
            
            // Inicializar vistas principales con protección
            recyclerViewEquipos = view.findViewById(R.id.recyclerViewEquipos);
            if (recyclerViewEquipos == null) {
                Log.e(TAG, "Error: recyclerViewEquipos no encontrado");
                return;
            }
            
            textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
            fabAgregarEquipo = view.findViewById(R.id.fabAgregarEquipo);
            
            // Inicializar vistas de filtros avanzados con protección
            layoutHeaderFiltros = view.findViewById(R.id.layoutHeaderFiltros);
            layoutContenidoFiltros = view.findViewById(R.id.layoutContenidoFiltros);
            imageViewExpandirFiltros = view.findViewById(R.id.imageViewExpandirFiltros);
            spinnerFiltroCategoria = view.findViewById(R.id.spinnerFiltroCategoria);
            spinnerFiltroEstado = view.findViewById(R.id.spinnerFiltroEstado);
            spinnerFiltroNumJugadores = view.findViewById(R.id.spinnerFiltroNumJugadores);
            spinnerFiltroEntrenador = view.findViewById(R.id.spinnerFiltroEntrenador);
            buttonAplicarFiltros = view.findViewById(R.id.buttonAplicarFiltros);
            buttonLimpiarFiltros = view.findViewById(R.id.buttonLimpiarFiltros);
            
            // Inicializar vistas de estadísticas con protección
            textViewTotalEquipos = view.findViewById(R.id.textViewTotalEquipos);
            textViewPromedioJugadores = view.findViewById(R.id.textViewPromedioJugadores);
            textViewTotalCategorias = view.findViewById(R.id.textViewTotalCategorias);
            
            // Inicializar vistas de controles con protección
            buttonExportarEquipos = view.findViewById(R.id.buttonExportarEquipos);
            buttonCrearEquipo = view.findViewById(R.id.buttonCrearEquipo);
            buttonImportarExcel = view.findViewById(R.id.buttonImportarExcel);
            buttonDescargarPlantilla = view.findViewById(R.id.buttonDescargarPlantilla);
            
            // Configurar listeners con protección
            if (fabAgregarEquipo != null) {
                fabAgregarEquipo.setOnClickListener(v -> {
                    try {
                        mostrarDialogoCrearEquipo();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en fabAgregarEquipo click", e);
                        Toast.makeText(requireContext(), "Error al crear equipo", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (buttonCrearEquipo != null) {
                buttonCrearEquipo.setOnClickListener(v -> {
                    try {
                        mostrarDialogoCrearEquipo();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en buttonCrearEquipo click", e);
                        Toast.makeText(requireContext(), "Error al crear equipo", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (buttonExportarEquipos != null) {
                buttonExportarEquipos.setOnClickListener(v -> {
                    try {
                        exportarEquiposPDF();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en exportarEquiposPDF", e);
                        Toast.makeText(requireContext(), "Error al exportar equipos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (buttonImportarExcel != null) {
                buttonImportarExcel.setOnClickListener(v -> {
                    try {
                        importarEquiposExcel();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en importarEquiposExcel", e);
                        Toast.makeText(requireContext(), "Error al importar equipos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (buttonDescargarPlantilla != null) {
                buttonDescargarPlantilla.setOnClickListener(v -> {
                    try {
                        descargarPlantillaExcel();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en descargarPlantillaExcel", e);
                        Toast.makeText(requireContext(), "Error al descargar plantilla", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            // Configurar filtros avanzados con protección
            configurarFiltrosAvanzados();
            
        } catch (Exception e) {
            Log.e(TAG, "Error en inicializarVistas", e);
            Toast.makeText(requireContext(), "Error al inicializar la vista", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void configurarFiltrosAvanzados() {
        try {
            // Configurar expansión/colapso de filtros
            if (layoutHeaderFiltros != null) {
                layoutHeaderFiltros.setOnClickListener(v -> {
                    try {
                        toggleFiltros();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en toggleFiltros", e);
                    }
                });
            }
            
            // Configurar spinners con protección
            configurarSpinnerCategoria();
            configurarSpinnerEstado();
            configurarSpinnerNumJugadores();
            configurarSpinnerEntrenador();
            
            // Configurar botones de acción con protección
            if (buttonAplicarFiltros != null) {
                buttonAplicarFiltros.setOnClickListener(v -> {
                    try {
                        aplicarFiltros();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en aplicarFiltros", e);
                        Toast.makeText(requireContext(), "Error al aplicar filtros", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            if (buttonLimpiarFiltros != null) {
                buttonLimpiarFiltros.setOnClickListener(v -> {
                    try {
                        limpiarFiltros();
                    } catch (Exception e) {
                        Log.e(TAG, "Error en limpiarFiltros", e);
                        Toast.makeText(requireContext(), "Error al limpiar filtros", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            // Configurar visibilidad según rol
            configurarVisibilidadFiltros();
            
        } catch (Exception e) {
            Log.e(TAG, "Error en configurarFiltrosAvanzados", e);
        }
    }
    
    private void configurarVisibilidadFiltros() {
        // Por ahora, mostrar filtros para todos los usuarios
        // En el futuro se puede diferenciar por roles
        if (layoutHeaderFiltros != null) {
            layoutHeaderFiltros.setVisibility(View.VISIBLE);
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
    
    private void configurarSpinnerCategoria() {
        if (spinnerFiltroCategoria == null) return;
        
        String[] categorias = {"TODOS", "Biberones", "Prebenjamín", "Benjamín", "Alevín", "Infantil", "Cadete", "Juvenil"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroCategoria.setAdapter(adapter);
        
        spinnerFiltroCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroCategoria = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroCategoria = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerEstado() {
        if (spinnerFiltroEstado == null) return;
        
        String[] estados = {"TODOS", "ACTIVO", "INACTIVO", "EN FORMACIÓN"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEstado.setAdapter(adapter);
        
        spinnerFiltroEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEstado = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroEstado = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerNumJugadores() {
        if (spinnerFiltroNumJugadores == null) return;
        
        String[] numeros = {"TODOS", "0-5", "6-10", "11-15", "16-20", "20+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, numeros);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroNumJugadores.setAdapter(adapter);
        
        spinnerFiltroNumJugadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroNumJugadores = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroNumJugadores = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerEntrenador() {
        if (spinnerFiltroEntrenador == null) return;
        
        List<String> entrenadores = new ArrayList<>();
        entrenadores.add("TODOS");
        
        // Obtener entrenadores únicos de los equipos
        List<Equipo> equipos = dataManager.getEquipos();
        for (Equipo equipo : equipos) {
            if (equipo.getEntrenador() != null && !equipo.getEntrenador().isEmpty() && 
                !entrenadores.contains(equipo.getEntrenador())) {
                entrenadores.add(equipo.getEntrenador());
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, entrenadores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEntrenador.setAdapter(adapter);
        
        spinnerFiltroEntrenador.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEntrenador = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroEntrenador = "TODOS";
            }
        });
    }
    
    private void aplicarFiltros() {
        // Aplicar filtros y recargar equipos
        cargarEquipos();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Filtros aplicados correctamente", Toast.LENGTH_SHORT).show();
    }
    
    private void limpiarFiltros() {
        // Limpiar todos los filtros
        filtroCategoria = "TODOS";
        filtroEstado = "TODOS";
        filtroNumJugadores = "TODOS";
        filtroEntrenador = "TODOS";
        
        // Resetear spinners
        if (spinnerFiltroCategoria != null) spinnerFiltroCategoria.setSelection(0);
        if (spinnerFiltroEstado != null) spinnerFiltroEstado.setSelection(0);
        if (spinnerFiltroNumJugadores != null) spinnerFiltroNumJugadores.setSelection(0);
        if (spinnerFiltroEntrenador != null) spinnerFiltroEntrenador.setSelection(0);
        
        // Recargar equipos
        cargarEquipos();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Filtros limpiados", Toast.LENGTH_SHORT).show();
    }

    private void configurarRecyclerView() {
        Log.d(TAG, "configurarRecyclerView: Configurando RecyclerView");
        if (recyclerViewEquipos != null) {
            recyclerViewEquipos.setLayoutManager(new LinearLayoutManager(requireContext()));
            equipoAdapter = new EquipoAdapter(new ArrayList<>(), 
                this::mostrarDialogoEliminarEquipo,
                this::mostrarDialogoEditarEquipo,
                this::mostrarDialogoGestionarJugadores);
            recyclerViewEquipos.setAdapter(equipoAdapter);
        }
        
        // Configurar jugadorAdapter solo si es necesario (se usará en diálogos)
        jugadorAdapter = new UsuarioAdapter(new ArrayList<>(), null, null);
    }

    private void cargarEquipos() {
        Log.d(TAG, "cargarEquipos: Cargando lista de equipos");
        List<Equipo> equipos = dataManager.getEquipos();
        
        // Si no hay equipos, crear algunos de ejemplo
        if (equipos.isEmpty()) {
            crearEquiposEjemplo();
            equipos = dataManager.getEquipos();
        }
        
        // Aplicar filtros avanzados
        equipos = aplicarFiltrosAvanzados(equipos);
        
        if (equipoAdapter != null) {
            equipoAdapter.actualizarEquipos(equipos);
            Log.d(TAG, "cargarEquipos: " + equipos.size() + " equipos cargados");
        } else {
            Log.e(TAG, "Error: equipoAdapter es null");
        }
    }
    
    private void crearEquiposEjemplo() {
        Log.d(TAG, "crearEquiposEjemplo: Creando equipos de ejemplo");
        
        // Crear equipos para cada categoría
        String[] categorias = {"Biberones", "Prebenjamín", "Benjamín", "Alevín", "Infantil", "Cadete", "Juvenil"};
        String[] nombres = {
            "Biberones A", "Prebenjamín A", "Prebenjamín B", "Benjamín A", "Benjamín B",
            "Alevín A", "Alevín B", "Infantil", "Cadete", "Juvenil"
        };
        String[] entrenadores = {
            "Carlos López", "Miguel García", "Ana Martínez", "David Rodríguez", "Laura Sánchez",
            "Javier Pérez", "María González", "Roberto Fernández", "Carmen Jiménez", "Antonio Ruiz"
        };
        
        for (int i = 0; i < nombres.length; i++) {
            Equipo equipo = new Equipo(nombres[i], categorias[Math.min(i, categorias.length - 1)], entrenadores[i]);
            equipo.setJugadoresIds(new ArrayList<>());
            dataManager.agregarEquipo(equipo);
        }
        
        Log.d(TAG, "crearEquiposEjemplo: " + nombres.length + " equipos de ejemplo creados");
    }
    
    private List<Equipo> aplicarFiltrosAvanzados(List<Equipo> equipos) {
        List<Equipo> equiposFiltrados = new ArrayList<>();
        
        for (Equipo equipo : equipos) {
            boolean cumpleFiltros = true;
            
            // Filtro por categoría
            if (!filtroCategoria.equals("TODOS")) {
                if (!filtroCategoria.equals(equipo.getCategoria())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por estado (por ahora todos están activos, pero se puede expandir)
            if (!filtroEstado.equals("TODOS")) {
                // Por defecto todos los equipos están activos
                if (!filtroEstado.equals("ACTIVO")) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por número de jugadores
            if (!filtroNumJugadores.equals("TODOS")) {
                int numJugadores = equipo.getJugadoresIds() != null ? equipo.getJugadoresIds().size() : 0;
                if (!cumpleFiltroNumJugadores(numJugadores)) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por entrenador
            if (!filtroEntrenador.equals("TODOS")) {
                if (!filtroEntrenador.equals(equipo.getEntrenador())) {
                    cumpleFiltros = false;
                }
            }
            
            if (cumpleFiltros) {
                equiposFiltrados.add(equipo);
            }
        }
        
        return equiposFiltrados;
    }
    
    private boolean cumpleFiltroNumJugadores(int numJugadores) {
        switch (filtroNumJugadores) {
            case "0-5":
                return numJugadores >= 0 && numJugadores <= 5;
            case "6-10":
                return numJugadores >= 6 && numJugadores <= 10;
            case "11-15":
                return numJugadores >= 11 && numJugadores <= 15;
            case "16-20":
                return numJugadores >= 16 && numJugadores <= 20;
            case "20+":
                return numJugadores > 20;
            default:
                return true;
        }
    }

    private void actualizarEstadisticas() {
        Log.d(TAG, "actualizarEstadisticas: Actualizando estadísticas");
        List<Equipo> equipos = dataManager.getEquipos();
        
        // Aplicar filtros para las estadísticas
        List<Equipo> equiposFiltrados = aplicarFiltrosAvanzados(equipos);
        
        int totalEquipos = equiposFiltrados.size();
        int totalJugadores = 0;
        int totalCategorias = 0;
        Set<String> categoriasUnicas = new HashSet<>();
        
        for (Equipo equipo : equiposFiltrados) {
            if (equipo.getJugadoresIds() != null) {
                totalJugadores += equipo.getJugadoresIds().size();
            }
            if (equipo.getCategoria() != null) {
                categoriasUnicas.add(equipo.getCategoria());
            }
        }
        
        totalCategorias = categoriasUnicas.size();
        double promedioJugadores = totalEquipos > 0 ? (double) totalJugadores / totalEquipos : 0;
        
        // Actualizar vistas de estadísticas
        if (textViewTotalEquipos != null) {
            textViewTotalEquipos.setText(String.valueOf(totalEquipos));
        }
        
        if (textViewPromedioJugadores != null) {
            textViewPromedioJugadores.setText(String.format("%.1f", promedioJugadores));
        }
        
        if (textViewTotalCategorias != null) {
            textViewTotalCategorias.setText(String.valueOf(totalCategorias));
        }
        
        // Mantener compatibilidad con la vista antigua
        if (textViewEstadisticas != null) {
            String estadisticas = String.format("Total Equipos: %d | Total Jugadores: %d", totalEquipos, totalJugadores);
            textViewEstadisticas.setText(estadisticas);
        }
        
        Log.d(TAG, "actualizarEstadisticas: Estadísticas actualizadas - Equipos: " + totalEquipos + ", Jugadores: " + totalJugadores);
    }

    private void mostrarDialogoCrearEquipo() {
        Log.d(TAG, "mostrarDialogoCrearEquipo: Mostrando diálogo");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_equipo, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinnerCategoria);
        EditText editTextEntrenador = dialogView.findViewById(R.id.editTextEntrenador);
        
        // Configurar spinner de categorías
        String[] categorias = {"Biberones", "Prebenjamín", "Benjamín", "Alevín", "Infantil", "Cadete", "Juvenil"};
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);
        
        builder.setView(dialogView)
                .setTitle("Crear Nuevo Equipo")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String categoria = spinnerCategoria.getSelectedItem().toString();
                    String entrenador = editTextEntrenador.getText().toString().trim();
                    
                    if (validarDatos(nombre, categoria)) {
                        crearEquipo(nombre, categoria, entrenador);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatos(String nombre, String categoria) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre del equipo es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (categoria.isEmpty()) {
            Toast.makeText(requireContext(), "La categoría es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void crearEquipo(String nombre, String categoria, String entrenador) {
        Log.d(TAG, "crearEquipo: Creando equipo " + nombre);
        
        // Verificar si el equipo ya existe
        if (dataManager.existeEquipo(nombre)) {
            Toast.makeText(requireContext(), "Ya existe un equipo con ese nombre", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Equipo nuevoEquipo = new Equipo(nombre, categoria, entrenador);
        nuevoEquipo.setJugadoresIds(new ArrayList<>());
        
        dataManager.agregarEquipo(nuevoEquipo);
        cargarEquipos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Equipo creado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "crearEquipo: Equipo " + nombre + " creado correctamente");
    }

    private void mostrarDialogoEditarEquipo(Equipo equipo) {
        Log.d(TAG, "mostrarDialogoEditarEquipo: Mostrando diálogo para " + equipo.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_equipo, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinnerCategoria);
        EditText editTextEntrenador = dialogView.findViewById(R.id.editTextEntrenador);
        
        // Pre-llenar campos
        editTextNombre.setText(equipo.getNombre());
        editTextEntrenador.setText(equipo.getEntrenador());
        
        // Configurar spinner de categorías
        String[] categorias = {"Biberones", "Prebenjamín", "Benjamín", "Alevín", "Infantil", "Cadete", "Juvenil"};
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);
        
        // Seleccionar categoría actual
        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(equipo.getCategoria())) {
                spinnerCategoria.setSelection(i);
                break;
            }
        }
        
        builder.setView(dialogView)
                .setTitle("Editar Equipo")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String categoria = spinnerCategoria.getSelectedItem().toString();
                    String entrenador = editTextEntrenador.getText().toString().trim();
                    
                    if (validarDatosEdicion(nombre, categoria, equipo.getNombre())) {
                        editarEquipo(equipo, nombre, categoria, entrenador);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatosEdicion(String nombre, String categoria, String nombreOriginal) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre del equipo es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (categoria.isEmpty()) {
            Toast.makeText(requireContext(), "La categoría es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Verificar si el nombre ya existe (excepto para el equipo actual)
        if (!nombre.equalsIgnoreCase(nombreOriginal) && dataManager.existeEquipo(nombre)) {
            Toast.makeText(requireContext(), "Ya existe un equipo con ese nombre", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void editarEquipo(Equipo equipoOriginal, String nombre, String categoria, String entrenador) {
        Log.d(TAG, "editarEquipo: Editando equipo " + equipoOriginal.getNombre());
        
        // Actualizar datos del equipo
        equipoOriginal.setNombre(nombre);
        equipoOriginal.setCategoria(categoria);
        equipoOriginal.setEntrenador(entrenador);
        
        // Guardar cambios
        dataManager.actualizarEquipo(equipoOriginal);
        cargarEquipos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Equipo actualizado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "editarEquipo: Equipo " + nombre + " actualizado correctamente");
    }

    private void mostrarDialogoGestionarJugadores(Equipo equipo) {
        Log.d(TAG, "mostrarDialogoGestionarJugadores: Mostrando diálogo para " + equipo.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gestionar_jugadores, null);
        
        RecyclerView recyclerViewJugadoresDisponibles = dialogView.findViewById(R.id.recyclerViewJugadoresDisponibles);
        RecyclerView recyclerViewJugadoresEquipo = dialogView.findViewById(R.id.recyclerViewJugadoresEquipo);
        TextView textViewTitulo = dialogView.findViewById(R.id.textViewTitulo);
        
        textViewTitulo.setText("Gestionar Jugadores - " + equipo.getNombre());
        
        // Obtener todos los usuarios que son padres/tutores con jugadores
        List<Usuario> todosUsuarios = dataManager.getUsuarios();
        List<Usuario> jugadoresDisponibles = new ArrayList<>();
        List<Usuario> jugadoresEquipo = new ArrayList<>();
        
        for (Usuario usuario : todosUsuarios) {
            if (usuario.isEsPadre() && usuario.getJugador() != null && !usuario.getJugador().isEmpty()) {
                if (equipo.getJugadoresIds() != null && equipo.getJugadoresIds().contains(usuario.getId())) {
                    jugadoresEquipo.add(usuario);
                } else {
                    jugadoresDisponibles.add(usuario);
                }
            }
        }
        
        // Adaptadores como arreglos finales para referencia en lambdas
        final JugadorEquipoAdapter[] adapterDisponibles = new JugadorEquipoAdapter[1];
        final JugadorEquipoAdapter[] adapterEquipo = new JugadorEquipoAdapter[1];

        adapterDisponibles[0] = new JugadorEquipoAdapter(
            jugadoresDisponibles,
            jugador -> {
                jugadoresDisponibles.remove(jugador);
                jugadoresEquipo.add(jugador);
                adapterDisponibles[0].actualizarJugadores(jugadoresDisponibles);
                adapterEquipo[0].actualizarJugadores(jugadoresEquipo);
            },
            false // agregar
        );

        adapterEquipo[0] = new JugadorEquipoAdapter(
            jugadoresEquipo,
            jugador -> {
                jugadoresEquipo.remove(jugador);
                jugadoresDisponibles.add(jugador);
                adapterDisponibles[0].actualizarJugadores(jugadoresDisponibles);
                adapterEquipo[0].actualizarJugadores(jugadoresEquipo);
            },
            true // quitar
        );

        recyclerViewJugadoresDisponibles.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewJugadoresEquipo.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewJugadoresDisponibles.setAdapter(adapterDisponibles[0]);
        recyclerViewJugadoresEquipo.setAdapter(adapterEquipo[0]);
        
        builder.setView(dialogView)
                .setTitle("Gestionar Jugadores")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Guardar cambios en el equipo
                    guardarCambiosEquipo(equipo, jugadoresEquipo);
                    cargarEquipos();
                    actualizarEstadisticas();
                    Toast.makeText(requireContext(), "Jugadores actualizados", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarCambiosEquipo(Equipo equipo, List<Usuario> jugadoresEquipo) {
        // Actualizar la lista de IDs de jugadores en el equipo
        List<String> nuevosJugadoresIds = new ArrayList<>();
        for (Usuario jugador : jugadoresEquipo) {
            nuevosJugadoresIds.add(jugador.getId());
        }
        equipo.setJugadoresIds(nuevosJugadoresIds);
        
        // Guardar el equipo actualizado
        dataManager.actualizarEquipo(equipo);
    }

    private void mostrarDialogoEliminarEquipo(Equipo equipo) {
        Log.d(TAG, "mostrarDialogoEliminarEquipo: Mostrando diálogo para " + equipo.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Equipo")
                .setMessage("¿Estás seguro de que quieres eliminar el equipo '" + equipo.getNombre() + "'?\n\nEsto también eliminará todas las asociaciones con jugadores.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarEquipo(equipo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarEquipo(Equipo equipo) {
        Log.d(TAG, "eliminarEquipo: Eliminando equipo " + equipo.getNombre());
        
        dataManager.eliminarEquipo(equipo.getNombre());
        cargarEquipos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Equipo eliminado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "eliminarEquipo: Equipo " + equipo.getNombre() + " eliminado correctamente");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarEquipos();
        actualizarEstadisticas();
        // cargarJugadoresEquipo(); // Método comentado porque no existe en este fragmento
    }

    // Método comentado porque usa elementos que no existen en este fragmento
    /*
    private void cargarJugadoresEquipo() {
        Usuario usuarioActual = dataManager.getUsuarioActual();
        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            // Mostrar Spinner y cargar equipos
            spinnerEquipos.setVisibility(View.VISIBLE);
            listaEquipos = dataManager.getEquipos();
            List<String> nombresEquipos = new ArrayList<>();
            for (Equipo equipo : listaEquipos) {
                nombresEquipos.add(equipo.getNombre());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresEquipos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEquipos.setAdapter(adapter);
            spinnerEquipos.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    Equipo equipoSeleccionado = listaEquipos.get(position);
                    List<Usuario> jugadores = dataManager.getJugadoresPorEquipo(equipoSeleccionado.getId());
                    jugadorAdapter.actualizarUsuarios(jugadores);
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
            // Mostrar jugadores del primer equipo por defecto
            if (!listaEquipos.isEmpty()) {
                List<Usuario> jugadores = dataManager.getJugadoresPorEquipo(listaEquipos.get(0).getId());
                jugadorAdapter.actualizarUsuarios(jugadores);
            }
        } else if (usuarioActual != null && usuarioActual.getEquipoId() != null) {
            spinnerEquipos.setVisibility(View.GONE);
            List<Usuario> jugadores = dataManager.getJugadoresPorEquipo(usuarioActual.getEquipoId());
            jugadorAdapter.actualizarUsuarios(jugadores);
        } else {
            spinnerEquipos.setVisibility(View.GONE);
            jugadorAdapter.actualizarUsuarios(new ArrayList<>());
        }
    }
    */
    
    private void exportarEquiposPDF() {
        try {
            // Crear directorio si no existe
            File directory = new File(requireContext().getExternalFilesDir(null), "Reportes");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generar nombre de archivo con timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            String fileName = "Equipos_Club_" + timestamp + ".pdf";
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
            Paragraph title = new Paragraph("REPORTE DE EQUIPOS", titleFont);
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
            if (!filtroCategoria.equals("TODOS") || !filtroEstado.equals("TODOS") || 
                !filtroNumJugadores.equals("TODOS") || !filtroEntrenador.equals("TODOS")) {
                
                document.add(new Paragraph(" ", normalFont));
                Paragraph filtrosTitle = new Paragraph("FILTROS APLICADOS:", subtitleFont);
                document.add(filtrosTitle);
                
                PdfPTable filtrosTable = new PdfPTable(2);
                filtrosTable.setWidthPercentage(100);
                
                if (!filtroCategoria.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Categoría:", filtroCategoria);
                }
                if (!filtroEstado.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Estado:", filtroEstado);
                }
                if (!filtroNumJugadores.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Nº Jugadores:", filtroNumJugadores);
                }
                if (!filtroEntrenador.equals("TODOS")) {
                    agregarFilaTabla(filtrosTable, "Entrenador:", filtroEntrenador);
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
            
            // Obtener equipos filtrados
            List<Equipo> equipos = dataManager.getEquipos();
            equipos = aplicarFiltrosAvanzados(equipos);
            
            // Resumen general
            Paragraph resumenTitle = new Paragraph("RESUMEN GENERAL", subtitleFont);
            document.add(resumenTitle);
            
            PdfPTable resumenTable = new PdfPTable(2);
            resumenTable.setWidthPercentage(100);
            
            agregarFilaTabla(resumenTable, "Total de Equipos:", String.valueOf(equipos.size()));
            
            int totalJugadores = 0;
            Set<String> categoriasUnicas = new HashSet<>();
            for (Equipo equipo : equipos) {
                if (equipo.getJugadoresIds() != null) {
                    totalJugadores += equipo.getJugadoresIds().size();
                }
                if (equipo.getCategoria() != null) {
                    categoriasUnicas.add(equipo.getCategoria());
                }
            }
            
            agregarFilaTabla(resumenTable, "Total de Jugadores:", String.valueOf(totalJugadores));
            agregarFilaTabla(resumenTable, "Categorías:", String.valueOf(categoriasUnicas.size()));
            
            double promedioJugadores = equipos.size() > 0 ? (double) totalJugadores / equipos.size() : 0;
            agregarFilaTabla(resumenTable, "Promedio Jugadores/Equipo:", String.format("%.1f", promedioJugadores));
            
            document.add(resumenTable);
            
            // Listado detallado de equipos
            document.add(new Paragraph(" ", normalFont));
            Paragraph equiposTitle = new Paragraph("LISTADO DETALLADO DE EQUIPOS", subtitleFont);
            document.add(equiposTitle);
            
            for (Equipo equipo : equipos) {
                document.add(new Paragraph(" ", normalFont));
                
                PdfPTable equipoTable = new PdfPTable(2);
                equipoTable.setWidthPercentage(100);
                
                agregarFilaTabla(equipoTable, "Nombre:", equipo.getNombre());
                agregarFilaTabla(equipoTable, "Categoría:", equipo.getCategoria());
                agregarFilaTabla(equipoTable, "Entrenador:", equipo.getEntrenador() != null ? equipo.getEntrenador() : "No asignado");
                
                int numJugadores = equipo.getJugadoresIds() != null ? equipo.getJugadoresIds().size() : 0;
                agregarFilaTabla(equipoTable, "Nº Jugadores:", String.valueOf(numJugadores));
                
                document.add(equipoTable);
                document.add(new Paragraph("─".repeat(30), smallFont));
            }
            
            // Estadísticas por categoría
            document.add(new Paragraph(" ", normalFont));
            Paragraph categoriasTitle = new Paragraph("ESTADÍSTICAS POR CATEGORÍA", subtitleFont);
            document.add(categoriasTitle);
            
            Map<String, Integer> equiposPorCategoria = new HashMap<>();
            Map<String, Integer> jugadoresPorCategoria = new HashMap<>();
            
            for (Equipo equipo : equipos) {
                String categoria = equipo.getCategoria();
                int equiposCount = equiposPorCategoria.containsKey(categoria) ? equiposPorCategoria.get(categoria) : 0;
                equiposPorCategoria.put(categoria, equiposCount + 1);
                int numJugadores = equipo.getJugadoresIds() != null ? equipo.getJugadoresIds().size() : 0;
                int jugadoresCount = jugadoresPorCategoria.containsKey(categoria) ? jugadoresPorCategoria.get(categoria) : 0;
                jugadoresPorCategoria.put(categoria, jugadoresCount + numJugadores);
            }
            
            PdfPTable categoriasTable = new PdfPTable(3);
            categoriasTable.setWidthPercentage(100);
            
            // Encabezados
            PdfPCell header1 = new PdfPCell(new Phrase("Categoría", subtitleFont));
            PdfPCell header2 = new PdfPCell(new Phrase("Equipos", subtitleFont));
            PdfPCell header3 = new PdfPCell(new Phrase("Jugadores", subtitleFont));
            
            categoriasTable.addCell(header1);
            categoriasTable.addCell(header2);
            categoriasTable.addCell(header3);
            
            for (Map.Entry<String, Integer> entry : equiposPorCategoria.entrySet()) {
                String categoria = entry.getKey();
                int numEquipos = entry.getValue();
                int numJugadores = jugadoresPorCategoria.containsKey(categoria) ? jugadoresPorCategoria.get(categoria) : 0;
                
                categoriasTable.addCell(new PdfPCell(new Phrase(categoria, normalFont)));
                categoriasTable.addCell(new PdfPCell(new Phrase(String.valueOf(numEquipos), normalFont)));
                categoriasTable.addCell(new PdfPCell(new Phrase(String.valueOf(numJugadores), normalFont)));
            }
            
            document.add(categoriasTable);
            
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
            Log.e(TAG, "Error al exportar PDF", e);
            Toast.makeText(requireContext(), "Error al exportar el reporte", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void agregarFilaTabla(PdfPTable table, String label, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
        
        cell1.setBorder(PdfPCell.NO_BORDER);
        cell2.setBorder(PdfPCell.NO_BORDER);
        
        table.addCell(cell1);
        table.addCell(cell2);
    }
    
    // Métodos para importación de Excel
    private void configurarFilePicker() {
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        procesarArchivoExcel(uri);
                    }
                }
            }
        );
    }
    
    private void importarEquiposExcel() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv"
        });
        filePickerLauncher.launch(Intent.createChooser(intent, "Seleccionar archivo Excel"));
    }
    
    private void procesarArchivoExcel(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(requireContext(), "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show();
                return;
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int lineNumber = 0;
            int equiposCreados = 0;
            int equiposExistentes = 0;
            
            // Saltar la primera línea (encabezados)
            reader.readLine();
            lineNumber++;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] columns = line.split(",");
                    if (columns.length >= 3) {
                        String nombre = columns[0].trim();
                        String categoria = columns[1].trim();
                        String entrenador = columns[2].trim();
                        
                        // Validar datos
                        if (!nombre.isEmpty() && !categoria.isEmpty()) {
                            // Verificar si el equipo ya existe
                            if (!dataManager.existeEquipo(nombre)) {
                                Equipo nuevoEquipo = new Equipo(nombre, categoria, entrenador);
                                nuevoEquipo.setJugadoresIds(new ArrayList<>());
                                dataManager.agregarEquipo(nuevoEquipo);
                                equiposCreados++;
                            } else {
                                equiposExistentes++;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error procesando línea " + lineNumber + ": " + line, e);
                }
            }
            
            reader.close();
            inputStream.close();
            
            // Actualizar la vista
            cargarEquipos();
            actualizarEstadisticas();
            
            // Mostrar resultado
            String mensaje = String.format("Importación completada:\n%d equipos creados\n%d equipos existentes", 
                equiposCreados, equiposExistentes);
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error procesando archivo Excel", e);
            Toast.makeText(requireContext(), "Error al procesar el archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void descargarPlantillaExcel() {
        try {
            // Crear contenido de la plantilla
            StringBuilder plantilla = new StringBuilder();
            plantilla.append("Nombre,Categoría,Entrenador\n");
            plantilla.append("Biberones A,Biberones,Carlos López\n");
            plantilla.append("Prebenjamín A,Prebenjamín,Miguel García\n");
            plantilla.append("Benjamín A,Benjamín,Ana Martínez\n");
            plantilla.append("Alevín A,Alevín,David Rodríguez\n");
            plantilla.append("Infantil,Infantil,Laura Sánchez\n");
            plantilla.append("Cadete,Cadete,Javier Pérez\n");
            plantilla.append("Juvenil,Juvenil,María González\n");
            
            // Crear archivo temporal
            File archivo = new File(requireContext().getExternalFilesDir(null), "plantilla_equipos.csv");
            FileOutputStream fos = new FileOutputStream(archivo);
            fos.write(plantilla.toString().getBytes());
            fos.close();
            
            // Compartir archivo
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            Uri uri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(), 
                requireContext().getPackageName() + ".provider", 
                archivo
            );
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Plantilla Equipos - CD Santiaguino Guizán");
            intent.putExtra(Intent.EXTRA_TEXT, "Plantilla para importar equipos al sistema");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            startActivity(Intent.createChooser(intent, "Compartir plantilla"));
            
        } catch (Exception e) {
            Log.e(TAG, "Error creando plantilla", e);
            Toast.makeText(requireContext(), "Error al crear la plantilla: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 