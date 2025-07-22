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
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class ResultadosPartidosFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewVictorias;
    private TextView textViewEmpates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resultados_partidos, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarBotones(view);
            cargarResultados();
            actualizarEstadisticas();
        } catch (Exception e) {
            Log.e("ResultadosPartidos", "Error al cargar resultados", e);
            Toast.makeText(requireContext(), "Error al cargar los resultados", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        textViewVictorias = view.findViewById(R.id.textViewVictorias);
        textViewEmpates = view.findViewById(R.id.textViewEmpates);
    }

    private void configurarBotones(View view) {


        // Configurar botón de agregar resultado (solo para admins)
        View btnAddResult = view.findViewById(R.id.btnAddResult);
        if (btnAddResult != null) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                btnAddResult.setVisibility(View.VISIBLE);
                btnAddResult.setOnClickListener(v -> {
                    mostrarDialogoAgregarResultado();
                });
            } else {
                btnAddResult.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarDialogoAgregarResultado() {
        // Aquí se mostraría un diálogo para agregar un nuevo resultado
        Toast.makeText(requireContext(), "Función de agregar resultado próximamente", Toast.LENGTH_SHORT).show();
    }

    private void cargarResultados() {
        try {
            // Obtener todos los eventos y filtrar los de tipo partido
            List<Evento> todosLosEventos = dataManager.getEventos();
            List<Evento> eventosPartido = new ArrayList<>();
            
            for (Evento evento : todosLosEventos) {
                if ("PARTIDO".equals(evento.getTipo())) {
                    eventosPartido.add(evento);
                }
            }
            
            // Aquí se procesarían los eventos de partido para mostrar resultados
            // Por ahora, los resultados están hardcodeados en el layout XML
        } catch (Exception e) {
            Log.e("ResultadosPartidos", "Error al cargar resultados", e);
        }
    }

    private void actualizarEstadisticas() {
        try {
            // Calcular estadísticas basadas en los resultados
            int victorias = 12; // Esto vendría de la base de datos
            int empates = 5;    // Esto vendría de la base de datos
            
            if (textViewVictorias != null) {
                textViewVictorias.setText(String.valueOf(victorias));
            }
            
            if (textViewEmpates != null) {
                textViewEmpates.setText(String.valueOf(empates));
            }
            
        } catch (Exception e) {
            Log.e("ResultadosPartidos", "Error al actualizar estadísticas", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarResultados();
        actualizarEstadisticas();
    }
} 