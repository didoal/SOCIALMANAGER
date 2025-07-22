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
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class EquipoFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewEquipoNombre;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipo, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarBotones(view);
            cargarEquipo();
        } catch (Exception e) {
            Log.e("EquipoFragment", "Error al cargar equipo", e);
            Toast.makeText(requireContext(), "Error al cargar el equipo", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        textViewEquipoNombre = view.findViewById(R.id.textViewEquipoNombre);
    }

    private void configurarBotones(View view) {


        // Configurar botón de agregar miembro (solo para admins)
        View btnAddMember = view.findViewById(R.id.btnAddMember);
        if (btnAddMember != null) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                btnAddMember.setVisibility(View.VISIBLE);
                btnAddMember.setOnClickListener(v -> {
                    mostrarDialogoAgregarMiembro();
                });
            } else {
                btnAddMember.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarDialogoAgregarMiembro() {
        // Aquí se mostraría un diálogo para agregar un nuevo miembro al equipo
        Toast.makeText(requireContext(), "Función de agregar miembro próximamente", Toast.LENGTH_SHORT).show();
    }

    private void cargarEquipo() {
        try {
            if (usuarioActual == null) {
                return;
            }

            // Determinar qué equipo mostrar basado en el rol del usuario
            if (usuarioActual.isEsAdmin()) {
                // Administrador ve todos los equipos
                mostrarSelectorEquipos();
            } else {
                // Usuario normal ve solo su equipo asignado
                mostrarEquipoUsuario();
            }
            
        } catch (Exception e) {
            Log.e("EquipoFragment", "Error al cargar equipo", e);
        }
    }

    private void mostrarSelectorEquipos() {
        try {
            List<Equipo> equipos = dataManager.getEquipos();
            if (!equipos.isEmpty()) {
                // Por ahora mostrar el primer equipo, pero aquí se podría implementar un selector
                Equipo equipoSeleccionado = equipos.get(0);
                if (textViewEquipoNombre != null) {
                    textViewEquipoNombre.setText(equipoSeleccionado.getNombre());
                }
                
                // Cargar miembros del equipo seleccionado
                cargarMiembrosEquipo(equipoSeleccionado.getId());
            }
        } catch (Exception e) {
            Log.e("EquipoFragment", "Error al mostrar selector de equipos", e);
        }
    }

    private void mostrarEquipoUsuario() {
        try {
            String equipoId = usuarioActual.getEquipoId();
            if (equipoId != null && !equipoId.isEmpty()) {
                Equipo equipo = dataManager.getEquipoPorId(equipoId);
                if (equipo != null && textViewEquipoNombre != null) {
                    textViewEquipoNombre.setText(equipo.getNombre());
                }
                
                // Cargar miembros del equipo del usuario
                cargarMiembrosEquipo(equipoId);
            } else {
                // Usuario sin equipo asignado
                if (textViewEquipoNombre != null) {
                    textViewEquipoNombre.setText("Sin equipo asignado");
                }
            }
        } catch (Exception e) {
            Log.e("EquipoFragment", "Error al mostrar equipo del usuario", e);
        }
    }

    private void cargarMiembrosEquipo(String equipoId) {
        try {
            List<Usuario> miembros = dataManager.getJugadoresPorEquipo(equipoId);
            // Aquí se cargarían los miembros en el layout
            // Por ahora, los miembros están hardcodeados en el layout XML
            
            Log.d("EquipoFragment", "Cargando " + miembros.size() + " miembros para el equipo " + equipoId);
        } catch (Exception e) {
            Log.e("EquipoFragment", "Error al cargar miembros del equipo", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEquipo();
    }
} 