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
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class ChatFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            configurarBotones(view);
            cargarChats();
        } catch (Exception e) {
            Log.e("ChatFragment", "Error al cargar chat", e);
            Toast.makeText(requireContext(), "Error al cargar el chat", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void configurarBotones(View view) {
        // Configurar botón de retroceso
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        // Configurar botón de agregar chat
        View btnAddChat = view.findViewById(R.id.btnAddChat);
        if (btnAddChat != null) {
            btnAddChat.setOnClickListener(v -> {
                mostrarDialogoNuevoChat();
            });
        }
    }

    private void mostrarDialogoNuevoChat() {
        // Aquí se mostraría un diálogo para iniciar un nuevo chat
        Toast.makeText(requireContext(), "Función de nuevo chat próximamente", Toast.LENGTH_SHORT).show();
    }

    private void cargarChats() {
        try {
            List<Mensaje> mensajes = dataManager.getMensajes();
            // Aquí se cargarían los chats en el layout
            // Por ahora, los chats están hardcodeados en el layout XML
            
            Log.d("ChatFragment", "Cargando " + mensajes.size() + " mensajes");
        } catch (Exception e) {
            Log.e("ChatFragment", "Error al cargar chats", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarChats();
    }
} 