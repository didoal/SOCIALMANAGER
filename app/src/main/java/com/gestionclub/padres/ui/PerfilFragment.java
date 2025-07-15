package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.adapter.UsuarioAdapter;

import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {
    private DataManager dataManager;
    private Usuario usuarioActual;
    
    // Vistas del perfil
    private TextView textViewNombre;
    private TextView textViewEmail;
    private TextView textViewRol;
    private TextView textViewEquipo;
    private TextView textViewJugador;
    private TextView textViewFechaRegistro;
    
    // Botones de acción
    private Button buttonEditarPerfil;
    private Button buttonCambiarPassword;
    private Button buttonConfiguracion;
    private Button buttonCerrarSesion;
    private RecyclerView recyclerViewJugadoresEquipo;
    private UsuarioAdapter jugadorAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            
            inicializarVistas(view);
            cargarInformacionPerfil();
            configurarListeners();
            
        } catch (Exception e) {
            Log.e("Perfil", "Error al cargar perfil", e);
            mostrarErrorCarga(view);
        }
        
        return view;
    }
    
    private void mostrarErrorCarga(View view) {
        // Ocultar vistas principales
        if (textViewNombre != null) textViewNombre.setVisibility(View.GONE);
        if (textViewEmail != null) textViewEmail.setVisibility(View.GONE);
        if (textViewRol != null) textViewRol.setVisibility(View.GONE);
        if (textViewEquipo != null) textViewEquipo.setVisibility(View.GONE);
        if (textViewJugador != null) textViewJugador.setVisibility(View.GONE);
        if (textViewFechaRegistro != null) textViewFechaRegistro.setVisibility(View.GONE);
        
        // Mostrar solo el Toast de error
        Toast.makeText(requireContext(), R.string.error_cargar_perfil, Toast.LENGTH_LONG).show();
    }

    private void inicializarVistas(View view) {
        // Vistas de información
        textViewNombre = view.findViewById(R.id.textViewNombre);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewRol = view.findViewById(R.id.textViewRol);
        textViewEquipo = view.findViewById(R.id.textViewEquipo);
        textViewJugador = view.findViewById(R.id.textViewJugador);
        textViewFechaRegistro = view.findViewById(R.id.textViewFechaRegistro);
        
        // Botones
        buttonEditarPerfil = view.findViewById(R.id.buttonEditarPerfil);
        buttonCambiarPassword = view.findViewById(R.id.buttonCambiarPassword);
        buttonConfiguracion = view.findViewById(R.id.buttonConfiguracion);
        buttonCerrarSesion = view.findViewById(R.id.buttonCerrarSesion);
        // RecyclerView para jugadores del equipo
        recyclerViewJugadoresEquipo = view.findViewById(R.id.recyclerViewJugadoresEquipo);
        recyclerViewJugadoresEquipo.setLayoutManager(new LinearLayoutManager(requireContext()));
        jugadorAdapter = new UsuarioAdapter(new ArrayList<>(), null, null);
        recyclerViewJugadoresEquipo.setAdapter(jugadorAdapter);
    }

    private void cargarInformacionPerfil() {
        if (usuarioActual != null) {
            textViewNombre.setText(usuarioActual.getNombre());
            textViewEmail.setText(usuarioActual.getEmail());
            textViewRol.setText(usuarioActual.isEsAdmin() ? "Administrador" : "Usuario");
            String equipo = usuarioActual.getEquipo();
            if (equipo != null && !equipo.isEmpty()) {
                textViewEquipo.setText(equipo);
                // Mostrar jugadores del equipo
                if (usuarioActual.getEquipoId() != null) {
                    List<Usuario> jugadores = dataManager.getJugadoresPorEquipo(usuarioActual.getEquipoId());
                    jugadorAdapter.actualizarUsuarios(jugadores);
                }
            } else {
                textViewEquipo.setText("No asignado");
                jugadorAdapter.actualizarUsuarios(new ArrayList<>());
            }
            String jugador = usuarioActual.getJugador();
            if (jugador != null && !jugador.isEmpty()) {
                textViewJugador.setText(jugador);
            } else {
                textViewJugador.setText("No asignado");
            }
            // Formatear fecha de registro
            String fechaRegistro = usuarioActual.getFechaRegistro().toString().substring(0, 10);
            textViewFechaRegistro.setText(fechaRegistro);
        }
    }

    private void configurarListeners() {
        buttonEditarPerfil.setOnClickListener(v -> mostrarDialogoEditarPerfil());
        buttonCambiarPassword.setOnClickListener(v -> mostrarDialogoCambiarPassword());
        buttonConfiguracion.setOnClickListener(v -> mostrarDialogoConfiguracion());
        buttonCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
    }

    private void mostrarDialogoEditarPerfil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_perfil, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextJugador = dialogView.findViewById(R.id.editTextJugador);
        
        // Pre-llenar con datos actuales
        editTextNombre.setText(usuarioActual.getNombre());
        editTextEmail.setText(usuarioActual.getEmail());
        editTextJugador.setText(usuarioActual.getJugador());
        
        builder.setView(dialogView)
                .setTitle("Editar Perfil")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = editTextNombre.getText().toString().trim();
                    String nuevoEmail = editTextEmail.getText().toString().trim();
                    String nuevoJugador = editTextJugador.getText().toString().trim();
                    
                    if (nuevoNombre.isEmpty()) {
                        Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Actualizar usuario
                    usuarioActual.setNombre(nuevoNombre);
                    usuarioActual.setEmail(nuevoEmail);
                    usuarioActual.setJugador(nuevoJugador);
                    
                    dataManager.actualizarUsuario(usuarioActual);
                    dataManager.guardarUsuarioActual(usuarioActual);
                    
                    cargarInformacionPerfil();
                    Toast.makeText(requireContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoCambiarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cambiar_password, null);
        
        EditText editTextPasswordActual = dialogView.findViewById(R.id.editTextPasswordActual);
        EditText editTextPasswordNueva = dialogView.findViewById(R.id.editTextPasswordNueva);
        EditText editTextPasswordConfirmar = dialogView.findViewById(R.id.editTextPasswordConfirmar);
        
        builder.setView(dialogView)
                .setTitle("Cambiar Contraseña")
                .setPositiveButton("Cambiar", (dialog, which) -> {
                    String passwordActual = editTextPasswordActual.getText().toString();
                    String passwordNueva = editTextPasswordNueva.getText().toString();
                    String passwordConfirmar = editTextPasswordConfirmar.getText().toString();
                    
                    // Validaciones
                    if (passwordActual.isEmpty() || passwordNueva.isEmpty() || passwordConfirmar.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (!passwordActual.equals(usuarioActual.getPassword())) {
                        Toast.makeText(requireContext(), "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (!passwordNueva.equals(passwordConfirmar)) {
                        Toast.makeText(requireContext(), "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (passwordNueva.length() < 6) {
                        Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Actualizar contraseña
                    usuarioActual.setPassword(passwordNueva);
                    dataManager.actualizarUsuario(usuarioActual);
                    dataManager.guardarUsuarioActual(usuarioActual);
                    
                    Toast.makeText(requireContext(), "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoConfiguracion() {
        String[] opciones = {"Notificaciones", "Privacidad", "Tema", "Idioma"};
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Configuración")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(requireContext(), "Configuración de notificaciones", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(requireContext(), "Configuración de privacidad", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(requireContext(), "Configuración de tema", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(requireContext(), "Configuración de idioma", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    dataManager.cerrarSesion();
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarInformacionPerfil();
    }
} 