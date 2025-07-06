package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Notificacion;
import com.gestionclub.padres.model.Usuario;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {
    private DataManager dataManager;
    private Usuario usuarioActual;
    
    // Vistas
    private TextView textViewBienvenida;
    private TextView textViewEventosProximos;
    private TextView textViewNotificaciones;
    private TextView textViewUsuarioInfo;
    private LinearLayout layoutAdminAccesos;
    
    // Tarjetas de acceso rápido
    private View cardCalendario;
    private View cardAsistencia;
    private View cardMensajes;
    private View cardObjetosPerdidos;
    private View cardEstadisticas;
    private View cardGestion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            
            inicializarVistas(view);
            configurarAccesosRapidos();
            cargarDatos();
            configurarVisibilidadAdmin();
            
        } catch (Exception e) {
            // Manejar errores
        }
        
        return view;
    }

    private void inicializarVistas(View view) {
        textViewBienvenida = view.findViewById(R.id.textViewBienvenida);
        textViewEventosProximos = view.findViewById(R.id.textViewEventosProximos);
        textViewNotificaciones = view.findViewById(R.id.textViewNotificaciones);
        textViewUsuarioInfo = view.findViewById(R.id.textViewUsuarioInfo);
        layoutAdminAccesos = view.findViewById(R.id.layoutAdminAccesos);
        
        // Tarjetas de acceso
        cardCalendario = view.findViewById(R.id.cardCalendario);
        cardAsistencia = view.findViewById(R.id.cardAsistencia);
        cardMensajes = view.findViewById(R.id.cardMensajes);
        cardObjetosPerdidos = view.findViewById(R.id.cardObjetosPerdidos);
        cardEstadisticas = view.findViewById(R.id.cardEstadisticas);
        cardGestion = view.findViewById(R.id.cardGestion);
    }

    private void configurarAccesosRapidos() {
        // Configurar listeners para las tarjetas de acceso rápido
        cardCalendario.setOnClickListener(v -> navegarAFragmento(new CalendarioFragment()));
        cardAsistencia.setOnClickListener(v -> navegarAFragmento(new AsistenciaFragment()));
        cardMensajes.setOnClickListener(v -> navegarAFragmento(new MensajesFragment()));
        cardObjetosPerdidos.setOnClickListener(v -> navegarAFragmento(new ObjetosPerdidosFragment()));
        cardEstadisticas.setOnClickListener(v -> navegarAFragmento(new EstadisticasFragment()));
        cardGestion.setOnClickListener(v -> navegarAFragmento(new GestionUsuariosFragment()));
    }

    private void navegarAFragmento(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).mostrarFragmento(fragment);
        }
    }

    private void cargarDatos() {
        if (usuarioActual != null) {
            // Configurar bienvenida personalizada
            String bienvenida = "Bienvenido, " + usuarioActual.getNombre();
            textViewBienvenida.setText(bienvenida);
            
            // Cargar eventos próximos
            cargarEventosProximos();
            
            // Cargar notificaciones no leídas
            cargarNotificacionesNoLeidas();
            
            // Cargar información del usuario
            cargarInformacionUsuario();
        }
    }

    private void cargarEventosProximos() {
        try {
            List<Evento> todosEventos = dataManager.getEventos();
            Calendar cal = Calendar.getInstance();
            Date ahora = cal.getTime();
            
            // Calcular fecha límite (próximos 7 días)
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date fechaLimite = cal.getTime();
            
            int eventosProximos = 0;
            String equipoUsuario = usuarioActual.getEquipo();
            
            for (Evento evento : todosEventos) {
                // Filtrar por equipo si no es admin
                if (usuarioActual != null && !usuarioActual.isEsAdmin() && equipoUsuario != null) {
                    if (evento.getEquipo() != null && !evento.getEquipo().equals(equipoUsuario)) {
                        continue;
                    }
                }
                
                // Verificar si el evento está en los próximos 7 días
                if (evento.getFechaInicio().after(ahora) && evento.getFechaInicio().before(fechaLimite)) {
                    eventosProximos++;
                }
            }
            
            textViewEventosProximos.setText(String.valueOf(eventosProximos));
            
        } catch (Exception e) {
            textViewEventosProximos.setText("0");
        }
    }

    private void cargarNotificacionesNoLeidas() {
        try {
            int notificacionesNoLeidas = dataManager.getNotificacionesNoLeidas();
            textViewNotificaciones.setText(String.valueOf(notificacionesNoLeidas));
        } catch (Exception e) {
            textViewNotificaciones.setText("0");
        }
    }

    private void cargarInformacionUsuario() {
        if (usuarioActual != null) {
            StringBuilder info = new StringBuilder();
            info.append("• Nombre: ").append(usuarioActual.getNombre()).append("\n");
            info.append("• Rol: ").append(usuarioActual.isEsAdmin() ? "Administrador" : "Usuario").append("\n");
            
            if (usuarioActual.getJugador() != null && !usuarioActual.getJugador().isEmpty()) {
                info.append("• Jugador: ").append(usuarioActual.getJugador()).append("\n");
            }
            
            if (usuarioActual.getEquipo() != null && !usuarioActual.getEquipo().isEmpty()) {
                info.append("• Equipo/Categoría: ").append(usuarioActual.getEquipo()).append("\n");
            }
            
            info.append("• Fecha de registro: ").append(usuarioActual.getFechaRegistro().toString().substring(0, 10));
            
            textViewUsuarioInfo.setText(info.toString());
        }
    }

    private void configurarVisibilidadAdmin() {
        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            layoutAdminAccesos.setVisibility(View.VISIBLE);
        } else {
            layoutAdminAccesos.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Actualizar datos cuando se vuelve al dashboard
        cargarDatos();
    }
} 