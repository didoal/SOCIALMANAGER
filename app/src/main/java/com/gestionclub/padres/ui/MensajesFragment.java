package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.gestionclub.padres.adapter.MensajeAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class MensajesFragment extends Fragment {
    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private Button buttonEnviar;
    private Button buttonFiltroEquipo;
    private TextView textViewChatInfo;
    private TextView textViewEquipoFiltro;
    private MensajeAdapter mensajeAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private String equipoFiltroActual = "Todos";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensajes, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarRecyclerView();
            configurarFiltroEquipo();
            cargarMensajes();
            configurarListeners();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "No se pudo cargar los mensajes. Intenta más tarde.", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        buttonEnviar = view.findViewById(R.id.buttonEnviar);
        buttonFiltroEquipo = view.findViewById(R.id.buttonFiltroEquipo);
        textViewChatInfo = view.findViewById(R.id.textViewChatInfo);
        textViewEquipoFiltro = view.findViewById(R.id.textViewEquipoFiltro);
    }

    private void configurarRecyclerView() {
        mensajeAdapter = new MensajeAdapter(new ArrayList<>(), usuarioActual);
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewMensajes.setAdapter(mensajeAdapter);
    }

    private void configurarFiltroEquipo() {
        // Mostrar botón de filtro solo para administradores
        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            buttonFiltroEquipo.setVisibility(View.VISIBLE);
        } else {
            buttonFiltroEquipo.setVisibility(View.GONE);
            // Para usuarios normales, filtrar automáticamente por su equipo
            if (usuarioActual != null && usuarioActual.getEquipo() != null) {
                equipoFiltroActual = usuarioActual.getEquipo();
                textViewEquipoFiltro.setText("Equipo: " + equipoFiltroActual);
            }
        }
    }

    private void cargarMensajes() {
        List<Mensaje> todosMensajes = dataManager.getMensajes();
        List<Mensaje> mensajesFiltrados = new ArrayList<>();
        
        for (Mensaje mensaje : todosMensajes) {
            // Si es "Todos", mostrar todos los mensajes
            if ("Todos".equals(equipoFiltroActual)) {
                mensajesFiltrados.add(mensaje);
            } else {
                // Filtrar por equipo específico
                if (equipoFiltroActual.equals(mensaje.getEquipo()) || 
                    (mensaje.getEquipo() == null && usuarioActual != null && equipoFiltroActual.equals(usuarioActual.getEquipo()))) {
                    mensajesFiltrados.add(mensaje);
                }
            }
        }
        
        mensajeAdapter.actualizarMensajes(mensajesFiltrados);
        if (!mensajesFiltrados.isEmpty()) {
            recyclerViewMensajes.smoothScrollToPosition(mensajesFiltrados.size() - 1);
        }
    }

    private void configurarListeners() {
        buttonEnviar.setOnClickListener(v -> enviarMensaje());
        buttonFiltroEquipo.setOnClickListener(v -> mostrarDialogoFiltro());
        
        editTextMensaje.setOnEditorActionListener((v, actionId, event) -> {
            enviarMensaje();
            return true;
        });
    }

    private void mostrarDialogoFiltro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Filtrar por Equipo");

        // Crear spinner con opciones
        Spinner spinner = new Spinner(requireContext());
        String[] equipos = {"Todos", "Biberones", "Prebenjamín A", "Prebenjamín B", "Benjamín A", "Benjamín B", 
                           "Alevín A", "Alevín B", "Infantil", "Cadete", "Juvenil"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Seleccionar el equipo actual
        for (int i = 0; i < equipos.length; i++) {
            if (equipos[i].equals(equipoFiltroActual)) {
                spinner.setSelection(i);
                break;
            }
        }

        builder.setView(spinner);
        builder.setPositiveButton("Aplicar", (dialog, which) -> {
            equipoFiltroActual = spinner.getSelectedItem().toString();
            textViewEquipoFiltro.setText("Equipo: " + equipoFiltroActual);
            cargarMensajes();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
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
        
        // Asignar equipo al mensaje
        nuevoMensaje.setEquipo(usuarioActual.getEquipo());

        dataManager.agregarMensaje(nuevoMensaje);
        cargarMensajes();
        editTextMensaje.setText("");

        // Crear notificación para nuevos mensajes (solo si es admin)
        if (usuarioActual.isEsAdmin()) {
            crearNotificacionMensaje(nuevoMensaje);
        }
    }

    private void crearNotificacionMensaje(Mensaje mensaje) {
        dataManager.crearNotificacionMensaje(mensaje);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMensajes();
    }
} 