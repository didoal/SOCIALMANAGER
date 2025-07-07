package com.gestionclub.padres.ui;

import android.os.Bundle;
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

public class MuroDestacadosFragment extends Fragment {
    private RecyclerView recyclerViewMensajes;
    private TextView textViewTitulo;
    private TextView textViewInfo;
    private MensajeAdapter mensajeAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

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
        } catch (Exception e) {
            Toast.makeText(requireContext(), "No se pudo cargar el muro de destacados.", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);
        textViewInfo = view.findViewById(R.id.textViewInfo);
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
        List<Mensaje> mensajesDestacados = dataManager.getMensajesDestacados();
        
        if (mensajesDestacados.isEmpty()) {
            textViewInfo.setText("No hay mensajes destacados aún.\nLos administradores pueden destacar mensajes importantes.");
            textViewInfo.setVisibility(View.VISIBLE);
        } else {
            textViewInfo.setVisibility(View.GONE);
        }
        
        mensajeAdapter.actualizarMensajes(mensajesDestacados);
    }

    private void mostrarDialogoQuitarDestacado(Mensaje mensaje) {
        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("Quitar de destacados")
            .setMessage("¿Quieres quitar este mensaje del muro de destacados?")
            .setPositiveButton("Sí", (dialog, which) -> {
                dataManager.toggleDestacadoMensaje(mensaje.getId());
                cargarMensajesDestacados();
                Toast.makeText(requireContext(), "Mensaje quitado de destacados", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("No", null)
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMensajesDestacados();
    }
} 