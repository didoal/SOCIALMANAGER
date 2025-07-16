package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.EventoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

public class CalendarioListadoFragment extends Fragment {
    
    private static final String TAG = "CalendarioListado"; // Tag corto para logging
    
    private RecyclerView recyclerViewEventos;
    private EventoAdapter eventoAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private View textViewNoEventos; // Cambiar a View ya que es un CardView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_calendario_listado, container, false);
            
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            
            inicializarVistas(view);
            configurarRecyclerView();
            cargarEventos();
            
            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error crítico al crear vista", e);
            // Retornar una vista simple en caso de error crítico
            TextView errorView = new TextView(requireContext());
            errorView.setText(R.string.error_cargar_eventos);
            return errorView;
        }
    }
    
    private void inicializarVistas(View view) {
        try {
            recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
            textViewNoEventos = view.findViewById(R.id.textViewNoEventos); // Es un CardView, no TextView
            
            // Verificar que las vistas principales existen
            if (recyclerViewEventos == null) {
                Log.e(TAG, "Error: recyclerViewEventos no encontrado en el layout");
                return;
            }
            if (textViewNoEventos == null) {
                Log.e(TAG, "Error: textViewNoEventos no encontrado en el layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas", e);
        }
    }
    
    private void configurarRecyclerView() {
        Context context = getContext();
        if (context != null && recyclerViewEventos != null) {
            recyclerViewEventos.setLayoutManager(new LinearLayoutManager(context));
            eventoAdapter = new EventoAdapter(new ArrayList<>(), new EventoAdapter.OnEventoClickListener() {
                @Override
                public void onEditarClick(Evento evento) {
                    // Solo permitir editar si es admin o entrenador
                    if (usuarioActual != null && (usuarioActual.isEsAdmin() || "entrenador".equals(usuarioActual.getRol()))) {
                        // Aquí podrías abrir el diálogo de edición
                        Toast.makeText(requireContext(), "Clic en evento: " + evento.getTitulo(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onEliminarClick(Evento evento) {
                    // Solo permitir eliminar si es admin
                    if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                        // Aquí podrías mostrar el diálogo de confirmación
                        Toast.makeText(requireContext(), "Eliminar evento: " + evento.getTitulo(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, false); // false para no mostrar botones de edición/eliminación por defecto
            recyclerViewEventos.setAdapter(eventoAdapter);
        } else {
            Log.e(TAG, "Error: Context o RecyclerView es null");
        }
    }
    
    private void cargarEventos() {
        try {
            if (dataManager == null) {
                Log.e(TAG, "Error: DataManager es null");
                return;
            }
            
            List<Evento> eventos = dataManager.getEventos();
            if (eventos == null) {
                Log.w(TAG, "Advertencia: getEventos() devolvió null, usando lista vacía");
                eventos = new ArrayList<>();
            }
            
            // Ordenar eventos por fecha (más recientes primero)
            try {
                Collections.sort(eventos, (e1, e2) -> {
                    if (e1.getFechaInicio() == null || e2.getFechaInicio() == null) {
                        return 0; // Si alguna fecha es null, no cambiar el orden
                    }
                    return e2.getFechaInicio().compareTo(e1.getFechaInicio());
                });
            } catch (Exception e) {
                Log.e(TAG, "Error al ordenar eventos", e);
            }
            
            if (eventoAdapter != null) {
                eventoAdapter.actualizarEventos(eventos);
            } else {
                Log.w(TAG, "Advertencia: eventoAdapter es null");
            }
            
            // Mostrar mensaje si no hay eventos
            if (textViewNoEventos != null) {
                if (eventos.isEmpty()) {
                    textViewNoEventos.setVisibility(View.VISIBLE);
                    if (recyclerViewEventos != null) {
                        recyclerViewEventos.setVisibility(View.GONE);
                    }
                } else {
                    textViewNoEventos.setVisibility(View.GONE);
                    if (recyclerViewEventos != null) {
                        recyclerViewEventos.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error crítico al cargar eventos", e);
            // En caso de error, mostrar mensaje de error
            if (textViewNoEventos != null) {
                textViewNoEventos.setVisibility(View.VISIBLE);
            }
            if (recyclerViewEventos != null) {
                recyclerViewEventos.setVisibility(View.GONE);
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        cargarEventos(); // Recargar eventos cuando se vuelve a la pantalla
    }
} 