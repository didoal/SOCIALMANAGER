package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.MensajeAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import android.widget.EditText;
import android.widget.Button;
import android.view.inputmethod.InputMethodManager;

public class MuroDestacadosFragment extends Fragment {
    private RecyclerView recyclerViewMensajes;
    private TextView textViewTitulo;
    private TextView textViewInfo;
    private TextView textViewTotalMensajes;
    private MensajeAdapter mensajeAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private EditText editTextMensaje;
    private Button buttonPublicar;
    private View layoutPublicar;
    private View layoutInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_muro_destacados, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarRecyclerView();
            cargarMensajesDestacados();
            actualizarEstadisticas();
        } catch (Exception e) {
            Log.e("MuroDestacados", "Error al cargar muro de destacados", e);
            mostrarErrorCarga(view);
        }
        return view;
    }
    
    private void mostrarErrorCarga(View view) {
        // Ocultar vistas principales
        if (recyclerViewMensajes != null) {
            recyclerViewMensajes.setVisibility(View.GONE);
        }
        
        // Mostrar solo el Toast de error
        Toast.makeText(requireContext(), R.string.error_cargar_muro, Toast.LENGTH_LONG).show();
    }

    private void inicializarVistas(View view) {
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);
        textViewInfo = view.findViewById(R.id.textViewInfo);
        textViewTotalMensajes = view.findViewById(R.id.textViewInfo); // Reutilizar el mismo ID
        layoutPublicar = view.findViewById(R.id.layoutPublicar);
        layoutInfo = view.findViewById(R.id.layoutInfo);
        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        buttonPublicar = view.findViewById(R.id.buttonPublicar);

        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            layoutPublicar.setVisibility(View.VISIBLE);
            buttonPublicar.setOnClickListener(v -> publicarMensajeDestacado());
        } else {
            layoutPublicar.setVisibility(View.GONE);
        }
    }

    private void actualizarEstadisticas() {
        try {
            List<Mensaje> mensajesDestacados = dataManager.getMensajesDestacados();
            List<Mensaje> todosLosMensajes = dataManager.getMensajes();
            
            // Actualizar contador de mensajes destacados
            if (textViewTitulo != null) {
                textViewTitulo.setText(String.valueOf(mensajesDestacados.size()));
            }
            
            // Actualizar contador total de mensajes
            if (textViewTotalMensajes != null) {
                textViewTotalMensajes.setText(String.valueOf(todosLosMensajes.size()));
            }
            
        } catch (Exception e) {
            Log.e("MuroDestacados", "Error al actualizar estadísticas", e);
        }
    }

    private void publicarMensajeDestacado() {
        String mensajeTexto = editTextMensaje.getText().toString().trim();
        if (mensajeTexto.isEmpty()) {
            Toast.makeText(requireContext(), "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            Mensaje nuevoMensaje = new Mensaje();
            nuevoMensaje.setContenido(mensajeTexto);
            nuevoMensaje.setDestacado(true);
            nuevoMensaje.setRemitenteNombre(usuarioActual != null ? usuarioActual.getNombre() : "Admin");
            nuevoMensaje.setFechaCreacion(new java.util.Date());
            nuevoMensaje.setId("msg_" + System.currentTimeMillis()); // ID único
            
            dataManager.agregarMensajeDestacado(nuevoMensaje);
            editTextMensaje.setText("");
            
            cargarMensajesDestacados();
            actualizarEstadisticas();
            
            // Ocultar teclado
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editTextMensaje.getWindowToken(), 0);
            }
            
            Toast.makeText(requireContext(), "Mensaje destacado publicado", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e("MuroDestacados", "Error al publicar mensaje", e);
            Toast.makeText(requireContext(), "Error al publicar mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarRecyclerView() {
        mensajeAdapter = new MensajeAdapter(new ArrayList<>(), usuarioActual);
        mensajeAdapter.setOnMensajeClickListener(new MensajeAdapter.OnMensajeClickListener() {
            @Override
            public void onMensajeLongClick(Mensaje mensaje, View view) {
                // En el muro de destacados, permitir quitar del destacado
                if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                    mostrarDialogoQuitarDestacado(mensaje);
                }
            }
        });
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewMensajes.setAdapter(mensajeAdapter);
    }

    private void cargarMensajesDestacados() {
        try {
            List<Mensaje> mensajesDestacados = dataManager.getMensajesDestacados();
            
            if (mensajesDestacados.isEmpty()) {
                if (layoutInfo != null) {
                    layoutInfo.setVisibility(View.VISIBLE);
                }
                if (recyclerViewMensajes != null) {
                    recyclerViewMensajes.setVisibility(View.GONE);
                }
            } else {
                if (layoutInfo != null) {
                    layoutInfo.setVisibility(View.GONE);
                }
                if (recyclerViewMensajes != null) {
                    recyclerViewMensajes.setVisibility(View.VISIBLE);
                }
                mensajeAdapter.actualizarMensajes(mensajesDestacados);
            }
            
        } catch (Exception e) {
            Log.e("MuroDestacados", "Error al cargar mensajes destacados", e);
            Toast.makeText(requireContext(), "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoQuitarDestacado(Mensaje mensaje) {
        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("Quitar de destacados")
            .setMessage("¿Quieres quitar este mensaje del muro de destacados?")
            .setPositiveButton("Sí", (dialog, which) -> {
                try {
                    dataManager.toggleDestacadoMensaje(mensaje.getId());
                    cargarMensajesDestacados();
                    actualizarEstadisticas();
                    Toast.makeText(requireContext(), "Mensaje quitado de destacados", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("MuroDestacados", "Error al quitar destacado", e);
                    Toast.makeText(requireContext(), "Error al quitar destacado", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMensajesDestacados();
        actualizarEstadisticas();
    }
} 