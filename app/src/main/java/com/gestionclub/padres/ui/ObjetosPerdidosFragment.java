package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.ObjetoPerdidoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.ObjetoPerdido;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class ObjetosPerdidosFragment extends Fragment implements ObjetoPerdidoAdapter.OnObjetoClickListener {
    private RecyclerView recyclerViewObjetos;
    private Button buttonAgregarObjeto;
    private Button buttonFiltroTodos;
    private Button buttonFiltroPerdidos;
    private Button buttonFiltroEncontrados;
    private TextView textViewPerdidos;
    private TextView textViewEncontrados;
    private TextView textViewSinObjetos;
    private ObjetoPerdidoAdapter objetoAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private String filtroActual = "TODOS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objetos_perdidos, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarRecyclerView();
            configurarFiltros();
            cargarObjetos();
            configurarListeners();
            actualizarEstadisticas();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "No se pudo cargar los objetos perdidos. Intenta más tarde.", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewObjetos = view.findViewById(R.id.recyclerViewObjetos);
        buttonAgregarObjeto = view.findViewById(R.id.buttonAgregarObjeto);
        buttonFiltroTodos = view.findViewById(R.id.buttonFiltroTodos);
        buttonFiltroPerdidos = view.findViewById(R.id.buttonFiltroPerdidos);
        buttonFiltroEncontrados = view.findViewById(R.id.buttonFiltroEncontrados);
        textViewPerdidos = view.findViewById(R.id.textViewPerdidos);
        textViewEncontrados = view.findViewById(R.id.textViewEncontrados);
        textViewSinObjetos = view.findViewById(R.id.textViewSinObjetos);
    }

    private void configurarRecyclerView() {
        objetoAdapter = new ObjetoPerdidoAdapter(new ArrayList<>(), this);
        recyclerViewObjetos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewObjetos.setAdapter(objetoAdapter);
    }

    private void configurarFiltros() {
        // Configurar estado inicial de los filtros
        actualizarEstadosFiltros();
    }

    private void actualizarEstadosFiltros() {
        // Resetear todos los botones
        buttonFiltroTodos.setBackgroundResource(R.drawable.button_secondary_background);
        buttonFiltroTodos.setTextColor(getResources().getColor(R.color.text_primary));
        buttonFiltroPerdidos.setBackgroundResource(R.drawable.button_secondary_background);
        buttonFiltroPerdidos.setTextColor(getResources().getColor(R.color.text_primary));
        buttonFiltroEncontrados.setBackgroundResource(R.drawable.button_secondary_background);
        buttonFiltroEncontrados.setTextColor(getResources().getColor(R.color.text_primary));

        // Activar el filtro actual
        switch (filtroActual) {
            case "TODOS":
                buttonFiltroTodos.setBackgroundResource(R.drawable.button_gold_background);
                buttonFiltroTodos.setTextColor(getResources().getColor(R.color.black));
                break;
            case "PERDIDOS":
                buttonFiltroPerdidos.setBackgroundResource(R.drawable.button_gold_background);
                buttonFiltroPerdidos.setTextColor(getResources().getColor(R.color.black));
                break;
            case "ENCONTRADOS":
                buttonFiltroEncontrados.setBackgroundResource(R.drawable.button_gold_background);
                buttonFiltroEncontrados.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }

    private void cargarObjetos() {
        List<ObjetoPerdido> todosObjetos = dataManager.getObjetosPerdidos();
        List<ObjetoPerdido> objetosFiltrados = new ArrayList<>();
        
        for (ObjetoPerdido objeto : todosObjetos) {
            switch (filtroActual) {
                case "TODOS":
                    objetosFiltrados.add(objeto);
                    break;
                case "PERDIDOS":
                    if ("PERDIDO".equals(objeto.getEstado())) {
                        objetosFiltrados.add(objeto);
                    }
                    break;
                case "ENCONTRADOS":
                    if ("ENCONTRADO".equals(objeto.getEstado()) || "RECLAMADO".equals(objeto.getEstado())) {
                        objetosFiltrados.add(objeto);
                    }
                    break;
            }
        }
        
        objetoAdapter.actualizarObjetos(objetosFiltrados);
        
        // Mostrar/ocultar mensaje de sin objetos
        if (objetosFiltrados.isEmpty()) {
            textViewSinObjetos.setVisibility(View.VISIBLE);
            recyclerViewObjetos.setVisibility(View.GONE);
        } else {
            textViewSinObjetos.setVisibility(View.GONE);
            recyclerViewObjetos.setVisibility(View.VISIBLE);
        }
    }

    private void actualizarEstadisticas() {
        List<ObjetoPerdido> todosObjetos = dataManager.getObjetosPerdidos();
        int perdidos = 0;
        int encontrados = 0;
        
        for (ObjetoPerdido objeto : todosObjetos) {
            if ("PERDIDO".equals(objeto.getEstado())) {
                perdidos++;
            } else {
                encontrados++;
            }
        }
        
        textViewPerdidos.setText(String.valueOf(perdidos));
        textViewEncontrados.setText(String.valueOf(encontrados));
    }

    private void configurarListeners() {
        buttonAgregarObjeto.setOnClickListener(v -> mostrarDialogoAgregarObjeto());
        buttonFiltroTodos.setOnClickListener(v -> {
            filtroActual = "TODOS";
            actualizarEstadosFiltros();
            cargarObjetos();
        });
        buttonFiltroPerdidos.setOnClickListener(v -> {
            filtroActual = "PERDIDOS";
            actualizarEstadosFiltros();
            cargarObjetos();
        });
        buttonFiltroEncontrados.setOnClickListener(v -> {
            filtroActual = "ENCONTRADOS";
            actualizarEstadosFiltros();
            cargarObjetos();
        });
    }

    private void mostrarDialogoAgregarObjeto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_agregar_objeto, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextUbicacion = dialogView.findViewById(R.id.editTextUbicacion);
        
        builder.setView(dialogView)
                .setTitle("Reportar Objeto Perdido")
                .setPositiveButton("Reportar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String descripcion = editTextDescripcion.getText().toString().trim();
                    String ubicacion = editTextUbicacion.getText().toString().trim();
                    
                    if (nombre.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    agregarObjeto(nombre, descripcion, ubicacion);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void agregarObjeto(String nombre, String descripcion, String ubicacion) {
        if (usuarioActual == null) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        ObjetoPerdido nuevoObjeto = new ObjetoPerdido(
            nombre,
            descripcion,
            ubicacion,
            usuarioActual.getId(),
            usuarioActual.getNombre(),
            usuarioActual.isEsAdmin()
        );

        dataManager.agregarObjetoPerdido(nuevoObjeto);
        cargarObjetos();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Objeto reportado exitosamente", Toast.LENGTH_SHORT).show();
        
        // Crear notificación para nuevos objetos
        crearNotificacionObjeto(nuevoObjeto);
    }

    @Override
    public void onReclamarClick(ObjetoPerdido objeto) {
        if (usuarioActual == null) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Reclamar Objeto")
                .setMessage("¿Estás seguro de que quieres reclamar este objeto?")
                .setPositiveButton("Sí, Reclamar", (dialog, which) -> {
                    objeto.setEstado("RECLAMADO");
                    objeto.setReclamadoPor(usuarioActual.getId());
                    objeto.setReclamadoPorNombre(usuarioActual.getNombre());
                    objeto.setFechaReclamo(new java.util.Date());
                    
                    dataManager.actualizarObjetoPerdido(objeto);
                    cargarObjetos();
                    actualizarEstadisticas();
                    
                    Toast.makeText(requireContext(), "Objeto reclamado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearNotificacionObjeto(ObjetoPerdido objeto) {
        dataManager.crearNotificacionObjeto(objeto);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarObjetos();
        actualizarEstadisticas();
    }
} 