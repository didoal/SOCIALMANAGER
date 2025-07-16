package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;

public class CalendarioMesFragment extends Fragment {
    private static final String TAG = "CalendarioMesFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_calendario_mes, container, false);
            
            TextView textView = view.findViewById(R.id.textViewMes);
            if (textView != null) {
                textView.setText("Vista de Mes");
            } else {
                Log.e(TAG, "Error: textViewMes no encontrado en el layout");
            }
            
            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error al crear vista de CalendarioMesFragment", e);
            // Retornar una vista simple en caso de error
            TextView errorView = new TextView(requireContext());
            errorView.setText("Error al cargar vista de mes");
            return errorView;
        }
    }
} 