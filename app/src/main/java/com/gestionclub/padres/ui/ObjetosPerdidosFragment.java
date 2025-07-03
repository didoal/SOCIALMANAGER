package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

public class ObjetosPerdidosFragment extends Fragment implements ObjetoPerdidoAdapter.OnObjetoClickListener {
    private RecyclerView recyclerViewObjetos;
    private Button buttonAgregarObjeto;
    private ObjetoPerdidoAdapter objetoAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objetos_perdidos, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarObjetos();
        configurarListeners();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewObjetos = view.findViewById(R.id.recyclerViewObjetos);
        buttonAgregarObjeto = view.findViewById(R.id.buttonAgregarObjeto);
    }

    private void configurarRecyclerView() {
        objetoAdapter = new ObjetoPerdidoAdapter(dataManager.getObjetosPerdidos(), this);
        recyclerViewObjetos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewObjetos.setAdapter(objetoAdapter);
    }

    private void cargarObjetos() {
        List<ObjetoPerdido> objetos = dataManager.getObjetosPerdidos();
        objetoAdapter.actualizarObjetos(objetos);
    }

    private void configurarListeners() {
        buttonAgregarObjeto.setOnClickListener(v -> mostrarDialogoAgregarObjeto());
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
    }
} 