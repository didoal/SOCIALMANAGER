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

public class CalendarioAnoFragment extends Fragment {
    private static final String TAG = "CalendarioAnoFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_calendario_ano, container, false);
            
            TextView textView = view.findViewById(R.id.textViewAno);
            if (textView != null) {
                textView.setText("Vista de Año");
            } else {
                Log.e(TAG, "Error: textViewAno no encontrado en el layout");
            }
            
            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error al crear vista de CalendarioAnoFragment", e);
            // Retornar una vista simple en caso de error
            TextView errorView = new TextView(requireContext());
            errorView.setText("Error al cargar vista de año");
            return errorView;
        }
    }
} 