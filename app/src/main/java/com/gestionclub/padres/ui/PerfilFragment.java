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
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Asistencia;
import java.util.List;

public class PerfilFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewNombre;
    private TextView textViewRol;
    private TextView textViewEquipo;
    private TextView textViewEmail;
    private TextView textViewTelefono;
    private TextView textViewEventosAsistidos;
    private TextView textViewPorcentajeAsistencia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarBotones(view);
            cargarPerfil();
        } catch (Exception e) {
            Log.e("PerfilFragment", "Error al cargar perfil", e);
            Toast.makeText(requireContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        textViewNombre = view.findViewById(R.id.textViewNombre);
        textViewRol = view.findViewById(R.id.textViewRol);
        textViewEquipo = view.findViewById(R.id.textViewEquipo);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewTelefono = view.findViewById(R.id.textViewTelefono);
        textViewEventosAsistidos = view.findViewById(R.id.textViewEventosAsistidos);
        textViewPorcentajeAsistencia = view.findViewById(R.id.textViewPorcentajeAsistencia);
    }

    private void configurarBotones(View view) {


        // Configurar botón de editar perfil
        View btnEditProfile = view.findViewById(R.id.btnEditProfile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                mostrarDialogoEditarPerfil();
            });
        }

        // Configurar botón de cambiar contraseña
        View btnCambiarPassword = view.findViewById(R.id.btnCambiarPassword);
        if (btnCambiarPassword != null) {
            btnCambiarPassword.setOnClickListener(v -> {
                mostrarDialogoCambiarPassword();
            });
        }

        // Configurar botón de cerrar sesión
        View btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                cerrarSesion();
            });
        }
    }

    private void mostrarDialogoEditarPerfil() {
        // Aquí se mostraría un diálogo para editar el perfil
        Toast.makeText(requireContext(), "Función de editar perfil próximamente", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoCambiarPassword() {
        // Aquí se mostraría un diálogo para cambiar la contraseña
        Toast.makeText(requireContext(), "Función de cambiar contraseña próximamente", Toast.LENGTH_SHORT).show();
    }

    private void cerrarSesion() {
        try {
            dataManager.cerrarSesion();
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            // Aquí se navegaría de vuelta al login
        } catch (Exception e) {
            Log.e("PerfilFragment", "Error al cerrar sesión", e);
            Toast.makeText(requireContext(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPerfil() {
        try {
            if (usuarioActual == null) {
                return;
            }

            // Cargar información básica del usuario
            if (textViewNombre != null) {
                textViewNombre.setText(usuarioActual.getNombre());
            }

            if (textViewRol != null) {
                String rol = usuarioActual.isEsAdmin() ? "Administrador" : "Padre/Tutor";
                textViewRol.setText(rol);
            }

            if (textViewEmail != null) {
                textViewEmail.setText(usuarioActual.getEmail() != null ? usuarioActual.getEmail() : "No especificado");
            }

            // Cargar información del equipo
            if (textViewEquipo != null) {
                String equipoId = usuarioActual.getEquipoId();
                if (equipoId != null && !equipoId.isEmpty()) {
                    Equipo equipo = dataManager.getEquipoPorId(equipoId);
                    if (equipo != null) {
                        textViewEquipo.setText(equipo.getNombre());
                    } else {
                        textViewEquipo.setText("Sin equipo asignado");
                    }
                } else {
                    textViewEquipo.setText("Sin equipo asignado");
                }
            }

            // Cargar estadísticas
            cargarEstadisticas();

        } catch (Exception e) {
            Log.e("PerfilFragment", "Error al cargar perfil", e);
        }
    }

    private void cargarEstadisticas() {
        try {
            if (usuarioActual == null) {
                return;
            }

            // Calcular eventos asistidos
            List<Asistencia> asistencias = dataManager.getAsistenciasPorJugador(usuarioActual.getId());
            int eventosAsistidos = 0;
            for (Asistencia asistencia : asistencias) {
                if (asistencia.isAsistio()) {
                    eventosAsistidos++;
                }
            }

            if (textViewEventosAsistidos != null) {
                textViewEventosAsistidos.setText(String.valueOf(eventosAsistidos));
            }

            // Calcular porcentaje de asistencia
            double porcentaje = dataManager.getPorcentajeAsistencia(usuarioActual.getId());
            if (textViewPorcentajeAsistencia != null) {
                textViewPorcentajeAsistencia.setText(String.format("%.0f%%", porcentaje));
            }

        } catch (Exception e) {
            Log.e("PerfilFragment", "Error al cargar estadísticas", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarPerfil();
    }
} 