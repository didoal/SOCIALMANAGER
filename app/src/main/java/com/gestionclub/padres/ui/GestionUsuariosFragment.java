package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.UsuarioAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosFragment extends Fragment {
    private static final String TAG = "GestionUsuariosFragment";
    
    private RecyclerView recyclerViewUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private DataManager dataManager;
    private TextView textViewEstadisticas;
    private FloatingActionButton fabAgregarUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creando vista de gestión de usuarios");
        View view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false);
        
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        configurarRecyclerView();
        cargarUsuarios();
        actualizarEstadisticas();
        
        return view;
    }

    private void inicializarVistas(View view) {
        Log.d(TAG, "inicializarVistas: Inicializando vistas");
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        fabAgregarUsuario = view.findViewById(R.id.fabAgregarUsuario);
        
        fabAgregarUsuario.setOnClickListener(v -> mostrarDialogoCrearUsuario());
    }

    private void configurarRecyclerView() {
        Log.d(TAG, "configurarRecyclerView: Configurando RecyclerView");
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        usuarioAdapter = new UsuarioAdapter(new ArrayList<>(), 
            this::mostrarDialogoEliminarUsuario,
            this::mostrarDialogoEditarUsuario);
        recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void cargarUsuarios() {
        Log.d(TAG, "cargarUsuarios: Cargando lista de usuarios");
        List<Usuario> usuarios = dataManager.getUsuarios();
        usuarioAdapter.actualizarUsuarios(usuarios);
        Log.d(TAG, "cargarUsuarios: " + usuarios.size() + " usuarios cargados");
    }

    private void actualizarEstadisticas() {
        Log.d(TAG, "actualizarEstadisticas: Actualizando estadísticas");
        List<Usuario> usuarios = dataManager.getUsuarios();
        int totalUsuarios = usuarios.size();
        int admins = 0, padres = 0, madres = 0, tutores = 0, jugadores = 0;
        
        for (Usuario usuario : usuarios) {
            if (usuario.isEsAdmin()) admins++;
            else if ("padre".equalsIgnoreCase(usuario.getRol())) padres++;
            else if ("madre".equalsIgnoreCase(usuario.getRol())) madres++;
            else if ("tutor".equalsIgnoreCase(usuario.getRol())) tutores++;
            else if ("jugador".equalsIgnoreCase(usuario.getRol())) jugadores++;
        }
        
        String estadisticas = String.format("Total: %d | Admins: %d | Padres: %d | Madres: %d | Tutores: %d | Jugadores: %d",
                totalUsuarios, admins, padres, madres, tutores, jugadores);
        textViewEstadisticas.setText(estadisticas);
    }

    private void mostrarDialogoCrearUsuario() {
        Log.d(TAG, "mostrarDialogoCrearUsuario: Mostrando diálogo");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_usuario, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = dialogView.findViewById(R.id.editTextConfirmPassword);
        EditText editTextJugador = dialogView.findViewById(R.id.editTextJugador);
        Spinner spinnerRol = dialogView.findViewById(R.id.spinnerRol);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Configurar spinner de roles (sin opción administrador para usuarios normales)
        String[] roles = {"Padre", "Madre", "Tutor", "Jugador"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);
        
        // Configurar spinner de equipos
        String[] equipos = {"Biberones", "Prebenjamín A", "Prebenjamín B", "Benjamín A", "Benjamín B", 
                           "Alevín A", "Alevín B", "Infantil", "Cadete", "Juvenil", "Todo el Club"};
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        builder.setView(dialogView)
                .setTitle("Crear Nuevo Usuario")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                    String jugador = editTextJugador.getText().toString().trim();
                    String rol = spinnerRol.getSelectedItem().toString().toLowerCase();
                    String equipo = spinnerEquipo.getSelectedItem().toString();
                    
                    if (validarDatosCreacion(nombre, email, password, confirmPassword, rol)) {
                        crearUsuario(nombre, email, password, jugador, rol, equipo);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatosCreacion(String nombre, String email, String password, String confirmPassword, String rol) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "El email es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "El formato del email no es válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "La contraseña es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void crearUsuario(String nombre, String email, String password, String jugador, String rol, String equipo) {
        Log.d(TAG, "crearUsuario: Creando usuario " + nombre);
        
        // Verificar si el email ya existe
        if (dataManager.existeUsuario(email)) {
            Toast.makeText(requireContext(), "Ya existe un usuario con ese email", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Usuario nuevoUsuario = new Usuario(nombre, jugador, password, rol);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setEquipo(equipo);
        
        dataManager.agregarUsuario(nuevoUsuario);
        cargarUsuarios();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "crearUsuario: Usuario " + nombre + " creado correctamente");
    }

    private void mostrarDialogoEditarUsuario(Usuario usuario) {
        Log.d(TAG, "mostrarDialogoEditarUsuario: Mostrando diálogo para " + usuario.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_usuario, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextJugador = dialogView.findViewById(R.id.editTextJugador);
        Spinner spinnerRol = dialogView.findViewById(R.id.spinnerRol);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Pre-llenar campos
        editTextNombre.setText(usuario.getNombre());
        editTextEmail.setText(usuario.getEmail());
        editTextJugador.setText(usuario.getJugador());
        
        // Configurar spinner de roles
        String[] roles = {"Padre", "Madre", "Tutor", "Jugador"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);
        
        // Seleccionar rol actual
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equalsIgnoreCase(usuario.getRol())) {
                spinnerRol.setSelection(i);
                break;
            }
        }
        
        // Configurar spinner de equipos
        String[] equipos = {"Biberones", "Prebenjamín A", "Prebenjamín B", "Benjamín A", "Benjamín B", 
                           "Alevín A", "Alevín B", "Infantil", "Cadete", "Juvenil", "Todo el Club"};
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Seleccionar equipo actual
        if (usuario.getEquipo() != null) {
            for (int i = 0; i < equipos.length; i++) {
                if (equipos[i].equals(usuario.getEquipo())) {
                    spinnerEquipo.setSelection(i);
                    break;
                }
            }
        }
        
        builder.setView(dialogView)
                .setTitle("Editar Usuario")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();
                    String jugador = editTextJugador.getText().toString().trim();
                    String rol = spinnerRol.getSelectedItem().toString().toLowerCase();
                    String equipo = spinnerEquipo.getSelectedItem().toString();
                    
                    if (validarDatosEdicion(nombre, email, usuario.getEmail())) {
                        editarUsuario(usuario, nombre, email, jugador, rol, equipo);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatosEdicion(String nombre, String email, String emailOriginal) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "El email es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "El formato del email no es válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Verificar si el email ya existe (excepto para el usuario actual)
        if (!email.equalsIgnoreCase(emailOriginal) && dataManager.existeUsuario(email)) {
            Toast.makeText(requireContext(), "Ya existe un usuario con ese email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void editarUsuario(Usuario usuarioOriginal, String nombre, String email, String jugador, String rol, String equipo) {
        Log.d(TAG, "editarUsuario: Editando usuario " + usuarioOriginal.getNombre());
        
        // No permitir cambiar rol de administrador
        if (usuarioOriginal.isEsAdmin() && !rol.equalsIgnoreCase("administrador")) {
            Toast.makeText(requireContext(), "No se puede cambiar el rol de un administrador", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Actualizar datos del usuario
        usuarioOriginal.setNombre(nombre);
        usuarioOriginal.setEmail(email);
        usuarioOriginal.setJugador(jugador);
        usuarioOriginal.setRol(rol);
        usuarioOriginal.setEquipo(equipo);
        
        // Guardar cambios
        dataManager.actualizarUsuario(usuarioOriginal);
        cargarUsuarios();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "editarUsuario: Usuario " + nombre + " actualizado correctamente");
    }

    private void mostrarDialogoEliminarUsuario(Usuario usuario) {
        Log.d(TAG, "mostrarDialogoEliminarUsuario: Mostrando diálogo para " + usuario.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que quieres eliminar al usuario '" + usuario.getNombre() + "'?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarUsuario(usuario);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(Usuario usuario) {
        Log.d(TAG, "eliminarUsuario: Eliminando usuario " + usuario.getNombre());
        
        // No permitir eliminar administradores
        if (usuario.isEsAdmin()) {
            Toast.makeText(requireContext(), "No se puede eliminar un administrador", Toast.LENGTH_SHORT).show();
            return;
        }
        
        dataManager.eliminarUsuario(usuario.getEmail());
        cargarUsuarios();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "eliminarUsuario: Usuario " + usuario.getNombre() + " eliminado correctamente");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarUsuarios();
        actualizarEstadisticas();
    }
} 