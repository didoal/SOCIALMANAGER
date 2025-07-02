package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.util.SessionManager;

public class PerfilFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        
        SessionManager sessionManager = new SessionManager(requireContext());
        TextView tvNombre = view.findViewById(R.id.tv_nombre);
        TextView tvRol = view.findViewById(R.id.tv_rol);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        
        if (sessionManager.getCurrentUser() != null) {
            tvNombre.setText(sessionManager.getCurrentUser().getNombreReal());
            tvRol.setText("Rol: " + sessionManager.getCurrentUser().getRol());
        }
        
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            requireActivity().finish();
        });
        
        return view;
    }
} 