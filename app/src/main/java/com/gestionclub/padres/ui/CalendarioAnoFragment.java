package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;

public class CalendarioAnoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_ano, container, false);
        
        TextView textView = view.findViewById(R.id.textViewAno);
        textView.setText("Vista de Año");
        
        return view;
    }
} 