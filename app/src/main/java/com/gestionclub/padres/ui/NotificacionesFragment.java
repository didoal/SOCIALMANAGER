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
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class NotificacionesFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView tabNuevas;
    private TextView tabHistorico;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarBotones(view);
            configurarTabs(view);
            cargarNotificaciones();
        } catch (Exception e) {
            Log.e("NotificacionesFragment", "Error al cargar notificaciones", e);
            Toast.makeText(requireContext(), "Error al cargar las notificaciones", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        tabNuevas = view.findViewById(R.id.tabNuevas);
        tabHistorico = view.findViewById(R.id.tabHistorico);
    }

    private void configurarBotones(View view) {
        // Configurar botón de marcar todo como leído
        View btnMarcarTodoLeido = view.findViewById(R.id.btnMarcarTodoLeido);
        if (btnMarcarTodoLeido != null) {
            btnMarcarTodoLeido.setOnClickListener(v -> {
                marcarTodoComoLeido();
            });
        }

        // Configurar botón de borrar leídas
        View btnBorrarLeidas = view.findViewById(R.id.btnBorrarLeidas);
        if (btnBorrarLeidas != null) {
            btnBorrarLeidas.setOnClickListener(v -> {
                confirmarBorrarLeidas();
            });
        }
    }

    private void configurarTabs(View view) {
        if (tabNuevas != null) {
            tabNuevas.setOnClickListener(v -> {
                seleccionarTabNuevas();
            });
        }

        if (tabHistorico != null) {
            tabHistorico.setOnClickListener(v -> {
                seleccionarTabHistorico();
            });
        }
    }

    private void seleccionarTabNuevas() {
        if (tabNuevas != null) {
            tabNuevas.setBackgroundResource(R.drawable.tab_selected_background);
        }
        if (tabHistorico != null) {
            tabHistorico.setBackgroundResource(android.R.color.transparent);
        }
        
        // Cargar notificaciones nuevas
        cargarNotificacionesNuevas();
    }

    private void seleccionarTabHistorico() {
        if (tabNuevas != null) {
            tabNuevas.setBackgroundResource(android.R.color.transparent);
        }
        if (tabHistorico != null) {
            tabHistorico.setBackgroundResource(R.drawable.tab_selected_background);
        }
        
        // Cargar historial de notificaciones
        cargarHistorialNotificaciones();
    }

    private void marcarTodoComoLeido() {
        try {
            List<Notificacion> notificaciones = dataManager.getNotificaciones();
            for (Notificacion notificacion : notificaciones) {
                if (!notificacion.isLeida()) {
                    dataManager.marcarNotificacionComoLeida(notificacion.getId());
                }
            }
            
            Toast.makeText(requireContext(), "Todas las notificaciones marcadas como leídas", Toast.LENGTH_SHORT).show();
            cargarNotificaciones();
        } catch (Exception e) {
            Log.e("NotificacionesFragment", "Error al marcar notificaciones como leídas", e);
            Toast.makeText(requireContext(), "Error al marcar notificaciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarBorrarLeidas() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmar borrado")
               .setMessage("¿Estás seguro de que quieres borrar todas las notificaciones leídas?")
               .setPositiveButton("Borrar", (dialog, which) -> {
                   borrarNotificacionesLeidas();
               })
               .setNegativeButton("Cancelar", null)
               .show();
    }

    private void borrarNotificacionesLeidas() {
        try {
            List<Notificacion> notificaciones = dataManager.getNotificaciones();
            int contadorBorradas = 0;
            
            for (Notificacion notificacion : notificaciones) {
                if (notificacion.isLeida()) {
                    dataManager.eliminarNotificacion(notificacion.getId());
                    contadorBorradas++;
                }
            }
            
            if (contadorBorradas > 0) {
                Toast.makeText(requireContext(), contadorBorradas + " notificaciones leídas borradas", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No hay notificaciones leídas para borrar", Toast.LENGTH_SHORT).show();
            }
            
            cargarNotificaciones();
        } catch (Exception e) {
            Log.e("NotificacionesFragment", "Error al borrar notificaciones leídas", e);
            Toast.makeText(requireContext(), "Error al borrar notificaciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarNotificaciones() {
        try {
            // Por defecto mostrar notificaciones nuevas
            seleccionarTabNuevas();
        } catch (Exception e) {
            Log.e("NotificacionesFragment", "Error al cargar notificaciones", e);
        }
    }

    private void cargarNotificacionesNuevas() {
        try {
            List<Notificacion> notificaciones = dataManager.getNotificaciones();
            // Aquí se cargarían las notificaciones nuevas en el layout
            // Por ahora, las notificaciones están hardcodeadas en el layout XML
            
            Log.d("NotificacionesFragment", "Cargando " + notificaciones.size() + " notificaciones nuevas");
        } catch (Exception e) {
            Log.e("NotificacionesFragment", "Error al cargar notificaciones nuevas", e);
        }
    }

    private void cargarHistorialNotificaciones() {
        try {
            List<Notificacion> notificaciones = dataManager.getNotificaciones();
            // Aquí se cargaría el historial de notificaciones
            // Por ahora, las notificaciones están hardcodeadas en el layout XML
            
            Log.d("NotificacionesFragment", "Cargando historial de " + notificaciones.size() + " notificaciones");
        } catch (Exception e) {
            Log.e("NotificacionesFragment", "Error al cargar historial de notificaciones", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarNotificaciones();
    }
} 