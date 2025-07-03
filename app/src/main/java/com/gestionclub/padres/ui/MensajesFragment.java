package com.gestionclub.padres.ui;

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
import com.gestionclub.padres.adapter.MensajeAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class MensajesFragment extends Fragment {
    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private Button buttonEnviar;
    private MensajeAdapter mensajeAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensajes, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarMensajes();
        configurarListeners();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        buttonEnviar = view.findViewById(R.id.buttonEnviar);
    }

    private void configurarRecyclerView() {
        mensajeAdapter = new MensajeAdapter(dataManager.getMensajes());
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewMensajes.setAdapter(mensajeAdapter);
    }

    private void cargarMensajes() {
        List<Mensaje> mensajes = dataManager.getMensajes();
        mensajeAdapter.actualizarMensajes(mensajes);
        if (!mensajes.isEmpty()) {
            recyclerViewMensajes.smoothScrollToPosition(mensajes.size() - 1);
        }
    }

    private void configurarListeners() {
        buttonEnviar.setOnClickListener(v -> enviarMensaje());
        
        editTextMensaje.setOnEditorActionListener((v, actionId, event) -> {
            enviarMensaje();
            return true;
        });
    }

    private void enviarMensaje() {
        String contenido = editTextMensaje.getText().toString().trim();
        
        if (contenido.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioActual == null) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Mensaje nuevoMensaje = new Mensaje(
            usuarioActual.getId(),
            usuarioActual.getNombre(),
            contenido,
            usuarioActual.isEsAdmin()
        );

        dataManager.agregarMensaje(nuevoMensaje);
        cargarMensajes();
        editTextMensaje.setText("");

        // Crear notificación para nuevos mensajes (solo si es admin)
        if (usuarioActual.isEsAdmin()) {
            crearNotificacionMensaje(nuevoMensaje);
        }
    }

    private void crearNotificacionMensaje(Mensaje mensaje) {
        // Esta funcionalidad se implementará cuando se complete el sistema de notificaciones
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMensajes();
    }
} 