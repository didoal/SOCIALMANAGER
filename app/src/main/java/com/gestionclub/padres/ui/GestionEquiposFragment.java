package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class GestionEquiposFragment extends Fragment implements EquipoAdapter.OnEquipoClickListener {
    private RecyclerView recyclerViewEquipos;
    private TextView textViewTitulo;
    private Button buttonAgregarEquipo;
    private EquipoAdapter equipoAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_equipos, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarEquipos();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewEquipos = view.findViewById(R.id.recyclerViewEquipos);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);
        buttonAgregarEquipo = view.findViewById(R.id.buttonAgregarEquipo);
        
        buttonAgregarEquipo.setOnClickListener(v -> mostrarDialogoEquipo(null));
    }

    private void configurarRecyclerView() {
        equipoAdapter = new EquipoAdapter(dataManager.getEquipos(), this);
        recyclerViewEquipos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEquipos.setAdapter(equipoAdapter);
    }

    private void cargarEquipos() {
        List<Equipo> equipos = dataManager.getEquipos();
        equipoAdapter.actualizarEquipos(equipos);
        textViewTitulo.setText("Gestión de Equipos (" + equipos.size() + ")");
    }

    private void mostrarDialogoEquipo(Equipo equipoExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_equipo, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinnerCategoria);
        Spinner spinnerEntrenador = dialogView.findViewById(R.id.spinnerEntrenador);
        
        // Configurar spinner de categorías
        String[] categorias = {"Sub-8", "Sub-10", "Sub-12", "Sub-14", "Sub-16", "Sub-18"};
        android.widget.ArrayAdapter<String> categoriaAdapter = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);
        
        // Configurar spinner de entrenadores
        List<Usuario> entrenadores = dataManager.getUsuarios();
        List<Usuario> entrenadoresFiltrados = new java.util.ArrayList<>();
        for (Usuario usuario : entrenadores) {
            if (usuario.isEsEntrenador()) {
                entrenadoresFiltrados.add(usuario);
            }
        }
        
        String[] nombresEntrenadores = new String[entrenadoresFiltrados.size()];
        for (int i = 0; i < entrenadoresFiltrados.size(); i++) {
            nombresEntrenadores[i] = entrenadoresFiltrados.get(i).getNombre();
        }
        
        android.widget.ArrayAdapter<String> entrenadorAdapter = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, nombresEntrenadores);
        entrenadorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntrenador.setAdapter(entrenadorAdapter);
        
        // Llenar datos si es edición
        if (equipoExistente != null) {
            editTextNombre.setText(equipoExistente.getNombre());
            editTextDescripcion.setText(equipoExistente.getDescripcion());
            
            // Seleccionar categoría
            for (int i = 0; i < categorias.length; i++) {
                if (categorias[i].equals(equipoExistente.getCategoria())) {
                    spinnerCategoria.setSelection(i);
                    break;
                }
            }
            
            // Seleccionar entrenador
            for (int i = 0; i < entrenadoresFiltrados.size(); i++) {
                if (entrenadoresFiltrados.get(i).getId().equals(equipoExistente.getEntrenadorId())) {
                    spinnerEntrenador.setSelection(i);
                    break;
                }
            }
        }
        
        String tituloDialogo = equipoExistente != null ? "Editar Equipo" : "Nuevo Equipo";
        
        builder.setView(dialogView)
                .setTitle(tituloDialogo)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String descripcion = editTextDescripcion.getText().toString().trim();
                    String categoria = spinnerCategoria.getSelectedItem().toString();
                    
                    if (nombre.isEmpty() || descripcion.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (entrenadoresFiltrados.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay entrenadores disponibles", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    Usuario entrenadorSeleccionado = entrenadoresFiltrados.get(spinnerEntrenador.getSelectedItemPosition());
                    
                    if (equipoExistente != null) {
                        actualizarEquipo(equipoExistente, nombre, descripcion, categoria, entrenadorSeleccionado);
                    } else {
                        agregarEquipo(nombre, descripcion, categoria, entrenadorSeleccionado);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void agregarEquipo(String nombre, String descripcion, String categoria, Usuario entrenador) {
        Equipo nuevoEquipo = new Equipo(nombre, categoria, entrenador.getId(), entrenador.getNombre(), descripcion);
        dataManager.agregarEquipo(nuevoEquipo);
        cargarEquipos();
        Toast.makeText(requireContext(), "Equipo creado exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void actualizarEquipo(Equipo equipo, String nombre, String descripcion, String categoria, Usuario entrenador) {
        equipo.setNombre(nombre);
        equipo.setDescripcion(descripcion);
        equipo.setCategoria(categoria);
        equipo.setEntrenadorId(entrenador.getId());
        equipo.setEntrenadorNombre(entrenador.getNombre());
        
        List<Equipo> equipos = dataManager.getEquipos();
        for (int i = 0; i < equipos.size(); i++) {
            if (equipos.get(i).getId().equals(equipo.getId())) {
                equipos.set(i, equipo);
                break;
            }
        }
        dataManager.guardarEquipos(equipos);
        cargarEquipos();
        Toast.makeText(requireContext(), "Equipo actualizado exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEquipoClick(Equipo equipo) {
        // Mostrar opciones: editar o eliminar
        String[] opciones = {"Editar", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Opciones del equipo")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        // Editar
                        mostrarDialogoEquipo(equipo);
                    } else if (which == 1) {
                        // Eliminar
                        confirmarEliminarEquipo(equipo);
                    }
                })
                .show();
    }

    private void confirmarEliminarEquipo(Equipo equipo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Equipo")
                .setMessage("¿Estás seguro de que quieres eliminar el equipo '" + equipo.getNombre() + "'? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, Eliminar", (dialog, which) -> {
                    List<Equipo> equipos = dataManager.getEquipos();
                    List<Equipo> equiposFiltrados = new java.util.ArrayList<>();
                    for (Equipo e : equipos) {
                        if (!e.getId().equals(equipo.getId())) {
                            equiposFiltrados.add(e);
                        }
                    }
                    dataManager.guardarEquipos(equiposFiltrados);
                    cargarEquipos();
                    Toast.makeText(requireContext(), "Equipo eliminado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEquipos();
    }
} 